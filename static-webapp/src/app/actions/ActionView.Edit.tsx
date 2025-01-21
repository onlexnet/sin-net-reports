import { EntityId, ServiceAppModel } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import _ from "lodash";
import { Select, Button, Input, Space, Row, Flex, Col } from "antd";
import React, { useCallback, useEffect, useRef, useState } from "react";
import { AppDatePicker } from "../../services/ActionList.DatePicker";
import { LocalDate } from "../../store/viewcontext/TimePeriod";
import { useGetUsers } from "../../api/useGetUsers";
import { useRemoveActionMutation, useUpdateActionMutation } from "../../components/.generated/components";
import CustomerView from "./ActionView.Edit.CustomerView"
import { CustomerComboBox } from "./CustomerComboBox";
import { asDtoDate } from "../../api/Mapper";
import { useListCustomersQuery } from "../../components/.generated/components"
import PaddedRow from "../../components/PaddedRow";
import LabelCol from "../../components/LabelCol";

const { Option } = Select;

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

/**
 * View used to edit data for a Service.
 * @param props
 * @returns 
 */
export const ActionViewEditLocal: React.FC<ActionViewEditProps> = props => {
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
    }, [propsEntityId, propsEntityVersion, propsProjectId]);

    const [entityVersion, setEntityVersion] = useState(propsEntityVersion);
    useEffect(() => {
        setEntityVersion(propsEntityVersion);
    }, [propsEntityId, propsEntityVersion, propsProjectId]);

    const defaultServicemanName = item?.servicemanName;
    const [servicemanName, setServicemanName] = useState(defaultServicemanName);
    const onChangeServicemanName = useCallback(
        (ev, option) => {
            const a = option?.key as string;
            setServicemanName(a);
        },
        [setServicemanName],
    );

    const initialValue = item?.when;
    const [actionDate, setActionDate] = useState(initialValue);
    useEffect(() => {
        setActionDate(initialValue);
    }, [initialValue, propsEntityId, propsEntityVersion, propsProjectId]);
    const onChangeDate = useCallback(
        (newValue: LocalDate) => setActionDate(newValue),
        [],
    );

    const defaultCustomerId = item?.customer?.id.entityId;
    const [customerId, setCustomerId] = useState(defaultCustomerId);
    const onChangeCustomerId = (id: string | undefined) => {
        setCustomerId(id);
    };

    const propsDescription = item?.description;
    const [description, setDescription] = useState(propsDescription);
    const [descriptionError, setDescriptionError] = useState('');
    useEffect(() => {
        setDescription(propsDescription)
    }, [propsDescription]);
    const onChangeDescription = useCallback(
        (event: React.FormEvent<HTMLTextAreaElement>) => {
            var value = event.currentTarget.value ?? '';
            setDescription(value);
            var errorMessage = value.length > 4000
                ? 'Za długi opis'
                : '';
            if (errorMessage !== descriptionError) setDescriptionError(errorMessage);
        },
        [descriptionError],
    );


    const timeToText = (value: number) => {
        const hours = Math.floor(value / 60);
        const minutes = value - hours * 60;
        return hours + ':' + ('00' + minutes).substr(-2);
    }
    const textToTime = (value: string) => {


        const tested = value.trim();
        const pattern = /^\d{1,3}:\d{2}$/;
        if (!(pattern.test(tested))) {
            return NaN
        }

        const [hoursAsText, minutesAsText] = value.split(":")
        const hours = Number(hoursAsText);
        const minutes = Number(minutesAsText);
        const result = hours * 60 + minutes;
        return result;
    }

    const [durationAsText, setDurationAsText] = useState(timeToText(props.item.duration ?? 0));
    const [durationError, setDurationError] = useState("");


    const onChangeDuration = useCallback((event: React.FormEvent<HTMLInputElement>) => {

        const newDurationAsText = event.currentTarget.value ?? "0:00";
        console.log(`Split: ${newDurationAsText}`)

        const time = textToTime(newDurationAsText)
        const newDuration = timeToText(time)


        setDurationAsText(newDurationAsText);
        if (isNaN(time)) {
            setDurationError(`Nieprawidłowy czas: ${newDurationAsText}`)
        } else {
            setDurationAsText(newDurationAsText);
            setDurationError("");
        }
        },
        [],
    );

    const propsDistance = "" + item?.distance;
    const [distance, setDistance] = useState(propsDistance);
    const onChangeDistance = useCallback(
        (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>) => {
            var value = event.currentTarget.value
            setDistance(value ?? "0");
        },
        [],
    );

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

    const [removeConfirmed, setRemoveConfirmed] = useState(false);

    const removeAndExit1 = () => {
        setRemoveConfirmed(true);
    }

    const removeAndExit2 = () => {
        removeActionMutation({
            variables: {
                projectId,
                entityId,
                entityVersion
            }
        })
    }

    const updateAction = () => {
        const dtoDate = asDtoDate(actionDate);
        updateActionMutation({
            variables: {
                projectId,
                entityId,
                entityVersion,
                distance: Number(distance),
                duration: textToTime(durationAsText),
                what: description,
                when: dtoDate,
                who: servicemanName,
                customerId: customerId
            }
        })
    };

    return (
        <>
            <PaddedRow>
                <LabelCol span={3} text="Pracownik:" />
                <Col span={9}>
                    <Select style={{ width: '100%' }} id="selectServiceman" onChange={onChangeServicemanName} defaultValue={servicemanName}>
                        {comboBoxBasicOptions.map((option) => (
                            <Option key={option.key} value={option.text}>
                                {option.text}
                            </Option>
                        ))}
                    </Select>
                </Col>

                <LabelCol span={3} text="Data:"/>
                <Col span={9}>
                    <AppDatePicker
                        gotoTodayText='Idź do aktualnego miesiąca'
                        onSelectDate={value => onChangeDate(value)}
                        current={actionDate} />
                </Col>
            </PaddedRow>
            <PaddedRow>
                <LabelCol span={3} text="Wybór klienta:" />
                <Col span={9}>
                    <CustomerComboBox
                        projectId={projectId}
                        customerId={customerId}
                        onSelected={onChangeCustomerId}
                        useListCustomersQuery={useListCustomersQuery}
                    />
                </Col>
                <Col span={12}>
                    <Input.TextArea
                        value={description}
                        onChange={onChangeDescription}
                        autoSize={{ minRows: 3 }}
                        status={descriptionError ? 'error' : ''}
                    />
                    {descriptionError && <div className="ant-form-item-explain-error">{descriptionError}</div>}
                </Col>
            </PaddedRow>

            <PaddedRow>
                <Col span={12}>
                    <CustomerView projectId={projectId} customerId={customerId} />
                </Col>
                <Col span={12}>
                </Col>
            </PaddedRow>

            <PaddedRow>
                <LabelCol span={2} text="Czas" />
                <Col span={9}>
                    <Input
                        placeholder="0:00"
                        value={durationAsText}
                        onChange={onChangeDuration}
                        status={durationError ? 'error' : ''}
                    />
                    {durationError && <div className="ant-form-item-explain-error">{durationError}</div>}
                </Col>
                <LabelCol span={2} text="Dojazd:" />
                <Col span={9}>
                    <Input
                        value={distance}
                        onChange={onChangeDistance}
                    />

                </Col>
            </PaddedRow>

            <Row>
                <div className="ms-Grid" dir="ltr">
                    <div className="ms-Grid-row">
                        <div className="ms-Grid-col ms-sm6 ms-md8 ms-lg10">

                            <Row gutter={[0, 8]}>
                                <div className="ms-Grid-row">
                                    <div className="ms-Grid-col ms-sm4">
                                        <div className="ms-Grid-row">
                                            <div className="ms-Grid-col ms-sm12">
                                                <div>
                                                </div>
                                                <div className="ms-Grid-row">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="ms-Grid-col ms-sm2">
                                    </div>
                                </div>

                                <div className="ms-Grid-row">
                                    <div className="ms-Grid-col ms-sm4">
                                    </div>
                                    <div className="ant-col ant-col-sm-6">
                                    </div>
                                </div>

                                <div className="ant-row">
                                    <div className="ant-col ant-col-sm-4">
                                    </div>
                                    <div className="ant-col ant-col-sm-2">
                                    </div>
                                </div>
                                <div className="ms-Grid-row">
                                    <div className="ms-Grid-col ms-sm12">
                                        <Space>
                                            <Button type="primary" disabled={updateActionInProgress || (customerId === undefined)}
                                                onClick={() => {
                                                    updateAction();
                                                }}>Aktualizuj</Button>
                                            <Button onClick={() => cancelEdit()}>Wyjdź</Button>
                                            <Button disabled={updateActionInProgress || removeConfirmed} onClick={removeAndExit1}>Usuń i wyjdź</Button>
                                            <Button disabled={updateActionInProgress || !removeConfirmed} onClick={removeAndExit2}>Tak, Usuń i wyjdź</Button>
                                        </Space>
                                    </div>
                                </div>
                            </Row>

                        </div>
                    </div>
                </div>
            </Row>
        </>
    );
}

export default connect(mapStateToProps, mapDispatchToProps)(ActionViewEditLocal);

