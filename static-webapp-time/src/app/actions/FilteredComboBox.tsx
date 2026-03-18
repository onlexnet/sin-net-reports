import { Select } from "antd";
import React, { useCallback } from "react";

export interface FilteredComboBoxProps {
  /** Key value of just selected option to allow select proper element */
  selectedKey?: string,
  /** List of available options */
  items: { key: string, text: string }[]
  onChange: (key?: string) => void,
  onSearch: (value: string) => void,
}

/**
 * Simplified version of ComboBox used in selection key-value options.
 */
export const FilteredComboBox: React.FC<FilteredComboBoxProps> = props => {

  const { selectedKey, items, onChange, onSearch } = props;

  const onChangeLocal = useCallback((value: string) => {
    onChange(value);
  }, [onChange]);

  const onSearchLocal = useCallback((value: string) => {
    onSearch(value);
  }, [onSearch])

  return (
      <Select style={{ width: '100%' }}
        filterOption={false} // it is already filtered, so no a need to filter it once again by Select component
        value={selectedKey}
        options={items.map(item => ({ value: item.key, label: item.text }))}
        showSearch
        onChange={onChangeLocal}
        onSearch={onSearchLocal}
        allowClear
      />
  );

}
