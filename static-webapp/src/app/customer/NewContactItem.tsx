import { Button, Col, Input, Row } from 'antd';
import React from "react";
import { ContactDetails } from "./CustomerView";

interface NewContactItemProps {
    model: ContactDetails,
    onChange: (newValue: ContactDetails) => void,
    onRemove: (localKey: string) => void
}

export const NewContactItem: React.FC<NewContactItemProps> = props => {
    const { localKey, firstName, lastName, phoneNo, email } = props.model;
    const handler = (action: (m: ContactDetails, v?: string) => void) => {
        return (e: any) => {
            var newModel: ContactDetails = {
                localKey,
                firstName,
                lastName,
                phoneNo,
                email
            };
            action(newModel, e.target.value);
            props.onChange(newModel);
        }
    }

    return (
        <Row gutter={16}>
            <Col span={4}>
                <Input placeholder="Imię" value={firstName} onChange={handler((m, v) => m.firstName = v)} />
            </Col>
            <Col span={4}>
                <Input placeholder="Nazwisko" value={lastName} onChange={handler((m, v) => m.lastName = v)} />
            </Col>
            <Col span={4}>
                <Input placeholder="Nr telefonu" value={phoneNo} onChange={handler((m, v) => m.phoneNo = v)} />
            </Col>
            <Col span={4}>
                <Input placeholder="email" value={email} onChange={handler((m, v) => m.email = v)} />
            </Col>
            <Col span={4}>
                <Button onClick={() => props.onRemove(localKey)}>Usuń</Button>
            </Col>
        </Row>
    );
}
