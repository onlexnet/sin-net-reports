import { Col, Row } from "components/ui/layout";
import React from "react";

export const HorizontalSeparatorStack = (props: { children: JSX.Element[] }) => (
    <>
        {React.Children.map(props.children, child => {
            return <Row gutter={12}><Col span={24}>{child}</Col></Row>;
        })}
    </>
);
