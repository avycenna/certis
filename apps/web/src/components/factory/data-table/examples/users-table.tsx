/**
 * EXAMPLES
 * Errors in this file should be ignored
 * They come from a different codebase to showcase data table factory usage
 */

"use client";

import { ColumnDef } from "@tanstack/react-table";
import { Eye, Edit, Trash2, RotateCcw } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import {
  DataTable,
  createSelectionColumn,
  createActionsColumn,
  createAvatarColumn,
  createDateColumn,
  createBadgeColumn,
  createBooleanColumn,
  DataTableAction,
  DataTableQuickAction,
  DataTableFilterConfig,
} from "@/components/factory/data-table";

import { User } from "@prisma/client";

export function UsersTable({ users, isLoading }: { users: User[]; isLoading?: boolean }) {
  
  // Define actions for the table
  const actions: DataTableAction<User>[] = [
    {
      label: 'View Details',
      icon: <Eye className="h-4 w-4" />,
      onClick: (user) => {
        console.log("View user:", user);
        // Navigate to user detail page
        // router.push(`/dashboard/users/${user.id}`);
      },
    },
    {
      label: 'Edit',
      icon: <Edit className="h-4 w-4" />,
      onClick: (user) => {
        console.log("Edit user:", user);
        // Open edit dialog or navigate to edit page
      },
    },
    {
      label: 'Reset Password',
      icon: <RotateCcw className="h-4 w-4" />,
      onClick: async (user) => {
        console.log("Reset password for:", user.email);
        // Call API to send password reset email
      },
      requiresConfirmation: true,
      confirmationTitle: 'Reset Password?',
      confirmationDescription: 'This will send a password reset email to the user.',
    },
    {
      label: 'Delete',
      icon: <Trash2 className="h-4 w-4" />,
      variant: "destructive",
      onClick: async (user) => {
        console.log("Delete user:", user);
        // Call API to delete user
      },
      requiresConfirmation: true,
      confirmationTitle: 'Delete User?',
      confirmationDescription: 'This action cannot be undone. This will permanently delete the user account.',
      isVisible: (user) => user.systemRole !== "sudoer" && user.systemRole !== "staff", // Don't allow deleting system admins
    },
  ];

  // Define quick actions (icon buttons)
  const quickActions: DataTableQuickAction<User>[] = [
    {
      icon: <Eye className="h-4 w-4" />,
      tooltip: 'View Details',
      onClick: (user) => {
        console.log("Quick view:", user);
      },
    },
    {
      icon: <Edit className="h-4 w-4" />,
      tooltip: 'Edit',
      onClick: (user) => {
        console.log("Quick edit:", user);
      },
    },
  ];

  // Define filters
  const filters: DataTableFilterConfig[] = [
    {
      columnId: "systemRole",
      title: 'Role',
      type: "select",
      options: [
        {
          label: 'Sudoer',
          value: "sudoer",
          icon: <Badge variant="destructive" className="h-2 w-2 rounded-full p-0" />,
        },
        {
          label: 'Staff',
          value: "staff",
          icon: <Badge variant="default" className="h-2 w-2 rounded-full p-0" />,
        },
        {
          label: 'User',
          value: "user",
          icon: <Badge variant="secondary" className="h-2 w-2 rounded-full p-0" />,
        },
        {
          label: 'Guest',
          value: "guest",
          icon: <Badge variant="outline" className="h-2 w-2 rounded-full p-0" />,
        },
      ],
    },
    {
      columnId: "isActive",
      title: 'Status',
      type: "select",
      options: [
        { label: 'Active', value: "true" },
        { label: 'Inactive', value: "false" },
      ],
    },
    {
      columnId: "createdAt",
      title: 'Created',
      type: "dateRange",
    },
  ];

  // Define columns
  const columns: ColumnDef<User, unknown>[] = [
    createSelectionColumn<User>(),
    createAvatarColumn<User>("name", "image", 'User', {
      sortable: true,
      filterable: true,
      emailAccessor: "email",
    }),
    {
      accessorKey: "email",
      header: 'Email',
      cell: ({ getValue }) => (
        <div className="max-w-[300px] truncate font-mono text-sm">
          {getValue() as string}
        </div>
      ),
    },
    createBadgeColumn<User>("systemRole", 'Role', {
      sortable: true,
      filterable: true,
      variantMap: {
        sudoer: "destructive",
        staff: "default",
        user: "secondary",
        guest: "outline",
      },
      labelMap: {
        sudoer: 'Sudoer',
        staff: 'Staff',
        user: 'User',
        guest: 'Guest',
      },
      filterOptions: [
        { label: 'Sudoer', value: "sudoer" },
        { label: 'Staff', value: "staff" },
        { label: 'User', value: "user" },
        { label: 'Guest', value: "guest" },
      ],
    }),
    createBooleanColumn<User>("isActive", 'Status', {
      sortable: true,
      filterable: true,
      trueLabel: 'Active',
      falseLabel: 'Inactive',
    }),
    createDateColumn<User>("lastLogin", 'Last Login', {
      sortable: true,
      formatString: "PPp",
    }),
    createDateColumn<User>("createdAt", 'Created', {
      sortable: true,
      filterable: true,
      formatString: "PPP",
    }),
    createActionsColumn<User>(actions, quickActions),
  ];

  return (
    <DataTable<User>
      columns={columns}
      data={users}
      enableSorting
      enableFiltering
      enableColumnVisibility
      enableRowSelection
      enableMultiRowSelection
      enablePagination
      enableGlobalFilter
      enableExport
      filters={filters}
      exportFileName="users"
      isLoading={isLoading}
      searchPlaceholder='Search users...'
      pageSize={10}
      pageSizeOptions={[10, 20, 50, 100]}
      rowIdAccessor="id"
      onRowSelectionChange={(selectedRows) => {
        console.log("Selected users:", selectedRows);
      }}
      emptyState={{
        title: 'No users found',
        description: 'No users match your search criteria.',
      }}
    />
  );
}
