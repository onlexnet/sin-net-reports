import * as React from 'react';
import { RouteComponentProps } from "react-router-dom";
import { routing } from './Routing';
import _ from 'lodash';
import { Menu } from 'antd';

interface NavBarProps extends RouteComponentProps<any> { }

export const NavBar: React.FC<NavBarProps> = (props) => {
  return (
    <Menu mode='horizontal'>
      <Menu.Item key='home' onClick={() => props.history.push(routing.actions)}>Usługi</Menu.Item>
      <Menu.Item key='customers' onClick={() => props.history.push(routing.customers)}>Klienci</Menu.Item>
      <Menu.Item key='debug' onClick={() => props.history.push(routing.debug)}>Debug</Menu.Item>
    </Menu>
  );
};

