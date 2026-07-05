import React, { useState } from "react";
import { Link, RouteComponentProps } from "react-router-dom";
import { routing } from "../../Routing";
import { useListCustomers, ListCustomersItem } from "../../api/useListCustomers";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import _ from "lodash";
import { Button } from "components/ui/button";
import { Input } from "components/ui/input";
import { Separator } from "components/ui/separator";
import { TanStackTableView, useTanStackTableAdapter, TableAdapterColumn } from "components/table";
import { PageContentContainer } from "components/ui/page-content-container";

const mapStateToProps = (state: RootState) => {
    if (state.appState.empty) {
        throw new Error('Invalid state');
    }
    return state;
}

const mapDispatchToProps = (dispatch: Dispatch) => {
    return { }
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;
  
interface ReportsProps extends PropsFromRedux, RouteComponentProps {

}

type SortedCustomerItem = ListCustomersItem & { sortPriority: number };

const reportsCustomerColumns: TableAdapterColumn<SortedCustomerItem>[] = [
    {
        key: "name",
        title: "Klient",
        dataIndex: "name",
        render: (value, record) => (
            <Link to={`/customers/${record.customerId.projectId}/${record.customerId.entityId}/${record.customerId.entityVersion}`}>
                {String(value ?? "")}
            </Link>
        ),
        width: "100%",
    },
];

const Reports: React.FC<ReportsProps> = (props) => {

    const items = useListCustomers(props.appState.projectId);
    const [searchPhrase, setSearchPhrase] = useState<string | undefined>('');

    const similarity = (actual: string, template?: string): number => {
        if (!actual) return 0;
        if (!template) return 1;
        const lowerActual = actual.toLowerCase();
        const lowerTemplate = template.toLowerCase().trim();
        if (lowerTemplate === '') return 1;
        if (lowerActual === lowerTemplate) return 100;
        if (lowerActual.startsWith(lowerTemplate)) return 80;
        if (lowerActual.includes(' '+lowerTemplate)) return 60;
        if (lowerActual.includes(lowerTemplate)) return 40;
        return 0;
    }

    const sortedItems = _.chain(items)
        .map(it => ({ ...it, sortPriority: similarity(it.name, searchPhrase) }))
        .filter(it => it.sortPriority > 0)
        .orderBy(it => it.name)
        .orderBy(it => -it.sortPriority)
        .value();

    const { table } = useTanStackTableAdapter({
        data: sortedItems,
        columns: reportsCustomerColumns,
    });

    return (
        <PageContentContainer>
            <div className="w-full space-y-4">
                <Button className="w-full" onClick={() => props.history.push(routing.newCustomer)}>
                    Dodaj nowego klienta
                </Button>
                <Input
                    className="w-full"
                    placeholder="Wprowadź fragment nazwy klienta ..."
                    value={searchPhrase}
                    onChange={(e) => setSearchPhrase(e.target.value)}
                />
                <Separator />
                <div className="w-full overflow-y-auto" style={{ maxHeight: "calc(100vh - 250px)" }}>
                    <TanStackTableView table={table} showPagination={false} className="w-full" />
                </div>
            </div>
        </PageContentContainer>
    )
}

export const Customers = connect(mapStateToProps, mapDispatchToProps)(Reports);
