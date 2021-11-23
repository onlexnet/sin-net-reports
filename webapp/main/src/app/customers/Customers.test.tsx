import { configure, shallow } from 'enzyme';

import Adapter from '@wojtekmaj/enzyme-adapter-react-17';
import { CustomersRoutedConnectedView } from './Customers.Routed';

configure({ adapter: new Adapter() });

describe('<Customers />', () => {

  it('renders three <Main /> components', () => {
    const wrapper = shallow(<CustomersRoutedConnectedView match={{}} history={undefined} location={undefined} />);
    // //expect(wrapper.find(Foo)).to.have.lengthOf(3);
  });

  it('exposes provided list', () => {
    // const wrapper = shallow(<Main />);
    // //expect(wrapper.find(Foo)).to.have.lengthOf(3);
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