import React, { useState } from "react";
import { TextField, PrimaryButton, Separator, DetailsList, IColumn } from "@fluentui/react";
import { Link, RouteComponentProps } from "react-router-dom";
import { routing } from "../../Routing";
import { HorizontalSeparatorStack } from "../../Components/HorizontalSeparatorStack";
import { useListCustomers, UseListCustomersItem } from "../../api/useListCustomers";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import _ from "lodash";
import { CustomersView } from "./Customers";


const mapStateToProps = (state: RootState) => {
    if (state.appState.empty) {
        throw new Error('Invalid state');
    }
    return state;
}

const mapDispatchToProps = (dispatch: Dispatch) => {
    return {}
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface CustomersProps extends PropsFromRedux, RouteComponentProps {
}

const CustomersRoutedView: React.FC<CustomersProps> = (props) => {
    const projectId = props.appState.projectId;
    const newCustomerCommand = () => props.history.push(routing.newCustomer);
    return (<CustomersView projectId={projectId} onNewClientCommand={newCustomerCommand} />);
}



export const CustomersRoutedConnectedView = connect(mapStateToProps, mapDispatchToProps)(CustomersRoutedView);