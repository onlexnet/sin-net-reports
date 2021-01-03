import React from "react"
import { EntityId } from "../../store/actions/ServiceModel";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { AuthorisationModel, CustomerView, CustomerViewEntry } from "./CustomerView";
import { useGetCustomerQuery } from "../../Components/.generated/components";
import _ from "lodash";
import { v1 as uuid } from 'uuid';

const mapStateToProps = (state: RootState) => {
    if (state.appState.empty) {
        throw 'Invalid state';
    }
    return state;
}

const mapDispatchToProps = (dispatch: Dispatch) => {
    return { }
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;
  
interface CustomerViewEditProps extends PropsFromRedux {
    id: EntityId,
    itemSaved: () => void
}

  
export const CustomerViewEditLocal: React.FC<CustomerViewEditProps> = props => {
    const { data, error } = useGetCustomerQuery({
        variables: {
            projectId: props.appState.projectId,
            id: props.id
        }
    })
    
    if(error) {
        return <div>Error: {JSON.stringify(error)}</div>;
    }

    if (data) {
        const id = data.Customers.get?.id
        const input = data.Customers.get;
        if (!id) {
            return <div>No data</div>;
        }

        const autoryzacje = _.chain(input?.authorizations)
            .map(it => {
                const ret: AuthorisationModel = {
                    localKey: uuid(),
                    location: it.location,
                    username: it.username ?? undefined,
                    password: it.password ?? undefined,
                    who: it.changedWho,
                    when: it.changedWhen
                };
                return ret;
            })
            .value();

        const autoryzacjeEx = _.chain(input?.authorizations)
        .map(it => {
            const ret: AuthorisationModel = {
                localKey: uuid(),
                location: it.location,
                username: it.username ?? undefined,
                password: it.password ?? undefined,
                who: it.changedWho,
                when: it.changedWhen
            };
            return ret;
        })
        .value();

        const entry: CustomerViewEntry = {
            EmailOfOperator: input?.data.operatorEmail ?? undefined,
            ModelOfBilling: input?.data.billingModel ?? undefined,
            ModelOfSupport: input?.data.supportStatus ?? undefined,
            Distance: input?.data.distance ?? undefined,
            KlientNazwa: input?.data.customerName ?? 'Nowy klient',
            KlientMiejscowosc: input?.data.customerCityName ?? undefined,
            KlientAdres: input?.data.customerAddress ?? undefined,
            nfzUmowa: input?.data.nfzUmowa ?? undefined,
            nfzMaFilie: input?.data.nfzMaFilie ?? undefined,
            nfzLekarz: input?.data.nfzLekarz ?? undefined,
            nfzPolozna: input?.data.nfzPolozna ?? undefined,
            nfzPielegniarkaSrodowiskowa: input?.data.nfzPielegniarkaSrodowiskowa ?? undefined,
            nfzMedycynaSzkolna: input?.data.nfzMedycynaSzkolna ?? undefined,
            nfzTransportSanitarny: input?.data.nfzTransportSanitarny ?? undefined,
            nfzNocnaPomocLekarska: input?.data.nfzNocnaPomocLekarska ?? undefined,
            nfzAmbulatoryjnaOpiekaSpecjalistyczna: input?.data.nfzAmbulatoryjnaOpiekaSpecjalistyczna ?? undefined,
            nfzRehabilitacja: input?.data.nfzRehabilitacja ?? undefined,
            nfzStomatologia: input?.data.nfzStomatologia ?? undefined,
            nfzPsychiatria: input?.data.nfzPsychiatria ?? undefined,
            nfzSzpitalnictwo: input?.data.nfzSzpitalnictwo ?? undefined,
            nfzProgramyProfilaktyczne: input?.data.nfzProgramyProfilaktyczne ?? undefined,
            nfzZaopatrzenieOrtopedyczne: input?.data.nfzZaopatrzenieOrtopedyczne ?? undefined,
            nfzOpiekaDlugoterminowa: input?.data.nfzOpiekaDlugoterminowa ?? undefined,
            nfzNotatki: input?.data.nfzNotatki ?? undefined,
            komercjaJest: input?.data.komercjaJest ?? undefined,
            komercjaNotatki: input?.data.komercjaNotatki ?? undefined,
            autoryzacje
        }
        return <CustomerView id={id} entry={entry} itemSaved={props.itemSaved}/>;
    }

    return <div>Loading customer details...</div>
}

export const CustomerViewEdit = connect(mapStateToProps, mapDispatchToProps)(CustomerViewEditLocal);