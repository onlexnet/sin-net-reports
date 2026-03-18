import * as React from 'react';
import { RouteComponentProps } from "react-router-dom";
import { routing } from './Routing';
import { Menu } from 'antd';
import { HomeOutlined, UserOutlined, BugOutlined } from '@ant-design/icons';

interface NavBarProps extends RouteComponentProps<any> { }

export const NavBar: React.FC<NavBarProps> = (props) => {
  return (
    <div style={{ height: '100vh', backgroundColor: '#001529', color: '#fff' }}>
      <Menu
        mode='vertical'
        theme='light'
        style={{ height: '100%', borderRight: 0 }}
      >
        <Menu.Item key='home' icon={<HomeOutlined />} onClick={() => props.history.push(routing.actions)}>Usługi</Menu.Item>
        <Menu.Item key='customers' icon={<UserOutlined />} onClick={() => props.history.push(routing.customers)}>Klienci</Menu.Item>
        <Menu.Item key='debug' icon={<BugOutlined />} onClick={() => props.history.push(routing.debug)}>Debug</Menu.Item>
      </Menu>
    </div>
  );
};

