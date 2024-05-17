import { DefaultButton, IStackTokens, Label, Separator, Stack, TextField } from "@fluentui/react";
import React, { useEffect, useState } from "react";
import { TOTP } from "totp-generator";

const stackTokens: IStackTokens = { childrenGap: 12 };

interface UserPasswordModel {
    localKey: string,
    location: string
    username?: string,
    password?: string
    otpSecret?: string
    otpRecoveryKeys?: string
}

interface UserPasswordItemProps {
    model: UserPasswordModel,
    changedBy: string,
    changedWhen: string,
    onChange: (newValue: UserPasswordModel) => void,
    onRemove: (localKey: string) => void
}

export const UserPasswordItem: React.FC<UserPasswordItemProps> = props => {
    const init = new Date()

    const [date, setDate] = useState(init)
    const tick = () => {
        setDate(new Date())
    }

    // refresh component every sec to allow recalculate totp
    // the logic should be moved to hook around TOTP returning code when changed
    useEffect(() => {
        const timerID = setInterval(() => tick(), 1000)
        return () => {
            clearInterval(timerID)
        }
    }, [])


    const { localKey, location: sectionName, username, password, otpSecret, otpRecoveryKeys } = props.model;
    const handler = (action: (m: UserPasswordModel, v?: string) => void) => {
        return (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
            var newModel: UserPasswordModel = {
                localKey,
                location: sectionName,
                username,
                password,
                otpSecret,
                otpRecoveryKeys
            };
            action(newModel, newValue);
            props.onChange(newModel);
        }
    }

    const extendedWidth50 = { minWidth: "50%" }
    const extendedWidth1 = { minWidth: "300px" }
    const extendedWidth2 = { minWidth: "492px" }

    let otpDesc = "TOTP nie zdefiniowany"
    let expiresDesc = "-"
    const period = 30
    if (otpSecret) {
        try {
            const { otp, expires } = TOTP.generate(otpSecret!, {
                digits: 6,
                algorithm: "SHA-1",
                period: period
            })
            otpDesc = otp
            const now = new Date().getTime()
            const differenceInMillis = expires - now
            const differenceInSeconds = Math.floor(differenceInMillis / 1000);
            expiresDesc = differenceInSeconds + ""
        } catch (error) {
            otpDesc = "Nieprawidłowy sekret!"
            expiresDesc = "!"
        }
    }

    // Mutating styles definition

    return (
        <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-smPush1">
                <Separator alignContent="start"><Label>{sectionName}</Label></Separator>
                <Stack tokens={stackTokens}>
                    <Stack horizontal tokens={stackTokens}>
                        <TextField style={extendedWidth1} placeholder="Użytkownik" value={username} onChange={handler((m, v) => m.username = v)} />
                        <TextField style={extendedWidth1} placeholder="Hasło" value={password} onChange={handler((m, v) => m.password = v)} />
                        <DefaultButton text="Usuń" onClick={() => props.onRemove(props.model.localKey)} />
                    </Stack>
                    <Stack horizontal tokens={stackTokens}>
                        <TextField value={otpDesc} disabled />
                        <TextField value={expiresDesc} disabled />
                        <TextField style={extendedWidth1} placeholder="TOTP secret" value={otpSecret} onChange={handler((m, v) => m.otpSecret = v)} />
                        <TextField style={extendedWidth1} multiline placeholder="TOTP recovery keys" value={otpRecoveryKeys} onChange={handler((m, v) => m.otpRecoveryKeys = v)} />
                    </Stack>
                    <Stack horizontal tokens={stackTokens}>
                        <TextField value={props.changedBy} disabled />
                        <TextField value={props.changedWhen} disabled />
                    </Stack>
                </Stack>
            </div>
        </div>
    );
}


