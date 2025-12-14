/**
 * EXAMPLES
 * Errors in this file should be ignored
 * They come from a different codebase to showcase form factory usage
 */

'use client';

import { FormFactory, FormConfig } from '@/components/factory/forms';

/**
 * Registration Form Data Interface
 */
interface RegistrationFormData {
  // Step 1: Account
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  
  // Step 2: Profile
  firstName: string;
  lastName: string;
  dateOfBirth: Date;
  avatar?: File;
  
  // Step 3: Preferences
  accountType: 'personal' | 'business';
  companyName?: string;
  interests: string[];
  newsletter: boolean;
  notifications: boolean;
}

/**
 * Multi-Step Registration Form Example
 * Demonstrates: multi-step form, password validation, conditional fields, combobox
 */
export function RegistrationFormExample() {
  const formConfig: FormConfig<RegistrationFormData> = {
    title: 'Create Your Account',
    description: 'Follow the steps to set up your account',
    
    steps: [
      {
        id: 'account-step',
        title: 'Account Details',
        description: 'Create your login credentials',
        sections: [
          {
            id: 'credentials',
            fields: [
              {
                name: 'username',
                type: 'text',
                label: 'Username',
                placeholder: 'Choose a unique username',
                description: 'This will be your public display name',
                validation: {
                  required: 'Username is required',
                  minLength: { value: 3, message: 'At least 3 characters' },
                  maxLength: { value: 20, message: 'Max 20 characters' },
                  pattern: {
                    value: /^[a-zA-Z0-9_-]+$/,
                    message: 'Only letters, numbers, underscore and dash allowed',
                  },
                  validate: {
                    noSpaces: (value) => {
                      const str = String(value || '');
                      return !str.includes(' ') || 'No spaces allowed';
                    },
                    // Simulate async username check
                    unique: async (value) => {
                      await new Promise((resolve) => setTimeout(resolve, 500));
                      const taken = ['admin', 'user', 'test'];
                      const str = String(value || '').toLowerCase();
                      return !taken.includes(str) || 'Username already taken';
                    },
                  },
                },
              },
              {
                name: 'email',
                type: 'email',
                label: 'Email Address',
                placeholder: 'your.email@example.com',
                validation: {
                  required: 'Email is required',
                },
              },
              {
                name: 'password',
                type: 'password',
                label: 'Password',
                placeholder: 'Create a strong password',
                showPasswordToggle: true,
                validation: {
                  required: 'Password is required',
                  minLength: { value: 8, message: 'At least 8 characters' },
                  validate: {
                    hasUpperCase: (value) => {
                      const str = String(value || '');
                      return /[A-Z]/.test(str) || 'Must contain uppercase letter';
                    },
                    hasLowerCase: (value) => {
                      const str = String(value || '');
                      return /[a-z]/.test(str) || 'Must contain lowercase letter';
                    },
                    hasNumber: (value) => {
                      const str = String(value || '');
                      return /[0-9]/.test(str) || 'Must contain number';
                    },
                  },
                },
              },
              {
                name: 'confirmPassword',
                type: 'password',
                label: 'Confirm Password',
                placeholder: 'Re-enter your password',
                showPasswordToggle: true,
                validation: {
                  required: 'Please confirm your password',
                },
              },
            ],
          },
        ],
        validation: async (values) => {
          // Custom step validation
          if (values.password !== values.confirmPassword) {
            return false;
          }
          return true;
        },
      },
      {
        id: 'profile-step',
        title: 'Profile Information',
        description: 'Tell us about yourself',
        sections: [
          {
            id: 'personal-info',
            layout: { columns: 2 },
            fields: [
              {
                name: 'firstName',
                type: 'text',
                label: 'First Name',
                placeholder: 'John',
                validation: {
                  required: 'First name is required',
                },
              },
              {
                name: 'lastName',
                type: 'text',
                label: 'Last Name',
                placeholder: 'Doe',
                validation: {
                  required: 'Last name is required',
                },
              },
              {
                name: 'dateOfBirth',
                type: 'date',
                label: 'Date of Birth',
                config: {
                  placeholder: 'Select your birth date',
                  maxDate: new Date(), // Can't be in the future
                },
                validation: {
                  required: 'Date of birth is required',
                },
                layout: { columns: 2 },
              },
              {
                name: 'avatar',
                type: 'file',
                label: 'Profile Picture (Optional)',
                description: 'Upload a profile picture',
                config: {
                  accept: 'image/*',
                  maxSize: 2 * 1024 * 1024, // 2MB
                  multiple: false,
                  showPreview: true,
                },
                layout: { columns: 2 },
              },
            ],
          },
        ],
      },
      {
        id: 'preferences-step',
        title: 'Preferences',
        description: 'Customize your experience',
        sections: [
          {
            id: 'account-type-section',
            fields: [
              {
                name: 'accountType',
                type: 'radio',
                label: 'Account Type',
                options: [
                  {
                    label: 'Personal',
                    value: 'personal',
                    description: 'For individual use',
                  },
                  {
                    label: 'Business',
                    value: 'business',
                    description: 'For companies and teams',
                  },
                ],
                defaultValue: 'personal',
                validation: {
                  required: 'Please select an account type',
                },
              },
              {
                name: 'companyName',
                type: 'text',
                label: 'Company Name',
                placeholder: 'Your company name',
                dependencies: [
                  {
                    field: 'accountType',
                    condition: (value) => value === 'business',
                  },
                ],
                validation: {
                  required: 'Company name is required for business accounts',
                },
              },
            ],
          },
          {
            id: 'interests-section',
            fields: [
              {
                name: 'interests',
                type: 'combobox',
                label: 'Interests',
                description: 'Select topics you\'re interested in',
                options: [
                  { label: 'Technology', value: 'tech' },
                  { label: 'Design', value: 'design' },
                  { label: 'Business', value: 'business' },
                  { label: 'Marketing', value: 'marketing' },
                  { label: 'Development', value: 'dev' },
                  { label: 'Data Science', value: 'data' },
                  { label: 'AI/ML', value: 'ai' },
                  { label: 'Product Management', value: 'product' },
                ],
                config: {
                  mode: 'multiple',
                  searchable: true,
                  placeholder: 'Search and select interests...',
                },
                defaultValue: [],
              },
            ],
          },
          {
            id: 'notifications-section',
            title: 'Communication Preferences',
            fields: [
              {
                name: 'newsletter',
                type: 'switch',
                label: 'Newsletter',
                switchLabel: 'Receive weekly newsletter with updates and tips',
                defaultValue: true,
              },
              {
                name: 'notifications',
                type: 'switch',
                label: 'Push Notifications',
                switchLabel: 'Get notified about important updates',
                defaultValue: true,
              },
            ],
          },
        ],
      },
    ],
    
    submit: {
      onSubmit: async (data) => {
        console.log('Registration data:', data);
        
        // Simulate API call
        await new Promise((resolve) => setTimeout(resolve, 2000));
        
        // Here you would typically call your registration API
        // await fetch('/api/auth/register', {
        //   method: 'POST',
        //   body: JSON.stringify(data),
        // });
      },
      showSuccessMessage: true,
      successMessage: 'Account created successfully! Welcome aboard! 🎉',
      showErrorMessage: true,
      preventDoubleSubmit: true,
    },
    
    errorDisplay: {
      showInline: true,
      showSummary: true,
      scrollToError: true,
      focusOnError: true,
    },
    
    layout: {
      variant: 'default',
      width: 'lg',
      spacing: 'lg',
    },
    
    a11y: {
      enableKeyboardNav: true,
      announceErrors: true,
    },
  };

  return (
    <div className="container mx-auto py-12">
      <FormFactory config={formConfig} />
    </div>
  );
}
