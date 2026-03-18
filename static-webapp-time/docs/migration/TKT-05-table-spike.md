# TKT-05 — Spike tabel (TanStack adapter)

Data: 2026-03-18
Repo: `static-webapp-time`

## 1) Cel spike

Zweryfikować wspólny adapter tabel oparty o `@tanstack/react-table` i prymitywy tabeli zgodne z `shadcn/ui`, tak aby w TKT-06 migrować ekrany tabelaryczne bez zmiany kontraktów danych i callbacków biznesowych.

## 2) Zakres wykonany

- Dodano zależność: `@tanstack/react-table`.
- Dodano prymitywy tabeli UI: `src/components/ui/table.tsx`.
- Dodano wspólny adapter: `src/components/table/tanstackTableAdapter.tsx`.
- Dodano eksporty adaptera: `src/components/table/index.ts`.
- Dodano test POC: `src/components/table/tanstackTableAdapter.test.tsx`.

## 3) Finalny interfejs adaptera (zatwierdzony po spike)

### Wejście kolumn (mapowanie z podejścia antd-like)

`TableAdapterColumn<TData>`:
- `key` — stabilny identyfikator kolumny,
- `title` — nagłówek,
- `dataIndex?` lub `accessorFn?` — źródło wartości,
- `render?` — renderer komórki,
- `sortable?` — sortowanie,
- `filterable?` — filtrowanie,
- `width?` — szerokość kolumny.

### Hook adaptera

`useTanStackTableAdapter<TData>(options)` zwraca:
- `table` — instancję TanStack,
- `sorting`, `columnFilters`, `pagination` — kontrolowany stan,
- `setColumnFilterValue(columnId, value)` — uproszczone API filtrowania.

Obsługiwane mechanizmy:
- sortowanie (`getSortedRowModel`),
- filtrowanie kolumn (`getFilteredRowModel`),
- paginacja (`getPaginationRowModel`),
- mapowanie kolumn (`mapColumnsToTanStack`).

### Renderer

`TanStackTableView<TData>`:
- renderuje nagłówki i wiersze przez `flexRender`,
- obsługuje sortowanie z UI,
- pokazuje prostą paginację (`Poprzednia` / `Następna`),
- ma fallback `emptyLabel` dla pustej tabeli.

## 4) Wynik POC vs wymagania

Wymagania spike (`mapowanie kolumn`, `sortowanie`, `filtrowanie`, `paginacja`) zostały potwierdzone testem `tanstackTableAdapter.test.tsx`.

## 5) Wniosek techniczny do TKT-06

Adapter spełnia wymagania migracyjne dla warstwy widoku i pozwala przepinać istniejące tabele `antd` ekran po ekranie, utrzymując dotychczasowe modele danych i callbacki domenowe.

Rekomendacja implementacyjna dla TKT-06:
- migrować tabele etapowo (`services` -> `customers` -> `reports`),
- zostawić logikę pobierania/filterów domenowych w aktualnych kontenerach,
- ograniczyć zmiany do mapowania kolumn i renderera.