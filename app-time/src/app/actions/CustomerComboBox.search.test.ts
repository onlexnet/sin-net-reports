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
    expect(result.slice(4, 6).map(o => o.text)).toEqual(["ApoZnak", "XxpoZyy"]);
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

  it("does not include non-name matches for non-empty query", () => {
    const result = rankAndFilterCustomers(items, "poz");

    expect(result.some(o => o.key === "fallback")).toBe(false);
    expect(result.some(o => o.key === "excluded")).toBe(false);
  });
});
