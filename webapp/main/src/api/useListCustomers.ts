import _ from 'lodash';
import { useState } from 'react';
import { useListCustomersQuery } from '../Components/.generated/components';

export interface UseListCustomersItem {
  name: string
}

export const useListCustomers: (projectId: string) => UseListCustomersItem[] = (projectId) => {
  const { data } = useListCustomersQuery({
    variables: {
      projectId
    }
  });
  const [state, setState] = useState<{ name: string }[]>([]);

  if (data) {
    const result = _.chain(data?.Customers.list)
      .map(it => ({ name: it.data.customerName }))
      .value();
    if (result.length != 0 && state.length == 0) {
      setState(result);
    }
  }

  return state;
}