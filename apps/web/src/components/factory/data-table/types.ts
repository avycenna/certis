import { ColumnDef, RowData } from "@tanstack/react-table";
import { ReactNode } from "react";

// Extend RowData to include custom meta
declare module "@tanstack/react-table" {
  interface TableMeta<TData extends RowData> {
    updateData?: (rowIndex: number, columnId: string, value: unknown) => void;
  }

  interface ColumnMeta<TData extends RowData, TValue> {
    filterVariant?: "text" | "range" | "select" | "date" | "dateRange";
    filterOptions?: Array<{ label: string; value: string }>;
    exportFormatter?: (value: TValue) => string | number;
    headerClassName?: string;
    cellClassName?: string;
  }
}

export type FilterType = "text" | "range" | "select" | "date" | "dateRange";

export interface DataTableFilterOption {
  label: string;
  value: string;
  icon?: ReactNode;
}

export interface DataTableFilterConfig {
  columnId: string;
  title: string;
  type: FilterType;
  options?: DataTableFilterOption[];
}

export interface DataTableAction<TData> {
  label: string;
  icon?: ReactNode;
  onClick: (row: TData) => void | Promise<void>;
  variant?: "default" | "destructive" | "outline" | "ghost";
  requiresConfirmation?: boolean;
  confirmationTitle?: string | ((row: TData) => string);
  confirmationDescription?: string | ((row: TData) => string);
  isVisible?: (row: TData) => boolean;
  isDisabled?: (row: TData) => boolean;
}

export interface DataTableQuickAction<TData> {
  icon: ReactNode;
  tooltip: string;
  onClick: (row: TData) => void | Promise<void>;
  variant?: "default" | "destructive" | "outline" | "ghost";
  isVisible?: (row: TData) => boolean;
  isDisabled?: (row: TData) => boolean;
}

export interface DataTableConfig<TData> {
  columns: ColumnDef<TData, any>[];
  data: TData[];
  
  // Features
  enableSorting?: boolean;
  enableFiltering?: boolean;
  enableColumnVisibility?: boolean;
  enableRowSelection?: boolean;
  enableMultiRowSelection?: boolean;
  enablePagination?: boolean;
  enableGlobalFilter?: boolean;
  enableExport?: boolean;
  enableColumnResizing?: boolean;
  
  // Row selection
  rowIdAccessor?: keyof TData | ((row: TData) => string);
  onRowSelectionChange?: (selectedRows: TData[]) => void;
  
  // Pagination
  pageSize?: number;
  pageSizeOptions?: number[];
  
  // Filters
  filters?: DataTableFilterConfig[];
  
  // Actions
  actions?: DataTableAction<TData>[];
  quickActions?: DataTableQuickAction<TData>[];
  
  // Export
  exportFileName?: string;
  
  // Loading & Empty states
  isLoading?: boolean;
  emptyState?: {
    title: string;
    description?: string;
    action?: ReactNode;
  };
  
  // Optimistic updates
  enableOptimisticUpdates?: boolean;
  onOptimisticUpdate?: (rowIndex: number, columnId: string, value: unknown) => void;
  
  // Search
  searchPlaceholder?: string;
  searchableColumns?: string[];
}

export interface ExportOptions {
  format: "csv" | "xlsx";
  filename: string;
  includeHeaders?: boolean;
  selectedRowsOnly?: boolean;
}
