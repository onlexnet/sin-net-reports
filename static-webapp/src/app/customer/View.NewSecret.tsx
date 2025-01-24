import React from "react";
import { Col, Row, Select } from 'antd';
import _ from "lodash";

type SECRET_TYPE = 'PORTAL_SWIADCZENIODAWCY'
    | 'SIMP'
    | 'PORTAL_PERSONELU'
    | 'MUS'
    | 'SZOI'
    | 'EWUS'
    | 'EPUB'
    | 'GUS'
    | 'DILO'
    | 'RPWDL';

interface NewAuthorisationProps {
    newAuthorisationRequested: (name: string) => void;
    newAuthorisationExRequested: (name: string) => void;
}

const onChangeHandler = (props: NewAuthorisationProps) => {
    return (value: SECRET_TYPE) => {
        const model = toHint(value);
        if (!model.extended) {
            props.newAuthorisationRequested(model.text);
        } else {
            props.newAuthorisationExRequested(model.text);
        }
    }
}

interface SecretHint {
    key: SECRET_TYPE
    text: string,
    extended: boolean
}

const toHint = (key: SECRET_TYPE): SecretHint => {
    switch (key) {
        case "PORTAL_SWIADCZENIODAWCY":
            return { key, text: 'Portal świadczeniodawcy', extended: true }
        case "SIMP":
            return { key, text: 'SIMP', extended: true }
        case "PORTAL_PERSONELU":
            return { key, text: 'Portal personelu', extended: true }
        case 'MUS':
            return { key, text: 'MUS', extended: true }
        case 'SZOI':
            return { key, text: 'SZOI', extended: false }
        case "EWUS":
            return { key, text: 'EWUŚ', extended: true }
        case "EPUB":
            return { key, text: 'ePUB', extended: false }
        case 'GUS':
            return { key, text: 'GUS', extended: false }
        case 'DILO':
            return { key, text: 'DILO', extended: false }
        case 'RPWDL':
            return { key, text: 'RPWDL', extended: false }
    }
}

const options: SECRET_TYPE[] = [
    "PORTAL_SWIADCZENIODAWCY",
    'SIMP',
    "PORTAL_PERSONELU",
    'MUS',
    'SZOI',
    "EWUS",
    "EPUB",
    'GUS',
    'DILO',
    'RPWDL']

export const NewSecret: React.FC<NewAuthorisationProps> = props => {
    const authorisationType = _.chain(options)
        .map(it => toHint(it))
        .map(it => ({ key: it.key, text: it.text }))
        .value()
    return (
        <Row gutter={32}>
            <Col offset={2}>
                <Select
                    placeholder="Dodaj nową autoryzację"
                    onChange={onChangeHandler(props)}>
                    {authorisationType.map(option => (
                        <Select.Option key={option.key} value={option.key}>{option.text}</Select.Option>
                    ))}
                </Select>
            </Col>
        </Row>
    );
}


