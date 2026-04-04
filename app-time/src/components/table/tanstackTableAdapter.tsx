import * as React from "react";
import {
  type ColumnDef,
  type ColumnFiltersState,
  type PaginationState,
  type SortingState,
  type Table as TanStackTable,
  type Updater,
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
} from "@tanstack/react-table";
import { ArrowDown, ArrowUp, ArrowUpDown } from "lucide-react";

import { cn } from "lib/utils";
import { Button } from "components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "components/ui/table";

export interface TableAdapterColumn<TData extends object> {
  key: string;
  title: React.ReactNode;
  dataIndex?: keyof TData & string;
  accessorFn?: (row: TData) => unknown;
  render?: (value: unknown, row: TData) => React.ReactNode;
  sortable?: boolean;
  filterable?: boolean;
  width?: string | number;
}

export interface UseTanStackTableAdapterOptions<TData extends object> {
  data: TData[];
  columns: TableAdapterColumn<TData>[];
  initialPageSize?: number;
  initialSorting?: SortingState;
  initialFilters?: ColumnFiltersState;
  onSortingChange?: (nextSorting: SortingState) => void;
  onFiltersChange?: (nextFilters: ColumnFiltersState) => void;
  onPaginationChange?: (nextPagination: PaginationState) => void;
}

export interface UseTanStackTableAdapterResult<TData extends object> {
  table: TanStackTable<TData>;
  sorting: SortingState;
  columnFilters: ColumnFiltersState;
  pagination: PaginationState;
  setColumnFilterValue: (columnId: string, value: unknown) => void;
}

const resolveUpdater = <TValue,>(updater: Updater<TValue>, previous: TValue): TValue => {
  if (typeof updater === "function") {
    return (updater as (old: TValue) => TValue)(previous);
  }

  return updater;
};

const defaultTextFilter = (rowValue: unknown, filterValue: unknown): boolean => {
  if (filterValue == null || filterValue === "") {
    return true;
  }

  return String(rowValue ?? "")
    .toLowerCase()
    .includes(String(filterValue).toLowerCase());
};

export const mapColumnsToTanStack = <TData extends object>(
  columns: TableAdapterColumn<TData>[]
): ColumnDef<TData>[] => {
  return columns.map((column): ColumnDef<TData> => {
    const hasAccessor = !!column.dataIndex || !!column.accessorFn;
    if (!hasAccessor) {
      throw new Error(`TableAdapter column "${column.key}" requires dataIndex or accessorFn.`);
    }

    return {
      id: column.key,
      header: () => column.title,
      accessorKey: column.dataIndex,
      accessorFn: column.accessorFn,
      enableSorting: !!column.sortable,
      enableColumnFilter: !!column.filterable,
      filterFn: column.filterable
        ? (row, columnId, filterValue) => defaultTextFilter(row.getValue(columnId), filterValue)
        : undefined,
      cell: (context) => {
        const value = context.getValue();

        if (column.render) {
          return column.render(value, context.row.original);
        }

        return String(value ?? "");
      },
      meta: {
        width: column.width,
      },
    };
  });
};

export const useTanStackTableAdapter = <TData extends object>(
  options: UseTanStackTableAdapterOptions<TData>
): UseTanStackTableAdapterResult<TData> => {
  const [sorting, setSorting] = React.useState<SortingState>(options.initialSorting ?? []);
  const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>(options.initialFilters ?? []);
  const [pagination, setPagination] = React.useState<PaginationState>({
    pageIndex: 0,
    pageSize: options.initialPageSize ?? 20,
  });

  const columns = React.useMemo(() => mapColumnsToTanStack(options.columns), [options.columns]);

  const table = useReactTable({
    data: options.data,
    columns,
    state: {
      sorting,
      columnFilters,
      pagination,
    },
    onSortingChange: (updater) => {
      setSorting((previous) => {
        const next = resolveUpdater(updater, previous);
        options.onSortingChange?.(next);
        return next;
      });
    },
    onColumnFiltersChange: (updater) => {
      setColumnFilters((previous) => {
        const next = resolveUpdater(updater, previous);
        options.onFiltersChange?.(next);
        return next;
      });
    },
    onPaginationChange: (updater) => {
      setPagination((previous) => {
        const next = resolveUpdater(updater, previous);
        options.onPaginationChange?.(next);
        return next;
      });
    },
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    enableSortingRemoval: false,
    autoResetPageIndex: false,
  });

  const setColumnFilterValue = React.useCallback(
    (columnId: string, value: unknown) => {
      table.getColumn(columnId)?.setFilterValue(value);
    },
    [table]
  );

  return {
    table,
    sorting,
    columnFilters,
    pagination,
    setColumnFilterValue,
  };
};

const sortIcon = (isSorted: false | "asc" | "desc") => {
  if (isSorted === "asc") {
    return <ArrowUp className="ml-2 h-4 w-4" aria-hidden="true" />;
  }

  if (isSorted === "desc") {
    return <ArrowDown className="ml-2 h-4 w-4" aria-hidden="true" />;
  }

  return <ArrowUpDown className="ml-2 h-4 w-4" aria-hidden="true" />;
};

export interface TanStackTableViewProps<TData extends object> {
  table: TanStackTable<TData>;
  emptyLabel?: string;
  className?: string;
  showPagination?: boolean;
}

export const TanStackTableView = <TData extends object>({
  table,
  emptyLabel = "Brak danych.",
  className,
  showPagination = true,
}: TanStackTableViewProps<TData>) => {
  return (
    <div className={cn("w-full", className)}>
      <Table className="w-full table-fixed">
        <TableHeader>
          {table.getHeaderGroups().map((headerGroup) => (
            <TableRow key={headerGroup.id}>
              {headerGroup.headers.map((header) => {
                const canSort = header.column.getCanSort();
                const isSorted = header.column.getIsSorted();
                const rawWidth =
                  header.column.columnDef.meta && "width" in header.column.columnDef.meta
                    ? header.column.columnDef.meta.width
                    : undefined;
                const width =
                  typeof rawWidth === "string" || typeof rawWidth === "number" ? rawWidth : undefined;

                return (
                  <TableHead key={header.id} style={{ width }}>
                    {header.isPlaceholder ? null : canSort ? (
                      <Button
                        type="button"
                        variant="ghost"
                        className="h-auto px-0 py-0 font-semibold text-foreground hover:bg-transparent hover:text-foreground"
                        onClick={header.column.getToggleSortingHandler()}
                        aria-label={`Sortuj po ${String(header.column.id)}`}
                      >
                        {flexRender(header.column.columnDef.header, header.getContext())}
                        {sortIcon(isSorted)}
                      </Button>
                    ) : (
                      flexRender(header.column.columnDef.header, header.getContext())
                    )}
                  </TableHead>
                );
              })}
            </TableRow>
          ))}
        </TableHeader>
        <TableBody>
          {table.getRowModel().rows.length ? (
            table.getRowModel().rows.map((row) => (
              <TableRow key={row.id} data-state={row.getIsSelected() && "selected"}>
                {row.getVisibleCells().map((cell) => (
                  <TableCell key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</TableCell>
                ))}
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={table.getAllColumns().length} className="h-24 text-center">
                {emptyLabel}
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      {showPagination ? (
        <div className="mt-2 flex items-center justify-between">
          <div className="text-sm text-muted-foreground">
            Strona {table.getState().pagination.pageIndex + 1} z {table.getPageCount() || 1}
          </div>
          <div className="flex items-center gap-2">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => table.previousPage()}
              disabled={!table.getCanPreviousPage()}
            >
              Poprzednia
            </Button>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => table.nextPage()}
              disabled={!table.getCanNextPage()}
            >
              Następna
            </Button>
          </div>
        </div>
      ) : null}
    </div>
  );
};