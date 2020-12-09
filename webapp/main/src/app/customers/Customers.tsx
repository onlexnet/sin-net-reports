import React from "react";
import { Stack, IStackTokens, Button, IComboBoxOption, SelectableOptionMenuItemType, IComboBox, ComboBox, TextField } from "office-ui-fabric-react";
import { RouteComponentProps } from "react-router-dom";
import { routing } from "../../Routing";
import { HorizontalSeparatorStack } from "../../Components/HorizontalSeparatorStack";

interface CustomersProps extends RouteComponentProps<any> {

}

export const Customers: React.FC<CustomersProps> = (props) => {

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


    const comboBoxRef = React.useRef<IComboBox>(null);


    return (
        <div className="ms-Grid">
            <HorizontalSeparatorStack >
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm3">
                        <Button text="Dodaj nowego klienta" onClick={ () => props.history.push(routing.newCustomer)  } />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm12">
                        <TextField placeholder="Softowanie: wprowadź fragment nazwy klienta ..." />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm12">
                        <TextField placeholder="Filtrowanie: wprowadź filtr wyszukiwania ..." />
                    </div>
                </div>

            </HorizontalSeparatorStack>
        </div >
    )
}


