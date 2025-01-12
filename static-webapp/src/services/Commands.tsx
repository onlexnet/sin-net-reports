import * as React from 'react';
import { Menu, Button } from "antd";
import { Dispatch } from 'redux';
import { connect, ConnectedProps } from 'react-redux';
import { RootState } from '../store/reducers';
import { previousPeriodCommand, nextPeriodCommand } from '../store/viewcontext/actions';
import { useNewActionMutation } from '../Components/.generated/components';
import { asDtoDate } from '../api/Mapper';
import { EntityId } from '../store/actions/ServiceModel';
import { ActionEditUpdated, VIEWCONTEXT_ACTION_EDIT_UPDATED } from '../store/viewcontext/types';
import { LocalDate } from '../store/viewcontext/TimePeriod';
import { Redirect } from 'react-router-dom';

const mapStateToProps = (state: RootState) => {
  if (state.appState.empty) {
    throw new Error('Invalid state');
  }

  return {
    viewContext: state.viewContext,
    session: state.auth,
    selectedProjectId: state.appState.projectId
  };
};
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    onPreviousMonthRequested: () => {
      dispatch(previousPeriodCommand())
    },
    onNextMonthRequested: () => {
      dispatch(nextPeriodCommand())
    },
    actionUpdated: (item: EntityId) => {
      var action: ActionEditUpdated = {
        type: VIEWCONTEXT_ACTION_EDIT_UPDATED,
        payload: {
          lastTouchedEntityId: item
        }
      }
      dispatch(action);
    }
  }
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>

interface ServiceCommandBarProps extends PropsFromRedux {
  onPreviousMonthRequested: () => void;
  onNextMonthRequested: () => void;
  onReportsViewRequested: () => void;
}

enum WaitingState {
  NOT_STARTED,
  IN_PROGRESS,
  FINISHED
}
const ServiceCommandBarView: React.FC<ServiceCommandBarProps> = (props) => {
  const { viewContext } = props;
  const [newServiceMutation] = useNewActionMutation();
  const [waiting, setWaiting] = React.useState(WaitingState.NOT_STARTED);
  const [waitingResult, setWaitingResult] = React.useState<EntityId | undefined>();

  if (waiting === WaitingState.IN_PROGRESS) {
    return <div>Czekanie na stworzenie wpisu usługi ...</div>
  }

  if (waiting === WaitingState.FINISHED) {
    return <Redirect
      from={'/actions/'}
      to={`/actions/${waitingResult?.projectId}/${waitingResult?.entityId}/${waitingResult?.entityVersion}`} />
  }
  const newService = () => {
    const currentYear = new Date().getFullYear();
    const currentMonth = new Date().getMonth() + 1;
    const { year: requestedYear, month: requestedMonth } = viewContext.period.getValue().dateFrom;

    let date: LocalDate;
    if (currentYear === requestedYear && currentMonth === requestedMonth) {
      date = {
        year: currentYear,
        month: currentMonth,
        day: new Date().getDate()
      }
    } else {
      date = viewContext.period.getValue().dateFrom
    }
    const dateFrom = asDtoDate(date);
    setWaiting(WaitingState.IN_PROGRESS);
    newServiceMutation({
      variables: {
        projectId: props.selectedProjectId,
        when: dateFrom
      }
    }).then(r => {
      var createdEntityId = r.data?.Actions.newAction;
      if (!createdEntityId) return;
      setWaitingResult(createdEntityId);
      setWaiting(WaitingState.FINISHED);
      props.actionUpdated(createdEntityId);
    })
  }

  interface MenuItem {
    key: string;
    label: string;
    onClick: () => void;
  }

  const menuItems: MenuItem[] = [
    {
      key: 'newService',
      label: 'Nowa usługa',
      onClick: () => newService()
    },
    {
      key: 'prevMonth',
      label: 'Poprzedni miesiąc',
      onClick: props.onPreviousMonthRequested
    },
    {
      key: 'nextMonth',
      label: 'Następny miesiąc',
      onClick: props.onNextMonthRequested
    },
    {
      key: 'navigateToReports',
      label: 'Raporty',
      onClick: props.onReportsViewRequested
    },
  ];

  return (
    <Menu theme='light' items={menuItems} mode="horizontal" />
  );
};

export const ServiceCommandBar = connect(mapStateToProps, mapDispatchToProps)(ServiceCommandBarView);
