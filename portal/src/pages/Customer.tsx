import React from "react";
import { Card, Elevation } from "@blueprintjs/core";
import { RouteComponentProps } from "react-router-dom";

interface ICustomerProps {
  name: string;
  city: string;
  address: string;
}

export const Customer: React.FC<
  ICustomerProps & RouteComponentProps<{ id: string }>
> = props => {
  const { match } = props;
  const id = match.params.id;

  //   <p>name: {props.name} </p>
  //   <p>city: {props.city}</p>
  //   <p>address: {props.address} </p>

  return (
    <Card interactive={true} elevation={Elevation.TWO}>
      <p>Customer id: {id}</p>
      <p>Customer url: {match.url}</p>
    </Card>
  );
};
