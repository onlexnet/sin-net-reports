import _ from "lodash";
import * as React from "react";
import { useMemo, useState } from "react";
import { Space } from "components/ui/layout";
import { connect, ConnectedProps } from "react-redux";
import { Link } from "react-router-dom";
import { Dispatch } from "redux";
import { toActionModel } from "../api/DtoMapper";
import { asDtoDates } from "../api/Mapper";
import { LocalDateView } from "../app/LocalDateView";
import { useFetchServicesQuery } from "../components/.generated/components";
import { ServiceAppModel } from "../store/actions/ServiceModel";
import { RootState } from "../store/reducers";
import { TimePeriod } from "../store/viewcontext/TimePeriod";
import { ActionEditItem, VIEWCONTEXT_ACTION_EDIT_START } from "../store/viewcontext/types";
import { Duration } from "./ActionList.Duration";
import { ServiceListModel } from "./ServiceListModel";
import { TanStackTableView, useTanStackTableAdapter, TableAdapterColumn } from "components/table";
import { Button } from "components/ui/button";
import { Input } from "components/ui/input";

import styles from './services.module.css';

const controlStyles = {
  margin: "0 30px 20px 0",
  maxWidth: "300px"
};

const serviceTableColumns: TableAdapterColumn<ServiceListModel>[] = [
  {
    key: "servicemanEmail",
    title: "Pracownik",
    dataIndex: "servicemanEmail",
    render: (value, record) => (
      <Link to={`/actions/${record.projectId}/${record.entityId}/${record.entityVersion}`}>
        {String(value ?? "")}
      </Link>
    ),
    width: "200px",
  },
  {
    key: "when",
    title: "Data",
    accessorFn: (row) => row.when.year * 10000 + row.when.month * 100 + row.when.day,
    render: (_, record) => <LocalDateView item={record.when} />,
    sortable: true,
    width: "120px",
  },
  {
    key: "customerName",
    title: "Klient",
    dataIndex: "customerName",
    width: "30%",
  },
  {
    key: "description",
    title: "Usługa",
    dataIndex: "description",
    width: "60%",
  },
  {
    key: "duration",
    title: "Czas",
    dataIndex: "duration",
    render: (value) => <Duration duration={value as number | undefined} />,
    width: "80px",
  },
  {
    key: "distance",
    title: "Dojazd",
    dataIndex: "distance",
    width: "80px",
  },
];

export interface IDocument extends ServiceListModel {
}

export interface ContentProps {
}

const mapStateToProps = (state: RootState) => {
  if (state.appState.empty) {
    throw new Error('Invalid state');
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


  // dev hack
  // https://stackoverflow.com/questions/75774800/how-to-stop-resizeobserver-loop-limit-exceeded-error-from-appearing-in-react-a
  // when removed you can see an exception on UI when clicking multiple times 'Tylko moje' button
  React.useEffect(() => {
    window.addEventListener('error', e => {
        //if (e.message === 'ResizeObserver loop completed with undelivered notifications') {
        if (e.message.startsWith('ResizeObserver loop')) {
            const resizeObserverErrDiv = document.getElementById(
                'webpack-dev-server-client-overlay-div'
            );
            const resizeObserverErr = document.getElementById(
                'webpack-dev-server-client-overlay'
            );
            if (resizeObserverErr) {
                resizeObserverErr.setAttribute('style', 'display: none');
            }
            if (resizeObserverErrDiv) {
                resizeObserverErrDiv.setAttribute('style', 'display: none');
            }
        }
    });
  }, []);

  const [lastTouchedActionId, setlastTouchedActionId] = useState(props.lastTouchedActionId);

  const newDataIsComing = props.lastTouchedActionId != null && props.lastTouchedActionId !== lastTouchedActionId;
  if (newDataIsComing) {
    console.log('newDataIsComing');
    setlastTouchedActionId(props.lastTouchedActionId);
  }

  const periodDto = asDtoDates(props.period);
  const { data, refetch } = useFetchServicesQuery({
    variables: {
      projectId: props.selectedProjectId,
      from: periodDto.dateFrom,
      to: periodDto.dateTo
    }
  });
  if (newDataIsComing) {
    refetch();
  }

  const [onlyMyData, setOnlyMyData] = useState(true);
  const filterByOnlyMyData = (item: ServiceListModel): boolean => {
    if (!onlyMyData) return true;
    if (item.servicemanEmail === props.currentEmail) return true;
    return false;
  }

  const [onlyDay, setOnlyDay] = useState<string | undefined>();
  const filterByOnlyDay = (item: ServiceListModel): boolean => {
    if (!onlyDay) return true;
    if (item.when.day.toString().includes(onlyDay)) return true;
    return false;
  }

  const [onlyCustomer, setOnlyCustomer] = useState<string | undefined>();
  const filterByOnlyCustomer = (item: ServiceListModel): boolean => {
    if (!onlyCustomer) return true;
    if (!item.customerName) return false;
    if (item.customerName?.toLowerCase().includes(onlyCustomer.toLowerCase())) return true;
    return false;
  }

  const items: ServiceListModel[] = useMemo(
    () =>
      _.chain(data?.Actions.search.items)
        .map(it => toActionModel(it))
        .map(it => toLocalModel(it))
        .filter(it => filterByOnlyMyData(it))
        .filter(it => filterByOnlyDay(it))
        .filter(it => filterByOnlyCustomer(it))
        .value(),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [data?.Actions.search.items, onlyMyData, onlyDay, onlyCustomer, props.currentEmail]
  );

  const { table } = useTanStackTableAdapter({
    data: items,
    columns: serviceTableColumns,
    initialSorting: [{ id: "when", desc: true }],
  });

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

  const [currentPeriod, setCurrentPeriod] = useState<TimePeriod | null>(null);
  if (currentPeriod !== props.period) {
    setCurrentPeriod(props.period);
  }

  return (
    <div>
      <Space direction="horizontal" align="baseline" size="middle">
        <Button
          variant={onlyMyData ? "default" : "outline"}
          size="sm"
          onClick={() => setOnlyMyData(!onlyMyData)}
        >
          {onlyMyData ? "Tylko moje" : "Wszystkie"}
        </Button>
        <Input
          placeholder="Tylko dzień"
          value={onlyDay}
          onChange={(e) => setOnlyDay(e.target.value)}
          style={controlStyles}
        />
        <Input
          placeholder="Kontrahent"
          value={onlyCustomer}
          onChange={(e) => setOnlyCustomer(e.target.value)}
          style={controlStyles}
        />
        <div style={{ ...controlStyles, display: 'flex', alignItems: 'center' }}>
          <span className="text-sm">Suma godzin: </span>
          <span className="ml-2 text-sm font-medium">{totalTime()}</span>
        </div>
      </Space>
      <div className="overflow-y-auto" style={{ maxHeight: "calc(100vh - 250px)" }}>
        <TanStackTableView table={table} className={styles.hideextra} showPagination={false} />
      </div>
    </div>
  );
}

export const Content = connector(ConnectedContent);

const toLocalModel = (it: ServiceAppModel): ServiceListModel => {
  return {
    projectId: it.projectId,
    entityId: it.entityId,
    entityVersion: it.entityVersion,
    servicemanEmail: it.servicemanEmail,
    description: it.description,
    when: it.when,
    duration: it.duration,
    distance: it.distance,
    customerName: it.customer?.name
  }
}
