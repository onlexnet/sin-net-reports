import React from "react"
import { v4 as uuid } from 'uuid';
import { EntityId } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { CustomerView } from "./CustomerView";


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
  
interface CustomerViewNewProps extends PropsFromRedux {
}

  
export const CustomerViewNewLocal: React.FC<CustomerViewNewProps> = props => {
    const id: EntityId = {
        projectId: props.appState.projectId,
        entityId: uuid(),
        entityVersion: 1
    }
    return <CustomerView id={id}/>;
}

export const CustomerViewNew = connect(mapStateToProps, mapDispatchToProps)(CustomerViewNewLocal);