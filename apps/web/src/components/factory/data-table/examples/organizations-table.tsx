/**
 * EXAMPLES
 * Errors in this file should be ignored
 * They come from a different codebase to showcase data table factory usage
 */

"use client";

import { ColumnDef } from "@tanstack/react-table";
import { Eye, Edit, Trash2, Archive, Users } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import {
  DataTable,
  createSelectionColumn,
  createActionsColumn,
  createTextColumn,
  createDateColumn,
  DataTableAction,
  DataTableQuickAction,
  DataTableFilterConfig,
} from "@/components/factory/data-table";

// Organization type based on your Prisma schema
type Organization = {
  id: string;
  name: string;
  slug: string;
  countryCode: string;
  timezone: string;
  legalName: string | null;
  industry: string | null;
  createdAt: Date;
  updatedAt: Date;
  _count?: {
    memberships: number;
  };
};

export function OrganizationsTable({
  organizations,
  isLoading,
}: {
  organizations: Organization[];
  isLoading?: boolean;  
}) {
  // Define actions for the table
  const actions: DataTableAction<Organization>[] = [
    {
      label: 'View Details',
      icon: <Eye className="h-4 w-4" />,
      onClick: (org) => {
        console.log("View organization:", org);
        // router.push(`/dashboard/organizations/${org.id}`);
      },
    },
    {
      label: 'Edit',
      icon: <Edit className="h-4 w-4" />,
      onClick: (org) => {
        console.log("Edit organization:", org);
        // Open edit dialog
      },
    },
    {
      label: 'Manage Members',
      icon: <Users className="h-4 w-4" />,
      onClick: (org) => {
        console.log("Manage members for:", org.name);
        // router.push(`/dashboard/organizations/${org.id}/members`);
      },
    },
    {
      label: 'Archive',
      icon: <Archive className="h-4 w-4" />,
      variant: "outline",
      onClick: async (org) => {
        console.log("Archive organization:", org);
        // Call API to archive organization
      },
      requiresConfirmation: true,
      confirmationTitle: 'Archive Organization?',
      confirmationDescription: 'This organization will be archived and no longer accessible.',
    },
    {
      label: 'Delete',
      icon: <Trash2 className="h-4 w-4" />,
      variant: "destructive",
      onClick: async (org) => {
        console.log("Delete organization:", org);
        // Call API to delete organization
      },
      requiresConfirmation: true,
      confirmationTitle: 'Delete Organization?',
      confirmationDescription: 'This action cannot be undone. This will permanently delete the organization.',
    },
  ];

  // Define quick actions
  const quickActions: DataTableQuickAction<Organization>[] = [
    {
      icon: <Eye className="h-4 w-4" />,
      tooltip: 'View Details',
      onClick: (org) => {
        console.log("Quick view:", org);
      },
    },
    {
      icon: <Edit className="h-4 w-4" />,
      tooltip: 'Edit',
      onClick: (org) => {
        console.log("Quick edit:", org);
      },
    },
  ];

  // Define filters
  const filters: DataTableFilterConfig[] = [
    {
      columnId: "industry",
      title: "Industry",
      type: "select",
      options: [
        { label: "Technology", value: "technology" },
        { label: "Finance", value: "finance" },
        { label: "Healthcare", value: "healthcare" },
        { label: "Education", value: "education" },
        { label: "Retail", value: "retail" },
        { label: "Other", value: "other" },
      ],
    },
    {
      columnId: "createdAt",
      title: "Created Date",
      type: "dateRange",
    },
  ];

  // Define columns
  const columns: ColumnDef<Organization, unknown>[] = [
    createSelectionColumn<Organization>(),
    {
      accessorKey: "name",
      header: 'Organization',
      cell: ({ row }) => {
        const name = row.getValue("name") as string;
        const slug = row.original.slug;
        return (
          <div className="flex flex-col">
            <span className="font-medium">{name}</span>
            <span className="text-xs text-muted-foreground">@{slug}</span>
          </div>
        );
      },
      enableSorting: true,
    },
    createTextColumn<Organization>("legalName", 'Legal Name', {
      sortable: false,
    }),
    {
      accessorKey: "industry",
      header: 'Industry',
      cell: ({ getValue }) => {
        const industry = getValue() as string | null;
        if (!industry) return <span className="text-muted-foreground">—</span>;
        return (
          <Badge variant="secondary" className="capitalize">
            {industry}
          </Badge>
        );
      },
      enableSorting: true,
      meta: {
        filterVariant: "select",
      },
    },
    {
      accessorKey: "countryCode",
      header: 'Country',
      cell: ({ getValue }) => {
        const code = getValue() as string;
        return <span className="font-mono text-sm uppercase">{code}</span>;
      },
      enableSorting: true,
    },
    {
      accessorKey: "_count.memberships",
      header: 'Members',
      cell: ({ row }) => {
        const count = row.original._count?.memberships ?? 0;
        return (
          <div className="flex items-center gap-2">
            <Users className="h-4 w-4 text-muted-foreground" />
            <span>{count}</span>
          </div>
        );
      },
      enableSorting: true,
    },
    createDateColumn<Organization>("createdAt", 'Created', {
      sortable: true,
      filterable: true,
      formatString: "PPP",
    }),
    createActionsColumn<Organization>(actions, quickActions),
  ];

  return (
    <DataTable<Organization>
      columns={columns}
      data={organizations}
      enableSorting
      enableFiltering
      enableColumnVisibility
      enableRowSelection
      enableMultiRowSelection
      enablePagination
      enableGlobalFilter
      enableExport
      filters={filters}
      exportFileName="organizations"
      isLoading={isLoading}
      searchPlaceholder='Search organizations...'
      pageSize={10}
      pageSizeOptions={[10, 20, 50, 100]}
      rowIdAccessor="id"
      onRowSelectionChange={(selectedRows) => {
        console.log("Selected organizations:", selectedRows);
      }}
      emptyState={{
        title: 'No organizations found',
        description: 'No organizations match your search criteria.',
      }}
    />
  );
}
