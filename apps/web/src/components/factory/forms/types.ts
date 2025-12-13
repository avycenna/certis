import { z } from 'zod';
import { FieldValues, UseFormReturn, Path, FieldErrors } from 'react-hook-form';
import { ReactNode } from 'react';

/**
 * Base field type enumeration
 */
export type FieldType =
  | 'text'
  | 'email'
  | 'password'
  | 'number'
  | 'textarea'
  | 'checkbox'
  | 'radio'
  | 'select'
  | 'multi-select'
  | 'combobox'
  | 'date'
  | 'date-range'
  | 'file'
  | 'range'
  | 'rating'
  | 'switch'
  | 'slug'
  | 'custom';

/**
 * Field validation configuration
 */
export interface FieldValidation {
  required?: boolean | string;
  minLength?: { value: number; message: string };
  maxLength?: { value: number; message: string };
  min?: { value: number; message: string };
  max?: { value: number; message: string };
  pattern?: { value: RegExp; message: string };
  validate?: Record<string, (value: unknown) => boolean | string | Promise<boolean | string>>;
}

/**
 * Field option for select, radio, checkbox, combobox
 */
export interface FieldOption<T = string> {
  label: string;
  value: T;
  disabled?: boolean;
  icon?: ReactNode;
  description?: string;
}

/**
 * File upload configuration
 */
export interface FileUploadConfig {
  accept?: string; // e.g., "image/*" or ".pdf,.doc"
  maxSize?: number; // in bytes
  maxFiles?: number;
  multiple?: boolean;
  showPreview?: boolean;
}

/**
 * Date configuration
 */
export interface DateConfig {
  format?: string;
  minDate?: Date;
  maxDate?: Date;
  disabledDates?: Date[];
  placeholder?: string;
}

/**
 * Range configuration
 */
export interface RangeConfig {
  min: number;
  max: number;
  step?: number;
  showValue?: boolean;
}

/**
 * Rating configuration
 */
export interface RatingConfig {
  max?: number; // default 5
  allowHalf?: boolean;
  icon?: 'star' | 'heart' | 'thumbs';
}

/**
 * Combobox configuration
 */
export interface ComboboxConfig<T = string> {
  mode?: 'single' | 'multiple';
  searchable?: boolean;
  creatable?: boolean;
  placeholder?: string;
  emptyMessage?: string;
  loadOptions?: (search: string) => Promise<FieldOption<T>[]>;
}

/**
 * Field dependency for conditional rendering
 */
export interface FieldDependency<TFieldValues extends FieldValues = FieldValues> {
  field: Path<TFieldValues>;
  condition: (value: unknown, formValues: TFieldValues) => boolean;
}

/**
 * Field layout configuration
 */
export interface FieldLayout {
  columns?: 1 | 2 | 3 | 4 | 6 | 12; // Grid columns (12-column system)
  order?: number;
  className?: string;
}

/**
 * Base field definition
 */
export interface BaseFieldConfig<TFieldValues extends FieldValues = FieldValues> {
  name: Path<TFieldValues>;
  label?: string;
  description?: string;
  placeholder?: string;
  defaultValue?: unknown;
  disabled?: boolean;
  readOnly?: boolean;
  validation?: FieldValidation;
  dependencies?: FieldDependency<TFieldValues>[];
  layout?: FieldLayout;
  className?: string;
}

/**
 * Specific field type configurations
 */
export interface TextFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'text' | 'email' | 'password';
  showPasswordToggle?: boolean; // for password type
  autoComplete?: string;
}

export interface NumberFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'number';
  min?: number;
  max?: number;
  step?: number;
}

export interface TextareaFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'textarea';
  rows?: number;
  autosize?: boolean;
  minRows?: number;
  maxRows?: number;
}

export interface CheckboxFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'checkbox';
  checkboxLabel?: string;
}

export interface RadioFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'radio';
  options: FieldOption[];
  orientation?: 'horizontal' | 'vertical';
}

export interface SelectFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'select';
  options: FieldOption[];
  emptyOption?: string;
}

export interface MultiSelectFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'multi-select';
  options: FieldOption[];
  max?: number;
}

export interface ComboboxFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'combobox';
  options: FieldOption[];
  config: ComboboxConfig;
}

export interface DateFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'date';
  config?: DateConfig;
}

export interface DateRangeFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'date-range';
  config?: DateConfig;
}

export interface FileFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'file';
  config: FileUploadConfig;
}

export interface RangeFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'range';
  config: RangeConfig;
}

export interface RatingFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'rating';
  config?: RatingConfig;
}

export interface SwitchFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'switch';
  switchLabel?: string;
}

export interface SlugFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'slug';
  prefix?: string;
  baseFieldName?: Path<TFieldValues>;
  validateSlug?: (slug: string) => Promise<{ available: boolean }>;
}

export interface CustomFieldConfig<TFieldValues extends FieldValues = FieldValues>
  extends BaseFieldConfig<TFieldValues> {
  type: 'custom';
  render: (props: {
    form: UseFormReturn<TFieldValues>;
    field: {
      name: string;
      value: unknown;
      onChange: (value: unknown) => void;
      onBlur: () => void;
    };
    fieldState: {
      invalid: boolean;
      error?: { message?: string };
    };
  }) => ReactNode;
}

/**
 * Union type for all field configurations
 */
export type FieldConfig<TFieldValues extends FieldValues = FieldValues> =
  | TextFieldConfig<TFieldValues>
  | NumberFieldConfig<TFieldValues>
  | TextareaFieldConfig<TFieldValues>
  | CheckboxFieldConfig<TFieldValues>
  | RadioFieldConfig<TFieldValues>
  | SelectFieldConfig<TFieldValues>
  | MultiSelectFieldConfig<TFieldValues>
  | ComboboxFieldConfig<TFieldValues>
  | DateFieldConfig<TFieldValues>
  | DateRangeFieldConfig<TFieldValues>
  | FileFieldConfig<TFieldValues>
  | RangeFieldConfig<TFieldValues>
  | RatingFieldConfig<TFieldValues>
  | SwitchFieldConfig<TFieldValues>
  | SlugFieldConfig<TFieldValues>
  | CustomFieldConfig<TFieldValues>;

/**
 * Form section configuration
 */
export interface FormSection<TFieldValues extends FieldValues = FieldValues> {
  id: string;
  title?: string;
  description?: string;
  fields: FieldConfig<TFieldValues>[];
  collapsible?: boolean;
  defaultCollapsed?: boolean;
  dependencies?: FieldDependency<TFieldValues>[];
  layout?: {
    columns?: number;
    gap?: 'sm' | 'md' | 'lg';
  };
}

/**
 * Form step configuration for multi-step forms
 */
export interface FormStep<TFieldValues extends FieldValues = FieldValues> {
  id: string;
  title: string;
  description?: string;
  sections: FormSection<TFieldValues>[];
  validation?: (values: Partial<TFieldValues>) => boolean | Promise<boolean>;
}

/**
 * Form submission configuration
 */
export interface FormSubmitConfig<TFieldValues extends FieldValues = FieldValues> {
  onSubmit: (data: TFieldValues) => void | Promise<void>;
  onError?: (errors: FieldErrors<TFieldValues>) => void;
  preventDoubleSubmit?: boolean;
  showSuccessMessage?: boolean;
  showErrorMessage?: boolean;
  successMessage?: string;
  errorMessage?: string;
  resetOnSuccess?: boolean;
}

/**
 * Form error display configuration
 */
export interface ErrorDisplayConfig {
  showInline?: boolean; // Show errors next to fields
  showSummary?: boolean; // Show error summary at top
  scrollToError?: boolean; // Scroll to first error
  focusOnError?: boolean; // Focus first error field
}

/**
 * Main form configuration
 */
export interface FormConfig<TFieldValues extends FieldValues = FieldValues> {
  id?: string;
  title?: string;
  description?: string;
  schema?: z.ZodSchema<TFieldValues>;
  defaultValues?: Partial<TFieldValues>;
  mode?: 'onChange' | 'onBlur' | 'onSubmit' | 'onTouched' | 'all';
  reValidateMode?: 'onChange' | 'onBlur' | 'onSubmit';
  
  // Single-step form
  sections?: FormSection<TFieldValues>[];
  
  // Multi-step form
  steps?: FormStep<TFieldValues>[];
  
  // Submission
  submit: FormSubmitConfig<TFieldValues>;
  
  // Error handling
  errorDisplay?: ErrorDisplayConfig;
  
  // Layout
  layout?: {
    variant?: 'default' | 'card' | 'inline';
    width?: 'sm' | 'md' | 'lg' | 'xl' | 'full';
    spacing?: 'sm' | 'md' | 'lg';
  };
  
  // Accessibility
  a11y?: {
    enableKeyboardNav?: boolean;
    announceErrors?: boolean;
  };
}

/**
 * Form state for multi-step forms
 */
export interface FormState {
  currentStep: number;
  isSubmitting: boolean;
  isValidating: boolean;
  hasErrors: boolean;
  completedSteps: Set<number>;
}

/**
 * Form context value
 */
export interface FormContextValue<TFieldValues extends FieldValues = FieldValues> {
  form: UseFormReturn<TFieldValues>;
  config: FormConfig<TFieldValues>;
  state: FormState;
  actions: {
    nextStep: () => void;
    prevStep: () => void;
    goToStep: (step: number) => void;
    reset: () => void;
  };
}
