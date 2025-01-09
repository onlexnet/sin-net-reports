import { Button, Input, Divider, Space, Typography } from "antd";
import React, { useEffect, useState } from "react";
import { TOTP } from "totp-generator"

const { Text } = Typography;

interface UserPasswordExtModel {
    // internal ID used by React to identify the element on list of elements
    localKey: string,
    // editable properties of the component
    location: string
    username?: string,
    password?: string,
    entityName?: string,
    entityCode?: string
    otpSecret?: string
    otpRecoveryKeys?: string
}

interface UserPasswordExtItemProps {
    model: UserPasswordExtModel,
    changedBy: string,
    changedWhen: string,
    onChange: (newValue: UserPasswordExtModel) => void,
    onRemove: (localKey: string) => void
}

export const UserPasswordItemExt: React.FC<UserPasswordExtItemProps> = props => {
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

    const { localKey, location, username, password, entityName, entityCode, otpSecret, otpRecoveryKeys } = props.model;
    const handler = (action: (m: UserPasswordExtModel, v?: string) => void) => {
        return (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
            var newModel: UserPasswordExtModel = {
                localKey,
                location,
                username,
                password,
                entityName,
                entityCode,
                otpSecret,
                otpRecoveryKeys
            };
            action(newModel, event.target.value);
            props.onChange(newModel);
        }
    }

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

    return (
        <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-smPush1">
                <Divider orientation="left"><Text>{props.model.location}</Text></Divider>
                <Space direction="vertical" size="middle">
                    <Space>
                        <Input placeholder="Oddział NFZ" value={entityName} onChange={handler((m, v) => m.entityName = v)} />
                        <Input placeholder="kod świadczeniodawcy" value={entityCode} onChange={handler((m, v) => m.entityCode = v)} />
                        <Input style={{ minWidth: "300px" }} placeholder="Użytkownik" value={username} onChange={handler((m, v) => m.username = v)} />
                        <Input style={{ minWidth: "300px" }} placeholder="Hasło" value={password} onChange={handler((m, v) => m.password = v)} />
                        <Button onClick={() => props.onRemove(props.model.localKey)}>Usuń</Button>
                    </Space>
                    <Space>
                        <Input value={otpDesc} disabled />
                        <Input value={expiresDesc} disabled />
                        <Input style={{ minWidth: "300px" }} placeholder="TOTP secret" value={otpSecret} onChange={handler((m, v) => m.otpSecret = v)} />
                        <Input style={{ minWidth: "300px" }} placeholder="TOTP recovery keys" value={otpRecoveryKeys} onChange={handler((m, v) => m.otpRecoveryKeys = v)} />
                    </Space>
                    <Space>
                        <Input value={props.changedBy} disabled defaultValue="-" />
                        <Input value={props.changedWhen} disabled defaultValue="-" />
                    </Space>
                </Space>
            </div>
        </div>
    );
}


