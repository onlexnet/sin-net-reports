import { Col, Select } from "antd";
import React, { useCallback } from "react";

export interface FilteredComboBoxProps {
  /** Key value of just selected option to allow select proper element */
  selectedKey?: string,
  /** List of available options */
  items: { key: string, text: string }[]
  onChange: (key?: string) => void,
  onPendingValueChanged: (value: string) => void,
}

/**
 * Simplified version of ComboBox used in selection key-value options.
 */
export const FilteredComboBox: React.FC<FilteredComboBoxProps> = props => {

  const { selectedKey, items, onChange, onPendingValueChanged } = props;

  const onChangeLocal = useCallback((value: string) => {
    onChange(value);
  }, [onChange]);

  const onSearchLocal = useCallback((value: string) => {
    onPendingValueChanged(value);
  }, [onPendingValueChanged])

  return (
      <Select style={{ width: '100%' }}
        value={selectedKey}
        options={items.map(item => ({ value: item.key, label: item.text }))}
        showSearch
        onChange={onChangeLocal}
        onSearch={onSearchLocal}
        allowClear
      />
  );

}
