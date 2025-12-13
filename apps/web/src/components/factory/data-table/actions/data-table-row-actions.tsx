"use client";

import { useState } from "react";
import { Row } from "@tanstack/react-table";
import { MoreHorizontal } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { DataTableAction } from "../types";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";

interface DataTableRowActionsProps<TData> {
  row: Row<TData>;
  actions?: DataTableAction<TData>[];
  quickActions?: Array<{
    icon: React.ReactNode;
    tooltip: string;
    onClick: (row: TData) => void | Promise<void>;
    variant?: "default" | "destructive" | "outline" | "ghost";
    isVisible?: (row: TData) => boolean;
    isDisabled?: (row: TData) => boolean;
  }>;
}

export function DataTableRowActions<TData>({
  row,
  actions = [],
  quickActions = [],
}: DataTableRowActionsProps<TData>) {
  const [confirmAction, setConfirmAction] = useState<DataTableAction<TData> | null>(null);
  const [isExecuting, setIsExecuting] = useState(false);

  const handleActionClick = async (action: DataTableAction<TData>) => {
    if (action.requiresConfirmation) {
      setConfirmAction(action);
    } else {
      await executeAction(action);
    }
  };

  const executeAction = async (action: DataTableAction<TData>) => {
    setIsExecuting(true);
    try {
      await action.onClick(row.original);
    } finally {
      setIsExecuting(false);
      setConfirmAction(null);
    }
  };

  const visibleQuickActions = quickActions.filter(
    (action) => !action.isVisible || action.isVisible(row.original)
  );

  const visibleActions = actions.filter(
    (action) => !action.isVisible || action.isVisible(row.original)
  );

  if (visibleQuickActions.length === 0 && visibleActions.length === 0) {
    return null;
  }

  return (
    <>
      <div className="flex items-center gap-2">
        {/* Quick action buttons */}
        {visibleQuickActions.length > 0 && (
          <TooltipProvider>
            <div className="flex items-center gap-1">
              {visibleQuickActions.map((action, index) => {
                const isDisabled = action.isDisabled?.(row.original) ?? false;
                
                return (
                  <Tooltip key={index}>
                    <TooltipTrigger asChild>
                      <Button
                        variant={action.variant ?? "ghost"}
                        size="icon"
                        className="h-8 w-8"
                        onClick={() => action.onClick(row.original)}
                        disabled={isDisabled}
                      >
                        {action.icon}
                        <span className="sr-only">{action.tooltip}</span>
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>
                      <p>{action.tooltip}</p>
                    </TooltipContent>
                  </Tooltip>
                );
              })}
            </div>
          </TooltipProvider>
        )}

        {/* Dropdown menu for more actions */}
        {visibleActions.length > 0 && (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                variant="ghost"
                className="h-8 w-8 p-0"
              >
                <span className="sr-only">Open menu</span>
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              {visibleActions.map((action, index) => {
                const isDisabled = action.isDisabled?.(row.original) ?? false;
                const isDestructive = action.variant === "destructive";

                return (
                  <div key={index}>
                    {index > 0 && isDestructive && <DropdownMenuSeparator />}
                    <DropdownMenuItem
                      onClick={() => handleActionClick(action)}
                      disabled={isDisabled}
                      className={isDestructive ? "text-destructive" : ""}
                    >
                      {action.icon && <span className="mr-2">{action.icon}</span>}
                      {action.label}
                    </DropdownMenuItem>
                  </div>
                );
              })}
            </DropdownMenuContent>
          </DropdownMenu>
        )}
      </div>

      {/* Confirmation dialog */}
      {confirmAction && (
        <AlertDialog
          open={!!confirmAction}
          onOpenChange={(open) => !open && setConfirmAction(null)}
        >
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>
                {typeof confirmAction.confirmationTitle === 'function'
                  ? confirmAction.confirmationTitle(row.original)
                  : confirmAction.confirmationTitle ?? 'Are you sure?'}
              </AlertDialogTitle>
              <AlertDialogDescription>
                {typeof confirmAction.confirmationDescription === 'function'
                  ? confirmAction.confirmationDescription(row.original)
                  : confirmAction.confirmationDescription ??
                  'This action cannot be undone.'}
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel disabled={isExecuting}>Cancel</AlertDialogCancel>
              <AlertDialogAction
                onClick={() => executeAction(confirmAction)}
                disabled={isExecuting}
                className={
                  confirmAction.variant === "destructive"
                    ? "bg-destructive text-destructive-foreground hover:bg-destructive/90"
                    : ""
                }
              >
                {isExecuting ? 'Processing...' : 'Continue'}
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      )}
    </>
  );
}
