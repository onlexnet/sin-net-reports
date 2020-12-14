import React from "react"
import { v4 as uuid } from 'uuid';
import { EntityId } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { CustomerView } from "./CustomerView";
import { RouteComponentProps, useParams } from "react-router-dom";


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
  
interface CustomerViewEditProps extends PropsFromRedux, RouteComponentProps<{ projectId: string, entityId: string, entityVersion: string}> {
}

  
export const CustomerViewEditLocal: React.FC<CustomerViewEditProps> = props => {
//    return <CustomerView id={id} itemSaved={itemSaved}/>;
    debugger;
    const id: EntityId = {
        projectId: props.match.params.projectId,
        entityId: props.match.params.entityId,
        entityVersion: Number(props.match.params.entityVersion)
    };
    return <p>Hello</p>
}

export const CustomerViewEdit = connect(mapStateToProps, mapDispatchToProps)(CustomerViewEditLocal);