import React from "react"
import { v4 as uuid } from 'uuid';
import { EntityId } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { CustomerView } from "./CustomerView";
import { RouteComponentProps } from "react-router-dom";
import { useReserveCustomerMutation } from "../../Components/.generated/components";


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
  
interface CustomerViewNewProps extends PropsFromRedux, RouteComponentProps  {
}

  
export const CustomerViewNewLocal: React.FC<CustomerViewNewProps> = props => {
    const [reserveCustomerMutation, { data, called}] = useReserveCustomerMutation();

    if (!called) {
        reserveCustomerMutation({
            variables: {
                projectId: props.appState.projectId
            }
        });
    }

    if (data) {
        const id: EntityId = data.Customers.reserve;
        const entry = {
            KlientNazwa: 'Nowy klient'
        }
        const itemSaved = () => props.history.goBack();
        return <CustomerView id={id} entry={entry} itemSaved={itemSaved}/>;
    }

    return null;
}

export const CustomerViewNew = connect(mapStateToProps, mapDispatchToProps)(CustomerViewNewLocal);