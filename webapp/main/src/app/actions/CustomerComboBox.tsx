import _ from "lodash"
import React, { useRef, useState } from "react"
import { useListCustomersQuery } from "../../Components/.generated/components"
import { FilteredComboBox } from "./FilteredComboBox"

export interface CustomersComboBox {
  projectId: string,
  customerId: string | undefined,
  onSelected: (id?: string) => void,
}

export const CustomerComboBox: React.FC<CustomersComboBox> = props => {

  type OptionType = { key: string, text: string }
  const [filteredCustomers, setFilteredCustomers] = useState<OptionType[]>([])

  const { data } = useListCustomersQuery({
    variables: {
        projectId: props.projectId
    }
})
  const items = data?.Customers.list;

  const filteredElements = (partName: string) => {
    return _.chain(items)
      .map(it => ({ key: it.id.entityId, text: it.data.customerName }))
      .filter(it => {
        if (!partName) return true;
        var optionText = it.text;
        return optionText.toUpperCase().indexOf(partName.toUpperCase()) !== -1;
      })
      .value()
  }

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

  const onPendingValueChanged = (value: string) => {
    setFilteredCustomers(filteredElements(value));
  }



  return <FilteredComboBox label="Wybór klienta"
    selectedKey={props.customerId}
    items={filteredCustomers}
    onChange={props.onSelected}
    onPendingValueChanged={onPendingValueChanged} />
}

