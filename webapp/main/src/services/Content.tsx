import * as React from "react";
import { DetailsList, DetailsListLayoutMode, SelectionMode, IColumn, mergeStyleSets } from "office-ui-fabric-react";
import { IStackTokens, Stack, TextField, Toggle, Announced } from "office-ui-fabric-react";
import { initialState } from "../reduxStore";
import { connect } from "react-redux";
import { _generateDocuments } from "./DummyData";
import { Dispatch } from "redux";

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

export interface IDocument {
  key: string;
  customer: string;
  description: string;
  modifiedBy: string;
  dateModified: string;
  dateModifiedValue: Date;
  duration: number;
  distance: number;
}

export interface TypedColumn extends IColumn {
  fieldName: keyof IDocument;
}

export interface ContentProps {
  items: IDocument[]
}

const ContentView: React.FC<ContentProps> = props => {

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
      fieldName: "modifiedBy",
      minWidth: 70,
      maxWidth: 90,
      isResizable: true,
      isCollapsible: true,
      data: "string",
      onColumnClick: (ev, col) => _onColumnClick(col, state.columns),
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
      onColumnClick: (ev, col) => _onColumnClick(col, state.columns),
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

  const [state, setState] = React.useState({
    columns: initialColumns,
    isModalSelection: false,
    announcedMessage: ""
  });

  const { columns, announcedMessage } = state;
  const { items } = props;
  const stackTokens: IStackTokens = { childrenGap: 40 };

  return (
    <>
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
            {/* <TextField label="Tylko dzień:" onChange={this._onChangeDay} styles={controlStyles} />
            <TextField label="Kontrahent:" onChange={this._onChangeText} styles={controlStyles} /> */}
            <TextField label="Tylko dzień:" styles={controlStyles} />
            <TextField label="Kontrahent:" styles={controlStyles} />
          </Stack>
        </Stack>
      </div>
      {announcedMessage ? <Announced message={announcedMessage} /> : undefined}

      <DetailsList
        items={items}
        compact={true}
        columns={columns}
        selectionMode={SelectionMode.none}
        //getKey={this._getKey}
        setKey="none"
        layoutMode={DetailsListLayoutMode.justified}
        isHeaderVisible={true}
        // onItemInvoked={this._onItemInvoked}
      />
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

const mapStateToProps = (state: typeof initialState)  => {
  const items = _generateDocuments(state.fakeRows);
  return { 
    articles: state.fakeRows,
    items: items
  };
};
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    onTodoClick: (id: string) => {
    }
  }
}

export const Content = connect(mapStateToProps, mapDispatchToProps)(ContentView)


function _copyAndSort<T>(items: T[], columnKey: string, isSortedDescending?: boolean): T[] {
  const key = columnKey as keyof T;
  return items.slice(0).sort((a: T, b: T) => ((isSortedDescending ? a[key] < b[key] : a[key] > b[key]) ? 1 : -1));
}

