import React from "react"

import { IComboBoxOption, ComboBox, IComboBox } from "office-ui-fabric-react";
import _ from "lodash";


import { v4 as uuid } from 'uuid';

type SECRET_TYPE = 'PORTAL_SWIADCZENIODAWCY'
    | 'SIMP'
    | 'PORTAL_PERSONELU'
    | 'MUS'
    | 'SZOI'
    | 'EWUS'
    | 'EPUB'
    | 'GUS'
    | 'DILO';


interface NewAuthorisationProps {
    newAuthorisationRequested: (name: string) => void;
    newAuthorisationExRequested: (name: string) => void;
}

const onChangeHandler = (props: NewAuthorisationProps) => {
    return (event: React.FormEvent<IComboBox>, option?: IComboBoxOption, index?: number, value?: string): void => {
        if (!option) return;
        const model = toHint(option.key as SECRET_TYPE);
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
            return { key, text: 'Portal personelu', extended: false }
        case 'MUS':
            return { key, text: 'MUS', extended: false }
        case 'SZOI':
            return { key, text: 'SZOI', extended: false }
        case "EWUS":
            return { key, text: 'EWUŚ', extended: false }
        case "EPUB":
            return { key, text: 'ePUB', extended: false }
        case 'GUS':
            return { key, text: 'GUS', extended: false }
        case 'DILO':
            return { key, text: 'DILO', extended: false }
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
    'DILO']


export const NewSecret: React.FC<NewAuthorisationProps> = props => {
    const authorisationType = _.chain(options)
        .map(it => toHint(it))
        .map(it => ({ key: it.key, text: it.text }))
        .value()
    return (<div className="ms-Grid-row">
        <div className="ms-Grid-col ms-smPush1 ms-sm4">
            <ComboBox
                label="Dodaj nową autoryzację"
                options={authorisationType}
                onChange={onChangeHandler(props)} />
        </div>
    </div>);
}


