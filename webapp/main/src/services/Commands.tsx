import * as React from 'react';
import { CommandBar, ICommandBarItemProps, IButtonProps } from 'office-ui-fabric-react';
import { Dispatch } from 'redux';
import { connect, ConnectedProps } from 'react-redux';
import { RootState } from '../store/reducers';
import { previousPeriodCommand, nextPeriodCommand } from '../store/viewcontext/actions';
import { useNewServiceActionMutation } from '../Components/.generated/components';
import { asDtoDates } from '../api/Mapper';

const overflowProps: IButtonProps = { ariaLabel: 'More commands' };

const mapStateToProps = (state: RootState) => ({ viewContext: state.viewContext, session: state.auth });
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    onPreviousMonthRequested: () => {
      dispatch(previousPeriodCommand())
    },
    onNextMonthRequested: () => {
      dispatch(nextPeriodCommand())
    }
  }
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>

interface ServiceCommandBarProps extends PropsFromRedux {
  onPreviousMonthRequested: () => void;
  onNextMonthRequested: () => void;
}

const ServiceCommandBarView: React.FC<ServiceCommandBarProps> = (props) => {
  const { viewContext, session } = props;
  const [newServiceMutation, { data }] = useNewServiceActionMutation();
  const newService = () => {
    const { dateFrom } = asDtoDates(viewContext.period);
    newServiceMutation({
      variables: {
        when: dateFrom,
        what: "Nowa usługa " + Date.now(),
        who: session.email,
        whom: "Customer name " + Date.now(),
        distance: Math.floor((Math.random() * 10) + 10),
        duration: Math.floor((Math.random() * 50) + 10)
      }
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