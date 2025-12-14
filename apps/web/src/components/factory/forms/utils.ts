import { z, ZodString, ZodNumber } from 'zod';
import { FieldConfig, FieldValidation } from './types';
import { FieldValues } from 'react-hook-form';

/**
 * Convert field validation config to Zod schema
 */
export function createFieldSchema<TFieldValues extends FieldValues = FieldValues>(
  field: FieldConfig<TFieldValues>,
  validation?: FieldValidation
): z.ZodTypeAny {
  const val = validation || field.validation;
  
  let schema: z.ZodTypeAny;

  // Base schema based on field type
  switch (field.type) {
    case 'text':
    case 'email':
    case 'password':
    case 'textarea':
      schema = z.string();
      if (field.type === 'email') {
        schema = (schema as ZodString).email('Invalid email address');
      }
      break;

    case 'number':
    case 'range':
      schema = z.number();
      break;

    case 'checkbox':
    case 'switch':
      schema = z.boolean();
      break;

    case 'radio':
    case 'select':
      schema = z.string();
      break;

    case 'multi-select':
    case 'combobox':
      if (field.type === 'combobox' && field.config.mode === 'single') {
        schema = z.string();
      } else {
        schema = z.array(z.string());
      }
      break;

    case 'date':
      schema = z.date();
      break;

    case 'date-range':
      schema = z.object({
        from: z.date(),
        to: z.date(),
      });
      break;

    case 'file':
      if (field.config.multiple) {
        schema = z.array(z.instanceof(File));
      } else {
        schema = z.instanceof(File);
      }
      break;

    case 'rating':
      schema = z.number();
      break;

    case 'custom':
      schema = z.unknown();
      break;

    default:
      schema = z.unknown();
  }

  // Apply validation rules
  if (val) {
    if (schema instanceof z.ZodString) {
      let stringSchema = schema as ZodString;
      if (val.minLength) {
        stringSchema = stringSchema.min(val.minLength.value, val.minLength.message);
      }
      if (val.maxLength) {
        stringSchema = stringSchema.max(val.maxLength.value, val.maxLength.message);
      }
      if (val.pattern) {
        stringSchema = stringSchema.regex(val.pattern.value, val.pattern.message);
      }
      schema = stringSchema;
    }

    if (schema instanceof z.ZodNumber) {
      let numberSchema = schema as ZodNumber;
      if (val.min) {
        numberSchema = numberSchema.min(val.min.value, val.min.message);
      }
      if (val.max) {
        numberSchema = numberSchema.max(val.max.value, val.max.message);
      }
      schema = numberSchema;
    }

    // Handle required validation
    if (val.required === false) {
      schema = schema.optional();
    } else if (typeof val.required === 'string') {
      // Custom required message not directly supported in Zod, handle in react-hook-form
      schema = schema;
    }

    // Custom validators will be handled in react-hook-form
  } else {
    // If no validation specified, make optional
    schema = schema.optional();
  }

  return schema;
}

/**
 * Generate Zod schema from form configuration
 */
export function generateFormSchema<TFieldValues extends FieldValues = FieldValues>(
  fields: FieldConfig<TFieldValues>[]
): z.ZodSchema<Partial<TFieldValues>> {
  const shape: Record<string, z.ZodTypeAny> = {};

  fields.forEach((field) => {
    shape[field.name] = createFieldSchema(field);
  });

  return z.object(shape) as z.ZodSchema<Partial<TFieldValues>>;
}

/**
 * Validate file upload constraints
 */
export function validateFile(
  file: File,
  config: { accept?: string; maxSize?: number }
): string | null {
  if (config.accept) {
    const acceptedTypes = config.accept.split(',').map((t) => t.trim());
    const fileType = file.type;
    const fileExt = `.${file.name.split('.').pop()?.toLowerCase()}`;

    const isAccepted = acceptedTypes.some((type) => {
      if (type.startsWith('.')) {
        return fileExt === type;
      }
      if (type.endsWith('/*')) {
        const category = type.split('/')[0];
        return fileType.startsWith(category + '/');
      }
      return fileType === type;
    });

    if (!isAccepted) {
      return `File type not accepted. Allowed types: ${config.accept}`;
    }
  }

  if (config.maxSize && file.size > config.maxSize) {
    const maxSizeMB = (config.maxSize / (1024 * 1024)).toFixed(2);
    return `File size exceeds ${maxSizeMB}MB`;
  }

  return null;
}

/**
 * Format file size for display
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

/**
 * Check if field should be visible based on dependencies
 */
export function shouldShowField<TFieldValues extends FieldValues = FieldValues>(
  field: FieldConfig<TFieldValues>,
  formValues: Partial<TFieldValues>
): boolean {
  if (!field.dependencies || field.dependencies.length === 0) {
    return true;
  }

  return field.dependencies.every((dep) => {
    const value = formValues[dep.field];
    return dep.condition(value, formValues as TFieldValues);
  });
}

/**
 * Get visible fields based on current form values
 */
export function getVisibleFields<TFieldValues extends FieldValues = FieldValues>(
  fields: FieldConfig<TFieldValues>[],
  formValues: Partial<TFieldValues>
): FieldConfig<TFieldValues>[] {
  return fields.filter((field) => shouldShowField(field, formValues));
}

/**
 * Get default values from field configurations
 */
export function getDefaultValues<TFieldValues extends FieldValues = FieldValues>(
  fields: FieldConfig<TFieldValues>[]
): Partial<TFieldValues> {
  const defaults: Record<string, unknown> = {};

  fields.forEach((field) => {
    if (field.defaultValue !== undefined) {
      defaults[field.name] = field.defaultValue;
    } else {
      // Set sensible defaults based on field type
      switch (field.type) {
        case 'checkbox':
        case 'switch':
          defaults[field.name] = false;
          break;
        case 'multi-select':
        case 'combobox':
          if (field.type === 'combobox' && field.config.mode === 'single') {
            defaults[field.name] = '';
          } else {
            defaults[field.name] = [];
          }
          break;
        case 'number':
        case 'range':
          defaults[field.name] = field.type === 'range' ? field.config.min : 0;
          break;
        case 'rating':
          defaults[field.name] = 0;
          break;
        default:
          defaults[field.name] = '';
      }
    }
  });

  return defaults as Partial<TFieldValues>;
}
