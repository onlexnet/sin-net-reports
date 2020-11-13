import _ from "lodash";
import React, { useEffect, useState } from "react";
import { connect, ConnectedProps } from "react-redux";
import { Dispatch } from "redux";
import { toActionModel } from "../api/DtoMapper";
import { useGetActionQuery } from "../Components/.generated/components";
import { RootState } from "../store/reducers";
import { ActionEditCancel, ActionEditUpdated, VIEWCONTEXT_ACTION_EDIT_CANCEL, VIEWCONTEXT_ACTION_EDIT_UPDATED } from "../store/viewcontext/types";
import { EditActionSome } from "./EditAction.Some";
import { EditActionNone } from "./EditAction.None";
import { EntityId } from "../store/actions/ServiceModel";

const mapStateToProps = (state: RootState) => {
  return { ...state.viewContext };
};
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    cancelEdit: () => {
      var action: ActionEditCancel = {
        type: VIEWCONTEXT_ACTION_EDIT_CANCEL,
        payload: {}
      }
      dispatch(action);
    },
    actionUpdated: (item: EntityId) => {
      var action: ActionEditUpdated = {
        type: VIEWCONTEXT_ACTION_EDIT_UPDATED,
        payload: {
          lastTouchedEntityId: item
        }
      }
      dispatch(action)
    }
  }
}
const connector = connect(mapStateToProps, mapDispatchToProps);

type PropsFromRedux = ConnectedProps<typeof connector>

/**
 * Place to take decision what state visualisation should be displayed based on give action id.
 * @param props 
 */
const EditActionInit: React.FC<PropsFromRedux> = props => {

  const isEditInProgress =  'editedActionId' in props;
  const { loading, data } = useGetActionQuery({
    skip: !isEditInProgress,
    variables: {
      actionId: props.editedActionId ?? "00000000-0000-0000-0000-000000000000"
    }
  });

  const propsEditedActionId = props.editedActionId;
  const [editedActionId, setEditedActionId] = useState(propsEditedActionId);
  useEffect(() => {
    setEditedActionId(propsEditedActionId)
  }, [propsEditedActionId])


  if (propsEditedActionId && data) {
    var dto = data.Services.get;
    if (dto) {
      var model = toActionModel(dto)
      return <EditActionSome item={model} cancelEdit={props.cancelEdit} actionUpdated={props.actionUpdated} />;
    }
  }

  return <EditActionNone />
}

export default connector(EditActionInit);
