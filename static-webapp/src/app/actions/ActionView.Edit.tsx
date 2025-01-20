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
    }, [versionedProps, props.item.duration]);
    const onChangeDuration = useCallback(
        (event: React.FormEvent<HTMLInputElement>) => {
            const newDurationAsText = event.currentTarget.value ?? "0:00";
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
        [],
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
                duration: durationRef.current,
                what: description,
                when: dtoDate,
                who: servicemanName,
                customerId: customerId
            }
        })
    };

    const labelStyle: React.CSSProperties = { textAlign: 'right', marginRight: '8px' };

    return (
        <>
            <PaddedRow>
                <LabelCol span={4} text="Pracownik:" >
                </LabelCol>
                <Col span={8}>
                    <Select style={{ width: '100%' }} id="selectServiceman" onChange={onChangeServicemanName} defaultValue={servicemanName}>
                        {comboBoxBasicOptions.map((option) => (
                            <Option key={option.key} value={option.text}>
                                {option.text}
                            </Option>
                        ))}
                    </Select>
                </Col>

                <Col span={3} style={labelStyle}>
                    <label>Data:</label>
                </Col>
                <Col span={8}>
                    <AppDatePicker
                        gotoTodayText='Idź do aktualnego miesiąca'
                        onSelectDate={value => onChangeDate(value)}
                        current={actionDate} />
                </Col>
                <Col span={1} />
            </PaddedRow>

            <PaddedRow>
                <LabelCol span={4} text="Wybór klienta:" />
                <Col span={8}>
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
                <LabelCol span={2} text="Dojazd" />
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

