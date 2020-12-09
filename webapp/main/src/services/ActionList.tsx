import * as React from "react";
import { DetailsList, DetailsListLayoutMode, SelectionMode, Selection, IColumn, mergeStyleSets, DefaultButton, setLanguage } from "office-ui-fabric-react";
import { IStackTokens, Stack, TextField, Toggle, Announced } from "office-ui-fabric-react";
import { connect, ConnectedProps } from "react-redux";
import { RootState } from "../store/reducers";
import { ServiceAppModel } from "../store/actions/ServiceModel";
import { useState } from "react";
import { TimePeriod } from "../store/viewcontext/TimePeriod";
import { HorizontalSeparatorStack } from "../Components/HorizontalSeparatorStack";
import { toActionModel } from "../api/DtoMapper";
import { useFetchServicesQuery } from "../Components/.generated/components";
import _ from "lodash";
import { asDtoDates } from "../api/Mapper";
import { Dispatch } from "redux";
import { ActionEditItem, VIEWCONTEXT_ACTION_EDIT_START } from "../store/viewcontext/types";
import { Duration } from "./ActionList.Duration";

const classNames = mergeStyleSets({
  fileIconHeaderIcon: {
    padding: 0,
    fontSize: "16px"
  },
  fileIconCell: {
    textAlign: "center",
    selectors: {
      "&:before": {
        content: ".",
        display: "inline-block",
        verticalAlign: "middle",
        height: "100%",
        width: "0px",
        visibility: "hidden"
      }
    }
  },
  fileIconImg: {
    verticalAlign: "middle",
    maxHeight: "16px",
    maxWidth: "16px"
  },
  controlWrapper: {
    display: "flex",
    flexWrap: "wrap"
  },
  exampleToggle: {
    display: "inline-block",
    marginBottom: "10px",
    marginRight: "30px"
  },
  selectionDetails: {
    marginBottom: "20px"
  }
});
const controlStyles = {
  root: {
    margin: "0 30px 20px 0",
    maxWidth: "300px"
  }
};

export interface IContentState {
  columns: IColumn[];
  items: IDocument[];
  isModalSelection: boolean;
  announcedMessage?: string;
}

export interface IDocument extends ServiceAppModel {
}

export interface TypedColumn extends IColumn {
  fieldName: keyof IDocument;
}

export interface ContentProps {
}

const mapStateToProps = (state: RootState) => {
  if (state.appState.empty) {
    throw 'Invalid state';
  }
  return { ...state.viewContext, selectedProjectId: state.appState.projectId };
};
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    editItem: (actionEntityId: string) => {
      var action: ActionEditItem = {
        type: VIEWCONTEXT_ACTION_EDIT_START,
        payload: { actionEntityId }
      }
      dispatch(action);
    }
  }
}
const connector = connect(mapStateToProps, mapDispatchToProps);


type PropsFromRedux = ConnectedProps<typeof connector>

const ConnectedContent: React.FC<PropsFromRedux> = props => {

  const initialColumns: TypedColumn[] = [
    {
      key: "column4", name: "Pracownik", fieldName: "servicemanName", minWidth: 70, maxWidth: 90, isResizable: true, isCollapsible: true, data: "string",
      onRender: (item: IDocument) => {
        return <span>{item.servicemanName}</span>;
      },
      isPadded: true
    },
    {
      key: "column3", name: "Data", fieldName: "when", minWidth: 70, maxWidth: 90, isResizable: true,
      data: "date",
      onRender: (item: IDocument) => {
        const { year, month, day } = item.when;
        return <span>{`${year}-${month}-${day}`}</span>;
      },
      isPadded: true
    },
    {
      key: "column2", name: "Klient", fieldName: "customerName", minWidth: 210, maxWidth: 350, isRowHeader: true, isResizable: true,
      data: "string",
      isPadded: true
    },
    {
      key: "column6", name: "Usługa", fieldName: "description", minWidth: 300, maxWidth: 300, isResizable: true, isCollapsible: true,
      data: "number",
    },
    {
      key: "column5", name: "Czas", fieldName: "duration", minWidth: 70, maxWidth: 90, isResizable: true, isCollapsible: true, data: "number",
      onRender: (item: IDocument) => <Duration duration={item.duration} />
    },
    {
      key: "column7", name: "Dojazd", fieldName: "distance", minWidth: 70, maxWidth: 90, isResizable: true, isCollapsible: true, data: "number",
      onRender: (item: IDocument) => {
        return <span>{item.distance}</span>;
      }
    }
  ];

  const [state, setState] = useState({
    columns: initialColumns,
    isModalSelection: false,
    announcedMessage: ""
  });
  const { columns, announcedMessage } = state;

  const [lastTouchedActionId, setlastTouchedActionId] = useState(props.lastTouchedActionId);

  const newDataIsComing = props.lastTouchedActionId != null && props.lastTouchedActionId != lastTouchedActionId;
  const firstTimeLoading = props.lastTouchedActionId == null && lastTouchedActionId == null;
  console.log(`props.lastTouchedActionId:${JSON.stringify(props.lastTouchedActionId)}`)
  if (newDataIsComing) {
    console.log('newDataIsComing');
    setlastTouchedActionId(props.lastTouchedActionId);
  }

  const periodDto = asDtoDates(props.period);
  const { data, loading, error, refetch } = useFetchServicesQuery({
    variables: {
      projectId: props.selectedProjectId,
      from: periodDto.dateFrom,
      to: periodDto.dateTo
    }
  });
  if (newDataIsComing) {
    refetch();
  }

  var itemsOrderBy: (keyof IDocument)[] = ['servicemanName', 'when', 'entityId'];
  var items: ServiceAppModel[] = _.chain(data?.Actions.search.items)
    .map(it => toActionModel(it))
    .orderBy(itemsOrderBy)
    .value();

  const stackTokens: IStackTokens = { childrenGap: 40 };



  const [currentPeriod, setCurrentPeriod] = useState<TimePeriod | null>(null);
  if (currentPeriod != props.period) {
    setCurrentPeriod(props.period);
    // setSelectedItem(null)
  }

  const getSelectionDetails = () => {
    const selectionCount = selection.getSelectedCount();

    switch (selectionCount) {
      case 0:
        return null;
      case 1:
        return selection.getSelection()[0] as ServiceAppModel;
      default:
        return null;
    }
  }

  const [selectionDetails, setSelectionDetails] = useState<ServiceAppModel | null>(null);
  const selection = new Selection({
    onSelectionChanged: () => {
      const selectedModel = getSelectionDetails();
      setSelectionDetails(selectedModel);
    },
  });

  return (
    <>

      <div className="ms-Grid">
        <HorizontalSeparatorStack >
          {/* <Separator alignContent="start">Dane ogólne: </Separator> */}

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm12">
              <div className={classNames.controlWrapper}>
                <Stack>
                  <Stack horizontal tokens={stackTokens}>
                    <Toggle
                      label="Tylko moje dane"
                      checked={false}
                      onText="Compact"
                      offText="Normal"
                      styles={controlStyles}
                    />
                    <TextField label="Tylko dzień:" styles={controlStyles} />
                    <TextField label="Kontrahent:" styles={controlStyles} />
                    <DefaultButton text="Edytuj ..." onClick={() => {
                      props.editItem(selectionDetails?.entityId ?? "undefined [1]");
                    }} />
                  </Stack>
                </Stack>
              </div>
              {announcedMessage ? <Announced message={announcedMessage} /> : undefined}
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm12">
              <DetailsList
                items={items}
                compact={true}
                columns={columns}
                selectionMode={SelectionMode.single}
                selection={selection}
                setKey="none"
                layoutMode={DetailsListLayoutMode.justified}
                isHeaderVisible={true}
              />
            </div>
          </div>

        </HorizontalSeparatorStack>
      </div>

    </>
  );
}

export const Content = connector(ConnectedContent);
