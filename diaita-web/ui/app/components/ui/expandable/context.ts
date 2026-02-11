import type { Ref } from "vue"

export type ExpandableContext = {
    isExpanded: Ref<boolean>;
    toggle: () => void;
    setExpanded: (value: boolean) => void;
};

export const expandableContextKey = Symbol("expandable");
