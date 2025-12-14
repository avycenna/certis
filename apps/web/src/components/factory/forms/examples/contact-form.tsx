/**
 * EXAMPLES
 * Errors in this file should be ignored
 * They come from a different codebase to showcase form factory usage
 */

'use client';

import { FormFactory, FormConfig } from '@/components/factory/forms';

/**
 * Contact Form Data Interface
 */
interface ContactFormData {
  // Personal Info
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  
  // Contact Preferences
  contactMethod: 'email' | 'phone' | 'both';
  newsletter: boolean;
  
  // Message
  subject: string;
  message: string;
  priority: 'low' | 'medium' | 'high';
  
  // Attachments
  attachments?: File[];
}

/**
 * Contact Form Example
 * Demonstrates: sections, validation, conditional fields, file upload
 */
export function ContactFormExample() {
  const formConfig: FormConfig<ContactFormData> = {
    title: 'Contact Us',
    description: 'Fill out the form below and we\'ll get back to you as soon as possible.',
    
    sections: [
      {
        id: 'personal-info',
        title: 'Personal Information',
        description: 'Tell us who you are',
        layout: { columns: 2, gap: 'md' },
        fields: [
          {
            name: 'firstName',
            type: 'text',
            label: 'First Name',
            placeholder: 'John',
            validation: {
              required: 'First name is required',
              minLength: { value: 2, message: 'At least 2 characters' },
            },
          },
          {
            name: 'lastName',
            type: 'text',
            label: 'Last Name',
            placeholder: 'Doe',
            validation: {
              required: 'Last name is required',
              minLength: { value: 2, message: 'At least 2 characters' },
            },
          },
          {
            name: 'email',
            type: 'email',
            label: 'Email Address',
            placeholder: 'john.doe@example.com',
            validation: {
              required: 'Email is required',
            },
            layout: { columns: 2 },
          },
          {
            name: 'phone',
            type: 'text',
            label: 'Phone Number',
            placeholder: '+1 (555) 123-4567',
            validation: {
              pattern: {
                value: /^[\d\s\-\+\(\)]+$/,
                message: 'Invalid phone number',
              },
            },
            layout: { columns: 2 },
          },
        ],
      },
      {
        id: 'contact-preferences',
        title: 'Contact Preferences',
        fields: [
          {
            name: 'contactMethod',
            type: 'radio',
            label: 'Preferred Contact Method',
            options: [
              { label: 'Email', value: 'email' },
              { label: 'Phone', value: 'phone' },
              { label: 'Both', value: 'both' },
            ],
            orientation: 'horizontal',
            defaultValue: 'email',
            validation: {
              required: 'Please select a contact method',
            },
          },
          {
            name: 'newsletter',
            type: 'switch',
            label: 'Newsletter Subscription',
            switchLabel: 'Yes, send me your newsletter and updates',
            defaultValue: false,
          },
        ],
      },
      {
        id: 'message-section',
        title: 'Your Message',
        layout: { columns: 1 },
        fields: [
          {
            name: 'subject',
            type: 'text',
            label: 'Subject',
            placeholder: 'What is your message about?',
            validation: {
              required: 'Subject is required',
              minLength: { value: 5, message: 'At least 5 characters' },
            },
          },
          {
            name: 'priority',
            type: 'select',
            label: 'Priority',
            placeholder: 'Select priority level',
            options: [
              { label: 'Low', value: 'low' },
              { label: 'Medium', value: 'medium' },
              { label: 'High', value: 'high' },
            ],
            defaultValue: 'medium',
          },
          {
            name: 'message',
            type: 'textarea',
            label: 'Message',
            placeholder: 'Tell us what you need help with...',
            rows: 6,
            autosize: true,
            minRows: 4,
            maxRows: 10,
            validation: {
              required: 'Message is required',
              minLength: { value: 20, message: 'Please provide more details (at least 20 characters)' },
              maxLength: { value: 1000, message: 'Message too long (max 1000 characters)' },
            },
          },
          {
            name: 'attachments',
            type: 'file',
            label: 'Attachments (Optional)',
            description: 'Upload any relevant files (images, documents, etc.)',
            config: {
              accept: '.pdf,.doc,.docx,.txt,.jpg,.jpeg,.png',
              maxSize: 5 * 1024 * 1024, // 5MB
              maxFiles: 3,
              multiple: true,
              showPreview: true,
            },
          },
        ],
      },
    ],
    
    submit: {
      onSubmit: async (data) => {
        console.log('Form submitted:', data);
        
        // Simulate API call
        await new Promise((resolve) => setTimeout(resolve, 2000));
        
        // Simulate random error for testing
        if (Math.random() > 0.8) {
          throw new Error('Failed to send message. Please try again.');
        }
      },
      showSuccessMessage: true,
      successMessage: 'Thank you for contacting us! We\'ll get back to you within 24 hours.',
      showErrorMessage: true,
      preventDoubleSubmit: true,
      resetOnSuccess: true,
    },
    
    errorDisplay: {
      showInline: true,
      showSummary: true,
      scrollToError: true,
      focusOnError: true,
    },
    
    layout: {
      variant: 'card',
      width: 'lg',
      spacing: 'md',
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
