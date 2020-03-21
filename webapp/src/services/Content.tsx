import * as React from "react";
import { TextField } from "office-ui-fabric-react/lib/TextField";
import { Toggle } from "office-ui-fabric-react/lib/Toggle";
import { Fabric } from "office-ui-fabric-react/lib/Fabric";
import { Announced } from "office-ui-fabric-react/lib/Announced";
import {
  DetailsList,
  DetailsListLayoutMode,
  Selection,
  SelectionMode,
  IColumn
} from "office-ui-fabric-react/lib/DetailsList";
import { MarqueeSelection } from "office-ui-fabric-react/lib/MarqueeSelection";
import { mergeStyleSets } from "office-ui-fabric-react/lib/Styling";
import {
  _randomDate,
  _randomEmployeeName,
  _randomCustomerName,
  _randomDuration,
  _randomDistance,
  _dandomServiceDescription
} from "./DummyData";

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

export interface IDetailsListDocumentsExampleState {
  columns: IColumn[];
  items: IDocument[];
  selectionDetails: string;
  isModalSelection: boolean;
  announcedMessage?: string;
}

export interface IDocument {
  key: string;
  customer: string;
  description: string;
  modifiedBy: string;
  dateModified: string;
  dateModifiedValue: number;
  duration: number;
  distance: number;
}

export interface TypedColumn extends IColumn {
  fieldName: keyof IDocument;
}

export class Content extends React.Component<
  {},
  IDetailsListDocumentsExampleState
> {
  private _selection: Selection;
  private _allItems: IDocument[];

  constructor(props: {}) {
    super(props);

    this._allItems = _generateDocuments();

    const columns: TypedColumn[] = [
      {
        key: "column4",
        name: "Pracownik",
        fieldName: "modifiedBy",
        minWidth: 70,
        maxWidth: 90,
        isResizable: true,
        isCollapsible: true,
        data: "string",
        onColumnClick: this._onColumnClick,
        onRender: (item: IDocument) => {
          return <span>{item.modifiedBy}</span>;
        },
        isPadded: true
      },
      {
        key: "column3",
        name: "Data",
        fieldName: "dateModifiedValue",
        minWidth: 70,
        maxWidth: 90,
        isResizable: true,
        onColumnClick: this._onColumnClick,
        data: "number",
        onRender: (item: IDocument) => {
          return <span>{item.dateModified}</span>;
        },
        isPadded: true
      },
      {
        key: "column2",
        name: "Klient",
        fieldName: "customer",
        minWidth: 210,
        maxWidth: 350,
        isRowHeader: true,
        isResizable: true,
        isSorted: true,
        isSortedDescending: false,
        sortAscendingAriaLabel: "Sorted A to Z",
        sortDescendingAriaLabel: "Sorted Z to A",
        onColumnClick: this._onColumnClick,
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
        onColumnClick: this._onColumnClick
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
        onColumnClick: this._onColumnClick,
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
        onColumnClick: this._onColumnClick,
        onRender: (item: IDocument) => {
          return <span>{item.distance}</span>;
        }
      }
    ];

    this._selection = new Selection({
      onSelectionChanged: () => {
        this.setState({
          selectionDetails: this._getSelectionDetails()
        });
      }
    });

    this.state = {
      items: this._allItems,
      columns: columns,
      selectionDetails: this._getSelectionDetails(),
      isModalSelection: false,
      announcedMessage: undefined
    };
  }

  public render() {
    const {
      columns,
      items,
      selectionDetails,
      isModalSelection,
      announcedMessage
    } = this.state;

    return (
      <Fabric>
        <div className={classNames.controlWrapper}>
          <Toggle
            label="Tylko moje dane"
            checked={false}
            onChange={this._onChangeCompactMode}
            onText="Compact"
            offText="Normal"
            styles={controlStyles}
          />
          <Toggle
            label="Enable modal selection"
            checked={isModalSelection}
            onChange={this._onChangeModalSelection}
            onText="Modal"
            offText="Normal"
            styles={controlStyles}
          />
          <TextField
            label="Filter by name:"
            onChange={this._onChangeText}
            styles={controlStyles}
          />
          <Announced
            message={`Number of items after filter applied: ${items.length}.`}
          />
        </div>
        <div className={classNames.selectionDetails}>{selectionDetails}</div>
        <Announced message={selectionDetails} />
        {announcedMessage ? (
          <Announced message={announcedMessage} />
        ) : (
          undefined
        )}
        {isModalSelection ? (
          <MarqueeSelection selection={this._selection}>
            <DetailsList
              items={items}
              compact={true}
              columns={columns}
              selectionMode={SelectionMode.multiple}
              getKey={this._getKey}
              setKey="multiple"
              layoutMode={DetailsListLayoutMode.justified}
              isHeaderVisible={true}
              selection={this._selection}
              selectionPreservedOnEmptyClick={true}
              onItemInvoked={this._onItemInvoked}
              enterModalSelectionOnTouch={true}
              ariaLabelForSelectionColumn="Toggle selection"
              ariaLabelForSelectAllCheckbox="Toggle selection for all items"
              checkButtonAriaLabel="Row checkbox"
            />
          </MarqueeSelection>
        ) : (
          <DetailsList
            items={items}
            compact={true}
            columns={columns}
            selectionMode={SelectionMode.none}
            getKey={this._getKey}
            setKey="none"
            layoutMode={DetailsListLayoutMode.justified}
            isHeaderVisible={true}
            onItemInvoked={this._onItemInvoked}
          />
        )}
      </Fabric>
    );
  }

  public componentDidUpdate(
    previousProps: any,
    previousState: IDetailsListDocumentsExampleState
  ) {
    if (
      previousState.isModalSelection !== this.state.isModalSelection &&
      !this.state.isModalSelection
    ) {
      this._selection.setAllSelected(false);
    }
  }

  private _getKey(item: any, index?: number): string {
    return item.key;
  }

  private _onChangeCompactMode = (
    ev: React.MouseEvent<HTMLElement>,
    checked?: boolean
  ): void => {
    // no-op
  };

  private _onChangeModalSelection = (
    ev: React.MouseEvent<HTMLElement>,
    checked?: boolean
  ): void => {
    this.setState({ isModalSelection: checked ?? false });
  };

  private _onChangeText = (
    ev: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>,
    text?: string
  ): void => {
    this.setState({
      items: text
        ? this._allItems.filter(i => i.customer.toLowerCase().indexOf(text) > -1)
        : this._allItems
    });
  };

  private _onItemInvoked(item: any): void {
    alert(`Item invoked: ${item.name}`);
  }

  private _getSelectionDetails(): string {
    const selectionCount = this._selection.getSelectedCount();

    switch (selectionCount) {
      case 0:
        return "No items selected";
      case 1:
        return (
          "1 item selected: " +
          (this._selection.getSelection()[0] as IDocument).customer
        );
      default:
        return `${selectionCount} items selected`;
    }
  }

  private _onColumnClick = (
    ev: React.MouseEvent<HTMLElement>,
    column: IColumn
  ): void => {
    const { columns, items } = this.state;
    const newColumns: IColumn[] = columns.slice();
    const currColumn: IColumn = newColumns.filter(
      currCol => column.key === currCol.key
    )[0];
    newColumns.forEach((newCol: IColumn) => {
      if (newCol === currColumn) {
        currColumn.isSortedDescending = !currColumn.isSortedDescending;
        currColumn.isSorted = true;
        this.setState({
          announcedMessage: `${currColumn.name} is sorted ${
            currColumn.isSortedDescending ? "descending" : "ascending"
          }`
        });
      } else {
        newCol.isSorted = false;
        newCol.isSortedDescending = true;
      }
    });
    const newItems = _copyAndSort(
      items,
      currColumn.fieldName!,
      currColumn.isSortedDescending
    );
    this.setState({
      columns: newColumns,
      items: newItems
    });
  };
}

function _copyAndSort<T>(
  items: T[],
  columnKey: string,
  isSortedDescending?: boolean
): T[] {
  const key = columnKey as keyof T;
  return items
    .slice(0)
    .sort((a: T, b: T) =>
      (isSortedDescending ? a[key] < b[key] : a[key] > b[key]) ? 1 : -1
    );
}

function _generateDocuments() {
  const items: IDocument[] = [];
  for (let i = 0; i < 10; i++) {
    const randomDate = _randomDate(new Date(2012, 0, 1), new Date());
    const randomFileSize = _randomFileSize();
    let fileName = _lorem(2);
    items.push({
      key: i.toString(),
      customer: _randomCustomerName(),
      description: _dandomServiceDescription(),
      modifiedBy: _randomEmployeeName(),
      dateModified: randomDate.dateFormatted,
      dateModifiedValue: randomDate.value,
      duration: _randomDuration(),
      distance: _randomDistance()
    });
  }
  return items;
}

function _randomFileSize(): { value: string; rawSize: number } {
  const fileSize: number = Math.floor(Math.random() * 100) + 30;
  return {
    value: `${fileSize} KB`,
    rawSize: fileSize
  };
}

const LOREM_IPSUM = (
  "lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut " +
  "labore et dolore magna aliqua ut enim ad minim veniam quis nostrud exercitation ullamco laboris nisi ut " +
  "aliquip ex ea commodo consequat duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore " +
  "eu fugiat nulla pariatur excepteur sint occaecat cupidatat non proident sunt in culpa qui officia deserunt "
).split(" ");
let loremIndex = 0;
function _lorem(wordCount: number): string {
  const startIndex =
    loremIndex + wordCount > LOREM_IPSUM.length ? 0 : loremIndex;
  loremIndex = startIndex + wordCount;
  return LOREM_IPSUM.slice(startIndex, loremIndex).join(" ");
}
