import _ from "lodash"
import React, { useRef, useState } from "react"
import { useListCustomersQuery } from "../../components/.generated/components"
import { FilteredComboBox } from "./FilteredComboBox"

export interface CustomerComboBoxProps {
  projectId: string,
  customerId: string | undefined,
  onSelected: (id?: string) => void,
  useListCustomersQuery: typeof useListCustomersQuery
}

type OptionType = { key: string, text: string }
type SearchableOptionType = OptionType & { secretsEx: any, rank: number }

export interface SearchableCustomerItem {
  id: { entityId: string },
  data: { customerName?: string | null },
  secretsEx?: any,
}

export const rankAndFilterCustomers = (
  items: SearchableCustomerItem[] | undefined,
  partName: string
): OptionType[] => {
  const normalizedQuery = partName.trim().toUpperCase();

  const getRank = (customerName: string): number | undefined => {
    if (!normalizedQuery) return undefined;

    const normalizedName = customerName.toUpperCase();
    if (normalizedName.startsWith(normalizedQuery)) return 1;

    const words = normalizedName.split(/\s+/).filter(Boolean);
    if (words.some(word => word.startsWith(normalizedQuery))) return 2;

    if (normalizedName.includes(normalizedQuery)) return 3;

    return undefined;
  };

  return _.chain(items)
    .map(it => {
      const text = it.data.customerName ?? "";
      return {
        key: it.id.entityId,
        text,
        secretsEx: it.secretsEx,
        rank: getRank(text) ?? 4,
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
      ["asc", "asc", "asc"]
    )
    .map(it => ({ key: it.key, text: it.text }))
    .value();
}


/**
 * Allows to find customer based on part of name of value from secret 'Portal świadczeniodawcy'
 * @param props 
 * @returns 
 */
export const CustomerComboBox: React.FC<CustomerComboBoxProps> = props => {
  const { useListCustomersQuery: listCustomers } = props;

  const [filteredCustomers, setFilteredCustomers] = useState<OptionType[]>([])


  const { data } = listCustomers({
    variables: {
        projectId: props.projectId
    }
})
  const items = data?.Customers.list;

  const filteredElements = (partName: string) => rankAndFilterCustomers(items, partName)

  const renderAfterLostLoad = useRef(0);
  if (renderAfterLostLoad.current === 0 && data) {
    renderAfterLostLoad.current = renderAfterLostLoad.current + 1;
    if (renderAfterLostLoad.current === 1) {
      setFilteredCustomers(filteredElements(''));
    }
  }
  if (renderAfterLostLoad.current !== 0 && !data) {
    renderAfterLostLoad.current = 0;
  }


  const onSearch = (value: string) => {
    // we have rerender issue, so let change value only of different from current value
    const filtered = filteredElements(value);
    if (_.isEqual(filtered, filteredCustomers)) return;

    setFilteredCustomers(filtered);
  }

  return <FilteredComboBox
    selectedKey={props.customerId}
    items={filteredCustomers}
    onChange={props.onSelected}
    onSearch={onSearch} />
}

