/**
 * EXAMPLES
 * Errors in this file should be ignored
 * They come from a different codebase to showcase form factory usage
 */

/**
 * Example: File Upload Field Usage
 * 
 * This demonstrates various configurations of the FileField using the new DiceUI FileUpload component
 */

import { FormFactory, FileFieldConfig } from '@/components/factory/forms';

// Example 1: Basic single file upload (images only)
const basicImageUpload: FileFieldConfig = {
  name: 'avatar',
  type: 'file',
  label: 'Profile Picture',
  placeholder: 'Upload your profile picture',
  config: {
    accept: 'image/*',
    maxSize: 2 * 1024 * 1024, // 2MB
    multiple: false,
    showPreview: true,
  },
  validation: {
    required: 'Profile picture is required',
  },
};

// Example 2: Multiple document uploads
const multipleDocuments: FileFieldConfig = {
  name: 'documents',
  type: 'file',
  label: 'Supporting Documents',
  placeholder: 'Upload up to 5 documents',
  description: 'Accepted formats: PDF, DOC, DOCX',
  config: {
    accept: '.pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    maxSize: 10 * 1024 * 1024, // 10MB
    maxFiles: 5,
    multiple: true,
    showPreview: true,
  },
  validation: {
    required: 'At least one document is required',
  },
};

// Example 3: Image gallery upload
const galleryUpload: FileFieldConfig = {
  name: 'gallery',
  type: 'file',
  label: 'Photo Gallery',
  placeholder: 'Upload up to 10 photos',
  config: {
    accept: 'image/jpeg,image/png,image/webp',
    maxSize: 5 * 1024 * 1024, // 5MB per image
    maxFiles: 10,
    multiple: true,
    showPreview: true, // Shows image thumbnails
  },
};

// Example 4: Any file type with no preview
const anyFileUpload: FileFieldConfig = {
  name: 'attachments',
  type: 'file',
  label: 'Attachments',
  placeholder: 'Upload any file type',
  config: {
    // No accept specified = all file types
    maxSize: 50 * 1024 * 1024, // 50MB
    maxFiles: 3,
    multiple: true,
    showPreview: false, // Don't show previews
  },
};

// Example 5: Conditional file upload
const conditionalUpload: FileFieldConfig = {
  name: 'proofOfAddress',
  type: 'file',
  label: 'Proof of Address',
  placeholder: 'Upload a utility bill or bank statement',
  config: {
    accept: 'image/*,.pdf',
    maxSize: 5 * 1024 * 1024, // 5MB
    multiple: false,
    showPreview: true,
  },
  dependencies: [
    {
      field: 'requiresProof',
      condition: (value) => value === true,
      action: 'show',
    },
  ],
  validation: {
    required: 'Proof of address is required',
  },
};

// Example 6: Video upload
const videoUpload: FileFieldConfig = {
  name: 'video',
  type: 'file',
  label: 'Upload Video',
  placeholder: 'Upload a video file (max 100MB)',
  config: {
    accept: 'video/mp4,video/webm,video/ogg',
    maxSize: 100 * 1024 * 1024, // 100MB
    multiple: false,
    showPreview: true, // Shows video file icon
  },
};

// Complete form example with file upload
const formWithFileUpload = {
  title: 'Document Upload Form',
  description: 'Upload your documents for verification',
  sections: [
    {
      title: 'Personal Documents',
      description: 'Please upload the required documents',
      fields: [
        {
          name: 'fullName',
          type: 'text' as const,
          label: 'Full Name',
          placeholder: 'Enter your full name',
          validation: { required: 'Name is required' },
        },
        basicImageUpload,
        multipleDocuments,
      ],
    },
    {
      title: 'Additional Information',
      fields: [
        {
          name: 'requiresProof',
          type: 'checkbox' as const,
          label: 'I need to provide proof of address',
        },
        conditionalUpload,
        galleryUpload,
      ],
    },
  ],
  onSubmit: async (data: any) => {
    console.log('Form data:', data);
    
    // Access uploaded files
    const avatar = data.avatar as File | undefined;
    const documents = data.documents as File[] | undefined;
    const gallery = data.gallery as File[] | undefined;
    
    // Example: Upload files to server
    if (avatar) {
      console.log('Avatar:', avatar.name, avatar.size);
      // const formData = new FormData();
      // formData.append('avatar', avatar);
      // await fetch('/api/upload', { method: 'POST', body: formData });
    }
    
    if (documents) {
      console.log(`Uploading ${documents.length} documents`);
      documents.forEach((doc, i) => {
        console.log(`Document ${i + 1}:`, doc.name, doc.size);
      });
    }
    
    if (gallery) {
      console.log(`Uploading ${gallery.length} photos`);
      gallery.forEach((photo, i) => {
        console.log(`Photo ${i + 1}:`, photo.name, photo.size);
      });
    }
  },
};

// Usage in a React component
export function FileUploadExamplePage() {
  return (
    <div className="container mx-auto py-8 max-w-2xl">
      <FormFactory config={formWithFileUpload} />
    </div>
  );
}

/**
 * Features Demonstrated:
 * 
 * 1. Single file upload (avatar)
 * 2. Multiple file upload with limit (documents, gallery)
 * 3. File type filtering (accept)
 * 4. File size limits (maxSize)
 * 5. Preview toggle (showPreview)
 * 6. Conditional file fields (dependencies)
 * 7. Various file types (images, documents, videos)
 * 
 * User Interactions:
 * 
 * - Click dropzone to browse files
 * - Click "Browse Files" button
 * - Drag and drop files onto dropzone
 * - Paste files with Ctrl+V (when focused)
 * - Remove files with X button
 * - View image previews automatically
 * - See file type icons for non-images
 * - View file size and name
 * - See validation errors in real-time
 */
