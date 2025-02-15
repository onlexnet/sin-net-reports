import React from "react";
import packageJson from '../package.json';
import { Col, Row } from "antd";


export const Home: React.FC<{}> = (props) => {
    return (
        <Row gutter={[0, 8]}>
            <Col span={24}>
                <h1>Witaj w systemie ewidencji usług.</h1>
            </Col>
            <Col span={24}>
                <p>version: {packageJson.version}</p>
            </Col>
        </Row>
    )
}
