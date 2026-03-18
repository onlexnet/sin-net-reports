import * as React from 'react';
import { RouteComponentProps } from "react-router-dom";
import { routing } from './Routing';
import { Home, User, Bug } from 'lucide-react';
import { cn } from 'lib/utils';

interface NavBarProps extends RouteComponentProps<any> { }

export const NavBar: React.FC<NavBarProps> = (props) => {
  const navItems = [
    { key: 'home', label: 'Usługi', icon: <Home className="h-4 w-4" />, route: routing.actions },
    { key: 'customers', label: 'Klienci', icon: <User className="h-4 w-4" />, route: routing.customers },
    { key: 'debug', label: 'Debug', icon: <Bug className="h-4 w-4" />, route: routing.debug },
  ];

  return (
    <div style={{ height: '100vh', backgroundColor: '#001529', color: '#fff' }}>
      <nav className="flex flex-col gap-1 p-2">
        {navItems.map(item => (
          <button
            key={item.key}
            onClick={() => props.history.push(item.route)}
            className={cn(
              "flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium text-white",
              "hover:bg-white/10 transition-colors text-left w-full"
            )}
          >
            {item.icon}
            {item.label}
          </button>
        ))}
      </nav>
    </div>
  );
};

