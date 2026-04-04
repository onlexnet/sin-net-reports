import { Link } from "react-router-dom";
import _ from "lodash";
import { useState } from "react";
import { ListCustomersItem } from "../../api/useListCustomers";
import { Button } from "components/ui/button";
import { Input } from "components/ui/input";
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
        title: "Nazwa",
        dataIndex: "name",
        render: (value, record) => (
            <Link to={`/customers/${record.customerId.projectId}/${record.customerId.entityId}/${record.customerId.entityVersion}`}>
                {String(value ?? "")}
            </Link>
        ),
        width: "100%",
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
        <div className="w-full pr-4 md:pr-16 lg:pr-64">
            <div className="w-full space-y-4">
                <Button className="w-full" onClick={() => props.onNewClientCommand()}>
                    Dodaj nowego klienta
                </Button>
                <Input
                    className="w-full"
                    placeholder="Wprowadź fragment nazwy klienta ..."
                    value={searchPhrase}
                    onChange={e => setSearchPhrase(e.target.value)}
                />
                <div className="w-full overflow-y-auto" style={{ maxHeight: "calc(100vh - 250px)" }}>
                    <TanStackTableView table={table} showPagination={false} className="w-full" />
                </div>
            </div>
        </div>
    )
}
