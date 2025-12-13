// Main exports
export { FormFactory } from './form-factory';

// Types
export type {
  FieldType,
  FieldValidation,
  FieldOption,
  FileUploadConfig,
  DateConfig,
  RangeConfig,
  RatingConfig,
  ComboboxConfig,
  FieldDependency,
  FieldLayout,
  BaseFieldConfig,
  TextFieldConfig,
  NumberFieldConfig,
  TextareaFieldConfig,
  CheckboxFieldConfig,
  RadioFieldConfig,
  SelectFieldConfig,
  MultiSelectFieldConfig,
  ComboboxFieldConfig,
  DateFieldConfig,
  DateRangeFieldConfig,
  FileFieldConfig,
  RangeFieldConfig,
  RatingFieldConfig,
  SwitchFieldConfig,
  CustomFieldConfig,
  FieldConfig,
  FormSection,
  FormStep,
  FormSubmitConfig,
  ErrorDisplayConfig,
  FormConfig,
  FormState,
  FormContextValue,
} from './types';

// Utilities
export {
  createFieldSchema,
  generateFormSchema,
  validateFile,
  formatFileSize,
  shouldShowField,
  getVisibleFields,
  getDefaultValues,
} from './utils';

// Components
export { FieldRenderer } from './fields/field-renderer';
export { FormSection as FormSectionComponent, FieldGrid } from './layout/form-section';
// export { MultiStepForm, StepIndicator } from './layout/multi-step-form';
export { MultiStepForm } from './layout/multi-step-form';
export {
  ErrorSummary,
  SuccessMessage,
  ErrorMessage,
  FieldError,
  useFocusOnError,
  useScrollToError,
} from './components/error-display';

// Field components
export {
  TextField,
  NumberField,
  TextareaField,
  CheckboxField,
  RadioField,
  SelectField,
  MultiSelectField,
} from './fields/base-fields';

export {
  DateField,
  DateRangeField,
  FileField,
  RangeField,
  RatingField,
  SwitchField,
  ComboboxField,
} from './fields/advanced-fields';
