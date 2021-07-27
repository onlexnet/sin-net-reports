import { Label, Separator, TextField } from "@fluentui/react"
import _ from "lodash";
import React from "react"
import { useGetCustomerLazyQuery, useGetCustomerQuery } from "../../Components/.generated/components";

interface ViewProps {
  projectId: string | undefined,
  customerId: string | undefined
}

const View: React.FC<ViewProps> = props => {

  const [ getData, { data, error, called }] = useGetCustomerLazyQuery();

  const { projectId, customerId } = props;

  if (!projectId) return null;
  if (!customerId) return null;

  if (!called) {
    getData({
      variables: {
          projectId,
          entityId: customerId
      }
    })
  }

  if (!data) return null;

  if(error) {
      return <div>Error: {JSON.stringify(error)}</div>;
  }

  const it = data.Customers.get!;
  const { customerName, customerCityName, customerAddress, operatorEmail, distance } = it.data ;
  const specialAuth = _.chain(it.secretsEx).filter(it => it.location === 'Portal świadczeniodawcy').first().value();
  const specialAuthValue = specialAuth?.entityCode;

  return (
    <div className="ms-Grid-col ms-sm12">
      <div className="ms-Grid-row">
        <div className="ms-Grid-col ms-sm12">
          <Separator alignContent="center">Dane wybranego klienta:</Separator>
        </div>
        <div className="ms-Grid-col ms-sm12">
          <Label>Nazwa: {customerName} </Label>
          <Label>Miejscowość: {customerCityName ?? '---'} </Label>
          <Label>Adres: {customerAddress ?? '---'} </Label>
          <Label>Operator: {operatorEmail ?? '---'}</Label>
          <Label>Kod świadczeniodawcy (z Portalu): {specialAuthValue ?? '---'}</Label>
        </div>
      </div>
    </div>
  )
}

export default View;