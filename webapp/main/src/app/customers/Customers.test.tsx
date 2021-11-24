import { DetailsList } from '@fluentui/react';
import Adapter from '@wojtekmaj/enzyme-adapter-react-17';
import { configure, shallow } from 'enzyme';
import { ListCustomersItem } from '../../api/useListCustomers';
import { EntityId } from '../../store/actions/ServiceModel';
import { CustomersView } from './Customers';


configure({ adapter: new Adapter() });

describe('<Customers />', () => {

  const createItem: () => ListCustomersItem = () => {
    const customerId: EntityId = {
      projectId: 'any',
      entityId: 'any',
      entityVersion: 42,
    }
    const result: ListCustomersItem = { name: 'any', customerId };
    return result;
  }

  it('exposes provided list', () => {
    const item1 = createItem();
    const wrapper = shallow(<CustomersView
      projectId="my project id"
      onNewClientCommand={() => { }}
      listCustomers={projectId => [ item1 ]} />);
      var details = wrapper.find(DetailsList).props();
      expect(details.items.length).toEqual(1);
  });

  it('filters by name', () => {
    // const wrapper = shallow(<Main />);
    // //expect(wrapper.find(Foo)).to.have.lengthOf(3);
  });

  it('filters by prop: portal świadczeniodawcy/kod świadczeniodawcy', () => {
    // const wrapper = shallow(<Main />);
    // //expect(wrapper.find(Foo)).to.have.lengthOf(3);
  });

  //   it('renders an `.icon-star`', () => {
  //     const wrapper = shallow(<MyComponent />);
  //     expect(wrapper.find('.icon-star')).to.have.lengthOf(1);
  //   });

  //   it('renders children when passed in', () => {
  //     const wrapper = shallow((
  //       <MyComponent>
  //         <div className="unique" />
  //       </MyComponent>
  //     ));
  //     expect(wrapper.contains(<div className="unique" />)).to.equal(true);
  //   });

  //   it('simulates click events', () => {
  //     const onButtonClick = sinon.spy();
  //     const wrapper = shallow(<Foo onButtonClick={onButtonClick} />);
  //     wrapper.find('button').simulate('click');
  //     expect(onButtonClick).to.have.property('callCount', 1);
  //   });
});