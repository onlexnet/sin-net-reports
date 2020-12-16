import React from "react"
import { v4 as uuid } from 'uuid';
import { EntityId } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { CustomerView } from "./CustomerView";
import { RouteComponentProps } from "react-router-dom";


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
    const id: EntityId = {
        projectId: props.appState.projectId,
        entityId: uuid(),
        entityVersion: 1
    }
    const entry = {
        KlientNazwa: 'Nowy klient'
    }
    const itemSaved = () => props.history.goBack();
    return <CustomerView id={id} entry={entry} itemSaved={itemSaved}/>;
}

export const CustomerViewNew = connect(mapStateToProps, mapDispatchToProps)(CustomerViewNewLocal);