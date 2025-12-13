'use client';

import { FieldValues } from 'react-hook-form';
import { useState, useEffect } from 'react';
import { Calendar } from '@/components/ui/calendar';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Button } from '@/components/ui/button';
import { Calendar as CalendarIcon, Upload, X, Star, StarHalf, Heart, ThumbsUp } from 'lucide-react';
import { format } from 'date-fns';
import { cn } from '@/lib/utils';
import { Slider } from '@/components/ui/slider';
import { Switch } from '@/components/ui/switch';
import { Label } from '@/components/ui/label';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from '@/components/ui/command';
import { Check } from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import {
  FileUpload,
  FileUploadDropzone,
  FileUploadTrigger,
  FileUploadList,
  FileUploadItem,
  FileUploadItemPreview,
  FileUploadItemMetadata,
  FileUploadItemDelete,
} from '@/components/ui/file-upload';
import {
  DateFieldConfig,
  DateRangeFieldConfig,
  FileFieldConfig,
  RangeFieldConfig,
  RatingFieldConfig,
  SwitchFieldConfig,
  ComboboxFieldConfig,
} from '../types';
import { validateFile, formatFileSize } from '../utils';

/**
 * Date Picker Field
 */
interface DateFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: DateFieldConfig<TFieldValues>;
  value: Date;
  onChange: (value: Date | undefined) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function DateField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  disabled,
}: DateFieldProps<TFieldValues>) {
  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className={cn(
            'w-full justify-start text-left font-normal',
            !value && 'text-muted-foreground',
            config.className
          )}
          disabled={disabled || config.disabled}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {value ? (
            format(value, config.config?.format || 'PPP')
          ) : (
            <span>{config.config?.placeholder || config.placeholder || 'Pick a date'}</span>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0" align="start">
        <Calendar
          mode="single"
          selected={value}
          onSelect={onChange}
          disabled={
            config.config?.disabledDates
              ? (date) => config.config!.disabledDates!.some((d) => d.getTime() === date.getTime())
              : undefined
          }
          initialFocus
        />
      </PopoverContent>
    </Popover>
  );
}

/**
 * Date Range Picker Field
 */
interface DateRangeFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: DateRangeFieldConfig<TFieldValues>;
  value: { from: Date; to: Date };
  onChange: (value: { from: Date; to: Date } | undefined) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function DateRangeField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  disabled,
}: DateRangeFieldProps<TFieldValues>) {
  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className={cn(
            'w-full justify-start text-left font-normal',
            !value && 'text-muted-foreground',
            config.className
          )}
          disabled={disabled || config.disabled}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {value?.from ? (
            value.to ? (
              <>
                {format(value.from, config.config?.format || 'LLL dd, y')} -{' '}
                {format(value.to, config.config?.format || 'LLL dd, y')}
              </>
            ) : (
              format(value.from, config.config?.format || 'LLL dd, y')
            )
          ) : (
            <span>{config.config?.placeholder || config.placeholder || 'Pick a date range'}</span>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0" align="start">
        <Calendar
          mode="range"
          selected={value}
          onSelect={(dateRange) => {
            // Only update if we have a valid date range with both from and to
            if (dateRange?.from && dateRange?.to) {
              onChange({ from: dateRange.from, to: dateRange.to });
            } else if (dateRange?.from) {
              // Allow partial selection during interaction
              onChange({ from: dateRange.from, to: dateRange.from });
            }
          }}
          numberOfMonths={2}
          initialFocus
        />
      </PopoverContent>
    </Popover>
  );
}

/**
 * File Upload Field with Preview (DiceUI)
 */
interface FileFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: FileFieldConfig<TFieldValues>;
  value: File | File[];
  onChange: (value: File | File[] | undefined) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function FileField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  disabled,
}: FileFieldProps<TFieldValues>) {
  const [rejectionErrors, setRejectionErrors] = useState<Array<{ fileName: string; error: string }>>([]);

  // Convert value to array format for FileUpload component
  const files = value ? (Array.isArray(value) ? value : [value]) : [];

  const handleValueChange = (newFiles: File[]) => {
    // Clear rejection errors when files change successfully
    setRejectionErrors([]);
    
    if (newFiles.length === 0) {
      onChange(undefined);
    } else if (config.config.multiple) {
      onChange(newFiles);
    } else {
      onChange(newFiles[0]);
    }
  };

  const handleFileValidate = (file: File): string | null | undefined => {
    const error = validateFile(file, {
      accept: config.config.accept,
      maxSize: config.config.maxSize,
    });
    return error || null;
  };

  const handleFileReject = (file: File, message: string) => {
    // Add rejected file to errors list
    setRejectionErrors((prev) => [
      ...prev,
      { fileName: file.name, error: message },
    ]);

    // Auto-clear error after 5 seconds
    setTimeout(() => {
      setRejectionErrors((prev) => prev.filter((err) => err.fileName !== file.name));
    }, 5000);
  };

  return (
    <FileUpload
      value={files}
      onValueChange={handleValueChange}
      onFileValidate={handleFileValidate}
      onFileReject={handleFileReject}
      accept={config.config.accept}
      maxFiles={config.config.maxFiles}
      maxSize={config.config.maxSize}
      multiple={config.config.multiple}
      disabled={disabled || config.disabled}
      className={config.className}
    >
      <FileUploadDropzone>
        <div className="flex flex-col items-center gap-2">
          <Upload className="h-8 w-8 text-muted-foreground" />
          <div className="text-center">
            <p className="text-sm text-muted-foreground">
              {config.placeholder || 'Click to upload or drag and drop'}
            </p>
            {config.config.accept && (
              <p className="text-xs text-muted-foreground mt-1">
                Accepted: {config.config.accept}
              </p>
            )}
            {config.config.maxSize && (
              <p className="text-xs text-muted-foreground">
                Max size: {formatFileSize(config.config.maxSize)}
              </p>
            )}
          </div>
          <FileUploadTrigger asChild>
            <Button variant="outline" size="sm">
              Browse Files
            </Button>
          </FileUploadTrigger>
        </div>
      </FileUploadDropzone>

      <FileUploadList>
        {files.map((file) => (
          <FileUploadItem key={file.name} value={file}>
            {config.config.showPreview !== false && (
              <FileUploadItemPreview />
            )}
            <FileUploadItemMetadata />
            <FileUploadItemDelete asChild>
              <Button
                type="button"
                variant="ghost"
                size="sm"
                disabled={disabled || config.disabled}
              >
                <X className="h-4 w-4" />
              </Button>
            </FileUploadItemDelete>
          </FileUploadItem>
        ))}
      </FileUploadList>

      {/* Display rejection errors */}
      {rejectionErrors.length > 0 && (
        <div className="space-y-2 mt-2">
          {rejectionErrors.map((error, index) => (
            <div
              key={`${error.fileName}-${index}`}
              className="flex items-start gap-2 p-3 rounded-md border border-destructive/50 bg-destructive/10"
            >
              <X className="h-4 w-4 text-destructive mt-0.5 shrink-0" />
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-destructive truncate">
                  {error.fileName}
                </p>
                <p className="text-xs text-destructive/80">
                  {error.error}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </FileUpload>
  );
}

/**
 * Range Slider Field
 */
interface RangeFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: RangeFieldConfig<TFieldValues>;
  value: number;
  onChange: (value: number) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function RangeField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  disabled,
}: RangeFieldProps<TFieldValues>) {
  return (
    <div className="space-y-2">
      <Slider
        min={config.config.min}
        max={config.config.max}
        step={config.config.step || 1}
        value={[value || config.config.min]}
        onValueChange={([val]) => onChange(val)}
        disabled={disabled || config.disabled}
        className={config.className}
      />
      {config.config.showValue !== false && (
        <div className="text-sm text-muted-foreground text-center">
          {value || config.config.min}
        </div>
      )}
    </div>
  );
}

/**
 * Rating Field
 */
interface RatingFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: RatingFieldConfig<TFieldValues>;
  value: number;
  onChange: (value: number) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function RatingField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  disabled,
}: RatingFieldProps<TFieldValues>) {
  const [hover, setHover] = useState(0);
  const max = config.config?.max || 5;
  const allowHalf = config.config?.allowHalf || false;
  const icon = config.config?.icon || 'star';

  const IconComponent = icon === 'heart' ? Heart : icon === 'thumbs' ? ThumbsUp : Star;

  const handleClick = (rating: number) => {
    if (disabled || config.disabled) return;
    onChange(rating);
  };

  const handleMouseEnter = (rating: number) => {
    if (disabled || config.disabled) return;
    setHover(rating);
  };

  const handleMouseLeave = () => {
    setHover(0);
  };

  const renderIcon = (index: number) => {
    const currentValue = hover || value || 0;
    const isFilled = index <= currentValue;
    const isHalf = allowHalf && index - 0.5 === currentValue;

    if (isHalf) {
      return <StarHalf className="h-6 w-6 fill-yellow-400 text-yellow-400" />;
    }

    return (
      <IconComponent
        className={cn(
          'h-6 w-6 cursor-pointer transition-colors',
          isFilled ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300',
          disabled || config.disabled ? 'cursor-not-allowed opacity-50' : ''
        )}
      />
    );
  };

  return (
    <div className={cn('flex gap-1', config.className)} onMouseLeave={handleMouseLeave}>
      {Array.from({ length: max }, (_, i) => i + 1).map((rating) => (
        <div
          key={rating}
          onClick={() => handleClick(rating)}
          onMouseEnter={() => handleMouseEnter(rating)}
        >
          {renderIcon(rating)}
        </div>
      ))}
    </div>
  );
}

/**
 * Switch/Toggle Field
 */
interface SwitchFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: SwitchFieldConfig<TFieldValues>;
  value: boolean;
  onChange: (value: boolean) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function SwitchField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  onBlur,
  disabled,
}: SwitchFieldProps<TFieldValues>) {
  return (
    <div className="flex items-center space-x-2">
      <Switch
        id={config.name}
        checked={value || false}
        onCheckedChange={onChange}
        onBlur={onBlur}
        disabled={disabled || config.disabled}
        className={config.className}
      />
      {config.switchLabel && (
        <Label
          htmlFor={config.name}
          className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
        >
          {config.switchLabel}
        </Label>
      )}
    </div>
  );
}

/**
 * Combobox Field (Single and Multi-Select)
 */
interface ComboboxFieldProps<TFieldValues extends FieldValues = FieldValues> {
  config: ComboboxFieldConfig<TFieldValues>;
  value: string | string[];
  onChange: (value: string | string[]) => void;
  onBlur: () => void;
  disabled?: boolean;
}

export function ComboboxField<TFieldValues extends FieldValues = FieldValues>({
  config,
  value,
  onChange,
  disabled,
}: ComboboxFieldProps<TFieldValues>) {
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState('');
  const [options, setOptions] = useState(config.options);
  const [loading, setLoading] = useState(false);

  const isSingleMode = config.config.mode === 'single';
  const selectedValues = isSingleMode ? (value ? [value as string] : []) : (value as string[] || []);

  useEffect(() => {
    if (config.config.loadOptions && search) {
      // Use an async function to avoid setState in effect warning
      let cancelled = false;
      
      const loadData = async () => {
        setLoading(true);
        try {
          const newOptions = await config.config.loadOptions!(search);
          if (!cancelled) {
            setOptions(newOptions);
            setLoading(false);
          }
        } catch {
          if (!cancelled) {
            setLoading(false);
          }
        }
      };
      
      loadData();
      
      return () => {
        cancelled = true;
      };
    }
  }, [search, config.config]);

  const handleSelect = (optionValue: string) => {
    if (isSingleMode) {
      onChange(optionValue);
      setOpen(false);
    } else {
      const newValue = selectedValues.includes(optionValue)
        ? selectedValues.filter((v) => v !== optionValue)
        : [...selectedValues, optionValue];
      onChange(newValue);
    }
  };

  const handleRemove = (optionValue: string) => {
    if (isSingleMode) {
      onChange('');
    } else {
      onChange(selectedValues.filter((v) => v !== optionValue));
    }
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
                ? config.config.placeholder || config.placeholder || 'Select...'
                : isSingleMode
                ? options.find((opt) => opt.value === selectedValues[0])?.label || selectedValues[0]
                : `${selectedValues.length} selected`}
            </span>
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-full p-0" align="start">
          <Command shouldFilter={!config.config.loadOptions}>
            {config.config.searchable !== false && (
              <CommandInput
                placeholder="Search..."
                value={search}
                onValueChange={setSearch}
              />
            )}
            <CommandList>
              {loading ? (
                <div className="p-2 text-center text-sm">Loading...</div>
              ) : (
                <>
                  <CommandEmpty>
                    {config.config.emptyMessage || 'No options found.'}
                  </CommandEmpty>
                  <CommandGroup>
                    {options.map((option) => (
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
                </>
              )}
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>

      {!isSingleMode && selectedValues.length > 0 && (
        <div className="flex flex-wrap gap-1">
          {selectedValues.map((val) => {
            const option = options.find((opt) => opt.value === val);
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
