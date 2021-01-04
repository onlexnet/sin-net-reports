import { DefaultButton, IStackTokens, Separator, Stack, TextField } from "office-ui-fabric-react";
import React from "react";

const stackTokens: IStackTokens = { childrenGap: 12 };

interface UserPasswordExtModel {
    localKey: string,
    location: string
    username?: string,
    password?: string,
    entityName?: string,
    entityCode?: string
}

interface UserPasswordExtItemProps {
    model: UserPasswordExtModel,
    onChange: (newValue: UserPasswordExtModel) => void,
    onRemove: (localKey: string) => void
}



export const UserPasswordItemExt: React.FC<UserPasswordExtItemProps> = props => {
    const { localKey, location, username, password, entityName, entityCode } = props.model;
    const handler = (action: (m: UserPasswordExtModel, v?: string) => void) => {
        return (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
            var newModel: UserPasswordExtModel = {
                localKey,
                location,
                username,
                password,
                entityName,
                entityCode
            };
            action(newModel, newValue);
            props.onChange(newModel);
        }
    }

    return (
        <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-smPush1">
                <Separator alignContent="start">{props.model.location}</Separator>
                <Stack tokens={stackTokens}>
                    <Stack horizontal tokens={stackTokens}>
                        <TextField placeholder="Oddział NFZ" value={entityName} onChange={handler((m, v) => m.entityName = v)} />
                        <TextField placeholder="Nr klienta" value={entityCode} onChange={handler((m, v) => m.entityCode = v)} />
                        <TextField placeholder="Użytkownik" value={username} onChange={handler((m, v) => m.username = v)} />
                        <TextField placeholder="Hasło" value={password} onChange={handler((m, v) => m.password = v)} />
                        <DefaultButton text="Usuń" />
                    </Stack>
                </Stack>
            </div>
        </div>
    );
}

