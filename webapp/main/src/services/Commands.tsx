import * as React from 'react';
import { CommandBar, ICommandBarItemProps, IButtonProps } from 'office-ui-fabric-react';
import { Dispatch } from 'redux';
import { connect, ConnectedProps } from 'react-redux';
import { addService, reloadServicesBegin } from '../store/services/actions';
import { RootState } from '../store/reducers';

const overflowProps: IButtonProps = { ariaLabel: 'More commands' };

const mapStateToProps = (state: RootState) => state.services;
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    refreshList: () => {
      dispatch(reloadServicesBegin(2020, 1))
    },
    onTodoClick: () => {
      dispatch(addService("aaa"));
    }
  }
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>

interface ServiceCommandBarProps extends PropsFromRedux {
  onPreviousMonthRequested: () => void;
  onNextMonthRequested: () => void;
  onTodoClick: () => void;
}

const ServiceCommandBarView: React.FC<ServiceCommandBarProps> = (props) => {

  const _items: ICommandBarItemProps[] = [
    {
      key: 'newService',
      text: 'Nowa usługa',
      split: true,
      iconProps: { iconName: 'Add' },
      onClick: () => props.onTodoClick()
    },
    {
      key: 'prevMonth',
      text: 'Poprzedni miesiąc',
      split: true,
      iconProps: { iconName: 'Previous' },
      onClick: () => {
        props.onPreviousMonthRequested();
        props.refreshList();
      }
    },
    {
      key: 'nextMonth',
      text: 'Następny miesiąc',
      split: true,
      iconProps: { iconName: 'Next' },
      onClick: props.onNextMonthRequested
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