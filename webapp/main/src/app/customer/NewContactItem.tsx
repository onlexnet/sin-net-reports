import { DefaultButton, IStackTokens, Stack, TextField } from "office-ui-fabric-react";
import React from "react";
import { ContactDetails } from "./CustomerView";

const stackTokens: IStackTokens = { childrenGap: 12 };

interface NewContactItemProps {
    model: ContactDetails,
    onChange: (newValue: ContactDetails) => void,
    onRemove: (localKey: string) => void

}

export const NewContactItem: React.FC<NewContactItemProps> = props => {
    const { localKey, firstName, lastName, phoneNo, email } = props.model;
    const handler = (action: (m: ContactDetails, v?: string) => void) => {
        return (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
            var newModel: ContactDetails = {
                localKey,
                firstName,
                lastName,
                phoneNo,
                email
            };
            action(newModel, newValue);
            props.onChange(newModel);
        }
    }
    
    return (<div className="ms-Grid-row">
        <div className="ms-Grid-col ms-smPush1">
            <Stack tokens={stackTokens}>
                <Stack horizontal tokens={stackTokens}>
                    <TextField placeholder="Imię" value={firstName} onChange={handler((m, v) => m.firstName = v)} />
                    <TextField placeholder="Nazwisko" value={lastName} onChange={handler((m, v) => m.lastName = v)} />
                    <TextField placeholder="Nr telefonu" value={phoneNo} onChange={handler((m, v) => m.phoneNo = v)} />
                    <TextField placeholder="email" value={email} onChange={handler((m, v) => m.email = v)} />
                    <DefaultButton text="Usuń" onClick={() => props.onRemove(localKey)} />
                </Stack>
            </Stack>
        </div>
    </div>);

}
