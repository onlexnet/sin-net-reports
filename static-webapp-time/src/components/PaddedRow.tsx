import React from 'react';
import { Row } from 'components/ui/layout';

interface PaddedRowProps {
    children: React.ReactNode;
    gutter?: number | [number, number];
    padding?: string;
}

const PaddedRow: React.FC<PaddedRowProps> = ({ children, gutter = [8, 8] }) => {
    return (
        <Row gutter={gutter} align={'middle'}>
            {children}
        </Row>
    );
};

export default PaddedRow;
