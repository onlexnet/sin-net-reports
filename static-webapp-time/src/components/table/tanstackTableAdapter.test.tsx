import * as React from "react";
import { fireEvent, render, screen } from "@testing-library/react";

import {
  type TableAdapterColumn,
  TanStackTableView,
  useTanStackTableAdapter,
} from "./tanstackTableAdapter";

interface SpikeRow {
  id: string;
  name: string;
  city: string;
}

const columns: TableAdapterColumn<SpikeRow>[] = [
  {
    key: "name",
    title: "Imię",
    dataIndex: "name",
    sortable: true,
    filterable: true,
  },
  {
    key: "city",
    title: "Miasto",
    dataIndex: "city",
    filterable: true,
  },
];

const rows: SpikeRow[] = [
  { id: "1", name: "Ola", city: "Warszawa" },
  { id: "2", name: "Bartek", city: "Gdańsk" },
  { id: "3", name: "Celina", city: "Kraków" },
];

const SpikeHarness: React.FC = () => {
  const { table, setColumnFilterValue } = useTanStackTableAdapter({
    columns,
    data: rows,
    initialPageSize: 2,
  });

  return (
    <div>
      <input
        aria-label="Filtr miasta"
        onChange={(event) => setColumnFilterValue("city", event.target.value)}
      />
      <TanStackTableView table={table} />
    </div>
  );
};

describe("tanstack table adapter spike", () => {
  it("maps columns and supports sorting/filtering/pagination", () => {
    render(<SpikeHarness />);

    expect(screen.getByText("Imię")).toBeInTheDocument();
    expect(screen.getByText("Miasto")).toBeInTheDocument();

    expect(screen.getByText("Ola")).toBeInTheDocument();
    expect(screen.getByText("Bartek")).toBeInTheDocument();
    expect(screen.queryByText("Celina")).not.toBeInTheDocument();

    fireEvent.click(screen.getByText("Następna"));
    expect(screen.getByText("Celina")).toBeInTheDocument();
    expect(screen.queryByText("Ola")).not.toBeInTheDocument();

    fireEvent.click(screen.getByText("Poprzednia"));
    fireEvent.click(screen.getByRole("button", { name: "Sortuj po name" }));
    expect(screen.getByText("Bartek")).toBeInTheDocument();

    fireEvent.change(screen.getByLabelText("Filtr miasta"), { target: { value: "krak" } });
    expect(screen.getByText("Celina")).toBeInTheDocument();
    expect(screen.queryByText("Bartek")).not.toBeInTheDocument();
  });
});