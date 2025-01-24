import { Row, Col } from "antd";
import React from "react";

export const HorizontalSeparatorStack = (props: { children: JSX.Element[] }) => (
    <>
        {React.Children.map(props.children, child => {
            return <Row gutter={12}><Col>{child}</Col></Row>;
        })}
    </>
);
