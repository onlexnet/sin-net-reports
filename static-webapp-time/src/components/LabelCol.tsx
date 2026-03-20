
import React from 'react';
import { Col } from 'components/ui/layout';

type ColSpanType = number | string;

interface LabelColProps {
    text: string
    span: ColSpanType
}

const LabelCol: React.FC<LabelColProps> = ({ text, span }) => {
    return (
        <Col span={span} className="flex h-9 items-center justify-end pr-2">
            <label className="text-sm font-medium leading-none">{text}</label>
        </Col>
    );
};

export default LabelCol;
