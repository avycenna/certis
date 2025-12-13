'use client';

import { FieldValues, UseFormReturn } from 'react-hook-form';
import { FormSection as FormSectionType, FieldConfig } from '../types';
import { cn } from '@/lib/utils';
import { FieldRenderer } from '../fields/field-renderer';
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card';
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible';
import { ChevronDown } from 'lucide-react';
import { useState } from 'react';
import { shouldShowField } from '../utils';

/**
 * Form Section Component
 */
interface FormSectionProps<TFieldValues extends FieldValues = FieldValues> {
  form: UseFormReturn<TFieldValues>;
  section: FormSectionType<TFieldValues>;
  variant?: 'default' | 'card';
}

export function FormSection<TFieldValues extends FieldValues = FieldValues>({
  form,
  section,
  variant = 'default',
}: FormSectionProps<TFieldValues>) {
  const [isOpen, setIsOpen] = useState(!section.defaultCollapsed);
  const formValues = form.watch();

  // Check section dependencies
  if (section.dependencies && section.dependencies.length > 0) {
    const shouldShow = section.dependencies.every((dep) => {
      const value = formValues[dep.field];
      return dep.condition(value, formValues);
    });
    if (!shouldShow) {
      return null;
    }
  }

  // Get visible fields
  const visibleFields = section.fields.filter((field) => shouldShowField(field, formValues));

  if (visibleFields.length === 0) {
    return null;
  }

  const columns = section.layout?.columns || 1;
  const gap = section.layout?.gap || 'md';

  const gapClasses = {
    sm: 'gap-2',
    md: 'gap-4',
    lg: 'gap-6',
  };

  const gridClass = cn(
    'grid',
    columns === 1 && 'grid-cols-1',
    columns === 2 && 'grid-cols-1 md:grid-cols-2',
    columns === 3 && 'grid-cols-1 md:grid-cols-3',
    columns === 4 && 'grid-cols-1 md:grid-cols-2 lg:grid-cols-4',
    gapClasses[gap]
  );

  const content = (
    <div className={gridClass}>
      {visibleFields.map((field) => (
        <div
          key={field.name}
          className={cn(
            field.layout?.columns && `md:col-span-${field.layout.columns}`,
            field.layout?.order && `order-${field.layout.order}`
          )}
          style={{
            gridColumn: field.layout?.columns ? `span ${field.layout.columns}` : undefined,
            order: field.layout?.order,
          }}
        >
          <FieldRenderer form={form} config={field} />
        </div>
      ))}
    </div>
  );

  if (variant === 'card') {
    if (section.collapsible) {
      return (
        <Card>
          <Collapsible open={isOpen} onOpenChange={setIsOpen}>
            <CardHeader className="cursor-pointer" onClick={() => setIsOpen(!isOpen)}>
              <CollapsibleTrigger asChild>
                <div className="flex items-center justify-between">
                  <div>
                    {section.title && <CardTitle>{section.title}</CardTitle>}
                    {section.description && <CardDescription>{section.description}</CardDescription>}
                  </div>
                  <ChevronDown
                    className={cn(
                      'h-4 w-4 transition-transform',
                      isOpen && 'transform rotate-180'
                    )}
                  />
                </div>
              </CollapsibleTrigger>
            </CardHeader>
            <CollapsibleContent>
              <CardContent>{content}</CardContent>
            </CollapsibleContent>
          </Collapsible>
        </Card>
      );
    }

    return (
      <Card>
        {(section.title || section.description) && (
          <CardHeader>
            {section.title && <CardTitle>{section.title}</CardTitle>}
            {section.description && <CardDescription>{section.description}</CardDescription>}
          </CardHeader>
        )}
        <CardContent>{content}</CardContent>
      </Card>
    );
  }

  // Default variant
  return (
    <div className="space-y-4">
      {(section.title || section.description) && (
        <div>
          {section.title && <h3 className="text-lg font-semibold">{section.title}</h3>}
          {section.description && (
            <p className="text-sm text-muted-foreground">{section.description}</p>
          )}
        </div>
      )}
      {content}
    </div>
  );
}

/**
 * Field Grid Layout Component
 */
interface FieldGridProps<TFieldValues extends FieldValues = FieldValues> {
  form: UseFormReturn<TFieldValues>;
  fields: FieldConfig<TFieldValues>[];
  columns?: number;
  gap?: 'sm' | 'md' | 'lg';
}

export function FieldGrid<TFieldValues extends FieldValues = FieldValues>({
  form,
  fields,
  columns = 1,
  gap = 'md',
}: FieldGridProps<TFieldValues>) {
  const formValues = form.watch();
  const visibleFields = fields.filter((field) => shouldShowField(field, formValues));

  const gapClasses = {
    sm: 'gap-2',
    md: 'gap-4',
    lg: 'gap-6',
  };

  const gridClass = cn(
    'grid',
    columns === 1 && 'grid-cols-1',
    columns === 2 && 'grid-cols-1 md:grid-cols-2',
    columns === 3 && 'grid-cols-1 md:grid-cols-3',
    columns === 4 && 'grid-cols-1 md:grid-cols-2 lg:grid-cols-4',
    gapClasses[gap]
  );

  return (
    <div className={gridClass}>
      {visibleFields.map((field) => (
        <div
          key={field.name}
          className={cn(
            field.layout?.columns && `md:col-span-${field.layout.columns}`,
            field.layout?.order && `order-${field.layout.order}`
          )}
          style={{
            gridColumn: field.layout?.columns ? `span ${field.layout.columns}` : undefined,
            order: field.layout?.order,
          }}
        >
          <FieldRenderer form={form} config={field} />
        </div>
      ))}
    </div>
  );
}
