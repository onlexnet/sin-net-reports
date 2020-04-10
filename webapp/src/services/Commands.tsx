import * as React from 'react';
import { CommandBar, ICommandBarItemProps, IButtonProps } from 'office-ui-fabric-react';

const overflowProps: IButtonProps = { ariaLabel: 'More commands' };

export const CommandBarBasicExample: React.FunctionComponent = () => {
  return (
    <div>
      <CommandBar
        items={_items}
        overflowButtonProps={overflowProps}
        farItems={_farItems}
        ariaLabel="Use left and right arrow keys to navigate between commands"
      />
    </div>
  );
};

const _items: ICommandBarItemProps[] = [
  {
    key: 'newService',
    text: 'Nowa usługa',
    split: true,
    iconProps: { iconName: 'Add' }
  },
  {
    key: 'prevMonth',
    text: 'Poprzedni miesiąc',
    split: true,
    iconProps: { iconName: 'Previous' }
  },
  {
    key: 'nextMonth',
    text: 'Następny miesiąc',
    split: true,
    iconProps: { iconName: 'Next' }
  },
];

const _farItems: ICommandBarItemProps[] = [
  {
    key: 'tile',
    text: 'Grid view',
    // This needs an ariaLabel since it's icon-only
    ariaLabel: 'Grid view',
    iconOnly: true,
    iconProps: { iconName: 'Tiles' },
    onClick: () => console.log('Tiles')
  },
  {
    key: 'info',
    text: 'Info',
    // This needs an ariaLabel since it's icon-only
    ariaLabel: 'Info',
    iconOnly: true,
    iconProps: { iconName: 'Info' },
    onClick: () => console.log('Info')
  }
];
