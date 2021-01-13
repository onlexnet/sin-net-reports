import * as React from 'react';
import { CommandBar, ICommandBarItemProps, IButtonProps } from 'office-ui-fabric-react';
import { Dispatch } from 'redux';
import { connect, ConnectedProps } from 'react-redux';
import { RootState } from '../store/reducers';
import { previousPeriodCommand, nextPeriodCommand } from '../store/viewcontext/actions';
import { useNewActionMutation } from '../Components/.generated/components';
import { asDtoDate, asDtoDates } from '../api/Mapper';
import { EntityId } from '../store/actions/ServiceModel';
import { ActionEditUpdated, VIEWCONTEXT_ACTION_EDIT_UPDATED } from '../store/viewcontext/types';
import { LocalDate, TimePeriod } from '../store/viewcontext/TimePeriod';

const overflowProps: IButtonProps = { ariaLabel: 'More commands' };

const mapStateToProps = (state: RootState) => {
  if (state.appState.empty) {
    throw 'Invalid state';
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
  getCustomerRaport: () => void;
}

const ServiceCommandBarView: React.FC<ServiceCommandBarProps> = (props) => {
  const { viewContext, session } = props;
  const [newServiceMutation, { data }] = useNewActionMutation();
  const newService = () => {
    const currentYear = new Date().getFullYear();
    const currentMonth = new Date().getMonth() + 1;
    const { year: requestedYear, month: requestedMonth } = viewContext.period.getValue().dateFrom;

    let date: LocalDate;
    if (currentYear == requestedYear && currentMonth == requestedMonth) {
      date = {
        year: currentYear,
        month: currentMonth,
        day: new Date().getDate()
      }
    } else {
      date = viewContext.period.getValue().dateFrom
    }
    const dateFrom = asDtoDate(date);
    newServiceMutation({
      variables: {
        projectId: props.selectedProjectId,
        when: dateFrom
      }
    }).then(r => {
      var createdEntityId = r.data?.Actions.newAction;
      if (!createdEntityId) return;
      props.actionUpdated(createdEntityId);
    })
  }
  const _items: ICommandBarItemProps[] = [
    {
      key: 'newService',
      text: 'Nowa usługa',
      split: true,
      iconProps: { iconName: 'Add' },
      onClick: () => newService()
    },
    {
      key: 'prevMonth',
      text: 'Poprzedni miesiąc',
      split: true,
      iconProps: { iconName: 'Previous' },
      onClick: props.onPreviousMonthRequested
    },
    {
      key: 'nextMonth',
      text: 'Następny miesiąc',
      split: true,
      iconProps: { iconName: 'Next' },
      onClick: props.onNextMonthRequested
    },
    {
      key: 'getPdf',
      text: 'Raport miesięczny',
      split: true,
      iconProps: { iconName: 'ZipFolder' },
      onClick: props.getCustomerRaport
    },
  ];

  return (
    <div>
      <CommandBar
        items={_items}
        overflowButtonProps={overflowProps}
      />
    </div>
  );
};


export const ServiceCommandBar = connect(mapStateToProps, mapDispatchToProps)(ServiceCommandBarView);