import React, { useReducer } from "react";
import { RouteComponentProps } from "react-router-dom";
import { useListCustomers } from "../../api/useListCustomers";
import { routing } from "../../Routing";
import { initialState, reducer } from "../../store/reducers";
import { CustomersView } from "./Customers";


interface CustomersProps extends RouteComponentProps {
}

const CustomersRoutedView: React.FC<CustomersProps> = (props) => {
    const [state, dispatch] = useReducer(reducer, initialState);
    const projectId = state.appState.projectId;
    const newCustomerCommand = () => props.history.push(routing.newCustomer);
    return (<CustomersView givenProjectId={projectId}
        onNewClientCommand={newCustomerCommand}
        listCustomers={useListCustomers} />);
}



export const CustomersRoutedConnectedView = CustomersRoutedView;