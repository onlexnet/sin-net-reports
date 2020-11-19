import React from "react"

import { Separator } from 'office-ui-fabric-react/lib/Separator';
import { mergeStyles } from 'office-ui-fabric-react/lib/Styling';
import { Stack, IStackTokens, IStackStyles, IStackProps } from 'office-ui-fabric-react/lib/Stack';
import { Text } from 'office-ui-fabric-react/lib/Text';
import { Button, IComboBoxOption, SelectableOptionMenuItemType, ComboBox, IComboBoxProps, ITextFieldStyles, TextField, Checkbox } from "office-ui-fabric-react";
import { useConstCallback } from '@uifabric/react-hooks';
import { useBoolean } from '@uifabric/react-hooks';
import _ from "lodash";
import { useGetUsers } from "../../api/useGetUsers";


const stackStyles: Partial<IStackStyles> = { root: { width: 650 } };
const stackTokens: IStackTokens = { childrenGap: 12 };
const columnProps: Partial<IStackProps> = {
    tokens: { childrenGap: 15 },
    styles: { root: { width: 300 } },
};



const HorizontalSeparatorStack = (props: { children: JSX.Element[] }) => (
    <>
        {React.Children.map(props.children, child => {
            return <Stack tokens={stackTokens}>{child}</Stack>;
        })}
    </>
);

const VerticalSeparatorStack = (props: { children: JSX.Element[] }) => (
    <Stack horizontal horizontalAlign="space-evenly">
        {React.Children.map(props.children, child => {
            return (
                <Stack horizontalAlign="center" tokens={stackTokens}>
                    {child}
                </Stack>
            );
        })}
    </Stack>
);

const verticalStyle = mergeStyles({
    height: '200px',
});

const obsluga: IComboBoxOption[] = [
    { key: 'Header1', text: 'First heading', itemType: SelectableOptionMenuItemType.Header },
    { key: 'A', text: 'Obsługiwany' },
    { key: 'B', text: 'Nie obsługiwany' },
    { key: 'C', text: 'Obsługa czasowo zawieszona' },
];

const rozliczenia: IComboBoxOption[] = [
    { key: 'Header1', text: 'First heading', itemType: SelectableOptionMenuItemType.Header },
    { key: 'A', text: 'Ryczałt' },
    { key: 'B', text: 'Godziny' },
];



const narrowTextFieldStyles: Partial<ITextFieldStyles> = { fieldGroup: { width: 100 } };

export const CustomerView: React.FC<{}> = () => {

    const users = useGetUsers();
    const comboBoxBasicOptions: IComboBoxOption[] = []
    users.forEach(user => {
        var userItem = { key: user, text: user };
        comboBoxBasicOptions.push(userItem);
    })

    const [selectedKey, setSelectedKey] = React.useState<string | undefined>('');
    const onChange: IComboBoxProps['onChange'] = (event, option) => setSelectedKey(option!.key as string);

    const [secondTextFieldValue, setSecondTextFieldValue] = React.useState('');
    const onChangeSecondTextFieldValue = useConstCallback(
        (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
            if (!newValue || newValue.length <= 5) {
                setSecondTextFieldValue(newValue || '');
            }
        },
    );

    const [isChecked1, setIsChecked1] = React.useState(true);
    const onChange1 = React.useCallback((ev?: React.FormEvent<HTMLElement>, checked?: boolean): void => {
        setIsChecked1(!!checked);
    }, []);

    const [multiline2, { toggle: toggleMultiline2 }] = useBoolean(false);
    const onChange2 = (ev?: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newText?: string): void => {
        const newMultiline = newText?.length ?? 0 > 50;
        if (newMultiline !== multiline2) {
            toggleMultiline2();
        }
    };

    return (


        <HorizontalSeparatorStack>
            <>
                <Separator alignContent="start">Dane ogólne: </Separator>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1 ms-sm4 ">
                        <ComboBox
                            label="Operator"
                            defaultSelectedKey="B"
                            onChange={onChange}
                            selectedKey={selectedKey}
                            errorMessage={selectedKey === 'B' ? 'B is not an allowed option' : undefined}
                            options={comboBoxBasicOptions}
                        />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1 ms-sm4 ">
                        <ComboBox
                            label="Obsługa"
                            options={rozliczenia}
                        />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1 ms-sm4 ">
                        <ComboBox
                            label="Rozliczenie"
                            options={obsluga}
                        />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1 ms-sm2">
                        <TextField
                            label="Dystans"
                            value={secondTextFieldValue}
                            onChange={onChangeSecondTextFieldValue}
                            styles={narrowTextFieldStyles}
                        />
                    </div>
                </div>

                <Separator alignContent="start">Dane adresowe</Separator>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm3 ms-smPush1">
                        <TextField label="Nazwa" />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm3 ms-smPush1">
                        <TextField label="Miejscowość" />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm3 ms-smPush1">
                        <TextField label="Adres" />
                    </div>
                </div>

                <Separator alignContent="start">NFZ</Separator>

                <Stack horizontal tokens={stackTokens} styles={stackStyles}>
                </Stack>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-smPush1">
                        <Stack tokens={stackTokens}>
                            <Stack horizontal tokens={stackTokens}>
                                <Checkbox label="Umowa z NFZ" checked={isChecked1} onChange={onChange1} />
                                <Checkbox label="Posiada filię" checked={isChecked1} onChange={onChange1} />
                            </Stack>

                        </Stack>
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="L Lekarz" checked={isChecked1} onChange={onChange1} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="PS Pielęgniarka Środowiskowa" checked={isChecked1} onChange={onChange1} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="O Położna" checked={isChecked1} onChange={onChange1} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="MPSZ Medycyna Szkolna" checked={isChecked1} onChange={onChange1} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="Transport Sanitarny" checked={isChecked1} onChange={onChange1} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="NPL Nocna pomoc lekarska" checked={isChecked1} onChange={onChange1} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="AOS Ambulatoryjna opieka specjalistyczna" checked={isChecked1} onChange={onChange1} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="REH Rehabilitacja" checked={isChecked1} onChange={onChange1} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="STM Stomatologia" checked={isChecked1} onChange={onChange1} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="PSY Psychiatria" checked={isChecked1} onChange={onChange1} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="SZP Szpitalnictwo" checked={isChecked1} onChange={onChange1} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="PROF Programy profilaktyczne" checked={isChecked1} onChange={onChange1} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="ZOP Zaopatrzenie ortopedyczne i pomocniczne" checked={isChecked1} onChange={onChange1} />
                    </div>
                    <div className="ms-Grid-col ms-sm5">
                        <Checkbox label="OPD Opieka długoterminowa" checked={isChecked1} onChange={onChange1} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm6 ms-smPush1">
                        <TextField
                            multiline={true}
                            // eslint-disable-next-line react/jsx-no-bind
                            onChange={onChange2}
                        />
                    </div>
                </div>


                <Separator alignContent="start">Komercja</Separator>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm5 ms-smPush1">
                        <Checkbox label="Komercja" checked={isChecked1} onChange={onChange1} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm6 ms-smPush1">
                        <TextField
                            multiline={true}
                            placeholder="Dane opisowe"
                            // eslint-disable-next-line react/jsx-no-bind
                            onChange={onChange2}
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