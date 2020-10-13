import * as React from "react";
import { DetailsList, DetailsListLayoutMode, SelectionMode, IColumn, mergeStyleSets, MaskedTextField, FocusTrapZone, PrimaryButton, DefaultButton, memoizeFunction, IStackStyles, ComboBox, SelectableOptionMenuItemType, IComboBoxOption, IComboBox } from "office-ui-fabric-react";
import { IStackTokens, Stack, TextField, Toggle, Announced } from "office-ui-fabric-react";
import { connect, ConnectedProps } from "react-redux";
import { RootState } from "../store/reducers";
import { ServiceAppModel } from "../store/actions/ServiceModel";
import { useState } from "react";
import { TimePeriod } from "../store/viewcontext/TimePeriod";
import { HorizontalSeparatorStack } from "../Components/HorizontalSeparatorStack";
import { toActionModel, toModel } from "../api/DtoMapper";
import { useFetchServicesQuery, useGetUsersQuery } from "../Components/.generated/components";
import _ from "lodash";
import { asDtoDates } from "../api/Mapper";
import EditAction from "./EditAction";

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
  return { ...state.viewContext };
};

const connector = connect(mapStateToProps);
type PropsFromRedux = ConnectedProps<typeof connector>

const ConnectedContent: React.FC<ContentProps & PropsFromRedux> = props => {

  const _onColumnClick = (column: IColumn, columns: TypedColumn[]): void => {
    const newColumns: IColumn[] = columns.slice();
    const currColumn: IColumn = newColumns.filter(currCol => column.key === currCol.key)[0];
    newColumns.forEach((newCol: IColumn) => {
      if (newCol === currColumn) {
        currColumn.isSortedDescending = !currColumn.isSortedDescending;
        currColumn.isSorted = true;
        setState({
          ...state,
          announcedMessage: `${currColumn.name} is sorted ${currColumn.isSortedDescending ? "descending" : "ascending"}`
        });
      } else {
        newCol.isSorted = false;
        newCol.isSortedDescending = true;
      }
    })
  };


  const initialColumns: TypedColumn[] = [
    {
      key: "column4",
      name: "Pracownik",
      fieldName: "servicemanName",
      minWidth: 70,
      maxWidth: 90,
      isResizable: true,
      isCollapsible: true,
      data: "string",
      onColumnClick: (ev, col) => _onColumnClick(col, state.columns),
      onRender: (item: IDocument) => {
        return <span>{item.servicemanName}</span>;
      },
      isPadded: true
    },
    {
      key: "column3",
      name: "Data",
      fieldName: "when",
      minWidth: 70,
      maxWidth: 90,
      isResizable: true,
      onColumnClick: (ev, col) => _onColumnClick(col, state.columns),
      data: "date",
      onRender: (item: IDocument) => {
        const { year, month, day } = item.when;
        return <span>{`${year}-${month}-${day}`}</span>;
      },
      isPadded: true
    },
    {
      key: "column2",
      name: "Klient",
      fieldName: "customerName",
      minWidth: 210,
      maxWidth: 350,
      isRowHeader: true,
      isResizable: true,
      isSorted: true,
      isSortedDescending: false,
      sortAscendingAriaLabel: "Sorted A to Z",
      sortDescendingAriaLabel: "Sorted Z to A",
      onColumnClick: (ev, col) => _onColumnClick(col, state.columns),
      data: "string",
      isPadded: true
    },
    {
      key: "column6",
      name: "Usługa",
      fieldName: "description",
      minWidth: 300,
      maxWidth: 300,
      isResizable: true,
      isCollapsible: true,
      data: "number",
      onColumnClick: (ev, col) => _onColumnClick(col, state.columns),
    },
    {
      key: "column5",
      name: "Czas",
      fieldName: "duration",
      minWidth: 70,
      maxWidth: 90,
      isResizable: true,
      isCollapsible: true,
      data: "number",
      onColumnClick: (ev, col) => _onColumnClick(col, state.columns),
      onRender: (item: IDocument) => {
        return <span>{item.duration}</span>;
      }
    },
    {
      key: "column7",
      name: "Dojazd",
      fieldName: "distance",
      minWidth: 70,
      maxWidth: 90,
      isResizable: true,
      isCollapsible: true,
      data: "number",
      onColumnClick: (ev, col) => _onColumnClick(col, state.columns),
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
  const [selectedItem, setSelectedItem] = useState<ServiceAppModel | null>(null);
  const { columns, announcedMessage } = state;
  
  const periodDto = asDtoDates(props.period);
  const { data, loading, error } = useFetchServicesQuery({
    variables: {
      from: periodDto.dateFrom,
      to: periodDto.dateTo
    }
  });
  var items = _.chain(data?.Services.search.items)
    .map(toActionModel).value();

  const stackTokens: IStackTokens = { childrenGap: 40 };



  const [currentPeriod, setCurrentPeriod] = useState<TimePeriod | null>(null);
  if (currentPeriod != props.period) {
    setCurrentPeriod(props.period);
    setSelectedItem(null)
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
                      checked={false}
                      onText="Compact"
                      offText="Normal"
                      styles={controlStyles}
                    />
                    <TextField label="Tylko dzień:" styles={controlStyles} />
                    <TextField label="Kontrahent:" styles={controlStyles} />
                  </Stack>
                </Stack>
              </div>
              {announcedMessage ? <Announced message={announcedMessage} /> : undefined}
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm12">
              <EditAction />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm12">
              <DetailsList
                items={items}
                compact={true}
                columns={columns}
                selectionMode={SelectionMode.none}
                //getKey={this._getKey}
                setKey="none"
                layoutMode={DetailsListLayoutMode.justified}
                isHeaderVisible={true}
                onActiveItemChanged={(item: ServiceAppModel) => {
                  setSelectedItem(item);
                }}
              />
            </div>
          </div>

        </HorizontalSeparatorStack>
      </div>

    </>
  );
}

export const Content = connector(ConnectedContent);
