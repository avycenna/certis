'use client';

import { FieldValues, UseFormReturn } from 'react-hook-form';
import { FormStep as FormStepType } from '../types';
import { FormSection } from './form-section';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import {
  Button, Progress,
  Stepper, StepperList, StepperItem, StepperTrigger, StepperIndicator, StepperSeparator, StepperTitle, StepperDescription, StepperContent,
} from '@/components/ui';

/**
 * Multi-Step Form Component
 * Built on top of the shared Stepper component for consistent UX
 */
interface MultiStepFormProps<TFieldValues extends FieldValues = FieldValues> {
  form: UseFormReturn<TFieldValues>;
  steps: FormStepType<TFieldValues>[];
  currentStep: number;
  onNext: () => void;
  onPrev: () => void;
  onStepChange: (step: number) => void;
  variant?: 'default' | 'card';
}

export function MultiStepForm<TFieldValues extends FieldValues = FieldValues>({
  form,
  steps,
  currentStep,
  onNext,
  onPrev,
  onStepChange,
  variant = 'default',
}: MultiStepFormProps<TFieldValues>) {
  const currentStepData = steps[currentStep];
  const isFirstStep = currentStep === 0;
  const isLastStep = currentStep === steps.length - 1;
  const progress = ((currentStep + 1) / steps.length) * 100;

  if (!currentStepData) {
    return <div>Invalid step</div>;
  }

  // Map step index to step id for Stepper value
  const currentStepValue = currentStepData.id;

  // Handle step change from Stepper - triggered by clicking step indicators
  const handleStepperChange = (value: string) => {
    const stepIndex = steps.findIndex((s) => s.id === value);
    if (stepIndex !== -1) {
      onStepChange(stepIndex);
    }
  };

  // Handle next button - uses form-factory's validation logic
  const handleNext = async () => {
    await onNext();
  };

  // Handle prev button - uses form-factory's navigation
  const handlePrev = () => {
    onPrev();
  };

  return (
    <div className="space-y-6">
      {/* Progress Bar */}
      <div className="space-y-2">
        <div className="flex justify-between text-sm text-muted-foreground">
          <span>
            Step {currentStep + 1} of {steps.length}
          </span>
          <span>{Math.round(progress)}% Complete</span>
        </div>
        <Progress value={progress} className="h-2" />
      </div>

      {/* Stepper Component */}
      <Stepper
        value={currentStepValue}
        onValueChange={handleStepperChange}
        orientation="horizontal"
        className="w-full"
      >
        {/* Step Indicators */}
        <StepperList className="mb-8">
          {steps.map((step, index) => (
            <StepperItem
              key={step.id}
              value={step.id}
              completed={index < currentStep}
              disabled={index > currentStep}
            >
              <StepperTrigger>
                <StepperIndicator>
                  {index < currentStep ? '✓' : index + 1}
                </StepperIndicator>
                <div className="flex flex-col text-left">
                  <StepperTitle>{step.title}</StepperTitle>
                  {step.description && (
                    <StepperDescription>{step.description}</StepperDescription>
                  )}
                </div>
              </StepperTrigger>
              <StepperSeparator />
            </StepperItem>
          ))}
        </StepperList>

        {/* Step Content */}
        {steps.map((step) => (
          <StepperContent key={step.id} value={step.id}>
            <div className="space-y-6">
              {step.sections.map((section) => (
                <FormSection
                  key={section.id}
                  form={form}
                  section={section}
                  variant={variant}
                />
              ))}
            </div>
          </StepperContent>
        ))}

        {/* Navigation Buttons */}
        <div className="flex justify-between pt-6 border-t mt-8">
          <Button
            type="button"
            variant="outline"
            onClick={handlePrev}
            disabled={isFirstStep}
            className="gap-2"
          >
            <ChevronLeft className="h-4 w-4" />
            Previous
          </Button>

          {!isLastStep ? (
            <Button 
              type="button" 
              onClick={handleNext} 
              className="gap-2"
              disabled={form.formState.isValidating || Object.keys(form.formState.errors).length > 0}
            >
              Next
              <ChevronRight className="h-4 w-4" />
            </Button>
          ) : (
            <Button 
              type="submit" 
              className="gap-2"
              disabled={form.formState.isSubmitting || form.formState.isValidating || Object.keys(form.formState.errors).length > 0}
            >
              Submit
            </Button>
          )}
        </div>
      </Stepper>
    </div>
  );
}
