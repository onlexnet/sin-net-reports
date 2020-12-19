import React, { MouseEventHandler } from "react"

import { Separator } from 'office-ui-fabric-react/lib/Separator';
import { Stack, IStackTokens, IStackStyles, IStackProps } from 'office-ui-fabric-react/lib/Stack';
import { Button, IComboBoxOption, ComboBox, ITextFieldStyles, TextField, Checkbox, PrimaryButton, IComboBox } from "office-ui-fabric-react";
import { useBoolean } from '@uifabric/react-hooks';
import _ from "lodash";
import { useGetUsers } from "../../api/useGetUsers";
import { useSaveCustomerMutation } from "../../Components/.generated/components";
import { EntityId } from "../../store/actions/ServiceModel";


const stackStyles: Partial<IStackStyles> = { root: { width: 650 } };
const stackTokens: IStackTokens = { childrenGap: 12 };

const HorizontalSeparatorStack = (props: { children: JSX.Element[] }) => (
    <>
        {React.Children.map(props.children, child => {
            return <Stack tokens={stackTokens}>{child}</Stack>;
        })}
    </>
);

const narrowTextFieldStyles: Partial<ITextFieldStyles> = { fieldGroup: { width: 100 } };

export interface CustomerViewEntry {
    EmailOfOperator?: string,
    ModelOfSupport?: string
    ModelOfBilling?: string,
    Distance?: number,
    KlientNazwa: string,
    KlientMiejscowosc?: string,
    KlientAdres?: string
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
    NfzNotes: string | undefined;
    Komercja: boolean;
    KomercjaNotatki: string | undefined;
}

interface CustomerViewProps {
    id: EntityId
    entry: CustomerViewEntry
    itemSaved: () => void
}

export const CustomerView: React.FC<CustomerViewProps> = props => {

    const obsluga: IComboBoxOption[] = [
        { key: 'Obsługiwany', text: 'Obsługiwany' },
        { key: 'Nie obsługiwany', text: 'Nie obsługiwany' },
        { key: 'Obsługa czasowo zawieszona', text: 'Obsługa czasowo zawieszona' },
    ];
    
    const rozliczenia: IComboBoxOption[] = [
        { key: 'Ryczałt', text: 'Ryczałt' },
        { key: 'Godziny', text: 'Godziny' },
    ];

    const users = useGetUsers();
    const comboBoxBasicOptions: IComboBoxOption[] = []
    users.forEach(user => {
        var userItem = { key: user, text: user };
        comboBoxBasicOptions.push(userItem);
    })

    const [model, setModel] = React.useState<CustomerViewModel>({
        Operator: props.entry.EmailOfOperator,
        Obsluga: props.entry.ModelOfSupport,
        Rozliczenie: props.entry.ModelOfBilling,
        Dystans: props.entry.Distance?.toString(),
        Nazwa: props.entry.KlientNazwa,
        Miejscowosc: props.entry.KlientMiejscowosc,
        Adres: props.entry.KlientAdres,
        UmowaZNFZ: false,
        MaFilie: false,
        Lekarz: false,
        Polozna: false,
        PielegniarkaSrodowiskowa: false,
        MedycynaSzkolna: false,
        TransportSanitarny: false,
        NocnaPomocLekarska: false,
        AmbulatoryjnaOpiekaSpecjalistyczna: false,
        Rehabilitacja: false,
        Stomatologia: false,
        Psychiatria: false,
        Szpitalnictwo: false,
        ProgramyProfilaktyczne: false,
        ZaopatrzenieOrtopedyczne: false,
        OpiekaDlugoterminowa: false,
        NfzNotes: undefined,
        Komercja: false,
        KomercjaNotatki: undefined,
    });

    const onChangeBoolean = (changer: (m: CustomerViewModel, current: boolean) => void) => {
        return (ev?: React.FormEvent<HTMLElement>, checked?: boolean): void => {
            const cloned = _.clone(model);
            changer(cloned, checked ?? false);
            setModel(cloned);
        };
    }

    const onChangeText = (changer: (m: CustomerViewModel, current: string | undefined) => void) => {
        return (ev?: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newText?: string): void => {
            const cloned = _.clone(model);
            changer(cloned, newText);
            setModel(cloned);
        };
    }

    const onChangeCombo = (changer: (m: CustomerViewModel, optionKey?: string) => void) => {
        return (event: React.FormEvent<IComboBox>, option?: IComboBoxOption, index?: number, value?: string): void => {
          const cloned = _.clone(model);
          changer(cloned, option!.key as string);
          setModel(cloned);
        }
    }


    const [multiline2, { toggle: toggleMultiline2 }] = useBoolean(false);
    const onChange2 = (ev?: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newText?: string): void => {
        const newMultiline = newText?.length ?? 0 > 50;
        if (newMultiline !== multiline2) {
            toggleMultiline2();
        }
    };

    const [saveCustomerMutation, { data }] = useSaveCustomerMutation();

    if (data) {
        props.itemSaved();
    }

    const saveEndExit: MouseEventHandler<PrimaryButton> = (event): void => {
        const { projectId, entityId, entityVersion } = props.id;
        saveCustomerMutation({
            variables: {
                projectId: props.id.projectId,
                id: { projectId, entityId, entityVersion },
                entry: {
                    operatorEmail: model.Operator,
                    billingModel: model.Rozliczenie,
                    supportStatus: model.Obsluga,
                    distance: Number(model.Dystans),
                    customerName: model.Nazwa ?? "Nazwa klienta",
                    customerCityName: model.Miejscowosc,
                    customerAddress: model.Adres
                }
            },
        });
    }

    return (
        <HorizontalSeparatorStack>
            <>
                <Separator alignContent="start">Akcje</Separator>
                <PrimaryButton text="Zapisz i wyjdź" onClick={saveEndExit} />
                <Separator alignContent="start">Dane ogólne: </Separator>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1 ms-sm4 ">
                        <ComboBox
                            label="Operator"
                            options={comboBoxBasicOptions}
                            defaultSelectedKey={model.Operator}
                            selectedKey={model.Operator}
                            onChange={onChangeCombo((m, v) => m.Operator = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1 ms-sm4 ">
                        <ComboBox
                            label="Obsługa"
                            options={obsluga}
                            defaultSelectedKey={model.Obsluga}
                            selectedKey={model.Obsluga}
                            onChange={onChangeCombo((m, v) => m.Obsluga = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1 ms-sm4 ">
                        <ComboBox
                            label="Rozliczenie"
                            options={rozliczenia}
                            defaultSelectedKey={model.Rozliczenie}
                            selectedKey={model.Rozliczenie}
                            onChange={onChangeCombo((m, v) => m.Rozliczenie = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1 ms-sm2">
                        <TextField
                            label="Dystans"
                            value={model.Dystans}
                            onChange={onChangeText((m, v) => m.Dystans = v)}
                            styles={narrowTextFieldStyles}
                        />
                    </div>
                </div>

                <Separator alignContent="start">Dane adresowe</Separator>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm3 ms-smPush1">
                        <TextField label="Nazwa"
                            value={model.Nazwa}
                            placeholder="Nazwa klienta"
                            onChange={onChangeText((m, v) => m.Nazwa = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm3 ms-smPush1">
                        <TextField label="Miejscowość"
                            value={model.Miejscowosc}
                            placeholder="Miejscowość klienta"
                            onChange={onChangeText((m, v) => m.Miejscowosc = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm3 ms-smPush1">
                        <TextField label="Adres"
                            value={model.Adres}
                            placeholder="Adres klienta"
                            onChange={onChangeText((m, v) => m.Adres = v)} />
                    </div>
                </div>

                <Separator alignContent="start">NFZ</Separator>

                <Stack horizontal tokens={stackTokens} styles={stackStyles}>
                </Stack>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1">
                        <Stack tokens={stackTokens}>
                            <Stack horizontal tokens={stackTokens}>
                                <Checkbox label="Umowa z NFZ"
                                    checked={model.UmowaZNFZ}
                                    onChange={onChangeBoolean((m, v) => m.UmowaZNFZ = v)} />
                                <Checkbox label="Posiada filię"
                                    checked={model.MaFilie}
                                    onChange={onChangeBoolean((m, v) => m.MaFilie = v)} />
                            </Stack>

                        </Stack>
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="L Lekarz" checked={model.Lekarz} onChange={onChangeBoolean((m, v) => m.Lekarz = v)} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="PS Pielęgniarka Środowiskowa"
                            checked={model.PielegniarkaSrodowiskowa}
                            onChange={onChangeBoolean((m, v) => m.PielegniarkaSrodowiskowa = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="O Położna"
                            checked={model.Polozna}
                            onChange={onChangeBoolean((m, v) => m.Polozna = v)} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="MPSZ Medycyna Szkolna"
                            checked={model.MedycynaSzkolna}
                            onChange={onChangeBoolean((m, v) => m.MedycynaSzkolna = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="Transport Sanitarny"
                            checked={model.TransportSanitarny}
                            onChange={onChangeBoolean((m, v) => m.TransportSanitarny = v)} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="NPL Nocna pomoc lekarska"
                            checked={model.NocnaPomocLekarska}
                            onChange={onChangeBoolean((m, v) => m.NocnaPomocLekarska = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="AOS Ambulatoryjna opieka specjalistyczna"
                            checked={model.AmbulatoryjnaOpiekaSpecjalistyczna}
                            onChange={onChangeBoolean((m, v) => m.AmbulatoryjnaOpiekaSpecjalistyczna = v)} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="REH Rehabilitacja"
                            checked={model.Rehabilitacja}
                            onChange={onChangeBoolean((m, v) => m.Rehabilitacja = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="STM Stomatologia"
                            checked={model.Stomatologia}
                            onChange={onChangeBoolean((m, v) => m.Stomatologia = v)} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="PSY Psychiatria"
                            checked={model.Psychiatria}
                            onChange={onChangeBoolean((m, v) => m.Psychiatria = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="SZP Szpitalnictwo"
                            checked={model.Szpitalnictwo}
                            onChange={onChangeBoolean((m, v) => m.Szpitalnictwo = v)} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="PROF Programy profilaktyczne"
                            checked={model.ProgramyProfilaktyczne}
                            onChange={onChangeBoolean((m, v) => m.ProgramyProfilaktyczne = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="ZOP Zaopatrzenie ortopedyczne i pomocniczne"
                            checked={model.ZaopatrzenieOrtopedyczne}
                            onChange={onChangeBoolean((m, v) => m.ZaopatrzenieOrtopedyczne = v)} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="OPD Opieka długoterminowa"
                            checked={model.OpiekaDlugoterminowa}
                            onChange={onChangeBoolean((m, v) => m.OpiekaDlugoterminowa = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm6 ms-smPush1">
                        <TextField
                            multiline={true}
                            value={model.NfzNotes}
                            placeholder="Notatki NFZ"
                            onChange={onChangeText((m, v) => m.NfzNotes = v)}
                        />
                    </div>
                </div>


                <Separator alignContent="start">Komercja</Separator>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="Komercja"
                            checked={model.Komercja}
                            onChange={onChangeBoolean((m, v) => m.Komercja = v)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm6 ms-smPush1">
                        <TextField
                            multiline={true}
                            placeholder="Dane opisowe"
                            value={model.KomercjaNotatki}
                            onChange={onChangeText((m, v) => m.NfzNotes)}
                        />
                    </div>
                </div>
            </>
            <>
                <Separator alignContent="start">Autoryzacje</Separator>
                <div className="ms-Grid-row">

                    <UserPasswordItem sectionName="Portal personelu" />
                    <UserPasswordItemExt sectionName="Portal świadczeniodawcy" />
                    <UserPasswordItem sectionName="MUS" />
                    <UserPasswordItemExt sectionName="SIMP" />
                    <UserPasswordItem sectionName="SZOI" />
                    <UserPasswordItem sectionName="EWUŚ" />
                    <UserPasswordItem sectionName="ePUB" />
                    <UserPasswordItem sectionName="GUS" />
                    <UserPasswordItem sectionName="DILO" />
                </div>
            </>
            <>
                <Separator alignContent="start">Dane kontaktowe</Separator>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1">

                        <Stack tokens={stackTokens}>
                            <Stack horizontal tokens={stackTokens}>
                                <TextField placeholder="Imię" />
                                <TextField placeholder="Nazwisko" />
                                <TextField placeholder="Nr telefonu" />
                                <TextField placeholder="email" />
                                <Button text="Usuń" />
                            </Stack>
                        </Stack>
                    </div>
                </div>
            </>
            <>
                <Separator alignContent="start">Dane techniczne</Separator>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm6 ms-smPush1">
                        <TextField
                            multiline={true}
                            placeholder="Adresy IP serwerów, inne"
                            // eslint-disable-next-line react/jsx-no-bind
                            onChange={onChange2}
                        />
                    </div>
                </div>
            </>
        </HorizontalSeparatorStack>
    );
}

interface UserPasswordItemProps {
    sectionName: string
}

const UserPasswordItem: React.FC<UserPasswordItemProps> = props => {
    return (
        <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-smPush1">
                <Separator alignContent="start">{props.sectionName}</Separator>
                <Stack tokens={stackTokens}>
                    <Stack horizontal tokens={stackTokens}>
                        <TextField placeholder="Użytkownik" />
                        <TextField placeholder="Hasło" />
                        <Button text="Usuń" />
                    </Stack>
                </Stack>
            </div>
        </div>
    );
}

interface UserPasswordItemPropsExt {
    sectionName: string
}

const UserPasswordItemExt: React.FC<UserPasswordItemPropsExt> = props => {
    return (
        <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-smPush1">
                <Separator alignContent="start">{props.sectionName}</Separator>
                <Stack tokens={stackTokens}>
                    <Stack horizontal tokens={stackTokens}>
                        <TextField placeholder="Oddział NFZ" />
                        <TextField placeholder="Nr klienta" />
                        <TextField placeholder="Użytkownik" />
                        <TextField placeholder="Hasło" />
                        <Button text="Usuń" />
                    </Stack>
                </Stack>
            </div>
        </div>
    );
}
