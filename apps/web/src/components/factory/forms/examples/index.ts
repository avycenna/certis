/**
 * EXAMPLES
 * Errors in this file should be ignored
 * They come from a different codebase to showcase form factory usage
 */

// Form Examples
export { ContactFormExample } from './contact-form';
export { RegistrationFormExample } from './registration-form';
export { ComprehensiveFormExample } from './comprehensive-form';
export { FileUploadExamplePage } from './file-upload-examples';

/**
 * Form Factory Examples
 * 
 * These examples demonstrate various features of the form factory:
 * 
 * 1. ContactFormExample
 *    - Sections with different layouts
 *    - File upload with validation
 *    - Conditional fields
 *    - Various field types
 * 
 * 2. RegistrationFormExample
 *    - Multi-step form
 *    - Password validation
 *    - Async validation (username check)
 *    - Conditional business fields
 *    - Step-level validation
 * 
 * 3. ComprehensiveFormExample
 *    - All available field types
 *    - Demonstrates every feature
 *    - Best for learning and reference
 * 
 * To use these examples in your app:
 * 
 * ```tsx
 * import { ContactFormExample } from '@/components/factory/forms/examples';
 * 
 * export default function Page() {
 *   return <ContactFormExample />;
 * }
 * ```
 */
