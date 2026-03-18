import { Link } from "react-router-dom";
import _ from "lodash";
import { useState } from "react";
import { HorizontalSeparatorStack } from "../../components/HorizontalSeparatorStack";
import { ListCustomersItem } from "../../api/useListCustomers";
import PaddedRow from "../../components/PaddedRow";
import { Button } from "components/ui/button";
import { Input } from "components/ui/input";
import { Col } from "components/ui/layout";
import { TanStackTableView, useTanStackTableAdapter, TableAdapterColumn } from "components/table";

interface CustomersProps {
    givenProjectId: string,
    onNewClientCommand: () => void;
    listCustomers: (projectId: string) => ListCustomersItem[];
}

type SortedCustomerItem = ListCustomersItem & { sortPriority: number };

const customerTableColumns: TableAdapterColumn<SortedCustomerItem>[] = [
    {
        key: "name",
        title: "Klient",
        dataIndex: "name",
        render: (value, record) => (
            <Link to={`/customers/${record.customerId.projectId}/${record.customerId.entityId}/${record.customerId.entityVersion}`}>
                {String(value ?? "")}
            </Link>
        ),
    },
];

export const CustomersView: React.FC<CustomersProps> = (props) => {

    const items = props.listCustomers(props.givenProjectId);
    const [searchPhrase, setSearchPhrase] = useState<string | undefined>('');

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

    const { table } = useTanStackTableAdapter({
        data: sortedItems,
        columns: customerTableColumns,
    });

    return (
        <>
            <PaddedRow>
                <Col span={24}>
                    <Button onClick={() => props.onNewClientCommand()}>Dodaj nowego klienta</Button>
                </Col>
            </PaddedRow>
            <PaddedRow>
                <Col span={24}>
                    <Input style={{ width: '100%'}} placeholder="Wprowadź fragment nazwy klienta ..." value={searchPhrase} onChange={e => setSearchPhrase(e.target.value)} />
                </Col>
            </PaddedRow>
            <PaddedRow>
                <Col span={24}>
                    <div className="overflow-y-auto" style={{ maxHeight: "calc(100vh - 250px)" }}>
                        <TanStackTableView table={table} showPagination={false} />
                    </div>
                </Col>
            </PaddedRow >
        </>
    )
}
