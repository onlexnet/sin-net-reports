import React, { useReducer } from "react"
import { EntityId } from "../../store/actions/ServiceModel";
import { initialState, reducer } from "../../store/reducers";
import { CustomerView, CustomerViewEntry } from "./CustomerView";
import { RouteComponentProps } from "react-router-dom";
import { useReserveCustomerMutation } from "../../Components/.generated/components";
import { routing } from "../../Routing";


interface CustomerViewNewProps extends RouteComponentProps  {
}
  
export const CustomerViewNewLocal: React.FC<CustomerViewNewProps> = props => {
    const [state, dispatch] = useReducer(reducer, initialState);
    const [reserveCustomerMutation, { data, called}] = useReserveCustomerMutation();

    if (!called) {
        reserveCustomerMutation({
            variables: {
                projectId: state.appState.projectId
            }
        });
    }

    if (data) {
        const id: EntityId = data.Customers.reserve;
        const entry: CustomerViewEntry = {
            KlientNazwa: 'Nowy klient',
            autoryzacje: [],
            autoryzacjeEx: [],
            kontakty: []
        }
        const itemSaved = () => props.history.push(routing.customers);
        const itemRemoved = () => props.history.push(routing.customers);
        return <CustomerView id={id} entry={entry} itemSaved={itemSaved} itemRemoved={itemRemoved} />;
    }

    return null;
}

export const CustomerViewNew = CustomerViewNewLocal;