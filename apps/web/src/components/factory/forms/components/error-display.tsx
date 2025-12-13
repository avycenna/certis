'use client';

import { FieldErrors, FieldValues } from 'react-hook-form';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { AlertCircle, CheckCircle2, XCircle } from 'lucide-react';
import { useEffect, useRef } from 'react';

/**
 * Error Summary Component
 */
interface ErrorSummaryProps<TFieldValues extends FieldValues = FieldValues> {
  errors: FieldErrors<TFieldValues>;
  fieldLabels?: Record<string, string>;
}

export function ErrorSummary<TFieldValues extends FieldValues = FieldValues>({
  errors,
  fieldLabels = {},
}: ErrorSummaryProps<TFieldValues>) {
  const errorEntries = Object.entries(errors);

  if (errorEntries.length === 0) {
    return null;
  }

  return (
    <Alert variant="destructive">
      <AlertCircle className="h-4 w-4" />
      <AlertTitle>Please fix the following errors:</AlertTitle>
      <AlertDescription>
        <ul className="list-disc list-inside space-y-1 mt-2">
          {errorEntries.map(([field, error]) => (
            <li key={field}>
              <button
                type="button"
                onClick={() => {
                  const element = document.getElementById(`${field}-form-item`);
                  element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
                  const focusable = element?.querySelector<HTMLElement>('input, textarea, select, button');
                  focusable?.focus();
                }}
                className="hover:underline text-left"
              >
                <strong>{fieldLabels[field] || field}:</strong>{' '}
                {error?.message as string || 'Invalid value'}
              </button>
            </li>
          ))}
        </ul>
      </AlertDescription>
    </Alert>
  );
}

/**
 * Success Message Component
 */
interface SuccessMessageProps {
  message: string;
  onDismiss?: () => void;
}

export function SuccessMessage({ message, onDismiss }: SuccessMessageProps) {
  return (
    <Alert>
      <CheckCircle2 className="h-4 w-4 text-green-600" />
      <AlertTitle>Success!</AlertTitle>
      <AlertDescription className="flex justify-between items-center">
        <span>{message}</span>
        {onDismiss && (
          <button
            type="button"
            onClick={onDismiss}
            className="text-sm hover:underline"
          >
            Dismiss
          </button>
        )}
      </AlertDescription>
    </Alert>
  );
}

/**
 * Error Message Component
 */
interface ErrorMessageProps {
  message: string;
  onDismiss?: () => void;
}

export function ErrorMessage({ message, onDismiss }: ErrorMessageProps) {
  return (
    <Alert variant="destructive">
      <XCircle className="h-4 w-4" />
      <AlertTitle>Error!</AlertTitle>
      <AlertDescription className="flex justify-between items-center">
        <span>{message}</span>
        {onDismiss && (
          <button
            type="button"
            onClick={onDismiss}
            className="text-sm hover:underline"
          >
            Dismiss
          </button>
        )}
      </AlertDescription>
    </Alert>
  );
}

/**
 * Focus on First Error Hook
 */
export function useFocusOnError<TFieldValues extends FieldValues = FieldValues>(
  errors: FieldErrors<TFieldValues>,
  isSubmitted: boolean
) {
  const prevErrorsRef = useRef<FieldErrors<TFieldValues>>(errors);

  useEffect(() => {
    if (!isSubmitted) return;

    const errorKeys = Object.keys(errors);
    const prevErrorKeys = Object.keys(prevErrorsRef.current);

    // Only focus if new errors appeared
    if (errorKeys.length > 0 && errorKeys.length !== prevErrorKeys.length) {
      const firstErrorField = errorKeys[0];
      const element = document.getElementById(`${firstErrorField}-form-item`);
      const input = element?.querySelector<HTMLElement>('input, textarea, select, button');
      
      if (input) {
        element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        setTimeout(() => {
          input.focus();
        }, 100);
      }
    }

    prevErrorsRef.current = errors;
  }, [errors, isSubmitted]);
}

/**
 * Scroll to Error Hook
 */
export function useScrollToError<TFieldValues extends FieldValues = FieldValues>(
  errors: FieldErrors<TFieldValues>
) {
  useEffect(() => {
    const errorKeys = Object.keys(errors);
    if (errorKeys.length > 0) {
      const firstErrorField = errorKeys[0];
      const element = document.getElementById(`${firstErrorField}-form-item`);
      
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }
  }, [errors]);
}

/**
 * Field Error Message Component
 */
interface FieldErrorProps {
  error?: { message?: string };
}

export function FieldError({ error }: FieldErrorProps) {
  if (!error?.message) return null;

  return (
    <p className="text-sm font-medium text-destructive flex items-center gap-1 mt-1">
      <AlertCircle className="h-3 w-3" />
      {error.message}
    </p>
  );
}
