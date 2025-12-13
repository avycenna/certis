import * as XLSX from "xlsx";
import { Row, Table } from "@tanstack/react-table";

export interface ExportConfig {
  format: "csv" | "xlsx";
  filename: string;
  includeHeaders?: boolean;
  selectedRowsOnly?: boolean;
}

/**
 * Export table data to CSV or XLSX format
 */
export function exportTableData<TData>(
  table: Table<TData>,
  config: ExportConfig
) {
  const {
    format,
    filename,
    includeHeaders = true,
    selectedRowsOnly = false,
  } = config;

  // Get rows to export
  const rows = selectedRowsOnly
    ? table.getFilteredSelectedRowModel().rows
    : table.getFilteredRowModel().rows;

  if (rows.length === 0) {
    console.warn("No data to export");
    return;
  }

  // Get visible columns
  const columns = table
    .getAllLeafColumns()
    .filter((col) => col.getIsVisible() && col.id !== "select" && col.id !== "actions");

  // Prepare data
  const data: any[] = [];

  // Add headers
  if (includeHeaders) {
    const headers = columns.map((col) => {
      const header = col.columnDef.header;
      return typeof header === "string" ? header : col.id;
    });
    data.push(headers);
  }

  // Add rows
  rows.forEach((row) => {
    const rowData = columns.map((col) => {
      const cell = row.getValue(col.id);
      
      // Use custom export formatter if defined
      const meta = col.columnDef.meta;
      if (meta?.exportFormatter) {
        return meta.exportFormatter(cell);
      }

      // Handle different data types
      if (cell === null || cell === undefined) {
        return "";
      }
      if (cell instanceof Date) {
        return cell.toISOString();
      }
      if (typeof cell === "object") {
        return JSON.stringify(cell);
      }
      return String(cell);
    });
    data.push(rowData);
  });

  if (format === "csv") {
    exportToCSV(data, filename);
  } else {
    exportToXLSX(data, filename);
  }
}

/**
 * Export data to CSV
 */
function exportToCSV(data: any[][], filename: string) {
  const csv = data
    .map((row) =>
      row
        .map((cell) => {
          // Escape quotes and wrap in quotes if contains comma, quote, or newline
          const cellStr = String(cell);
          if (cellStr.includes(",") || cellStr.includes('"') || cellStr.includes("\n")) {
            return `"${cellStr.replace(/"/g, '""')}"`;
          }
          return cellStr;
        })
        .join(",")
    )
    .join("\n");

  downloadFile(csv, `${filename}.csv`, "text/csv;charset=utf-8;");
}

/**
 * Export data to XLSX
 */
function exportToXLSX(data: any[][], filename: string) {
  const worksheet = XLSX.utils.aoa_to_sheet(data);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, "Sheet1");

  // Generate buffer
  const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });

  // Download
  const blob = new Blob([excelBuffer], {
    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = `${filename}.xlsx`;
  link.click();
  URL.revokeObjectURL(url);
}

/**
 * Download a file
 */
function downloadFile(content: string, filename: string, mimeType: string) {
  const blob = new Blob([content], { type: mimeType });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  link.click();
  URL.revokeObjectURL(url);
}
