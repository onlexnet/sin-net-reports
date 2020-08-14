import * as React from 'react';
import { CommandBar, ICommandBarItemProps, IButtonProps } from 'office-ui-fabric-react';
import { initialState } from '../reduxStore';
import { Dispatch } from 'redux';
import { connect } from 'react-redux';
import { addService } from './redux';

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

const mapStateToProps = (state: typeof initialState) => state;
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    onTodoClick: () => {
      const a = addService("aaa");
      dispatch(a);
    }
  }
}

export const ServiceCommandBar = connect(mapStateToProps, mapDispatchToProps)(ServiceCommandBarView);