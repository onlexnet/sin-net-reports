import React from "react"
import { EntityId } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { CustomerView } from "./CustomerView";
import { useGetCustomerQuery } from "../../Components/.generated/components";


const mapStateToProps = (state: RootState) => {
    if (state.appState.empty) {
        throw 'Invalid state';
    }
    return state;
}

const mapDispatchToProps = (dispatch: Dispatch) => {
    return { }
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;
  
interface CustomerViewEditProps extends PropsFromRedux {
    id: EntityId,
    itemSaved: () => void
}

  
export const CustomerViewEditLocal: React.FC<CustomerViewEditProps> = props => {
    const { data, error } = useGetCustomerQuery({
        variables: {
            projectId: props.appState.projectId,
            id: props.id
        }
    })
    
    if(error) {
        return <div>Error: {JSON.stringify(error)}</div>;
    }

    if (data) {
        const id = data.Customers.get?.id
        const input = data.Customers.get;
        if (!id) {
            return <div>No data</div>;
        }
        const entry = {
            KlientNazwa: input?.data.customerName ?? 'Nowy klient'
        }
        return <CustomerView id={id} entry={entry} itemSaved={props.itemSaved}/>;
    }

    return <div>Loading customer details...</div>
}

export const CustomerViewEdit = connect(mapStateToProps, mapDispatchToProps)(CustomerViewEditLocal);