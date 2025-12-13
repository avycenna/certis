/**
 * @file data-table.tsx
 * @description Data table component with sorting, filtering, and pagination
 * @author Oubara Mehdi
 * @version 1.0.0
 * @edit Oubara Mehdi - 29 Nov 2025
 */

"use client";

import {
  ColumnFiltersState,
  SortingState,
  VisibilityState,
  flexRender,
  getCoreRowModel,
  getFacetedRowModel,
  getFacetedUniqueValues,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
  RowSelectionState,
} from "@tanstack/react-table";
import { useState, useEffect } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { DataTableToolbar } from "./toolbar/data-table-toolbar";
import { DataTablePagination } from "./pagination/data-table-pagination";
import { Skeleton } from "@/components/ui/skeleton";
import { DataTableFilterConfig } from "./types";
import { ColumnDef } from "@tanstack/react-table";

export function DataTable<TData>({
  columns,
  data,
  enableSorting = true,
  enableFiltering = true,
  enableColumnVisibility = true,
  enableRowSelection = false,
  enableMultiRowSelection = true,
  enablePagination = true,
  enableGlobalFilter = true,
  enableExport = true,
  rowIdAccessor,
  onRowSelectionChange,
  totalRows,
  pageSize = 10,
  pageSizeOptions = [10, 20, 30, 40, 50],
  onPaginationChange,
  filters = [],
  exportFileName = "data",
  isLoading = false,
  emptyState,
  searchPlaceholder = "Search...",
  onRowClick,
  initialPageIndex,
}: {
  columns: ColumnDef<TData, any>[];
  data: TData[];
  enableSorting?: boolean;
  enableFiltering?: boolean;
  enableColumnVisibility?: boolean;
  enableRowSelection?: boolean;
  enableMultiRowSelection?: boolean;
  enablePagination?: boolean;
  enableGlobalFilter?: boolean;
  enableExport?: boolean;
  rowIdAccessor?: keyof TData | ((row: TData) => string);
  onRowSelectionChange?: (selectedRows: TData[]) => void;
  totalRows?: number;
  pageSize?: number;
  pageSizeOptions?: number[];
  onPaginationChange?: (pagination: { page: number; pageSize: number }) => void;
  filters?: DataTableFilterConfig[];
  exportFileName?: string;
  isLoading?: boolean;
  emptyState?: { title: string; description?: string; action?: React.ReactNode };
  searchPlaceholder?: string;
  onRowClick?: (row: { original: TData }) => void;
  initialPageIndex?: number;
}) {
  const [rowSelection, setRowSelection] = useState<RowSelectionState>({});
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const [sorting, setSorting] = useState<SortingState>([]);
  const [globalFilter, setGlobalFilter] = useState("");
  const [{ pageIndex, pageSize: currentPageSize }, setPagination] = useState({
    pageIndex: initialPageIndex !== undefined ? initialPageIndex : 0,
    pageSize,
  });

  useEffect(() => {
    if (onPaginationChange) {
      onPaginationChange({ page: pageIndex + 1, pageSize: currentPageSize });
    }
  }, [pageIndex, currentPageSize, onPaginationChange]);

  const pagination = { pageIndex, pageSize: currentPageSize };

  const table = useReactTable({
    data,
    columns,
    pageCount: totalRows ? Math.ceil(totalRows / currentPageSize) : -1,
    state: {
      sorting,
      columnVisibility,
      rowSelection,
      columnFilters,
      globalFilter,
      pagination,
    },
    enableRowSelection: enableRowSelection,
    enableMultiRowSelection: enableMultiRowSelection,
    onRowSelectionChange: (updater) => {
      setRowSelection(updater);
      if (onRowSelectionChange) {
        const newSelection = typeof updater === "function" ? updater(rowSelection) : updater;
        const selectedRows = Object.keys(newSelection)
          .filter((key) => newSelection[key])
          .map((key) => data[parseInt(key)]);
        onRowSelectionChange(selectedRows);
      }
    },
    onPaginationChange: setPagination,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    onColumnVisibilityChange: setColumnVisibility,
    onGlobalFilterChange: setGlobalFilter,
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: enablePagination ? getPaginationRowModel() : undefined,
    getSortedRowModel: enableSorting ? getSortedRowModel() : undefined,
    getFacetedRowModel: getFacetedRowModel(),
    getFacetedUniqueValues: getFacetedUniqueValues(),
    manualPagination: totalRows !== undefined,
    getRowId: rowIdAccessor
      ? typeof rowIdAccessor === "function"
        ? rowIdAccessor
        : (row) => String(row[rowIdAccessor])
      : undefined,
  });

  return (
    <div className="space-y-4">
      <DataTableToolbar
        table={table}
        searchPlaceholder={searchPlaceholder || 'Search...'}
        filters={filters}
        enableGlobalFilter={enableGlobalFilter}
        enableFiltering={enableFiltering && filters.length > 0}
        enableColumnVisibility={enableColumnVisibility}
        enableExport={enableExport}
        exportFileName={exportFileName}
      />

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead
                      key={header.id}
                      colSpan={header.colSpan}
                      style={{
                        width: header.getSize() !== 150 ? header.getSize() : undefined,
                      }}
                    >
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                    </TableHead>
                  );
                })}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {isLoading ? (
              Array.from({ length: pageSize }).map((_, index) => (
                <TableRow key={index}>
                  {columns.map((_, colIndex) => (
                    <TableCell key={colIndex}>
                      <Skeleton className="h-8 w-full" />
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  data-state={row.getIsSelected() && "selected"}
                  onClick={() => onRowClick?.({ original: row.original })}
                  className={onRowClick ? "cursor-pointer hover:bg-muted/50" : undefined}
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-24 text-center"
                >
                  {emptyState ? (
                    <div className="flex flex-col items-center justify-center gap-2 py-8">
                      <p className="text-lg font-medium">{emptyState.title}</p>
                      {emptyState.description && (
                        <p className="text-sm text-muted-foreground">
                          {emptyState.description}
                        </p>
                      )}
                      {emptyState.action}
                    </div>
                  ) : (
                    'No results found'
                  )}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {enablePagination && (
        <DataTablePagination 
          table={table} 
          pageSizeOptions={pageSizeOptions}
          totalRows={totalRows}
        />
      )}
    </div>
  );
}
