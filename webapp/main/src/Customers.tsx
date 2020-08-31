import React from "react";
import { Stack, IStackTokens, Button, IComboBoxOption, SelectableOptionMenuItemType, IComboBox, ComboBox, PrimaryButton } from "office-ui-fabric-react";

export const Customers: React.FC<{}> = (props) => {

    const stackTokens: IStackTokens = { childrenGap: 12 };
    const HorizontalSeparatorStack = (props: { children: JSX.Element[] }) => (
        <>
            {React.Children.map(props.children, child => {
                return <Stack tokens={stackTokens}>{child}</Stack>;
            })}
        </>
    );

    const comboBoxBasicOptions: IComboBoxOption[] = [
        { key: 'Header1', text: 'First heading', itemType: SelectableOptionMenuItemType.Header },
        { key: 'A', text: 'Option A' },
        { key: 'B', text: 'Option B' },
        { key: 'C', text: 'Option C' },
        { key: 'D', text: 'Option D' },
        { key: 'divider', text: '-', itemType: SelectableOptionMenuItemType.Divider },
        { key: 'Header2', text: 'Second heading', itemType: SelectableOptionMenuItemType.Header },
        { key: 'E', text: 'Option E' },
        { key: 'F', text: 'Option F', disabled: true },
        { key: 'G', text: 'Option G' },
        { key: 'H', text: 'Option H' },
        { key: 'I', text: 'Option I' },
        { key: 'J', text: 'Option J' },
    ];


    const comboBoxMultiStyle = { maxWidth: 300, display: 'block', marginTop: '10px' };

    const comboBoxRef = React.useRef<IComboBox>(null);
    const onOpenClick = React.useCallback(() => comboBoxRef.current?.focus(true), []);


    return (
        <HorizontalSeparatorStack>

            <Button text="Dodaj nowego klienta" />
                <ComboBox
                    componentRef={comboBoxRef}
                    defaultSelectedKey="C"
                    label="Basic ComboBox"
                    allowFreeform
                    autoComplete="on"
                    options={comboBoxBasicOptions}
                />

        </HorizontalSeparatorStack>
    )
}
