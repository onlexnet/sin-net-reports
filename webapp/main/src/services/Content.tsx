import * as React from "react";
import { DetailsList, DetailsListLayoutMode, SelectionMode, IColumn, mergeStyleSets, MaskedTextField, FocusTrapZone, PrimaryButton, DefaultButton, memoizeFunction, IStackStyles, ComboBox, SelectableOptionMenuItemType, IComboBoxOption, IComboBox } from "office-ui-fabric-react";
import { IStackTokens, Stack, TextField, Toggle, Announced } from "office-ui-fabric-react";
import { connect, ConnectedProps } from "react-redux";
import { RootState } from "../store/reducers";
import { ServiceAppModel } from "../store/actions/ServiceModel";
import { KeyboardEventHandler, useCallback, useEffect, useState } from "react";
import { LocalDate, TimePeriod } from "../store/viewcontext/TimePeriod";
import { HorizontalSeparatorStack } from "../Components/HorizontalSeparatorStack";
import { dates } from "../api/DtoMapper";
import { useGetUsersQuery } from "../Components/.generated/components";
import _ from "lodash";

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
  return { ...state.services, ...state.viewContext };
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
  const { items } = props;
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
              <ItemEdit item={selectedItem} />
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

//   private _getKey(item: any, index ?: number): string {
//   return item.key;
// }

//   private _onChangeModalSelection = (ev: React.MouseEvent<HTMLElement>, checked?: boolean): void => {
//   this.setState({ isModalSelection: checked ?? false });
// };

//   private _onChangeText = (ev: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, text?: string): void => {
//   this.setState({
//     items: text ? this._allItems.filter(i => i.customer.toLowerCase().indexOf(text) > -1) : this._allItems
//   });
// };

//   private _onChangeDay = (ev: React.FormEvent, text?: string): void => {
//   this.setState({
//     items: text ? this._allItems.filter(it => it.dateModified.includes(text)) : this._allItems
//   });
// };

//   private _onItemInvoked(item: any): void {
//   alert(`Item invoked: ${item.name}`);
//   }

// const newItems = _copyAndSort(items, currColumn.fieldName!, currColumn.isSortedDescending);
// this.setState({
//   columns: newColumns,
//   items: newItems
// });
// };

function _copyAndSort<T>(items: T[], columnKey: string, isSortedDescending?: boolean): T[] {
  const key = columnKey as keyof T;
  return items.slice(0).sort((a: T, b: T) => ((isSortedDescending ? a[key] < b[key] : a[key] > b[key]) ? 1 : -1));
}

export const Content = connector(ConnectedContent);

const ItemEdit: React.FC<{ item: ServiceAppModel | null }> = props => {

  const { item } = props;
  // force to rerender if parent is changing
  const { loading, error, data } = useGetUsersQuery();


  const defaultServicemanName = item?.servicemanName;
  const [servicemanName, setServicemanName] = useState(defaultServicemanName);
  useEffect(() => {
    setServicemanName(defaultServicemanName);
  }, [defaultServicemanName]);
  const onChangeServicemanName = useCallback(
    (ev: React.FormEvent<IComboBox>, option?: IComboBoxOption) => {
      const a = option?.key as string;
      setServicemanName(a);
    },
    [setServicemanName],
  );

  const defaultDate = dates(item?.when).toRawValue;
  const [date, setDate] = useState<string>(defaultDate);
  useEffect(() => {
    setDate(defaultDate);
  }, [defaultDate]);
  const onChangeDate = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      if (!newValue) return;
      setDate(newValue);
    },
    [],
  );

  const defaultCustomerName = item?.customerName;
  const [customerName, setCustomerName] = useState(defaultCustomerName);
  useEffect(() => {
    setCustomerName(defaultCustomerName);
  }, [defaultCustomerName]);
  const onChangeCustomerName = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setCustomerName(newValue || '');
    },
    [],
  );

  const propsDescription = item?.description;
  const [description, setDescription] = useState(propsDescription);
  useEffect(() => {
    setDescription(propsDescription)
  }, [propsDescription]);
  const onChangeDescription = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setDescription(newValue || '');
    },
    [],
  );

  const propsDuration = "" + item?.duration;
  const [duration, setDuration] = useState(propsDuration);
  useEffect(() => {
    setDuration(propsDuration)
  }, [propsDuration]);
  const onChangeDuration = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setDuration(newValue ?? "0");
    },
    [],
  );

  const propsDistance = "" + item?.distance;
  const [distance, setDistance] = useState(propsDistance);
  useEffect(() => {
    setDistance(propsDistance)
  }, [propsDistance]);
  const onChangeDistance = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setDistance(newValue ?? "0");
    },
    [],
  );

  const [disabledFocusTrap, setDisabledFocusTrap] = useState(false);
  useEffect(() => {
    setDisabledFocusTrap(false);
  }, [props.item?.entityId]);

  if (!item) return (
    <p>
      Empty data
    </p>
  )

  const onKeyUp: KeyboardEventHandler<any> = (event) => {
    if (event.charCode !== 13) return;
    setDisabledFocusTrap(true);
  }

  const getStackStyles = memoizeFunction(
    (useTrapZone: boolean): Partial<IStackStyles> => ({
      root: { border: `2px solid ${useTrapZone ? '#ababab' : 'transparent'}`, padding: 10 }
    })
  );
  const stackTokens = { childrenGap: 8 }

  let comboBoxBasicOptions: IComboBoxOption[] = [];
  if (data) {
    comboBoxBasicOptions = _.chain(data.Users?.search)
      .map(it => ({ key: it.email, text: it.email}))
      .value();
  }

  return (
    <>
      <FocusTrapZone disabled={disabledFocusTrap}>
        <Stack tokens={stackTokens} styles={getStackStyles(!disabledFocusTrap)}>
          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <ComboBox label="Pracownik" selectedKey={servicemanName} options={comboBoxBasicOptions} autoComplete="on" onChange={onChangeServicemanName}
                onKeyDown={onKeyUp}
              />
            </div>
            <div className="ms-Grid-col ms-sm2">
              <MaskedTextField label="Data" value={date} onChange={onChangeDate} mask="9999/99/99"
                onKeyDown={onKeyUp}
              />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <TextField label="Klient" value={customerName} onChange={onChangeCustomerName}
                onKeyDown={onKeyUp}
              />
            </div>
            <div className="ms-Grid-col ms-sm2">
              <TextField label="Usługa" value={description} onChange={onChangeDescription}
                onKeyDown={onKeyUp}
              />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <TextField label="Czas" value={duration} onChange={onChangeDuration}
                onKeyDown={onKeyUp}
              />
            </div>
            <div className="ms-Grid-col ms-sm2">
              <TextField label="Dojazd" value={distance} onChange={onChangeDistance}
                onKeyDown={onKeyUp}
              />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm12">
              <Stack horizontal tokens={stackTokens}>
                <PrimaryButton onClick={() => setDisabledFocusTrap(true)} />
                <DefaultButton onClick={() => setDisabledFocusTrap(true)} />
              </Stack>
            </div>
          </div>
        </Stack>
      </FocusTrapZone>
      <p>

        {JSON.stringify(item)}
      </p>
    </>

  );
}