import _ from "lodash";
import React from "react"
import { useGetCustomerQuery } from "../../components/.generated/components";
import { Col, Row } from "components/ui/layout";
import { Separator } from "components/ui/separator";

interface ViewProps {
  projectId: string | undefined,
  customerId: string | undefined
}

const View: React.FC<ViewProps> = props => {

  const { projectId, customerId } = props;
  const { data } = useGetCustomerQuery({
    variables: {
        projectId: projectId!,
        entityId: customerId!
    }});

  const it = data
    ? data.Customers.get!
    : {
      data: {
        customerName: '...',
        customerCityName: '...',
        customerAddress: '...',
        operatorEmail: '...',
        distance: '...'
      },
      secretsEx: [ ]
    }

  const { customerName, customerCityName, customerAddress, operatorEmail, distance } = it.data ;
  const specialAuth = _.chain(it.secretsEx).filter(it => it.location === 'Portal świadczeniodawcy').first().value();
  const specialAuthValue = specialAuth?.entityCode;

  return (
    <Row gutter={[8, 8]}>
      <Col span={24}>
        <div className="flex items-center gap-3 my-2">
          <span className="text-sm font-semibold text-muted-foreground whitespace-nowrap">Dane wybranego klienta:</span>
          <Separator className="flex-1" />
        </div>
      </Col>
      <Col span={24}>
        <p className="text-sm"><span className="font-semibold">Nazwa: </span><span>{customerName}</span></p>
        <p className="text-sm"><span className="font-semibold">Miejscowość: </span><span>{customerCityName ?? '---'}</span></p>
        <p className="text-sm"><span className="font-semibold">Adres: </span><span>{customerAddress ?? '---'}</span></p>
        <p className="text-sm"><span className="font-semibold">Operator: </span><span>{operatorEmail ?? '---'}</span></p>
        <p className="text-sm"><span className="font-semibold">Dystans: </span><span>{distance ?? '---'}</span></p>
        <p className="text-sm"><span className="font-semibold">Kod świadczeniodawcy (z Portalu): </span><span>{specialAuthValue ?? '---'}</span></p>
      </Col>
    </Row>
  );
}

export default View;
