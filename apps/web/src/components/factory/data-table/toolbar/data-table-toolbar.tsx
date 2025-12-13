"use client";

import { Table } from "@tanstack/react-table";
import { Input } from "@/components/ui/input";
import { DataTableFilters } from "../filters/data-table-filters";
import { DataTableViewOptions } from "./data-table-view-options";
import { Button } from "@/components/ui/button";
import { Download, FileSpreadsheet } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { exportTableData } from "../export";
import { DataTableFilterConfig } from "../types";

interface DataTableToolbarProps<TData> {
  table: Table<TData>;
  searchPlaceholder?: string;
  filters?: DataTableFilterConfig[];
  enableGlobalFilter?: boolean;
  enableFiltering?: boolean;
  enableColumnVisibility?: boolean;
  enableExport?: boolean;
  exportFileName?: string;
}

export function DataTableToolbar<TData>({
  table,
  searchPlaceholder = "Search...",
  filters = [],
  enableGlobalFilter = true,
  enableFiltering = true,
  enableColumnVisibility = true,
  enableExport = true,
  exportFileName = "export",
}: DataTableToolbarProps<TData>) {
  const hasSelectedRows = table.getFilteredSelectedRowModel().rows.length > 0;

  const handleExport = (format: "csv" | "xlsx", selectedOnly: boolean) => {
    exportTableData(table, {
      format,
      filename: exportFileName,
      includeHeaders: true,
      selectedRowsOnly: selectedOnly,
    });
  };

  return (
    <div className="flex items-center justify-between gap-2">
      <div className="flex flex-1 items-center gap-2">
        {enableGlobalFilter && (
          <Input
            placeholder={searchPlaceholder}
            value={(table.getState().globalFilter as string) ?? ""}
            onChange={(event) => table.setGlobalFilter(event.target.value)}
            className="h-8 w-[150px] lg:w-[250px]"
          />
        )}
        {enableFiltering && filters.length > 0 && (
          <DataTableFilters table={table} filters={filters} />
        )}
      </div>

      <div className="flex items-center gap-2">
        {enableExport && (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="sm" className="h-8 gap-2">
                <Download className="h-4 w-4" />
                Export
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => handleExport("csv", false)}>
                <FileSpreadsheet className="mr-2 h-4 w-4" />
                Export to CSV
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => handleExport("xlsx", false)}>
                <FileSpreadsheet className="mr-2 h-4 w-4" />
                Export to Excel
              </DropdownMenuItem>
              {hasSelectedRows && (
                <>
                  <DropdownMenuItem onClick={() => handleExport("csv", true)}>
                    <FileSpreadsheet className="mr-2 h-4 w-4" />
                    Export Selected (CSV)
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => handleExport("xlsx", true)}>
                    <FileSpreadsheet className="mr-2 h-4 w-4" />
                    Export Selected (Excel)
                  </DropdownMenuItem>
                </>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        )}

        {enableColumnVisibility && <DataTableViewOptions table={table} />}
      </div>
    </div>
  );
}
