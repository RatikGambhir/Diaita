export type PlaceholderFor = (value: string, fallback: string) => string;
export type IsSelected = (currentValue: string, defaultValue: string, targetValue: string) => boolean;

export type Option = {
    label: string;
    value: string;
};

export type SettingsFormState = Record<string, any>;
export type SettingsFormDefaults = Record<string, any>;
