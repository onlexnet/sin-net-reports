import { EntityId, ServiceAppModel } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import _ from "lodash";
import { ComboBox, DateRangeType, DefaultButton, IComboBox, IComboBoxOption, IComboBoxStyles, Label, PrimaryButton, Stack, TextField } from "@fluentui/react";
import React, { useCallback, useEffect, useRef, useState } from "react";
import { AppDatePicker } from "../../services/ActionList.DatePicker";
import { LocalDate } from "../../store/viewcontext/TimePeriod";
import { useGetUsers } from "../../api/useGetUsers";
import { useListCustomersQuery, useRemoveActionMutation, useUpdateActionMutation } from "../../Components/.generated/components";
import { dates } from "../../api/DtoMapper";
import CustomerView from "./ActionView.Edit.CustomerView"

const mapStateToProps = (state: RootState) => {
    if (state.appState.empty) {
        throw new Error('Invalid state');
    }
    return state;
}

const mapDispatchToProps = (dispatch: Dispatch) => {
    return {}
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;


interface ActionViewEditProps extends PropsFromRedux {
    item: ServiceAppModel,
    cancelEdit: () => void,
    actionUpdated: (entity: EntityId) => void
}

const ActionViewEditLocal: React.FC<ActionViewEditProps> = props => {

    const { item, cancelEdit, actionUpdated } = props;

    const propsProjectId = item?.projectId;
    const propsEntityId = item?.entityId;
    const propsEntityVersion = item?.entityVersion;
    const versionedProps = [propsEntityId, propsEntityVersion, propsProjectId];

    const [projectId,] = useState(propsProjectId);
    useEffect(() => {
        setEntityId(propsProjectId);
    }, [propsEntityId, propsEntityVersion, propsProjectId]);

    const [entityId, setEntityId] = useState(propsEntityId);
    useEffect(() => {
        setEntityId(propsEntityId);
    }, versionedProps);

    const [entityVersion, setEntityVersion] = useState(propsEntityVersion);
    useEffect(() => {
        setEntityVersion(propsEntityVersion);
    }, versionedProps);

    const defaultServicemanName = item?.servicemanName;
    const [servicemanName, setServicemanName] = useState(defaultServicemanName);
    const onChangeServicemanName = useCallback(
        (ev: React.FormEvent<IComboBox>, option?: IComboBoxOption) => {
            const a = option?.key as string;
            setServicemanName(a);
        },
        [setServicemanName],
    );

    const initialValue = item?.when;
    const [actionDate, setActionDate] = useState(initialValue);
    useEffect(() => {
        setActionDate(initialValue);
    }, versionedProps);
    const onChangeDate = useCallback(
        (newValue: LocalDate) => {
            setActionDate(newValue);
        },
        versionedProps,
    );

    const defaultCustomerId = item?.customer?.id.entityId;
    const [customerId, setCustomerId] = useState(defaultCustomerId);
    const onChangeCustomerId = (ev: React.FormEvent<IComboBox>, option?: IComboBoxOption) => {
        console.log('onChangeCustomerId ' + Date())
        const newCustomerId = option?.key as typeof defaultCustomerId;
        setCustomerId(newCustomerId);
    };
    const onPendingValueChanged = (option?: IComboBoxOption, index?: number, value?: string) => {
        // for some reason the event is invoked twice and value is undefined
        if (value == null) return; // it handles undefined as well

        setFilteredCustomers(filteredElements(value));
    }

    const propsDescription = item?.description;
    const [description, setDescription] = useState(propsDescription);
    const [descriptionError, setDescriptionError] = useState('');
    useEffect(() => {
        setDescription(propsDescription)
    }, [propsDescription]);
    const onChangeDescription = useCallback(
        (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
            var value = newValue ?? '';
            setDescription(value);
            var errorMessage = value.length > 4000
                ? 'Za długi opis'
                : '';
            if (errorMessage !== descriptionError) setDescriptionError(errorMessage);
        },
        [],
    );



    const [durationAsText, setDurationAsText] = useState("0:00");
    const durationRef = useRef(0);
    const [durationError, setDurationError] = useState("");
    useEffect(() => {
        const duration = props.item.duration ?? 0;
        var hours = Math.floor(duration / 60);
        var minutes = duration - hours * 60;
        setDurationAsText(hours + ':' + ('00' + minutes).substr(-2));
        durationRef.current = duration;
    }, versionedProps);
    const onChangeDuration = useCallback(
        (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
            const newDurationAsText = newValue ?? "0:00";
            const [hoursAsText, minutesAsText] = newDurationAsText.split(":")
            const hours = Number(hoursAsText);
            const minutes = Number(minutesAsText);
            const newDuration = hours * 60 + minutes;

            setDurationAsText(newDurationAsText);
            if (isNaN(newDuration)) {
                setDurationError(`Nieprawidłowy czas: ${newDurationAsText}`)
            } else {
                durationRef.current = newDuration;
                setDurationError("");
            }
        },
        versionedProps,
    );

    const propsDistance = "" + item?.distance;
    const [distance, setDistance] = useState(propsDistance);
    useEffect(() => {
        setDistance(propsDistance)
    }, versionedProps);
    const onChangeDistance = useCallback(
        (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
            setDistance(newValue ?? "0");
        },
        [],
    );

    const stackTokens = { childrenGap: 8 }

    const users = useGetUsers(projectId);

    const comboBoxBasicOptions = _.chain(users)
        .map(it => ({ key: it, text: it }))
        .value();

    const [updateActionMutation, { loading: updateActionInProgress, data: data2 }] = useUpdateActionMutation();
    if (data2) {
        actionUpdated({
            projectId: propsProjectId,
            entityId: propsEntityId,
            entityVersion: propsEntityVersion
        });
        cancelEdit();
    }

    const [removeActionMutation, { loading: removeActionInProgress, data: data3 }] = useRemoveActionMutation();
    if (data3) {
        actionUpdated({
            projectId: propsProjectId,
            entityId: propsEntityId,
            entityVersion: propsEntityVersion
        });
        cancelEdit();
    }

    const removeAndExit = () => {
        removeActionMutation({
            variables: {
                projectId,
                entityId,
                entityVersion
            }
        })
    }

    const updateAction = () => {
        const dtoDate = dates(actionDate).slashed;
        updateActionMutation({
            variables: {
                projectId,
                entityId,
                entityVersion,
                distance: Number(distance),
                duration: durationRef.current,
                what: description,
                when: dtoDate,
                who: servicemanName,
                customerId: customerId
            }
        })
    };

    const { data } = useListCustomersQuery({
        variables: {
            projectId
        }
    })

    type OptionType = { key: string, text: string }
    const [filteredCustomers, setFilteredCustomers] = useState<OptionType[]>([])


    const items = data?.Customers.list;

    const filteredElements = (partName: string) => {
        return _.chain(items)
        .map(it => ({ key: it.id.entityId, text: it.data.customerName }))
        .filter(it => {
            if (!partName) return true;
            var optionText = it.text;
            return optionText.toUpperCase().indexOf(partName.toUpperCase()) !== -1;
        })
        .value()
    }

    const renderAfterLostLoad = useRef(0);
    if (renderAfterLostLoad.current === 0 && data) {
        renderAfterLostLoad.current = renderAfterLostLoad.current + 1;
        if (renderAfterLostLoad.current === 1) {
            setFilteredCustomers(filteredElements(''));
        }
    }
    if (renderAfterLostLoad.current !== 0 && !data) {
        renderAfterLostLoad.current = 0;
    }

    const btnStyles = {
        rootHovered: {
            backgroundColor: "#d83b01"
        }
    };


    return (
        <>
            <Stack tokens={stackTokens}>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm4">
                        <div className="ms-Grid-row">
                            <div className="ms-Grid-col ms-sm12">
                                <ComboBox label="Pracownik" selectedKey={servicemanName} options={comboBoxBasicOptions} autoComplete="on" onChange={onChangeServicemanName}
                                />
                                <div className="ms-Grid-row">
                                    <CustomerView projectId={projectId} customerId={customerId} />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="ms-Grid-col ms-sm2">
                        <AppDatePicker
                            onSelectDate={value => onChangeDate(value)}
                            current={actionDate}
                            dateRangeType={DateRangeType.Day} autoNavigateOnSelection={true} showGoToToday={true} />
                    </div>
                </div>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm4">
                        <ComboBox label="Wybór klienta"
                            selectedKey={customerId}
                            options={filteredCustomers}
                            autoComplete="on"
                            allowFreeform
                            openOnKeyboardFocus
                            onChange={onChangeCustomerId}
                            onPendingValueChanged={onPendingValueChanged}
                        />
                    </div>
                    <div className="ms-Grid-col ms-sm6">
                        <TextField label="Usługa" multiline={true} value={description} errorMessage={descriptionError} onChange={onChangeDescription}
                        />
                    </div>
                </div>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm4">
                        <TextField label="Czas" placeholder="0:00" value={durationAsText} errorMessage={durationError} onChange={onChangeDuration} />
                    </div>
                    <div className="ms-Grid-col ms-sm2">
                        <TextField label="Dojazd" value={distance} onChange={onChangeDistance}
                        />
                    </div>
                </div>

                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm12">
                        <Stack horizontal tokens={stackTokens}>
                            <PrimaryButton disabled={updateActionInProgress} text="Aktualizuj"
                                onClick={() => {
                                    updateAction();
                                    // cancelEdit();
                                }} />
                            <DefaultButton onClick={() => cancelEdit()} text="Wyjdź" />
                            <DefaultButton text="Usuń i wyjdź" disabled={updateActionInProgress} styles={btnStyles} onClick={removeAndExit} />
                        </Stack>
                    </div>
                </div>
            </Stack>
        </>

    );
}

export default connect(mapStateToProps, mapDispatchToProps)(ActionViewEditLocal);

