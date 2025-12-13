/**
 * EXAMPLES
 * Errors in this file should be ignored
 * They come from a different codebase to showcase form factory usage
 */

'use client';

import { FormFactory, FormConfig } from '@/components/factory/forms';

/**
 * Comprehensive Form Data Interface
 * Demonstrates all available field types
 */
interface ComprehensiveFormData {
  // Text inputs
  textInput: string;
  emailInput: string;
  passwordInput: string;
  
  // Numbers
  numberInput: number;
  rangeInput: number;
  ratingInput: number;
  
  // Text area
  textareaInput: string;
  
  // Selection
  radioInput: string;
  selectInput: string;
  multiSelectInput: string[];
  comboboxSingle: string;
  comboboxMultiple: string[];
  
  // Boolean
  checkboxInput: boolean;
  switchInput: boolean;
  
  // Dates
  dateInput: Date;
  dateRangeInput: { from: Date; to: Date };
  
  // Files
  fileInput?: File;
}

/**
 * Comprehensive Form Example
 * Shows all field types in one form
 */
export function ComprehensiveFormExample() {
  const formConfig: FormConfig<ComprehensiveFormData> = {
    title: 'Field Types Showcase',
    description: 'This form demonstrates all available field types',
    
    sections: [
      {
        id: 'text-inputs',
        title: 'Text Inputs',
        layout: { columns: 2, gap: 'md' },
        fields: [
          {
            name: 'textInput',
            type: 'text',
            label: 'Text Input',
            placeholder: 'Enter some text',
            description: 'A simple text input field',
            validation: {
              required: 'This field is required',
              minLength: { value: 3, message: 'At least 3 characters' },
            },
          },
          {
            name: 'emailInput',
            type: 'email',
            label: 'Email Input',
            placeholder: 'your@email.com',
            description: 'Email validation built-in',
            validation: {
              required: 'Email is required',
            },
          },
          {
            name: 'passwordInput',
            type: 'password',
            label: 'Password Input',
            placeholder: 'Your password',
            description: 'Password with show/hide toggle',
            showPasswordToggle: true,
            layout: { columns: 2 },
            validation: {
              required: 'Password is required',
              minLength: { value: 6, message: 'At least 6 characters' },
            },
          },
        ],
      },
      {
        id: 'number-inputs',
        title: 'Number Inputs',
        layout: { columns: 3 },
        fields: [
          {
            name: 'numberInput',
            type: 'number',
            label: 'Number Input',
            placeholder: '0',
            description: 'Standard number input',
            min: 0,
            max: 100,
            validation: {
              required: 'Number is required',
              min: { value: 0, message: 'Min value is 0' },
              max: { value: 100, message: 'Max value is 100' },
            },
          },
          {
            name: 'rangeInput',
            type: 'range',
            label: 'Range Slider',
            description: 'Slide to select a value',
            config: {
              min: 0,
              max: 100,
              step: 5,
              showValue: true,
            },
            defaultValue: 50,
          },
          {
            name: 'ratingInput',
            type: 'rating',
            label: 'Rating',
            description: 'Rate from 1 to 5 stars',
            config: {
              max: 5,
              icon: 'star',
            },
            defaultValue: 0,
          },
        ],
      },
      {
        id: 'textarea-section',
        title: 'Text Area',
        fields: [
          {
            name: 'textareaInput',
            type: 'textarea',
            label: 'Textarea with Autosize',
            placeholder: 'Type a long message...',
            description: 'Automatically grows as you type',
            autosize: true,
            minRows: 3,
            maxRows: 10,
            validation: {
              required: 'Message is required',
              minLength: { value: 10, message: 'At least 10 characters' },
            },
          },
        ],
      },
      {
        id: 'selection-section',
        title: 'Selection Fields',
        layout: { columns: 2 },
        fields: [
          {
            name: 'radioInput',
            type: 'radio',
            label: 'Radio Buttons',
            description: 'Single selection',
            options: [
              { label: 'Option 1', value: 'opt1' },
              { label: 'Option 2', value: 'opt2' },
              { label: 'Option 3', value: 'opt3' },
            ],
            orientation: 'horizontal',
            validation: {
              required: 'Please select an option',
            },
          },
          {
            name: 'selectInput',
            type: 'select',
            label: 'Select Dropdown',
            description: 'Choose from dropdown',
            placeholder: 'Select an option',
            options: [
              { label: 'Red', value: 'red' },
              { label: 'Green', value: 'green' },
              { label: 'Blue', value: 'blue' },
            ],
            validation: {
              required: 'Please select a color',
            },
          },
          {
            name: 'multiSelectInput',
            type: 'multi-select',
            label: 'Multi-Select',
            description: 'Select multiple items',
            placeholder: 'Select multiple...',
            options: [
              { label: 'JavaScript', value: 'js' },
              { label: 'TypeScript', value: 'ts' },
              { label: 'Python', value: 'py' },
              { label: 'Rust', value: 'rust' },
            ],
            defaultValue: [],
          },
          {
            name: 'comboboxSingle',
            type: 'combobox',
            label: 'Combobox (Single)',
            description: 'Searchable single select',
            placeholder: 'Search and select...',
            options: [
              { label: 'Apple', value: 'apple' },
              { label: 'Banana', value: 'banana' },
              { label: 'Cherry', value: 'cherry' },
              { label: 'Date', value: 'date' },
              { label: 'Elderberry', value: 'elderberry' },
            ],
            config: {
              mode: 'single',
              searchable: true,
            },
          },
          {
            name: 'comboboxMultiple',
            type: 'combobox',
            label: 'Combobox (Multiple)',
            description: 'Searchable multi-select',
            placeholder: 'Search and select multiple...',
            options: [
              { label: 'React', value: 'react', description: 'UI library' },
              { label: 'Vue', value: 'vue', description: 'Progressive framework' },
              { label: 'Angular', value: 'angular', description: 'Full framework' },
              { label: 'Svelte', value: 'svelte', description: 'Compiler-based' },
            ],
            config: {
              mode: 'multiple',
              searchable: true,
            },
            defaultValue: [],
            layout: { columns: 2 },
          },
        ],
      },
      {
        id: 'boolean-section',
        title: 'Boolean Fields',
        layout: { columns: 2 },
        fields: [
          {
            name: 'checkboxInput',
            type: 'checkbox',
            label: 'Checkbox',
            checkboxLabel: 'I agree to the terms and conditions',
            validation: {
              required: 'You must agree to continue',
              validate: {
                mustBeTrue: (value) => value === true || 'You must check this box',
              },
            },
          },
          {
            name: 'switchInput',
            type: 'switch',
            label: 'Switch Toggle',
            switchLabel: 'Enable notifications',
            defaultValue: false,
          },
        ],
      },
      {
        id: 'date-section',
        title: 'Date Fields',
        layout: { columns: 2 },
        fields: [
          {
            name: 'dateInput',
            type: 'date',
            label: 'Date Picker',
            description: 'Select a single date',
            config: {
              placeholder: 'Pick a date',
              format: 'PPP',
            },
            validation: {
              required: 'Date is required',
            },
          },
          {
            name: 'dateRangeInput',
            type: 'date-range',
            label: 'Date Range Picker',
            description: 'Select a date range',
            config: {
              placeholder: 'Pick start and end dates',
              format: 'MM/dd/yyyy',
            },
            validation: {
              required: 'Date range is required',
            },
          },
        ],
      },
      {
        id: 'file-section',
        title: 'File Upload',
        fields: [
          {
            name: 'fileInput',
            type: 'file',
            label: 'File Upload',
            description: 'Upload an image file with preview',
            config: {
              accept: 'image/*',
              maxSize: 5 * 1024 * 1024, // 5MB
              multiple: false,
              showPreview: true,
            },
          },
        ],
      },
    ],
    
    submit: {
      onSubmit: async (data) => {
        console.log('Comprehensive form submitted:', data);
        await new Promise((resolve) => setTimeout(resolve, 1500));
      },
      showSuccessMessage: true,
      successMessage: 'Form submitted successfully! Check the console for data.',
      preventDoubleSubmit: true,
    },
    
    errorDisplay: {
      showInline: true,
      showSummary: true,
      scrollToError: true,
      focusOnError: true,
    },
    
    layout: {
      variant: 'card',
      width: 'xl',
      spacing: 'lg',
    },
    
    a11y: {
      enableKeyboardNav: true,
      announceErrors: true,
    },
  };

  return (
    <div className="container mx-auto py-12">
      <div className="mb-6">
        <h1 className="text-3xl font-bold mb-2">Form Factory - Field Types Demo</h1>
        <p className="text-muted-foreground">
          This example showcases all available field types. Try interacting with each field
          to see the validation, error handling, and accessibility features in action.
        </p>
      </div>
      
      <FormFactory config={formConfig} />
      
      <div className="mt-8 p-6 bg-muted rounded-lg">
        <h3 className="font-semibold mb-2">💡 Tips:</h3>
        <ul className="list-disc list-inside space-y-1 text-sm">
          <li>Press <kbd className="px-2 py-1 bg-background rounded">Ctrl</kbd> + <kbd className="px-2 py-1 bg-background rounded">Enter</kbd> to submit</li>
          <li>All fields support keyboard navigation</li>
          <li>Error messages appear inline and in a summary at the top</li>
          <li>The form will scroll to the first error automatically</li>
          <li>Try uploading an image to see the file preview</li>
        </ul>
      </div>
    </div>
  );
}
