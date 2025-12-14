'use client';

import { FieldValues, UseFormReturn } from 'react-hook-form';
import { FormField, FormItem, FormLabel, FormControl, FormDescription, FormMessage } from '@/components/ui/form';
import { FieldConfig } from '../types';
import { shouldShowField } from '../utils';
import {
  TextField,
  NumberField,
  SlugField,
  TextareaField,
  CheckboxField,
  RadioField,
  SelectField,
  MultiSelectField,
} from './base-fields';
import {
  DateField,
  DateRangeField,
  FileField,
  RangeField,
  RatingField,
  SwitchField,
  ComboboxField,
} from './advanced-fields';

/**
 * Field Renderer - Dynamically renders the correct field component
 */
interface FieldRendererProps<TFieldValues extends FieldValues = FieldValues> {
  form: UseFormReturn<TFieldValues>;
  config: FieldConfig<TFieldValues>;
}

export function FieldRenderer<TFieldValues extends FieldValues = FieldValues>({
  form,
  config,
}: FieldRendererProps<TFieldValues>) {
  const formValues = form.watch();
  
  // Check if field should be visible based on dependencies
  if (!shouldShowField(config, formValues)) {
    return null;
  }

  // Check if field is required
  const isRequired = config.validation?.required === true || typeof config.validation?.required === 'string';

  return (
    <FormField
      control={form.control}
      name={config.name}
      render={({ field, fieldState }) => (
        <FormItem className={config.layout?.className}>
          {config.label && config.type !== 'checkbox' && config.type !== 'switch' && (
            <FormLabel>
              {config.label}
              {isRequired && <span className="text-destructive">*</span>}
            </FormLabel>
          )}
          
          <FormControl>
            {renderField(config, field, fieldState, form)}
          </FormControl>
          
          {config.description && <FormDescription>{config.description}</FormDescription>}
          
          <FormMessage />
        </FormItem>
      )}
    />
  );
}

/**
 * Render the appropriate field component based on config type
 */
function renderField<TFieldValues extends FieldValues = FieldValues>(
  config: FieldConfig<TFieldValues>,
  field: { value: unknown; onChange: (value: unknown) => void; onBlur: () => void },
  fieldState: { invalid: boolean; error?: { message?: string } },
  form: UseFormReturn<TFieldValues>
) {
  const commonProps = {
    value: field.value,
    onChange: field.onChange,
    onBlur: field.onBlur,
    disabled: config.disabled,
    error: fieldState.error?.message,
  };

  switch (config.type) {
    case 'text':
    case 'email':
    case 'password':
      return <TextField config={config} {...commonProps} value={field.value as string} />;

    case 'number':
      return <NumberField config={config} {...commonProps} value={field.value as number} />;

    case 'textarea':
      return <TextareaField config={config} {...commonProps} value={field.value as string} />;

    case 'checkbox':
      return <CheckboxField config={config} {...commonProps} value={field.value as boolean} />;

    case 'radio':
      return <RadioField config={config} {...commonProps} value={field.value as string} />;

    case 'select':
      return <SelectField config={config} {...commonProps} value={field.value as string} />;

    case 'multi-select':
      return <MultiSelectField config={config} {...commonProps} value={field.value as string[]} />;

    case 'combobox':
      return <ComboboxField config={config} {...commonProps} value={field.value as string | string[]} />;

    case 'date':
      return <DateField config={config} {...commonProps} value={field.value as Date} />;

    case 'date-range':
      return <DateRangeField config={config} {...commonProps} value={field.value as { from: Date; to: Date }} />;

    case 'file':
      return <FileField config={config} {...commonProps} value={field.value as File | File[]} />;

    case 'range':
      return <RangeField config={config} {...commonProps} value={field.value as number} />;

    case 'rating':
      return <RatingField config={config} {...commonProps} value={field.value as number} />;

    case 'switch':
      return <SwitchField config={config} {...commonProps} value={field.value as boolean} />;

    case 'slug':
      return (
        <SlugField
          form={form}
          name={config.name as string}
          prefix={config.prefix}
          placeholder={config.placeholder}
          baseFieldName={config.baseFieldName as string | undefined}
          disabled={config.disabled}
          validateSlug={config.validateSlug}
        />
      );

    case 'custom':
      return config.render({
        form,
        field: {
          name: field.onBlur.name,
          value: field.value,
          onChange: field.onChange,
          onBlur: field.onBlur,
        },
        fieldState: {
          invalid: fieldState.invalid,
          error: fieldState.error,
        },
      });

    default:
      return <div>Unsupported field type</div>;
  }
}
