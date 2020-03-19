import * as React from 'react';
import { Nav, INavLink } from 'office-ui-fabric-react/lib/Nav';
import { RouteComponentProps } from "react-router-dom";

//Child component related stuff
interface ChildComponentProps extends RouteComponentProps<any> {}

export const NavBasicExample: React.FC<ChildComponentProps> = (props) => {
  return (
    <Nav
      onLinkClick={_onLinkClick}
      selectedKey="key1"
      selectedAriaLabel="Selected"
      ariaLabel="Nav basic example"
      styles={{
        root: {
          width: 208,
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
              onClick: () => props.history.push('/services'),
              url: '',
              expandAriaLabel: 'Expand Home section',
              collapseAriaLabel: 'Collapse Home section',
              isExpanded: true
            },
            {
              name: 'Klienci',
              onClick: () => props.history.push('/customers'),
              url: '',
              key: 'key3',
              isExpanded: true,
              target: '_blank'
            },
            {
              name: 'Raporty',
              onClick: () => props.history.push('/reports'),
              url: '',
              key: 'key4',
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
