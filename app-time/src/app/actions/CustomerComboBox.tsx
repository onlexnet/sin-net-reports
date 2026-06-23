import _ from "lodash"
import React, { useEffect, useRef, useState } from "react"
import { useListCustomersQuery } from "../../components/.generated/components"
import { Input } from "components/ui/input"
import { renderHighlightedText } from "./FilteredComboBox"

export interface CustomerComboBoxProps {
  projectId: string,
  customerId: string | undefined,
  onSelected: (id?: string) => void,
  useListCustomersQuery: typeof useListCustomersQuery
}

type OptionType = { key: string, text: string }
type SearchableOptionType = OptionType & { rank: number }

export interface SearchableCustomerItem {
  id: { entityId: string },
  data: { customerName?: string | null },
  secretsEx?: Array<{ location: string, entityCode?: string | null }> | null,
}

const NFZ_PORTAL_LOCATION = 'Portal świadczeniodawcy';

const extractNfzCode = (secretsEx: SearchableCustomerItem['secretsEx']): string =>
  secretsEx?.find(o => o.location === NFZ_PORTAL_LOCATION)?.entityCode ?? '';

export const rankAndFilterCustomers = (
  items: SearchableCustomerItem[] | undefined,
  partName: string
): OptionType[] => {
  const normalizedQuery = partName.trim().toUpperCase();

  const getRank = (value: string): number | undefined => {
    if (!normalizedQuery) return undefined;

    const normalized = value.toUpperCase();
    if (normalized.startsWith(normalizedQuery)) return 1;

    const words = normalized.split(/\s+/).filter(Boolean);
    if (words.some(word => word.startsWith(normalizedQuery))) return 2;

    if (normalized.includes(normalizedQuery)) return 3;

    return undefined;
  };

  return _.chain(items)
    .map(it => {
      const text = it.data.customerName ?? '';
      const nfzCode = extractNfzCode(it.secretsEx);
      const nameRank = getRank(text);
      const codeRank = getRank(nfzCode);
      const rank = Math.min(nameRank ?? Infinity, codeRank ?? Infinity);
      return {
        key: it.id.entityId,
        text,
        rank: Number.isFinite(rank) ? rank : 4,
      } as SearchableOptionType;
    })
    .filter(it => {
      if (!normalizedQuery) return true;

      return it.rank <= 3;
    })
    .orderBy(
      [
        it => normalizedQuery ? it.rank : 0,
        it => it.text.toUpperCase(),
        it => it.key
      ],
      ['asc', 'asc', 'asc']
    )
    .map(it => ({ key: it.key, text: it.text }))
    .value();
}


/**
 * Combobox allowing to find a customer by part of name or NFZ code ('Portal świadczeniodawcy').
 * - focused + empty → full list shown
 * - focused + text → filtered list shown
 * - not focused → dropdown hidden
 * - selection is restricted to items from the list
 */
export const CustomerComboBox: React.FC<CustomerComboBoxProps> = props => {
  const { projectId, customerId, onSelected, useListCustomersQuery: listCustomers } = props;

  const { data } = listCustomers({ variables: { projectId } });
  const items = data?.Customers.list;

  const [inputValue, setInputValue] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  const containerRef = useRef<HTMLDivElement>(null);
  const listRef = useRef<HTMLDivElement>(null);

  const selectedCustomer = items?.find(it => it.id.entityId === customerId);
  const selectedName = selectedCustomer?.data.customerName ?? '';

  const filteredItems = rankAndFilterCustomers(items, inputValue);

  useEffect(() => {
    setHighlightedIndex(-1);
  }, [inputValue]);

  useEffect(() => {
    if (highlightedIndex >= 0 && listRef.current) {
      const el = listRef.current.children[highlightedIndex] as HTMLElement;
      el?.scrollIntoView({ block: 'nearest' });
    }
  }, [highlightedIndex]);

  const handleFocus = () => {
    setInputValue(selectedName);
    setIsOpen(true);
  };

  const handleBlur = (e: React.FocusEvent) => {
    if (containerRef.current?.contains(e.relatedTarget as Node)) return;
    setIsOpen(false);
  };

  const handleSelect = (key: string) => {
    onSelected(key);
    setIsOpen(false);
    setInputValue('');
    setHighlightedIndex(-1);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (!isOpen) return;
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setHighlightedIndex(prev => Math.min(prev + 1, filteredItems.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setHighlightedIndex(prev => Math.max(prev - 1, -1));
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (highlightedIndex >= 0 && highlightedIndex < filteredItems.length) {
        handleSelect(filteredItems[highlightedIndex].key);
      }
    } else if (e.key === 'Escape') {
      e.preventDefault();
      setIsOpen(false);
    }
  };

  return (
    <div ref={containerRef} className="relative w-full" onBlur={handleBlur}>
      <Input
        value={isOpen ? inputValue : selectedName}
        onChange={e => setInputValue(e.target.value)}
        onFocus={handleFocus}
        onKeyDown={handleKeyDown}
        placeholder="Wybierz klienta..."
      />
      {isOpen && (
        <div ref={listRef} className="absolute z-50 w-full mt-1 border rounded-md bg-popover text-popover-foreground shadow-md max-h-60 overflow-y-auto">
          {filteredItems.length === 0 && (
            <div className="px-3 py-2 text-sm text-muted-foreground">Brak wyników</div>
          )}
          {filteredItems.map((item, index) => (
            <div
              key={item.key}
              className={`px-3 py-2 cursor-pointer text-sm ${index === highlightedIndex ? 'bg-accent text-accent-foreground' : 'hover:bg-accent hover:text-accent-foreground'}`}
              onMouseDown={e => {
                e.preventDefault(); // keep focus, prevent blur before select
                handleSelect(item.key);
              }}
            >
              {renderHighlightedText(item.text, inputValue)}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

