import _ from "lodash";
import { ComboBox, DefaultButton, FocusTrapZone, IComboBox, IComboBoxOption, IStackStyles, MaskedTextField, memoizeFunction, PrimaryButton, Stack, TextField } from "office-ui-fabric-react";
import React, { KeyboardEventHandler, useCallback, useEffect, useState } from "react";
import { connect, ConnectedProps } from "react-redux";
import { dates, toActionModel } from "../api/DtoMapper";
import { useGetActionLazyQuery } from "../Components/.generated/components";
import { ServiceAppModel } from "../store/actions/ServiceModel";
import { RootState } from "../store/reducers";

const mapStateToProps = (state: RootState) => {
  return { ...state.viewContext };
};

const connector = connect(mapStateToProps);
type PropsFromRedux = ConnectedProps<typeof connector>

const EditAction: React.FC<PropsFromRedux> = props => {

  const [getAction, { loading, data }] = useGetActionLazyQuery({
    variables: {
      actionId: props.editedActionId ?? "undefined"
    }
  });

  if (props.editedActionId && !loading && !data) {
    getAction();
  }

  if (data) {
    var dto = data.Services.get;
    if (dto) {
      var model = toActionModel(dto)
      return <SomeEditAction {...model} />;
    }
  }

  return <NoneEditAction />
}

const SomeEditAction: React.FC<ServiceAppModel> = props => {

  const defaultServicemanName = props?.servicemanName;
  const [servicemanName, setServicemanName] = useState(defaultServicemanName);
  useEffect(() => {
    setServicemanName(defaultServicemanName);
  }, [defaultServicemanName]);
  const onChangeServicemanName = useCallback(
    (ev: React.FormEvent<IComboBox>, option?: IComboBoxOption) => {
      const a = option?.key as string;
      setServicemanName(a);
    },
    [setServicemanName],
  );

  const defaultDate = dates(props?.when).toRawValue;
  const [date, setDate] = useState<string>(defaultDate);
  useEffect(() => {
    setDate(defaultDate);
  }, [defaultDate]);
  const onChangeDate = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      if (!newValue) return;
      setDate(newValue);
    },
    [],
  );

  const defaultCustomerName = props?.customerName;
  const [customerName, setCustomerName] = useState(defaultCustomerName);
  useEffect(() => {
    setCustomerName(defaultCustomerName);
  }, [defaultCustomerName]);
  const onChangeCustomerName = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setCustomerName(newValue || '');
    },
    [],
  );

  const propsDescription = props?.description;
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

  const propsDuration = "" + props?.duration;
  const [duration, setDuration] = useState(propsDuration);
  useEffect(() => {
    setDuration(propsDuration)
  }, [propsDuration]);
  const onChangeDuration = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setDuration(newValue ?? "0");
    },
    [],
  );

  const propsDistance = "" + props?.distance;
  const [distance, setDistance] = useState(propsDistance);
  useEffect(() => {
    setDistance(propsDistance)
  }, [propsDistance]);
  const onChangeDistance = useCallback(
    (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
      setDistance(newValue ?? "0");
    },
    [],
  );

  const [disabledFocusTrap, setDisabledFocusTrap] = useState(false);
  useEffect(() => {
    setDisabledFocusTrap(false);
  }, [props.entityId]);

  if (!props) return (
    <p>
      Empty data
    </p>
  )

  const onKeyUp: KeyboardEventHandler<any> = (event) => {
    if (event.charCode !== 13) return;
    setDisabledFocusTrap(true);
  }

  const getStackStyles = memoizeFunction(
    (useTrapZone: boolean): Partial<IStackStyles> => ({
      root: { border: `2px solid ${useTrapZone ? '#ababab' : 'transparent'}`, padding: 10 }
    })
  );
  const stackTokens = { childrenGap: 8 }

  let comboBoxBasicOptions: IComboBoxOption[] = [];
  // if (data) {
  //   comboBoxBasicOptions = _.chain(data.Users?.search)
  //     .map(it => ({ key: it.email, text: it.email }))
  //     .value();
  // }

  return (
    <>
      <FocusTrapZone disabled={disabledFocusTrap}>
        <Stack tokens={stackTokens} styles={getStackStyles(!disabledFocusTrap)}>
          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <ComboBox label="Pracownik" selectedKey={servicemanName} options={comboBoxBasicOptions} autoComplete="on" onChange={onChangeServicemanName}
                onKeyDown={onKeyUp}
              />
            </div>
            <div className="ms-Grid-col ms-sm2">
              <MaskedTextField label="Data" value={date} onChange={onChangeDate} mask="9999/99/99"
                onKeyDown={onKeyUp}
              />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <TextField label="Klient" value={customerName} onChange={onChangeCustomerName}
                onKeyDown={onKeyUp}
              />
            </div>
            <div className="ms-Grid-col ms-sm2">
              <TextField label="Usługa" value={description} onChange={onChangeDescription}
                onKeyDown={onKeyUp}
              />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm4">
              <TextField label="Czas" value={duration} onChange={onChangeDuration}
                onKeyDown={onKeyUp}
              />
            </div>
            <div className="ms-Grid-col ms-sm2">
              <TextField label="Dojazd" value={distance} onChange={onChangeDistance}
                onKeyDown={onKeyUp}
              />
            </div>
          </div>

          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm12">
              <Stack horizontal tokens={stackTokens}>
                <PrimaryButton onClick={() => setDisabledFocusTrap(true)} />
                <DefaultButton onClick={() => setDisabledFocusTrap(true)} />
              </Stack>
            </div>
          </div>
        </Stack>
      </FocusTrapZone>
      <p>

        {JSON.stringify(props)}
      </p>
    </>

  );
}

const NoneEditAction: React.FC<{}> = props => {
  return (<></>);
}


export default connector(EditAction);
