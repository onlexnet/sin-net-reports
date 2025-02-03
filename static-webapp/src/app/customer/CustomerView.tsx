import { Affix, Button, Checkbox, Col, Divider, Form, Input, Row, Select, Space, Spin } from 'antd';
import _ from "lodash";
import React, { MouseEventHandler, useEffect, useRef, useState } from "react";
import { v1 as uuid } from 'uuid';
import { useGetUsers } from "../../api/useGetUsers";
import { CustomerContactInput, CustomerInput, CustomerSecretExInput, CustomerSecretInput, useRemoveCustomerMutation, useSaveCustomerMutation } from "../../components/.generated/components";
import { EntityId } from "../../store/actions/ServiceModel";
import { NewContactItem } from "./NewContactItem";
import { SecretsTimestamp } from "./SecretsTimestamp";
import { NewSecret } from "./View.NewSecret";
import { UserPasswordItem } from "./View.UserPasswordItem";
import { UserPasswordItemExt } from "./View.UserPasswordItemEx";

const { Option } = Select;

export interface CustomerViewEntry {
    EmailOfOperator?: string,
    ModelOfSupport?: string
    ModelOfBilling?: string,
    Distance?: number,
    KlientNazwa: string,
    KlientMiejscowosc?: string,
    KlientAdres?: string
    nfzUmowa?: boolean,
    nfzMaFilie?: boolean,
    nfzLekarz?: boolean,
    nfzPolozna?: boolean,
    nfzPielegniarkaSrodowiskowa?: boolean,
    nfzMedycynaSzkolna?: boolean,
    nfzTransportSanitarny?: boolean,
    nfzNocnaPomocLekarska?: boolean,
    nfzAmbulatoryjnaOpiekaSpecjalistyczna?: boolean,
    nfzRehabilitacja?: boolean,
    nfzStomatologia?: boolean,
    nfzPsychiatria?: boolean,
    nfzSzpitalnictwo?: boolean,
    nfzProgramyProfilaktyczne?: boolean,
    nfzZaopatrzenieOrtopedyczne?: boolean,
    nfzOpiekaDlugoterminowa?: boolean,
    nfzNotatki?: string,
    komercjaJest?: boolean,
    komercjaNotatki?: string,
    daneTechniczne?: string,
    autoryzacje: SecretModel[],
    autoryzacjeEx: SecretExModel[],
    kontakty: ContactDetails[]
}

type CustomerViewModel = {
    Operator: string | undefined;
    Obsluga: string | undefined;
    Rozliczenie: string | undefined;
    Dystans: string | undefined;
    Nazwa: string | undefined;
    Miejscowosc: string | undefined;
    Adres: string | undefined;
    UmowaZNFZ: boolean;
    MaFilie: boolean;
    Lekarz: boolean;
    Polozna: boolean;
    PielegniarkaSrodowiskowa: boolean;
    MedycynaSzkolna: boolean;
    TransportSanitarny: boolean;
    NocnaPomocLekarska: boolean;
    AmbulatoryjnaOpiekaSpecjalistyczna: boolean;
    Rehabilitacja: boolean;
    Stomatologia: boolean;
    Psychiatria: boolean;
    Szpitalnictwo: boolean;
    ProgramyProfilaktyczne: boolean;
    ZaopatrzenieOrtopedyczne: boolean;
    OpiekaDlugoterminowa: boolean;
    NfzNotatki: string | undefined;
    Komercja: boolean;
    KomercjaNotatki: string | undefined;
    DaneTechniczne: string | undefined;
    Kontakty: ContactDetails[],
    Autoryzacje: SecretModel[],
    AutoryzacjeEx: SecretExModel[]
}

export interface ContactDetails {
    localKey: string,
    firstName?: string,
    lastName?: string
    phoneNo?: string,
    email?: string
}

export interface SecretModel {
    localKey: string,
    location: string
    username?: string
    password?: string
    who?: string
    when?: SecretsTimestamp
    otpSecret?: string
    otpRecoveryKeys?: string
}

export interface SecretExModel {
    // Used by React to identify elements during rendering of list elements
    localKey: string
    location: string
    username?: string
    password?: string
    entityName?: string
    entityCode?: string
    who?: string
    when?: SecretsTimestamp
    otpSecret?: string
    otpRecoveryKeys?: string
}

interface CustomerViewProps {
    id: EntityId
    entry: CustomerViewEntry
    itemSaved: () => void
    itemRemoved: () => void;
}

export const CustomerView: React.FC<CustomerViewProps> = props => {

    const topPartRef = useRef<HTMLDivElement>(null);
    const [topPartHeight, setTopPartHeight] = useState(0);    
    useEffect(() => {
        if (topPartRef.current) {
            setTopPartHeight(topPartRef.current.clientHeight);
        }
    }, []);
    
    const obsluga = [
        { key: 'Obsługiwany', text: 'Obsługiwany' },
        { key: 'Nie obsługiwany', text: 'Nie obsługiwany' },
        { key: 'Obsługa czasowo zawieszona', text: 'Obsługa czasowo zawieszona' },
    ];

    const rozliczenia = [
        { key: 'Ryczałt', text: 'Ryczałt' },
        { key: 'Godziny', text: 'Godziny' },
    ];

    const users = useGetUsers(props.id.projectId);
    const comboBoxBasicOptions = users.map(user => ({ key: user, text: user }));

    const [model, setModel] = React.useState<CustomerViewModel>({
        Operator: props.entry.EmailOfOperator,
        Obsluga: props.entry.ModelOfSupport,
        Rozliczenie: props.entry.ModelOfBilling,
        Dystans: props.entry.Distance?.toString(),
        Nazwa: props.entry.KlientNazwa,
        Miejscowosc: props.entry.KlientMiejscowosc,
        Adres: props.entry.KlientAdres,
        UmowaZNFZ: props.entry.nfzUmowa ?? false,
        MaFilie: props.entry.nfzMaFilie ?? false,
        Lekarz: props.entry.nfzLekarz ?? false,
        Polozna: props.entry.nfzPolozna ?? false,
        PielegniarkaSrodowiskowa: props.entry.nfzPielegniarkaSrodowiskowa ?? false,
        MedycynaSzkolna: props.entry.nfzMedycynaSzkolna ?? false,
        TransportSanitarny: props.entry.nfzTransportSanitarny ?? false,
        NocnaPomocLekarska: props.entry.nfzNocnaPomocLekarska ?? false,
        AmbulatoryjnaOpiekaSpecjalistyczna: props.entry.nfzAmbulatoryjnaOpiekaSpecjalistyczna ?? false,
        Rehabilitacja: props.entry.nfzRehabilitacja ?? false,
        Stomatologia: props.entry.nfzStomatologia ?? false,
        Psychiatria: props.entry.nfzPsychiatria ?? false,
        Szpitalnictwo: props.entry.nfzSzpitalnictwo ?? false,
        ProgramyProfilaktyczne: props.entry.nfzProgramyProfilaktyczne ?? false,
        ZaopatrzenieOrtopedyczne: props.entry.nfzZaopatrzenieOrtopedyczne ?? false,
        OpiekaDlugoterminowa: props.entry.nfzOpiekaDlugoterminowa ?? false,
        NfzNotatki: props.entry.nfzNotatki,
        Komercja: props.entry.komercjaJest ?? false,
        KomercjaNotatki: props.entry.komercjaNotatki,
        DaneTechniczne: props.entry.daneTechniczne,
        Kontakty: props.entry.kontakty,
        Autoryzacje: props.entry.autoryzacje,
        AutoryzacjeEx: props.entry.autoryzacjeEx
    });

    const addContact = () => {
        const localKey = uuid();
        const newContact: ContactDetails = {
            localKey
        };
        const cloned = _.clone(model);
        cloned.Kontakty.push(newContact);
        setModel(cloned);
    }

    const onChangeBoolean = (changer: (m: CustomerViewModel, current: boolean) => void) => {
        return (e: any) => {
            const cloned = _.clone(model);
            changer(cloned, e.target.checked);
            setModel(cloned);
        };
    }

    const onChangeText = (changer: (m: CustomerViewModel, current: string | undefined) => void) => {
        return (e: any) => {
            const cloned = _.clone(model);
            changer(cloned, e.target.value);
            setModel(cloned);
        };
    }

    const onChangeMemo = (changer: (m: CustomerViewModel, current: string | undefined) => void) => {
        return (e: any) => {
            if (e.target.value && e.target.value.length <= 2000) {
                const cloned = _.clone(model);
                changer(cloned, e.target.value);
                setModel(cloned);
            }
        };
    }

    const onChangeCombo = (changer: (m: CustomerViewModel, optionKey?: string) => void) => {
        return (value: string) => {
            const cloned = _.clone(model);
            changer(cloned, value);
            setModel(cloned);
        }
    }

    const [saveCustomerMutation, { data: saveResult, loading: saveInProgress }] = useSaveCustomerMutation();
    const [removeCustomerMutation, { data: removeResult }] = useRemoveCustomerMutation();

    if (saveResult) {
        props.itemSaved();
    }

    if (removeResult) {
        props.itemRemoved();
    }

    const [actionsDisabled, setActionsDisabled] = React.useState(false);

    const removeEndExit: MouseEventHandler<HTMLButtonElement> = (event): void => {
        setActionsDisabled(true);
        const { projectId, entityId, entityVersion } = props.id;
        removeCustomerMutation({
            variables: {
                projectId: props.id.projectId,
                id: { projectId, entityId, entityVersion },
            }
        });
    }

    const saveEndExit: MouseEventHandler<HTMLButtonElement> = (event): void => {
        setActionsDisabled(true);
        const { projectId, entityId, entityVersion } = props.id;
        const entry: CustomerInput = {
            operatorEmail: model.Operator,
            billingModel: model.Rozliczenie,
            supportStatus: model.Obsluga,
            distance: Number(model.Dystans),
            customerName: model.Nazwa ?? "Nazwa klienta",
            customerCityName: model.Miejscowosc,
            customerAddress: model.Adres,
            nfzUmowa: model.UmowaZNFZ,
            nfzMaFilie: model.MaFilie,
            nfzLekarz: model.Lekarz,
            nfzPolozna: model.Polozna,
            nfzPielegniarkaSrodowiskowa: model.PielegniarkaSrodowiskowa,
            nfzMedycynaSzkolna: model.MedycynaSzkolna,
            nfzTransportSanitarny: model.TransportSanitarny,
            nfzNocnaPomocLekarska: model.NocnaPomocLekarska,
            nfzAmbulatoryjnaOpiekaSpecjalistyczna: model.AmbulatoryjnaOpiekaSpecjalistyczna,
            nfzRehabilitacja: model.Rehabilitacja,
            nfzStomatologia: model.Stomatologia,
            nfzPsychiatria: model.Psychiatria,
            nfzSzpitalnictwo: model.Szpitalnictwo,
            nfzProgramyProfilaktyczne: model.ProgramyProfilaktyczne,
            nfzZaopatrzenieOrtopedyczne: model.ZaopatrzenieOrtopedyczne,
            nfzOpiekaDlugoterminowa: model.OpiekaDlugoterminowa,
            nfzNotatki: model.NfzNotatki,
            komercjaJest: model.Komercja,
            komercjaNotatki: model.KomercjaNotatki,
            daneTechniczne: model.DaneTechniczne
        }
        const secrets = _.chain(model.Autoryzacje)
            .map(it => {
                const ret: CustomerSecretInput = {
                    location: it.location ?? '---',
                    username: it.username,
                    password: it.password,
                    otpSecret: it.otpSecret,
                    otpRecoveryKeys: it.otpRecoveryKeys
                };
                return ret;
            })
            .value()
        const secretsEx = _.chain(model.AutoryzacjeEx)
            .map(it => {
                const ret: CustomerSecretExInput = {
                    location: it.location ?? '---',
                    username: it.username,
                    password: it.password,
                    entityName: it.entityName,
                    entityCode: it.entityCode,
                    otpSecret: it.otpSecret,
                    otpRecoveryKeys: it.otpRecoveryKeys
                };
                return ret;
            })
            .value()
        const contacts = _.chain(model.Kontakty)
            .map(it => {
                const ret: CustomerContactInput = {
                    firstName: it.firstName,
                    lastName: it.lastName,
                    phoneNo: it.phoneNo,
                    email: it.email
                };
                return ret;
            })
            .value()
        saveCustomerMutation({
            variables: {
                projectId: props.id.projectId,
                id: { projectId, entityId, entityVersion },
                entry,
                secrets,
                secretsEx,
                contacts
            },
        });
    }

    if (saveInProgress) {
        return <Spin size="large" />
    }

    return (
        <>
            <div ref={topPartRef}>
                <Divider orientation='left'>Akcje:</Divider>
                <Row gutter={16}>
                    <Col offset={2} span={3}>
                        <Button type="primary" disabled={actionsDisabled} onClick={saveEndExit}>Zapisz i wyjdź</Button>
                    </Col>
                    <Col span={3}>
                        <Button type="default" danger disabled={actionsDisabled} onClick={removeEndExit}>Usuń klienta i wyjdź</Button>
                    </Col>
                </Row>
            </div>
            <div style={{ height: `calc(100vh - ${topPartHeight}px)`, overflowY: 'auto' }}>
                <Divider orientation='left'>Dane ogólne:</Divider>
                <Row gutter={16}>
                    <Col span={22} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <label>Operator:</label>
                            <Select style={{ width: '100%' }} defaultValue={model.Operator} onChange={onChangeCombo((m, v) => m.Operator = v)}>
                                {comboBoxBasicOptions.map(option => (
                                    <Option key={option.key} value={option.key}>{option.text}</Option>
                                ))}
                            </Select>
                        </Space>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={22} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <label>Obsługa:</label>
                            <Select style={{ width: '100%' }} defaultValue={model.Obsluga} onChange={onChangeCombo((m, v) => m.Obsluga = v)}>
                                {obsluga.map(option => (
                                    <Option key={option.key} value={option.key}>{option.text}</Option>
                                ))}
                            </Select>
                        </Space>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={22} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <label>Rozliczenie:</label>
                            <Select style={{ width: '100%' }} defaultValue={model.Rozliczenie} onChange={onChangeCombo((m, v) => m.Rozliczenie = v)}>
                                {rozliczenia.map(option => (
                                    <Option key={option.key} value={option.key}>{option.text}</Option>
                                ))}
                            </Select>
                        </Space>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={4} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <label>Dystans:</label>
                            <Input value={model.Dystans} onChange={onChangeText((m, v) => m.Dystans = v)} />
                        </Space>
                    </Col>
                </Row>

                <Divider orientation='left'>Dane adresowe:</Divider>
                <Row gutter={16}>
                    <Col span={22} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <label>Nazwa:</label>
                            <Input value={model.Nazwa} placeholder="Nazwa klienta" onChange={onChangeText((m, v) => m.Nazwa = v)} />
                        </Space>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={22} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <label>Miejscowość:</label>
                            <Input value={model.Miejscowosc} placeholder="Miejscowość klienta" onChange={onChangeText((m, v) => m.Miejscowosc = v)} />
                        </Space>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={22} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <label>Adres:</label>
                            <Input value={model.Adres} placeholder="Adres klienta" onChange={onChangeText((m, v) => m.Adres = v)} />
                        </Space>
                    </Col>
                </Row>

                <Row gutter={16}>
                    <Col span={11} offset={2}>
                        <Checkbox checked={model.UmowaZNFZ} onChange={onChangeBoolean((m, v) => m.UmowaZNFZ = v)}>Umowa z NFZ</Checkbox>
                    </Col>
                    <Col span={11}>
                        <Checkbox checked={model.MaFilie} onChange={onChangeBoolean((m, v) => m.MaFilie = v)}>Posiada filię</Checkbox>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={11} offset={2}>
                        <Checkbox checked={model.Lekarz} onChange={onChangeBoolean((m, v) => m.Lekarz = v)}>L Lekarz</Checkbox>
                    </Col>
                    <Col span={11}>
                        <Checkbox checked={model.PielegniarkaSrodowiskowa} onChange={onChangeBoolean((m, v) => m.PielegniarkaSrodowiskowa = v)}>PS Pielęgniarka Środowiskowa</Checkbox>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={11} offset={2}>
                        <Checkbox checked={model.Polozna} onChange={onChangeBoolean((m, v) => m.Polozna = v)}>O Położna</Checkbox>
                    </Col>
                    <Col span={11}>
                        <Checkbox checked={model.MedycynaSzkolna} onChange={onChangeBoolean((m, v) => m.MedycynaSzkolna = v)}>MPSZ Medycyna Szkolna</Checkbox>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={11} offset={2}>
                        <Checkbox checked={model.TransportSanitarny} onChange={onChangeBoolean((m, v) => m.TransportSanitarny = v)}>Transport Sanitarny</Checkbox>
                    </Col>
                    <Col span={11}>
                        <Checkbox checked={model.NocnaPomocLekarska} onChange={onChangeBoolean((m, v) => m.NocnaPomocLekarska = v)}>NPL Nocna pomoc lekarska</Checkbox>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={11} offset={2}>
                        <Checkbox checked={model.AmbulatoryjnaOpiekaSpecjalistyczna} onChange={onChangeBoolean((m, v) => m.AmbulatoryjnaOpiekaSpecjalistyczna = v)}>AOS Ambulatoryjna opieka specjalistyczna</Checkbox>
                    </Col>
                    <Col span={11}>
                        <Checkbox checked={model.Rehabilitacja} onChange={onChangeBoolean((m, v) => m.Rehabilitacja = v)}>REH Rehabilitacja</Checkbox>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={11} offset={2}>
                        <Checkbox checked={model.Stomatologia} onChange={onChangeBoolean((m, v) => m.Stomatologia = v)}>STM Stomatologia</Checkbox>
                    </Col>
                    <Col span={11}>
                        <Checkbox checked={model.Psychiatria} onChange={onChangeBoolean((m, v) => m.Psychiatria = v)}>PSY Psychiatria</Checkbox>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={11} offset={2}>
                        <Checkbox checked={model.Szpitalnictwo} onChange={onChangeBoolean((m, v) => m.Szpitalnictwo = v)}>SZP Szpitalnictwo</Checkbox>
                    </Col>
                    <Col span={11}>
                        <Checkbox checked={model.ProgramyProfilaktyczne} onChange={onChangeBoolean((m, v) => m.ProgramyProfilaktyczne = v)}>PROF Programy profilaktyczne</Checkbox>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={11} offset={2}>
                        <Checkbox checked={model.ZaopatrzenieOrtopedyczne} onChange={onChangeBoolean((m, v) => m.ZaopatrzenieOrtopedyczne = v)}>ZOP Zaopatrzenie ortopedyczne i pomocniczne</Checkbox>
                    </Col>
                    <Col span={11}>
                        <Checkbox checked={model.OpiekaDlugoterminowa} onChange={onChangeBoolean((m, v) => m.OpiekaDlugoterminowa = v)}>OPD Opieka długoterminowa</Checkbox>
                    </Col>
                </Row>
                <Divider orientation='left'>NFZ:</Divider>
                <Row gutter={16}>
                    <Col span={22} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <label>Notatki NFZ:</label>
                            <Input.TextArea value={model.NfzNotatki} placeholder="Notatki NFZ" onChange={onChangeMemo((m, v) => m.NfzNotatki = v)} />
                        </Space>
                    </Col>
                </Row>

                <Divider orientation='left'>Komercja:</Divider>
                <Row gutter={16}>
                    <Col span={8} offset={2}>
                        <Checkbox checked={model.Komercja} onChange={onChangeBoolean((m, v) => m.Komercja = v)}>Komercja</Checkbox>
                    </Col>
                </Row>
                <Row gutter={16}>
                    <Col span={22} offset={2}>
                        <Space style={{ width: '100%' }} direction='vertical'>
                            <Input.TextArea value={model.KomercjaNotatki} placeholder="Dane opisowe" onChange={onChangeMemo((m, v) => m.KomercjaNotatki = v)} />
                        </Space>
                    </Col>
                </Row>

                <Divider orientation='left'>Autoryzacje:</Divider>
                {model.AutoryzacjeEx.map(item => (
                    <UserPasswordItemExt
                        model={item}
                        changedBy={item.who ?? "-"}
                        changedWhen={item.when?.value ?? "-"}
                        onChange={v => {
                            const clone = _.clone(model);
                            const index = _.findIndex(clone.AutoryzacjeEx, it => it.localKey === v.localKey);
                            const readOnlyValues = {};
                            const newValue = { ...v, ...readOnlyValues };
                            clone.AutoryzacjeEx[index] = newValue;
                            setModel(clone);
                        }}
                        onRemove={localKey => {
                            const clone = _.clone(model);
                            const index = _.findIndex(clone.AutoryzacjeEx, it => it.localKey === localKey);
                            clone.AutoryzacjeEx.splice(index, 1);
                            setModel(clone);
                        }}
                    />
                ))}
                {model.Autoryzacje.map(item => (
                    <UserPasswordItem
                        model={item}
                        changedBy={item.who ?? "-"}
                        changedWhen={item.when?.value ?? "-"}
                        onChange={v => {
                            const clone = _.clone(model);
                            const index = _.findIndex(clone.Autoryzacje, it => it.localKey === v.localKey);
                            clone.Autoryzacje[index] = v;
                            setModel(clone);
                        }}
                        onRemove={localKey => {
                            const clone = _.clone(model);
                            const index = _.findIndex(clone.Autoryzacje, it => it.localKey === localKey);
                            clone.Autoryzacje.splice(index, 1);
                            setModel(clone);
                        }}
                    />
                ))}
                <NewSecret
                    newAuthorisationExRequested={name => {
                        const clone = _.clone(model);
                        const localKey = uuid();
                        const newItem: SecretExModel = {
                            localKey,
                            location: name
                        };
                        clone.AutoryzacjeEx.push(newItem);
                        setModel(clone);
                    }}
                    newAuthorisationRequested={name => {
                        const clone = _.clone(model);
                        const localKey = uuid();
                        const newItem: SecretModel = {
                            localKey,
                            location: name
                        };
                        clone.Autoryzacje.push(newItem);
                        setModel(clone);
                    }}
                />
                <Divider orientation='left'>Dane kontaktowe:</Divider>
                {model.Kontakty.map(item => (
                    <NewContactItem
                        model={item}
                        onChange={v => {
                            const clone = _.clone(model);
                            const index = _.findIndex(clone.Kontakty, it => it.localKey === v.localKey);
                            clone.Kontakty[index] = v;
                            setModel(clone);
                        }}
                        onRemove={localKey => {
                            const clone = _.clone(model);
                            const index = _.findIndex(clone.Kontakty, it => it.localKey === localKey);
                            clone.Kontakty.splice(index, 1);
                            setModel(clone);
                        }}
                    />
                ))}
                <Row gutter={16}>
                    <Col span={8} offset={2}>
                        <Button onClick={addContact}>Dodaj nowy kontakt</Button>
                    </Col>
                </Row>

                <Divider>Dane techniczne:</Divider>
                <Row gutter={16}>
                    <Col span={16} offset={2}>
                        <Form.Item label="Adresy IP serwerów, inne">
                            <Input.TextArea value={model.DaneTechniczne} placeholder="Adresy IP serwerów, inne" onChange={onChangeMemo((m, v) => m.DaneTechniczne = v)} />
                        </Form.Item>
                    </Col>
                </Row>

            </div>
        </>);
}
