import React from 'react';
import { Row } from 'antd';

interface PaddedRowProps {
    children: React.ReactNode;
    gutter?: number | [number, number];
    padding?: string;
}

const PaddedRow: React.FC<PaddedRowProps> = ({ children, gutter = 0, padding = '8px' }) => {
    return (
        <Row gutter={gutter} style={{ padding }} align={'middle'}>
            {children}
        </Row>
    );
};

export default PaddedRow;
