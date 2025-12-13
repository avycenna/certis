"use client";

import { Table } from "@tanstack/react-table";
import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Badge } from "@/components/ui/badge";
import { SlidersHorizontal, X } from "lucide-react";
import { DataTableFilterConfig } from "../types";
import { ColumnFilter } from "./column-filter";
import { Separator } from "@/components/ui/separator";

interface DataTableFiltersProps<TData> {
  table: Table<TData>;
  filters: DataTableFilterConfig[];
}

export function DataTableFilters<TData>({
  table,
  filters,
}: DataTableFiltersProps<TData>) {
  // Count active filters
  const activeFilterCount = table
    .getAllColumns()
    .filter((column) => column.getIsFiltered()).length;

  const handleResetFilters = () => {
    table.resetColumnFilters();
  };

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button variant="outline" size="sm" className="h-8 gap-2">
          <SlidersHorizontal className="h-4 w-4" />
          Filters
          {activeFilterCount > 0 && (
            <Badge
              variant="secondary"
              className="ml-1 rounded-sm px-1 font-normal"
            >
              {activeFilterCount}
            </Badge>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-80 p-4" align="start">
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h4 className="font-medium text-sm">Filters</h4>
            {activeFilterCount > 0 && (
              <Button
                variant="ghost"
                size="sm"
                onClick={handleResetFilters}
                className="h-8 px-2 lg:px-3"
              >
                Reset
                <X className="ml-2 h-4 w-4" />
              </Button>
            )}
          </div>
          <Separator />
          <div className="space-y-4">
            {filters.map((filter) => {
              const column = table.getColumn(filter.columnId);
              if (!column) return null;

              return (
                <ColumnFilter
                  key={filter.columnId}
                  column={column}
                  title={filter.title}
                  type={filter.type}
                  options={filter.options}
                />
              );
            })}
          </div>
        </div>
      </PopoverContent>
    </Popover>
  );
}
