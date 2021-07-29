import { ComboBox, IComboBox, IComboBoxOption } from "@fluentui/react";
import React, { useCallback } from "react";

export interface FilteredComboBoxProps {
  label: string,
  selectedKey?: string,
  items: { key: string, text: string }[]
  onChange: (key?: string) => void,
  onPendingValueChanged: (value: string) => void,
}

export const FilteredComboBox: React.FC<FilteredComboBoxProps> = props => {

  const { label, selectedKey, items, onChange, onPendingValueChanged } = props;

  const onChangeLocal = useCallback((ev: React.FormEvent<IComboBox>, option?: IComboBoxOption) => {
    const key = option?.key as string | undefined;
    onChange(key);
  }, [onChange]);

  const onPendingValueChangedLocal = useCallback((option?: IComboBoxOption, index?: number, value?: string) => {
    // for some reason the event is invoked twice and value is undefined
    if (value == null) return; // it handles undefined as well

    onPendingValueChanged(value);
  }, [onPendingValueChanged])

  return (
    <ComboBox label={label}
      selectedKey={selectedKey}
      options={items}
      autoComplete="on"
      allowFreeform
      openOnKeyboardFocus
      onChange={onChangeLocal}
      onPendingValueChanged={onPendingValueChangedLocal}
    />);

}
