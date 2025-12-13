"use client";

import { Column } from "@tanstack/react-table";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { DataTableFilterOption } from "../types";
import { Label } from "@/components/ui/label";
import { Calendar } from "@/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { CalendarIcon } from "lucide-react";
import { format } from "date-fns";
import { cn } from "@/lib/utils";

interface ColumnFilterProps<TData, TValue> {
  column: Column<TData, TValue>;
  title: string;
  type: "text" | "range" | "select" | "date" | "dateRange";
  options?: DataTableFilterOption[];
}

export function ColumnFilter<TData, TValue>({
  column,
  title,
  type,
  options,
}: ColumnFilterProps<TData, TValue>) {
  if (type === "text") {
    return <TextFilter column={column} title={title} />;
  }

  if (type === "select" && options) {
    return <SelectFilter column={column} title={title} options={options} />;
  }

  if (type === "range") {
    return <RangeFilter column={column} title={title} />;
  }

  if (type === "date") {
    return <DateFilter column={column} title={title} />;
  }

  if (type === "dateRange") {
    return <DateRangeFilter column={column} title={title} />;
  }

  return null;
}

function TextFilter<TData, TValue>({
  column,
  title,
}: {
  column: Column<TData, TValue>;
  title: string;
}) {
  return (
    <div className="space-y-2">
      <Label className="text-xs font-medium">{title}</Label>
      <Input
        placeholder={`Filter ${title.toLowerCase()}...`}
        value={(column.getFilterValue() as string) ?? ""}
        onChange={(e) => column.setFilterValue(e.target.value)}
        className="h-8"
      />
    </div>
  );
}

function SelectFilter<TData, TValue>({
  column,
  title,
  options,
}: {
  column: Column<TData, TValue>;
  title: string;
  options: DataTableFilterOption[];
}) {
  return (
    <div className="space-y-2">
      <Label className="text-xs font-medium">{title}</Label>
      <Select
        value={(column.getFilterValue() as string) ?? "all"}
        onValueChange={(value) =>
          column.setFilterValue(value === "all" ? undefined : value)
        }
      >
        <SelectTrigger className="h-8 w-full">
          <SelectValue placeholder={`Select ${title.toLowerCase()}`} />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="all">All</SelectItem>
          {options.map((option) => (
            <SelectItem key={option.value} value={option.value}>
              <div className="flex items-center gap-2">
                {option.icon}
                {option.label}
              </div>
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}

function RangeFilter<TData, TValue>({
  column,
  title,
}: {
  column: Column<TData, TValue>;
  title: string;
}) {
  const filterValue = (column.getFilterValue() as [number, number]) ?? [
    undefined,
    undefined,
  ];

  return (
    <div className="space-y-2">
      <Label className="text-xs font-medium">{title}</Label>
      <div className="flex gap-2">
        <Input
          type="number"
          placeholder="Min"
          value={filterValue[0] ?? ""}
          onChange={(e) =>
            column.setFilterValue([
              e.target.value ? Number(e.target.value) : undefined,
              filterValue[1],
            ])
          }
          className="h-8"
        />
        <Input
          type="number"
          placeholder="Max"
          value={filterValue[1] ?? ""}
          onChange={(e) =>
            column.setFilterValue([
              filterValue[0],
              e.target.value ? Number(e.target.value) : undefined,
            ])
          }
          className="h-8"
        />
      </div>
    </div>
  );
}

function DateFilter<TData, TValue>({
  column,
  title,
}: {
  column: Column<TData, TValue>;
  title: string;
}) {
  const date = column.getFilterValue() as Date | undefined;

  return (
    <div className="space-y-2">
      <Label className="text-xs font-medium">{title}</Label>
      <Popover>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            className={cn(
              "h-8 w-full justify-start text-left font-normal",
              !date && "text-muted-foreground"
            )}
          >
            <CalendarIcon className="mr-2 h-4 w-4" />
            {date ? format(date, "PPP") : <span>Pick a date</span>}
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-auto p-0" align="start">
          <Calendar
            mode="single"
            selected={date}
            onSelect={(date) => column.setFilterValue(date)}
            initialFocus
          />
        </PopoverContent>
      </Popover>
    </div>
  );
}

function DateRangeFilter<TData, TValue>({
  column,
  title,
}: {
  column: Column<TData, TValue>;
  title: string;
}) {
  const dateRange = (column.getFilterValue() as [Date?, Date?]) ?? [
    undefined,
    undefined,
  ];

  return (
    <div className="space-y-2">
      <Label className="text-xs font-medium">{title}</Label>
      <div className="flex gap-2">
        <Popover>
          <PopoverTrigger asChild>
            <Button
              variant="outline"
              className={cn(
                "h-8 flex-1 justify-start text-left font-normal",
                !dateRange[0] && "text-muted-foreground"
              )}
            >
              <CalendarIcon className="mr-2 h-4 w-4" />
              {dateRange[0] ? format(dateRange[0], "PP") : <span>From</span>}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={dateRange[0]}
              onSelect={(date) =>
                column.setFilterValue([date, dateRange[1]])
              }
              initialFocus
            />
          </PopoverContent>
        </Popover>

        <Popover>
          <PopoverTrigger asChild>
            <Button
              variant="outline"
              className={cn(
                "h-8 flex-1 justify-start text-left font-normal",
                !dateRange[1] && "text-muted-foreground"
              )}
            >
              <CalendarIcon className="mr-2 h-4 w-4" />
              {dateRange[1] ? format(dateRange[1], "PP") : <span>To</span>}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="start">
            <Calendar
              mode="single"
              selected={dateRange[1]}
              onSelect={(date) =>
                column.setFilterValue([dateRange[0], date])
              }
              initialFocus
            />
          </PopoverContent>
        </Popover>
      </div>
    </div>
  );
}
