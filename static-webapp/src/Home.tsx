import React from "react";
import { Col, Row } from "antd";
import { getDisplayVersion } from './app/configuration/BuildInfo';


export const Home: React.FC<{}> = (props) => {
    return (
        <Row gutter={[0, 8]}>
            <Col span={24}>
                <h1>Witaj w systemie ewidencji usług.</h1>
            </Col>
            <Col span={24}>
                <p>version: {getDisplayVersion()}</p>
            </Col>
        </Row>
    )
}
