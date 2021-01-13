import * as React from "react";
import { DetailsList, DetailsListLayoutMode, SelectionMode, Selection, IColumn, mergeStyleSets, DefaultButton } from "office-ui-fabric-react";
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
import { Link } from "react-router-dom";
import { LocalDateView } from "../app/LocalDateView";

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
  return { ...state.viewContext, selectedProjectId: state.appState.projectId, currentEmail: state.auth.email };
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
        return <Link to={`/actions/${item.projectId}/${item.entityId}/${item.entityVersion}`}>{item.servicemanName}</Link>;
      },
      isPadded: true
    },
    {
      key: "column3", name: "Data", fieldName: "when", minWidth: 70, maxWidth: 90, isResizable: true,
      data: "date",
      onRender: (item: IDocument) => {
        return <LocalDateView item={item.when} />
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

  const [onlyMyData, setOnlyMyData] = useState(false);
  const filterByOnlyMyData = (item: ServiceAppModel): boolean => {
    if (!onlyMyData) return true;
    if (item.servicemanName === props.currentEmail) return true;
    return false;
  }

  const [onlyDay, setOnlyDay] = useState<string | undefined>();
  const filterByOnlyDay = (item: ServiceAppModel): boolean => {
    if (!onlyDay) return true;
    if (item.when.day.toString().includes(onlyDay)) return true;
    return false;
  }

  const [onlyCustomer, setOnlyCustomer] = useState<string | undefined>();
  const filterByOnlyCustomer = (item: ServiceAppModel): boolean => {
    if (!onlyCustomer) return true;
    if (!item.customerName) return false;
    if (item.customerName?.toLowerCase().includes(onlyCustomer.toLowerCase())) return true;
    return false;
  }

  var itemsOrderBy: (keyof IDocument)[] = ['servicemanName', 'when', 'entityId'];
  var items: ServiceAppModel[] = _.chain(data?.Actions.search.items)
    .map(it => toActionModel(it))
    .filter(it => filterByOnlyMyData(it))
    .filter(it => filterByOnlyDay(it))
    .filter(it => filterByOnlyCustomer(it))
    .orderBy(itemsOrderBy)
    .value();
  const fold = (reducer: (acc: number, it: number) => number, init: number, xs: number[]) => {
    let acc = init;
    for (const x of xs) {
      acc = reducer(acc, x);
    }
    return acc;
  };
  const totalTime = () => {
    const duration = fold((acc, it) => acc + it, 0, items.map(it => it.duration ?? 0));
    var hours = Math.floor(duration / 60);
    var minutes = duration - hours * 60;
    return hours + ':' + ('00' + minutes).substr(-2);
  };

  const stackTokens: IStackTokens = { childrenGap: 40 };



  const [currentPeriod, setCurrentPeriod] = useState<TimePeriod | null>(null);
  if (currentPeriod != props.period) {
    setCurrentPeriod(props.period);
  }

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
                      checked={onlyMyData}
                      onChange={(e, v) => setOnlyMyData(v ?? false)}
                      onText="Compact"
                      offText="Normal"
                      styles={controlStyles}
                    />
                    <TextField label="Tylko dzień:" value={onlyDay} onChange={(e, v) => setOnlyDay(v)} styles={controlStyles} />
                    <TextField label="Kontrahent:" styles={controlStyles} value={onlyCustomer} onChange={(e, v) => setOnlyCustomer(v)} />
                    <TextField disabled label="Suma godzin" value={totalTime()} />
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
                selectionMode={SelectionMode.none}
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
