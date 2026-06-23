import { rankAndFilterCustomers, SearchableCustomerItem } from "./CustomerComboBox";

const customer = (
  entityId: string,
  customerName: string,
  secretsEx?: any
): SearchableCustomerItem => ({
  id: { entityId },
  data: { customerName },
  secretsEx,
});

describe("rankAndFilterCustomers", () => {
  const items: SearchableCustomerItem[] = [
    customer("g2-b", "Beta Pozor"),
    customer("g3-b", "XxpoZyy"),
    customer("g1-b", "POZ Centrum"),
    customer("g3-a", "ApoZnak"),
    customer("g2-a", "Alfa Poznan"),
    customer("g1-a", "Poz Auto"),
    customer("fallback", "Klient Bez Dopasowania", [
      { location: "Portal świadczeniodawcy", entityCode: "AA-POZ-99" },
    ]),
    customer("excluded", "Inny Klient", [
      { location: "Portal świadczeniodawcy", entityCode: "AA-XYZ-99" },
    ]),
  ];

  it("returns ranked results in three groups for query fragment like poz", () => {
    const result = rankAndFilterCustomers(items, "poz");

    expect(result.map(o => o.key)).toEqual([
      "g1-a",
      "g1-b",
      "g2-a",
      "g2-b",
      "g3-a",
      "fallback",
      "g3-b",
    ]);
  });

  it("is case-insensitive for poz/Poz/POZ", () => {
    const lower = rankAndFilterCustomers(items, "poz");
    const mixed = rankAndFilterCustomers(items, "Poz");
    const upper = rankAndFilterCustomers(items, "POZ");

    expect(mixed).toEqual(lower);
    expect(upper).toEqual(lower);
  });

  it("sorts items ascending alphabetically inside each rank group", () => {
    const result = rankAndFilterCustomers(items, "poz");

    expect(result.slice(0, 2).map(o => o.text)).toEqual(["Poz Auto", "POZ Centrum"]);
    expect(result.slice(2, 4).map(o => o.text)).toEqual(["Alfa Poznan", "Beta Pozor"]);
    expect(result.slice(4, 7).map(o => o.text)).toEqual(["ApoZnak", "Klient Bez Dopasowania", "XxpoZyy"]);
  });

  it("keeps empty query behavior sane by returning all items sorted ascending", () => {
    const emptyQueryItems: SearchableCustomerItem[] = [
      customer("b", "Beta"),
      customer("a2", "Alfa"),
      customer("a1", "Alfa"),
      customer("g", "Gamma"),
    ];

    const result = rankAndFilterCustomers(emptyQueryItems, "   ");

    expect(result.map(o => `${o.text}:${o.key}`)).toEqual([
      "Alfa:a1",
      "Alfa:a2",
      "Beta:b",
      "Gamma:g",
    ]);
  });

  it("includes customer matched by NFZ code when query matches code", () => {
    const result = rankAndFilterCustomers(items, "poz");

    expect(result.some(o => o.key === "fallback")).toBe(true);
    expect(result.some(o => o.key === "excluded")).toBe(false);
  });

  it("finds customer by NFZ code prefix", () => {
    const result = rankAndFilterCustomers(items, "AA-POZ");

    expect(result.map(o => o.key)).toContain("fallback");
    expect(result.some(o => o.key === "excluded")).toBe(false);
  });

  it("returns code-matched customer with rank 3 (contains match)", () => {
    const codeOnlyItems: SearchableCustomerItem[] = [
      customer("code-match", "Klient Bez Dopasowania", [
        { location: "Portal \u015bwiadczeniodawcy", entityCode: "AA-POZ-99" },
      ]),
      customer("name-match", "POZ Centrum"),
    ];

    const result = rankAndFilterCustomers(codeOnlyItems, "POZ");

    // name-match starts with POZ (rank 1), code-match contains POZ in code (rank 3)
    expect(result[0].key).toBe("name-match");
    expect(result[1].key).toBe("code-match");
  });
});
