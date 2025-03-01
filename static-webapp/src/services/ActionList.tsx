import { Table, Tooltip, Input, Switch, Row, Col, Typography, Space } from "antd";
import _ from "lodash";
import * as React from "react";
import { useState } from "react";
import { connect, ConnectedProps } from "react-redux";
import { Link } from "react-router-dom";
import { Dispatch } from "redux";
import { toActionModel } from "../api/DtoMapper";
import { asDtoDates } from "../api/Mapper";
import { LocalDateView } from "../app/LocalDateView";
import { useFetchServicesQuery } from "../components/.generated/components";
import { ServiceAppModel } from "../store/actions/ServiceModel";
import { RootState } from "../store/reducers";
import { LocalDate, TimePeriod } from "../store/viewcontext/TimePeriod";
import { ActionEditItem, VIEWCONTEXT_ACTION_EDIT_START } from "../store/viewcontext/types";
import { Duration } from "./ActionList.Duration";
import { ServiceListModel } from "./ServiceListModel";
import { ColumnType } from "antd/es/table";
import styles from './services.module.css'

const { Text } = Typography;

const controlStyles = {
  margin: "0 30px 20px 0",
  maxWidth: "300px"
};

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

  const [dateSortedDescending, setDateSortedDescending] = useState(true);

  const initialColumns: ColumnType<ServiceListModel>[] = [
    {
      title: "Pracownik",
      dataIndex: "servicemanEmail",
      key: "servicemanEmail",
      render: (value: string, record: ServiceListModel) => {
        return <Link to={`/actions/${record.projectId}/${record.entityId}/${record.entityVersion}`}>{value}</Link>;
      },
      width: '200px'
    },
    {
      title: "Data",
      dataIndex: "when",
      key: "when",
      sorter: true,
      sortOrder: dateSortedDescending ? 'descend' : 'ascend',
      render: (text: LocalDate) => <LocalDateView item={text} />,
      width: '120px'
    },
    {
      title: "Klient",
      dataIndex: "customerName",
      key: "customerName",
      width: "30%"
    },
    {
      title: "Usługa",
      dataIndex: "description",
      key: "description",
      width: "60%"
    },
    {
      title: "Czas",
      dataIndex: "duration",
      key: "duration",
      render: (text: number) => <Duration duration={text} />,
      width: '80px'
    },
    {
      title: "Dojazd",
      dataIndex: "distance",
      key: "distance",
      width: '80px'
    }
  ];

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

  var items: ServiceListModel[] = _.chain(data?.Actions.search.items)
    .map(it => toActionModel(it))
    .map(it => toLocalModel(it))
    .filter(it => filterByOnlyMyData(it))
    .filter(it => filterByOnlyDay(it))
    .filter(it => filterByOnlyCustomer(it))
    .value();
  items.sort((i1, i2) => ('' + i1.customerName).localeCompare('' + i2.customerName));
  if (!dateSortedDescending) items.sort((i1, i2) => i1.when.day - i2.when.day);
  if (dateSortedDescending) items.sort((i1, i2) => i2.when.day - i1.when.day);

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
        <Switch
          checkedChildren="Tylko moje"
          unCheckedChildren="Wszystkie"
          checked={onlyMyData}
          onChange={setOnlyMyData}
        />
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
          <Text>Suma godzin: </Text>
          <Text style={{ marginLeft: '8px' }}>{totalTime()}</Text>
        </div>
      </Space>
      <div>
      <Table
        rowClassName={() => styles.hideextra }
        columns={initialColumns}
        dataSource={items}
        pagination={false}
        // https://medium.com/@anjantalatatam/antd-table-fixed-and-sticky-header-999812b4916a
        scroll={{ y: `calc(100vh - 250px)` }}
        rowKey="entityId"
        onChange={(pagination, filters, sorter) => {
          setDateSortedDescending(!dateSortedDescending);
        }}
      />
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
