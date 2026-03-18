import { Table, Button, Input, Divider, Col } from "antd";
import { Link } from "react-router-dom";
import _ from "lodash";
import { useState } from "react";
import { HorizontalSeparatorStack } from "../../components/HorizontalSeparatorStack";
import { ListCustomersItem } from "../../api/useListCustomers";
import PaddedRow from "../../components/PaddedRow";

interface CustomersProps {
    givenProjectId: string,
    onNewClientCommand: () => void;
    listCustomers: (projectId: string) => ListCustomersItem[];
}

export const CustomersView: React.FC<CustomersProps> = (props) => {

    const items = props.listCustomers(props.givenProjectId);
    const [searchPhrase, setSearchPhrase] = useState<string | undefined>('');

    const columns = [
        {
            title: "Klient",
            dataIndex: "name",
            key: "name",
            render: (text: string, record: ListCustomersItem) => <Link to={`/customers/${record.customerId.projectId}/${record.customerId.entityId}/${record.customerId.entityVersion}`}>{text}</Link>,
        }
    ];

    const similarity = (actual: string, template?: string): number => {
        if (!actual) return 0;
        if (!template) return 1;
        const lowerActual = actual.toLowerCase();
        const lowerTemplate = template.toLowerCase().trim();
        if (lowerTemplate === '') return 1;
        if (lowerActual === lowerTemplate) return 100;
        if (lowerActual.startsWith(lowerTemplate)) return 80;
        if (lowerActual.includes(' ' + lowerTemplate)) return 60;
        if (lowerActual.includes(lowerTemplate)) return 40;
        return 0;
    }

    const sortedItems = _.chain(items)
        .map(it => ({ ...it, sortPriority: similarity(it.name, searchPhrase) + similarity(it.termNfzKodSwiadczeniodawcy, searchPhrase) }))
        .filter(it => it.sortPriority > 0)
        .orderBy(it => it.name)
        .orderBy(it => -it.sortPriority)
        .value();

    return (
        <>
            <PaddedRow>
                <Col span={24}>
                    <Button type="primary" onClick={() => props.onNewClientCommand()}>Dodaj nowego klienta</Button>
                </Col>
            </PaddedRow>
            <PaddedRow>
                <Col span={24}>
                    <Input style={{ width: '100%'}} placeholder="Wprowadź fragment nazwy klienta ..." value={searchPhrase} onChange={e => setSearchPhrase(e.target.value)} />
                </Col>
            </PaddedRow>
            <PaddedRow>
                <Table
                    dataSource={sortedItems}
                    columns={columns}
                    pagination={false}
                    scroll={{ y: `calc(100vh - 250px)` }}
                />
            </PaddedRow >
        </>
    )
}
