import { DefaultButton, IStackTokens, Separator, Stack, TextField } from "office-ui-fabric-react";
import React from "react";

const stackTokens: IStackTokens = { childrenGap: 12 };

interface UserPasswordItemProps {
    sectionName: string
}

export const UserPasswordItem: React.FC<UserPasswordItemProps> = props => {
    return (
        <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-smPush1">
                <Separator alignContent="start">{props.sectionName}</Separator>
                <Stack tokens={stackTokens}>
                    <Stack horizontal tokens={stackTokens}>
                        <TextField placeholder="Użytkownik" />
                        <TextField placeholder="Hasło" />
                        <DefaultButton text="Usuń" />
                    </Stack>
                </Stack>
            </div>
        </div>
    );
}


