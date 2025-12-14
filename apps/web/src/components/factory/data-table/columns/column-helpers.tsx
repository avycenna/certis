/**
 * @file column-helpers.tsx
 * @description Helper functions for creating data table columns
 * @author Oubara Mehdi
 * @version 1.0.0
 * @edit Oubara Mehdi - 29 Nov 2025
 */

"use client";

import { ColumnDef } from "@tanstack/react-table";
import { Checkbox } from "@/components/ui/checkbox";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { format } from "date-fns";
import { ArrowUpDown } from "lucide-react";
import { Button } from "@/components/ui/button";
import { DataTableRowActions } from "../actions/data-table-row-actions";
import { DataTableAction, DataTableQuickAction } from "../types";

/**
 * Create a selection column for row selection
 */
export function createSelectionColumn<TData>(): ColumnDef<TData> {
  return {
    id: "select",
    header: ({ table }) => (
      <Checkbox
        checked={
          table.getIsAllPageRowsSelected() ||
          (table.getIsSomePageRowsSelected() && "indeterminate")
        }
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
        className="translate-y-0.5"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label="Select row"
        className="translate-y-0.5"
      />
    ),
    enableSorting: false,
    enableHiding: false,
  };
}

/**
 * Create an actions column with dropdown menu and quick actions
 */
export function createActionsColumn<TData>(
  actions: DataTableAction<TData>[],
  quickActions?: DataTableQuickAction<TData>[]
): ColumnDef<TData> {
  return {
    id: "actions",
    cell: ({ row }) => (
      <DataTableRowActions row={row} actions={actions} quickActions={quickActions} />
    ),
    enableSorting: false,
    enableHiding: false,
  };
}

/**
 * Create a sortable header
 */
export function createSortableHeader(column: any, title: string) {
  return (
    <Button
      variant="ghost"
      onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      className="-ml-3 h-8"
    >
      {title}
      <ArrowUpDown className="ml-2 h-4 w-4" />
    </Button>
  );
}

/**
 * Create a text column with optional sorting
 */
export function createTextColumn<TData>(
  accessorKey: string,
  header: string,
  options?: {
    sortable?: boolean;
    filterable?: boolean;
    exportFormatter?: (value: any) => string;
  }
): ColumnDef<TData> {
  return {
    accessorKey,
    header: options?.sortable
      ? ({ column }) => createSortableHeader(column, header)
      : header,
    cell: ({ getValue }) => {
      const value = getValue() as string;
      return <div className="max-w-[500px] truncate">{value}</div>;
    },
    meta: {
      filterVariant: options?.filterable ? "text" : undefined,
      exportFormatter: options?.exportFormatter,
    },
    enableSorting: options?.sortable ?? false,
  };
}

/**
 * Create a date column with formatting
 */
export function createDateColumn<TData>(
  accessorKey: string,
  header: string,
  options?: {
    sortable?: boolean;
    filterable?: boolean;
    formatString?: string;
  }
): ColumnDef<TData> {
  const formatString = options?.formatString ?? "PPP";

  return {
    accessorKey,
    header: options?.sortable
      ? ({ column }) => createSortableHeader(column, header)
      : header,
    cell: ({ getValue }) => {
      const value = getValue() as Date | string | null;
      if (!value) return <span className="text-muted-foreground">—</span>;
      
      const date = typeof value === "string" ? new Date(value) : value;
      return format(date, formatString);
    },
    filterFn: (row, columnId, filterValue) => {
      const value = row.getValue(columnId) as Date | string | null;
      if (!value) return false;
      
      const date = typeof value === "string" ? new Date(value) : value;
      
      // Handle date range filtering
      if (Array.isArray(filterValue)) {
        const [startDate, endDate] = filterValue as [Date?, Date?];
        if (startDate && endDate) {
          return date >= startDate && date <= endDate;
        } else if (startDate) {
          return date >= startDate;
        } else if (endDate) {
          return date <= endDate;
        }
        return true;
      }
      
      // Handle single date filtering
      if (filterValue instanceof Date) {
        return date.toDateString() === filterValue.toDateString();
      }
      
      return true;
    },
    meta: {
      filterVariant: options?.filterable ? "date" : undefined,
      exportFormatter: (value: unknown) => {
        if (!value) return "";
        const date = typeof value === "string" ? new Date(value) : value as Date;
        return format(date, formatString);
      },
    },
    enableSorting: options?.sortable ?? false,
  };
}

/**
 * Create a badge/status column with predefined variants
 */
export function createBadgeColumn<TData>(
  accessorKey: string,
  header: string,
  options: {
    sortable?: boolean;
    filterable?: boolean;
    variantMap?: Record<string, "default" | "secondary" | "destructive" | "outline">;
    labelMap?: Record<string, string>;
    filterOptions?: Array<{ label: string; value: string }>;
  }
): ColumnDef<TData> {
  const { variantMap = {}, labelMap = {}, filterOptions } = options;

  return {
    accessorKey,
    header: options?.sortable
      ? ({ column }) => createSortableHeader(column, header)
      : header,
    cell: ({ getValue }) => {
      const rawValue = getValue();
      let value: string;
      
      if (typeof rawValue === 'object' && rawValue !== null && 'name' in rawValue) {
        value = (rawValue as { name: string }).name;
      } else if (typeof rawValue === 'string') {
        value = rawValue;
      } else {
        value = String(rawValue ?? '');
      }
      
      const variant = variantMap[value] ?? "default";
      const label = labelMap[value] ?? value;

      return (
        <Badge variant={variant} className="capitalize">
          {label}
        </Badge>
      );
    },
    meta: {
      filterVariant: options?.filterable ? "select" : undefined,
      filterOptions,
      exportFormatter: (value: unknown) => labelMap[value as string] ?? String(value),
    },
    enableSorting: options?.sortable ?? false,
  };
}

/**
 * Create an avatar column with name and image
 */
export function createAvatarColumn<TData>(
  nameAccessor: string,
  imageAccessor: string,
  header: string,
  options?: {
    sortable?: boolean;
    filterable?: boolean;
    emailAccessor?: string;
  }
): ColumnDef<TData> {
  return {
    accessorKey: nameAccessor,
    header: options?.sortable
      ? ({ column }) => createSortableHeader(column, header)
      : header,
    cell: ({ row }) => {
      const name = row.getValue(nameAccessor) as string;
      // Use row.original for image and email, since they may not be columns
      const image = (row.original as any)[imageAccessor] as string | null;
      const email = options?.emailAccessor
        ? (row.original as any)[options.emailAccessor] as string
        : null;

      return (
        <div className="flex items-center gap-3">
          <Avatar className="h-8 w-8">
            <AvatarImage src={image ?? undefined} alt={name} />
            <AvatarFallback>
              {name
                ?.split(" ")
                .map((n) => n[0])
                .join("")
                .toUpperCase() ?? "?"}
            </AvatarFallback>
          </Avatar>
          <div className="flex flex-col">
            <span className="font-medium">{name}</span>
            {email && (
              <span className="text-xs text-muted-foreground">{email}</span>
            )}
          </div>
        </div>
      );
    },
    meta: {
      filterVariant: options?.filterable ? "text" : undefined,
      exportFormatter: (value: unknown) => String(value),
    },
    enableSorting: options?.sortable ?? false,
  };
}

/**
 * Create a boolean column with checkmark or cross
 */
export function createBooleanColumn<TData>(
  accessorKey: string,
  header: string,
  options?: {
    sortable?: boolean;
    filterable?: boolean;
    trueLabel?: string;
    falseLabel?: string;
  }
): ColumnDef<TData> {
  return {
    accessorKey,
    header: options?.sortable
      ? ({ column }) => createSortableHeader(column, header)
      : header,
    cell: ({ getValue }) => {
      const value = getValue() as boolean;
      return (
        <Badge variant={value ? "default" : "secondary"}>
          {value ? (options?.trueLabel ?? "Yes") : (options?.falseLabel ?? "No")}
        </Badge>
      );
    },
    meta: {
      filterVariant: options?.filterable ? "select" : undefined,
      filterOptions: [
        { label: options?.trueLabel ?? "Yes", value: "true" },
        { label: options?.falseLabel ?? "No", value: "false" },
      ],
      exportFormatter: (value: unknown) =>
        value ? (options?.trueLabel ?? "Yes") : (options?.falseLabel ?? "No"),
    },
    enableSorting: options?.sortable ?? false,
  };
}

/**
 * Create a number column with formatting
 */
export function createNumberColumn<TData>(
  accessorKey: string,
  header: string,
  options?: {
    sortable?: boolean;
    filterable?: boolean;
    formatter?: (value: number) => string;
  }
): ColumnDef<TData> {
  return {
    accessorKey,
    header: options?.sortable
      ? ({ column }) => createSortableHeader(column, header)
      : header,
    cell: ({ getValue }) => {
      const value = getValue() as number;
      if (value === null || value === undefined) {
        return <span className="text-muted-foreground">—</span>;
      }
      return options?.formatter ? options.formatter(value) : value;
    },
    meta: {
      filterVariant: options?.filterable ? "range" : undefined,
      exportFormatter: options?.formatter ? (value: unknown) => options.formatter!(value as number) : undefined,
    },
    enableSorting: options?.sortable ?? false,
  };
}
