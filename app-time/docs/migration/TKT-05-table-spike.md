# TKT-05 — Table Spike (TanStack adapter)

Date: 2026-03-18
Repo: `static-webapp-time`

## 1) Spike goal

Validate a shared table adapter based on `@tanstack/react-table` and table primitives compatible with `shadcn/ui`, so that TKT-06 can migrate tabular screens without changing data contracts or business callbacks.

## 2) Work completed

- Added dependency: `@tanstack/react-table`.
- Added UI table primitives: `src/components/ui/table.tsx`.
- Added shared adapter: `src/components/table/tanstackTableAdapter.tsx`.
- Added adapter exports: `src/components/table/index.ts`.
- Added POC test: `src/components/table/tanstackTableAdapter.test.tsx`.

## 3) Final adapter interface (approved after spike)

### Column input (mapping from antd-like approach)

`TableAdapterColumn<TData>`:
- `key` — stable column identifier,
- `title` — column header,
- `dataIndex?` or `accessorFn?` — value source,
- `render?` — cell renderer,
- `sortable?` — enable sorting,
- `filterable?` — enable filtering,
- `width?` — column width.

### Adapter hook

`useTanStackTableAdapter<TData>(options)` returns:
- `table` — TanStack table instance,
- `sorting`, `columnFilters`, `pagination` — controlled state,
- `setColumnFilterValue(columnId, value)` — simplified filter API.

Supported mechanisms:
- sorting (`getSortedRowModel`),
- column filtering (`getFilteredRowModel`),
- pagination (`getPaginationRowModel`),
- column mapping (`mapColumnsToTanStack`).

### Renderer

`TanStackTableView<TData>`:
- renders headers and rows via `flexRender`,
- handles sorting in the UI,
- shows simple pagination (`Previous` / `Next`),
- has an `emptyLabel` fallback for empty tables.

## 4) POC result vs. requirements

Spike requirements (`column mapping`, `sorting`, `filtering`, `pagination`) were confirmed by the `tanstackTableAdapter.test.tsx` test.

## 5) Technical conclusion for TKT-06

The adapter meets the migration requirements for the view layer and allows existing `antd` tables to be replaced screen by screen while keeping current data models and domain callbacks.

Implementation recommendation for TKT-06:
- migrate tables incrementally (`services` -> `customers` -> `reports`),
- keep domain fetch / filter logic in the existing container components,
- limit changes to column mapping and the renderer.