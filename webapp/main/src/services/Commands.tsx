import * as React from 'react';
import { CommandBar, ICommandBarItemProps, IButtonProps } from 'office-ui-fabric-react';
import { Dispatch } from 'redux';
import { connect } from 'react-redux';
import { RootState } from '../reduxStore';
import { addService } from '../store/services/actions';

const overflowProps: IButtonProps = { ariaLabel: 'More commands' };

interface ServiceCommandBarProps {
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
      onClick: props.onPreviousMonthRequested
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


const mapStateToProps = (state: RootState) => state.services;
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    onTodoClick: () => {
      dispatch(addService("aaa"));
    }
  }
}

export const ServiceCommandBar = connect(mapStateToProps, mapDispatchToProps)(ServiceCommandBarView);