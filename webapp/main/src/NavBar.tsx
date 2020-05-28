import * as React from 'react';
import { RouteComponentProps } from "react-router-dom";
import { Nav, INavLink } from 'office-ui-fabric-react';
import { routing } from './Routing';

//Child component related stuff
interface ChildComponentProps extends RouteComponentProps<any> {}

export const NavBasicExample: React.FC<ChildComponentProps> = (props) => {
  return (
    <Nav
      onLinkClick={_onLinkClick}
      selectedKey="key1"
      styles={{
        root: {
          maxWidth: 208,
          height: 350,
          boxSizing: 'border-box',
          border: '1px solid #eee',
          overflowY: 'auto'
        }
      }}
      groups={[
        {
          links: [
            {
              name: 'Usługi',
              onClick: () => props.history.push(routing.services),
              url: '',
              isExpanded: true
            },
            {
              name: 'Klienci',
              onClick: () => props.history.push(routing.customers),
              url: '',
              key: 'key3',
              isExpanded: true,
              target: '_blank'
            },
            {
              name: 'Raporty',
              onClick: () => props.history.push(routing.reports),
              url: '',
              key: 'key4',
              target: '_blank'
            },
            {
              name: 'Diagnostyka',
              onClick: () => props.history.push(routing.debug),
              url: '',
              key: 'key5',
              target: '_blank'
            },
          ]
        }
      ]}
    />
  );
};

function _onLinkClick(ev?: React.MouseEvent<HTMLElement>, item?: INavLink) {
  if (item && item.name === 'News') {
    alert('News link clicked');
  }
}
