import React from 'react';
import { Row } from 'antd';

interface PaddedRowProps {
    children: React.ReactNode;
    gutter?: number | [number, number];
    padding?: string;
}

const PaddedRow: React.FC<PaddedRowProps> = ({ children }) => {
    return (
        <Row gutter={[8, 8]} align={'middle'}>
            {children}
        </Row>
    );
};

export default PaddedRow;
