import React, { useState } from "react";
import { Input, Button, Divider, Table } from "antd";
import { Link, RouteComponentProps } from "react-router-dom";
import { routing } from "../../Routing";
import { HorizontalSeparatorStack } from "../../components/HorizontalSeparatorStack";
import { useListCustomers, ListCustomersItem } from "../../api/useListCustomers";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import _ from "lodash";

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

const Reports: React.FC<ReportsProps> = (props) => {

    const items = useListCustomers(props.appState.projectId);
    const [searchPhrase, setSearchPhrase] = useState<string | undefined>('');

    const columns = [
        {
            title: "Klient",
            dataIndex: "name",
            key: "name",
            render: (text: string, item: ListCustomersItem) => (
                <Link to={`/customers/${item.customerId.projectId}/${item.customerId.entityId}/${item.customerId.entityVersion}`}>{text}</Link>
            )
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

    return (
        <div>
            <HorizontalSeparatorStack >
                <div>
                    <Button type="primary" onClick={() => props.history.push(routing.newCustomer)}>Dodaj nowego klienta</Button>
                </div>
                <div>
                    <Input placeholder="Wprowadź fragment nazwy klienta ..." value={searchPhrase} onChange={(e) => setSearchPhrase(e.target.value)} />
                </div>
                <div>
                    <Divider />
                    <Table dataSource={sortedItems} columns={columns} pagination={false} scroll={{ y: `calc(100vh - 250px)` }} />
                </div>
            </HorizontalSeparatorStack>
        </div>
    )
}

export const Customers = connect(mapStateToProps, mapDispatchToProps)(Reports);
