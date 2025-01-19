import _ from "lodash";
import React from "react"
import { useGetCustomerQuery } from "../../Components/.generated/components";
import { Col, Row, Divider, Typography } from "antd";
const { Text } = Typography

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
        <Divider orientation="center">Dane wybranego klienta:</Divider>
      </Col>
      <Col span={24}>
        <Text strong>Nazwa: </Text><Text>{customerName}</Text><br />
        <Text strong>Miejscowość: </Text><Text>{customerCityName ?? '---'}</Text><br />
        <Text strong>Adres: </Text><Text>{customerAddress ?? '---'}</Text><br />
        <Text strong>Operator: </Text><Text>{operatorEmail ?? '---'}</Text><br />
        <Text strong>Dystans: </Text><Text>{distance ?? '---'}</Text><br />
        <Text strong>Kod świadczeniodawcy (z Portalu): </Text><Text>{specialAuthValue ?? '---'}</Text>
      </Col>
    </Row>
  );
}

export default View;
