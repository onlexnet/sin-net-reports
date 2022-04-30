// https://www.robinwieruch.de/react-testing-library/
import tl from '@testing-library/react';
import { CustomerComboBox } from './CustomerComboBox';

describe('CustomerComboBox', () => {

  test.skip('renders', () => {
    tl.render(<CustomerComboBox projectId='my id' customerId='my customerId' onSelected={id => { }}  />)
  });
});