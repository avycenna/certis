'use client';

import { FieldValues, UseFormReturn } from 'react-hook-form';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Checkbox } from '@/components/ui/checkbox';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Label } from '@/components/ui/label';
import { CheckCircle2, Eye, EyeOff, Loader2, XCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useState, useRef, useEffect, useCallback } from 'react';
import {
  TextFieldConfig,
  NumberFieldConfig,
  TextareaFieldConfig,
  CheckboxFieldConfig,
  RadioFieldConfig,
  SelectFieldConfig,
  MultiSelectFieldConfig,
} from '../types';
import { Badge } from '@/components/ui/badge';
import { X } from 'lucide-react';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from '@/components/ui/command';
import { Check } from 'lucide-react';
import { cn } from '@/lib/utils';

/**
 * Text Input Field
 */
interface TextFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: TextFieldConfig<TFieldValues>;
  value: string;
  onChange: (value: string) => void;
  onBlur: () => void;
  disabled?: boolean;
  error?: string;
}

export function TextField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  onBlur,
  disabled,
}: TextFieldProps<TFieldValues>) {
  const [showPassword, setShowPassword] = useState(false);
  const isPassword = config.type === 'password';

  return (
    <div className="relative">
      <Input
        type={isPassword && !showPassword ? 'password' : config.type}
        placeholder={config.placeholder}
        value={value || ''}
        onChange={(e) => onChange(e.target.value)}
        onBlur={onBlur}
        disabled={disabled || config.disabled}
        readOnly={config.readOnly}
        autoComplete={config.autoComplete}
        className={config.className}
      />
      {isPassword && config.showPasswordToggle && (
        <Button
          type="button"
          variant="ghost"
          size="sm"
          className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
          onClick={() => setShowPassword(!showPassword)}
          disabled={disabled || config.disabled}
        >
          {showPassword ? (
            <EyeOff className="h-4 w-4" />
          ) : (
            <Eye className="h-4 w-4" />
          )}
        </Button>
      )}
    </div>
  );
}

/**
 * Number Input Field
 */
interface NumberFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: NumberFieldConfig<TFieldValues>;
  value: number;
  onChange: (value: number) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function NumberField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  onBlur,
  disabled,
}: NumberFieldProps<TFieldValues>) {
  return (
    <Input
      type="number"
      placeholder={config.placeholder}
      value={value ?? ''}
      onChange={(e) => onChange(e.target.valueAsNumber)}
      onBlur={onBlur}
      disabled={disabled || config.disabled}
      readOnly={config.readOnly}
      min={config.min}
      max={config.max}
      step={config.step}
      className={config.className}
    />
  );
}

/**
 * Slug Field
 */
interface SlugValidationResult {
  available: boolean;
}

interface SlugFieldProps {
  form: UseFormReturn<any>;
  name: string;
  prefix?: string;
  placeholder?: string;
  baseFieldName?: string;
  disabled?: boolean;
  className?: string;
  validateSlug?: (slug: string) => Promise<SlugValidationResult>;
}

export function SlugField({
  form,
  name,
  prefix = '/',
  placeholder = 'slug-factory',
  baseFieldName,
  disabled = false,
  className,
  validateSlug,
}: SlugFieldProps) {
  const [checkingAvailability, setCheckingAvailability] = useState(false);
  const [isAvailable, setIsAvailable] = useState<boolean | null>(null);
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [manuallyEdited, setManuallyEdited] = useState(false);
  const previousBaseValueRef = useRef<string>('');

  // Debounced slug availability check
  const checkSlugAvailability = useCallback(
    async (slug: string) => {
      if (!slug || slug.length < 3) {
        setIsAvailable(null);
        setCheckingAvailability(false);
        return;
      }

      // Skip validation if no validation function provided
      if (!validateSlug) {
        setIsAvailable(null);
        return;
      }

      setCheckingAvailability(true);
      
      try {
        const result = await validateSlug(slug);
        setIsAvailable(result.available);
        
        // Generate suggestions on client side if slug is not available
        if (!result.available) {
          const timestamp = new Date().getTime().toString().slice(-4);
          setSuggestions([
            `${slug}-${timestamp}`,
            `${slug}-team`,
            `${slug}-sarl`,
          ]);
          
          form.setError(name, {
            type: 'manual',
            message: 'This URL is already taken',
          });
        } else {
          setSuggestions([]);
          form.clearErrors(name);
        }
      } catch (error) {
        console.error('Error checking slug availability:', error);
        setIsAvailable(null);
      } finally {
        setCheckingAvailability(false);
      }
    },
    [validateSlug, form, name]
  );

  // Watch the slug field value
  const slugValue = form.watch(name);
  const baseValue = baseFieldName ? form.watch(baseFieldName) : null;

  // Check availability when slug changes (debounced)
  useEffect(() => {
    if (!slugValue) {
      setIsAvailable(null);
      setSuggestions([]);
      return;
    }

    const timeoutId = setTimeout(() => {
      checkSlugAvailability(slugValue);
    }, 500); // 500ms debounce

    return () => clearTimeout(timeoutId);
  }, [slugValue, checkSlugAvailability]);

  // Auto-generate slug from base field (e.g., organization name)
  useEffect(() => {
    if (!baseValue) {
      previousBaseValueRef.current = '';
      return;
    }

    // Reset manual edit flag if base value changed significantly
    if (previousBaseValueRef.current && baseValue !== previousBaseValueRef.current) {
      setManuallyEdited(false);
    }
    previousBaseValueRef.current = baseValue;

    // Only auto-generate if user hasn't manually edited the slug
    if (!manuallyEdited) {
      const generatedSlug = baseValue
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-|-$/g, '')
        .substring(0, 50); // Limit length
      
      // Only update if the generated slug is different from current value
      if (generatedSlug !== slugValue) {
        form.setValue(name, generatedSlug, { shouldValidate: false, shouldDirty: false });
      }
    }
  }, [baseValue, slugValue, form, name, manuallyEdited]);

  const handleSuggestionClick = (suggestion: string) => {
    setManuallyEdited(true);
    form.setValue(name, suggestion, { shouldValidate: true });
  };

  // Get status icon
  const getStatusIcon = () => {
    if (checkingAvailability) {
      return <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />;
    }
    if (isAvailable === true) {
      return <CheckCircle2 className="h-4 w-4 text-green-600" />;
    }
    if (isAvailable === false) {
      return <XCircle className="h-4 w-4 text-destructive" />;
    }
    return null;
  };

  // Calculate padding based on prefix length
  const getPaddingLeft = () => {
    if (!prefix) return undefined;
    // Estimate character width: ~0.6rem per character + 1rem base padding
    return `${prefix.length * 0.6 + 1}rem`;
  };

  // Track user input to detect manual edits
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    
    // Check if this is a genuine user edit (not programmatic)
    if (document.activeElement === e.target) {
      setManuallyEdited(true);
    }
    
    form.setValue(name, newValue, { shouldValidate: true, shouldDirty: true });
  };

  return (
    <div className="space-y-2">
      <div className="relative flex items-center">
        {/* Prefix */}
        {prefix && (
          <div className="absolute left-3 flex items-center pointer-events-none z-10">
            <span className="text-sm text-muted-foreground">{prefix}</span>
          </div>
        )}
        
        {/* Input */}
        <Input
          value={slugValue || ''}
          placeholder={placeholder}
          disabled={disabled}
          className={cn('pr-10', className)}
          style={{
            paddingLeft: getPaddingLeft(),
          }}
          onChange={handleInputChange}
          onBlur={form.register(name).onBlur}
        />
        
        {/* Status Icon */}
        <div className="absolute right-3 flex items-center pointer-events-none">
          {getStatusIcon()}
        </div>
      </div>

      {/* Suggestions */}
      {isAvailable === false && suggestions.length > 0 && (
        <div className="space-y-2">
          <p className="text-xs text-muted-foreground">Try these available options:</p>
          <div className="flex flex-wrap gap-2">
            {suggestions.map((suggestion) => (
              <button
                key={suggestion}
                type="button"
                onClick={() => handleSuggestionClick(suggestion)}
                className="text-xs px-2 py-1 rounded-md bg-muted hover:bg-muted/80 transition-colors border border-border"
              >
                {prefix}{suggestion}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

/**
 * Textarea Field with optional autosize
 */
interface TextareaFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: TextareaFieldConfig<TFieldValues>;
  value: string;
  onChange: (value: string) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function TextareaField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  onBlur,
  disabled,
}: TextareaFieldProps<TFieldValues>) {
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (config.autosize && textareaRef.current) {
      const textarea = textareaRef.current;
      textarea.style.height = 'auto';
      const newHeight = textarea.scrollHeight;
      
      if (config.minRows) {
        const minHeight = config.minRows * 24; // approximate row height
        textarea.style.height = Math.max(newHeight, minHeight) + 'px';
      } else if (config.maxRows) {
        const maxHeight = config.maxRows * 24;
        textarea.style.height = Math.min(newHeight, maxHeight) + 'px';
      } else {
        textarea.style.height = newHeight + 'px';
      }
    }
  }, [value, config.autosize, config.minRows, config.maxRows]);

  return (
    <Textarea
      ref={textareaRef}
      placeholder={config.placeholder}
      value={value || ''}
      onChange={(e) => onChange(e.target.value)}
      onBlur={onBlur}
      disabled={disabled || config.disabled}
      readOnly={config.readOnly}
      rows={config.rows || 4}
      className={config.className}
    />
  );
}

/**
 * Checkbox Field
 */
interface CheckboxFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: CheckboxFieldConfig<TFieldValues>;
  value: boolean;
  onChange: (value: boolean) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function CheckboxField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  onBlur,
  disabled,
}: CheckboxFieldProps<TFieldValues>) {
  return (
    <div className="flex items-center space-x-2">
      <Checkbox
        id={config.name}
        checked={value || false}
        onCheckedChange={(checked) => onChange(checked as boolean)}
        onBlur={onBlur}
        disabled={disabled || config.disabled}
        className={config.className}
      />
      {config.checkboxLabel && (
        <Label
          htmlFor={config.name}
          className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
        >
          {config.checkboxLabel}
        </Label>
      )}
    </div>
  );
}

/**
 * Radio Group Field
 */
interface RadioFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: RadioFieldConfig<TFieldValues>;
  value: string;
  onChange: (value: string) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function RadioField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  onBlur,
  disabled,
}: RadioFieldProps<TFieldValues>) {
  return (
    <RadioGroup
      value={value || ''}
      onValueChange={onChange}
      onBlur={onBlur}
      disabled={disabled || config.disabled}
      className={cn(
        config.orientation === 'horizontal' ? 'flex flex-row space-x-4' : 'space-y-2',
        config.className
      )}
    >
      {config.options.map((option) => (
        <div key={option.value} className="flex items-center space-x-2">
          <RadioGroupItem value={option.value} id={`${config.name}-${option.value}`} disabled={option.disabled} />
          <Label
            htmlFor={`${config.name}-${option.value}`}
            className="text-sm font-normal cursor-pointer"
          >
            {option.icon && <span className="mr-2">{option.icon}</span>}
            {option.label}
            {option.description && (
              <span className="block text-xs text-muted-foreground">{option.description}</span>
            )}
          </Label>
        </div>
      ))}
    </RadioGroup>
  );
}

/**
 * Select Dropdown Field
 */
interface SelectFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: SelectFieldConfig<TFieldValues>;
  value: string;
  onChange: (value: string) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function SelectField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  disabled,
}: SelectFieldProps<TFieldValues>) {
  return (
    <Select value={value || ''} onValueChange={onChange} disabled={disabled || config.disabled}>
      <SelectTrigger className={config.className}>
        <SelectValue placeholder={config.placeholder || 'Select an option'} />
      </SelectTrigger>
      <SelectContent>
        {config.emptyOption && (
          <SelectItem value="">{config.emptyOption}</SelectItem>
        )}
        {config.options.map((option) => (
          <SelectItem key={option.value} value={option.value} disabled={option.disabled}>
            <div className="flex items-center">
              {option.icon && <span className="mr-2">{option.icon}</span>}
              <div>
                <div>{option.label}</div>
                {option.description && (
                  <div className="text-xs text-muted-foreground">{option.description}</div>
                )}
              </div>
            </div>
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
}

/**
 * Multi-Select Field
 */
interface MultiSelectFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: MultiSelectFieldConfig<TFieldValues>;
  value: string[];
  onChange: (value: string[]) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function MultiSelectField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  disabled,
}: MultiSelectFieldProps<TFieldValues>) {
  const [open, setOpen] = useState(false);
  const selectedValues = value || [];

  const handleSelect = (optionValue: string) => {
    const newValue = selectedValues.includes(optionValue)
      ? selectedValues.filter((v) => v !== optionValue)
      : [...selectedValues, optionValue];
    
    if (config.max && newValue.length > config.max) {
      return;
    }
    
    onChange(newValue);
  };

  const handleRemove = (optionValue: string) => {
    onChange(selectedValues.filter((v) => v !== optionValue));
  };

  return (
    <div className="space-y-2">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className={cn('w-full justify-between', config.className)}
            disabled={disabled || config.disabled}
          >
            <span className="truncate">
              {selectedValues.length === 0
                ? config.placeholder || 'Select options...'
                : `${selectedValues.length} selected`}
            </span>
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-full p-0" align="start">
          <Command>
            <CommandInput placeholder="Search..." />
            <CommandList>
              <CommandEmpty>No options found.</CommandEmpty>
              <CommandGroup>
                {config.options.map((option) => (
                  <CommandItem
                    key={option.value}
                    value={option.value}
                    onSelect={() => handleSelect(option.value)}
                    disabled={option.disabled}
                  >
                    <Check
                      className={cn(
                        'mr-2 h-4 w-4',
                        selectedValues.includes(option.value) ? 'opacity-100' : 'opacity-0'
                      )}
                    />
                    {option.icon && <span className="mr-2">{option.icon}</span>}
                    <div>
                      <div>{option.label}</div>
                      {option.description && (
                        <div className="text-xs text-muted-foreground">{option.description}</div>
                      )}
                    </div>
                  </CommandItem>
                ))}
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
      
      {selectedValues.length > 0 && (
        <div className="flex flex-wrap gap-1">
          {selectedValues.map((val) => {
            const option = config.options.find((opt) => opt.value === val);
            return (
              <Badge key={val} variant="secondary" className="gap-1">
                {option?.label || val}
                <X
                  className="h-3 w-3 cursor-pointer"
                  onClick={() => handleRemove(val)}
                />
              </Badge>
            );
          })}
        </div>
      )}
    </div>
  );
}
