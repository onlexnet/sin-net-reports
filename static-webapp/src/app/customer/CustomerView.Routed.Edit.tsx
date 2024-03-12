import React from "react"
import { EntityId } from "../../store/actions/ServiceModel";
import { RouteComponentProps } from "react-router-dom";
import { CustomerViewEdit } from "./CustomerView.Edit";
import { routing } from "../../Routing";


interface CustomerViewEditProps extends RouteComponentProps<{ projectId: string, entityId: string, entityVersion: string}> {
}

  
export const CustomerViewRoutedEditLocal: React.FC<CustomerViewEditProps> = props => {
    const id: EntityId = {
        projectId: props.match.params.projectId,
        entityId: props.match.params.entityId,
        entityVersion: Number(props.match.params.entityVersion)
    };

    const itemSaved = () => props.history.push(routing.customers);
    const itemRemoved = () => props.history.push(routing.customers)
    return <CustomerViewEdit id={id} itemSaved={itemSaved}
                                     itemRemoved={itemRemoved} />;
}

export const CustomerViewRoutedEdit = CustomerViewRoutedEditLocal;