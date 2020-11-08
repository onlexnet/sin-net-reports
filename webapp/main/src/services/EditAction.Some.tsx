import _ from "lodash";
import { ComboBox, DefaultButton, FocusTrapZone, IComboBox, IComboBoxOption, IStackStyles, MaskedTextField, memoizeFunction, PrimaryButton, Stack, TextField } from "office-ui-fabric-react";
import React, { useCallback, useEffect, useState } from "react";
import { dates } from "../api/DtoMapper";
import { useGetUsersQuery, useUpdateActionMutation } from "../Components/.generated/components";
import { EntityId, ServiceAppModel } from "../store/actions/ServiceModel";

interface EditActionSomeProps {
  item: ServiceAppModel,
  cancelEdit: () => void,
  actionUpdated: (entity: EntityId) => void
}

/**
 * Place to take decision what state visualisation should be displayed based on give action id.
 * @param props 
 */
export const EditActionSome: React.FC<EditActionSomeProps> = props => {

  const { item, cancelEdit, actionUpdated } = props;

  const propsEntityId = item?.entityId;
  const propsEntityVersion = item?.entityVersion;
  const versionedProps = [propsEntityId, propsEntityVersion];

  const [entityId, setEntityId] = useState(propsEntityId);
  useEffect(() => {
    setEntityId(propsEntityId);
  }, versionedProps);

  const [entityVersion, setEntityVersion] = useState(propsEntityVersion);
  useEffect(() => {
    setEntityVersion(propsEntityVersion);
  }, versionedProps);

  console.log(`edit : ${propsEntityId}:${propsEntityVersion}`);

  const defaultServicemanName = item?.servicemanName;
  const [servicemanName, setServicemanName] = useState(defaultServicemanName);
  // useEffect(() => {
  //   setServicemanName(defaultServicemanName);
  // }, [defaultServicemanName]);
  const onChangeServicemanName = useCallback(
    (ev: React.FormEvent<IComboBox>, option?: IComboBoxOption) => {
      const a = option?.key as string;
      setServicemanName(a);
    },
    [setServicemanName],
  );

  const defaultDate = dates(item?.when).noSeparator;
  const [date, setDate] = useState(defaultDate);
  // useEffect(() => {
  //   setDate(defaultDate);
  // }, [defaultDate]);
  const onChangeDate = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      if (!newValue) return;
      setDate(newValue);
    },
    [],
  );

  const defaultCustomerName = item?.customerName;
  const [customerName, setCustomerName] = useState(defaultCustomerName);
  // useEffect(() => {
  //   setCustomerName(defaultCustomerName);
  // }, [defaultCustomerName]);
  const onChangeCustomerName = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setCustomerName(newValue || '');
    },
    [],
  );

  const propsDescription = item?.description;
  const [description, setDescription] = useState(propsDescription);
  useEffect(() => {
    setDescription(propsDescription)
  }, [propsDescription]);
  const onChangeDescription = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setDescription(newValue || '');
    },
    [],
  );

  const propsDuration = "" + item?.duration;
  const [duration, setDuration] = useState(propsDuration);
  useEffect(() => {
    setDuration(propsDuration)
  }, [propsDuration]);
  const onChangeDuration = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setDuration(newValue ?? "0");
    },
    [propsEntityId, propsEntityVersion],
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

  const [disabledFocusTrap, setDisabledFocusTrap] = useState(false);
  // useEffect(() => {
  //   setDisabledFocusTrap(false);
  // }, [item.entityId]);

  const getStackStyles = memoizeFunction(
    (useTrapZone: boolean): Partial<IStackStyles> => ({
      root: { border: `2px solid ${useTrapZone ? '#ababab' : 'transparent'}`, padding: 10 }
    })
  );
  const stackTokens = { childrenGap: 8 }

  const { loading, data } = useGetUsersQuery();
  var users: string[] = [];
  if (loading) {
    //
  } else if (data) {
    const result = _.chain(data.Users?.search).map(it => it.email).value();

    if (result.length == 0) {
      console.error("Server does return invalid list of users.");
    }

    users = result;
  }

  const comboBoxBasicOptions = _.chain(users)
    .map(it => ({ key: it, text: it }))
    .value();

  const [updateActionMutation, { loading: updateActionInProgress, data: data2 }] = useUpdateActionMutation();
  if (data2) {
    actionUpdated({
      entityId: propsEntityId,
      entityVersion: propsEntityVersion
    });
    cancelEdit();
  }

  const updateAction = () => {
    let yearAsString;
    let monthAsString;
    let dayAsString;
    if (/[0-9]{8}/.test(date)) {
      yearAsString = date.substring(0, 4);
      monthAsString = date.substring(4, 6);
      dayAsString = date.substring(6, 8);
    } else if (/[0-9]{4}\/[0-9]{2}\/[0-9]{2}/.test(date)) {
      yearAsString = date.substring(0, 4);
      monthAsString = date.substring(5, 7);
      dayAsString = date.substring(8, 10);
    }

    const dateDto = ('0000' + yearAsString).substr(-4) +
      '/' + ('00' + monthAsString).substr(-2) +
      '/' + ('00' + dayAsString).substr(-2);

    updateActionMutation({
      variables: {
        entityId,
        entityVersion,
        distance: Number(distance),
        duration: Number(duration),
        what: description,
        when: dateDto,
        who: servicemanName,
        whom: customerName
      }
    })
  };

  return (
    <>
      <FocusTrapZone disabled={disabledFocusTrap}>
        <Stack tokens={stackTokens} styles={getStackStyles(!disabledFocusTrap)}>
          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <ComboBox label="Pracownik" selectedKey={servicemanName} options={comboBoxBasicOptions} autoComplete="on" onChange={onChangeServicemanName}
              />
            </div>
            <div className="ms-Grid-col ms-sm2">
              <MaskedTextField label="Data" value={date} onChange={onChangeDate} mask="9999/99/99"
              />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <TextField label="Klient" value={customerName} onChange={onChangeCustomerName}
              />
            </div>
            <div className="ms-Grid-col ms-sm6">
              <TextField label="Usługa" value={description} onChange={onChangeDescription}
              />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <TextField label="Czas" value={duration} onChange={onChangeDuration} />
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
                <DefaultButton onClick={() => cancelEdit()} text="Anuluj" />
              </Stack>
            </div>
          </div>
        </Stack>
      </FocusTrapZone>
    </>

  );
}
