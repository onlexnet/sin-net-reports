import { Button, Input, Divider, Space, Typography, Col, Row } from "antd";
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
        <>
            <Divider orientation="left"><Text>{props.model.location}</Text></Divider>
            <Row gutter={16}>
                <Col offset={2} span={4}>
                    <Input placeholder="Oddział NFZ" value={entityName} onChange={handler((m, v) => m.entityName = v)} />
                </Col>
                <Col span={4}>
                    <Input placeholder="kod świadczeniodawcy" value={entityCode} onChange={handler((m, v) => m.entityCode = v)} />
                </Col>
                <Col span={6}>
                    <Input placeholder="Użytkownik" value={username} onChange={handler((m, v) => m.username = v)} />
                </Col>
                <Col span={6}>
                    <Input placeholder="Hasło" value={password} onChange={handler((m, v) => m.password = v)} />
                </Col>
                <Col span={2}>
                    <Button onClick={() => props.onRemove(props.model.localKey)}>Usuń</Button>
                </Col>
            </Row>
            <Row gutter={16}>
                <Col offset={2} span={4}>
                    <Input value={otpDesc} disabled />
                </Col>
                <Col span={4}>
                    <Input value={expiresDesc} disabled />
                </Col>
                <Col span={6}>
                    <Input placeholder="TOTP secret" value={otpSecret} onChange={handler((m, v) => m.otpSecret = v)} />
                </Col>
                <Col span={6}>
                    <Input.TextArea value={otpRecoveryKeys} placeholder="TOTP recovery keys" onChange={handler((m, v) => m.otpRecoveryKeys = v)} />
                </Col>
            </Row>
            <Row gutter={16}>
                <Col offset={2} span={4}>
                    <Input value={props.changedBy} disabled defaultValue="-" />
                </Col>
                <Col span={4}>
                <Input value={props.changedWhen} disabled defaultValue="-" />
                </Col>
            </Row>
        </>
    );
}


