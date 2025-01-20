
import React from 'react';
import { Col } from 'antd';

type ColSpanType = number | string;

interface LabelColProps {
    text: string
    span: ColSpanType
}

const labelStyle: React.CSSProperties = { textAlign: 'right', marginRight: '8px' };

const LabelCol: React.FC<LabelColProps> = ({ children, text, span }) => {
    return (
        <Col span={span} style={labelStyle}>
            <label>{text}</label>
        </Col>
    );
};

export default LabelCol;
