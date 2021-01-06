import React from "react"
import { EntityId } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { RouteComponentProps } from "react-router-dom";
import { ActionViewEdit } from "./ActionView.Edit";
import { useGetActionQuery } from "../../Components/.generated/components";
import { toActionModel } from "../../api/DtoMapper";


const mapStateToProps = (state: RootState) => {
    if (state.appState.empty) {
        throw 'Invalid state';
    }
    return state;
}

const mapDispatchToProps = (dispatch: Dispatch) => {
    return {}
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface ActionViewEditProps extends PropsFromRedux, RouteComponentProps<{ projectId: string, entityId: string, entityVersion: string }> {
}


export const ActionViewRoutedEditLocal: React.FC<ActionViewEditProps> = props => {
    const projectId = props.match.params.projectId;
    const entityId = props.match.params.entityId;

    const actionUpdated = () => props.history.goBack();
    const actionCancel = () => props.history.goBack();
    const { loading, data } = useGetActionQuery({
        variables: {
            projectId,
            actionId: entityId
        }
    });

    const dto = data?.Actions.get;
    if (dto) {
        var model = toActionModel(dto)
        return <ActionViewEdit item={model}
                               actionUpdated={() => { }}
                               cancelEdit={actionCancel} />;
    };

    return <div>Loading ...</div>
}

export const ActionViewRoutedEdit = connect(mapStateToProps, mapDispatchToProps)(ActionViewRoutedEditLocal);