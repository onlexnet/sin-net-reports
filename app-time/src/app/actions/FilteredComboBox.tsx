import React, { useCallback, useState } from "react";
import { Input } from "components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "components/ui/select";

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
  const [searchText, setSearchText] = useState("");

  const onChangeLocal = useCallback((value: string) => {
    onChange(value);
  }, [onChange]);

  const onSearchLocal = useCallback((value: string) => {
    onSearch(value);
  }, [onSearch])

  return (
      <div className="w-full flex flex-col gap-2">
        <Input
          placeholder="Szukaj..."
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value);
            onSearchLocal(e.target.value);
          }}
        />
        <Select value={selectedKey} onValueChange={onChangeLocal}>
          <SelectTrigger className="w-full">
            <SelectValue placeholder="Wybierz..." />
          </SelectTrigger>
          <SelectContent>
            {items.map(item => (
              <SelectItem key={item.key} value={item.key}>{item.text}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
  );

}
