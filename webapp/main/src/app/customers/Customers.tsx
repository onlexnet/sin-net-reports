import React from "react";
import { TextField, PrimaryButton, Separator, DetailsList, IColumn } from "office-ui-fabric-react";
import { Link, RouteComponentProps } from "react-router-dom";
import { routing } from "../../Routing";
import { HorizontalSeparatorStack } from "../../Components/HorizontalSeparatorStack";
import { useListCustomers, UseListCustomersItem } from "../../api/useListCustomers";
import { RootState } from "../../store/reducers";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";


const mapStateToProps = (state: RootState) => {
    if (state.appState.empty) {
        throw 'Invalid state';
    }
    return state;
}

const mapDispatchToProps = (dispatch: Dispatch) => {
    return { }
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;
  
interface CustomersProps extends PropsFromRedux, RouteComponentProps {

}

const CustomersLocal: React.FC<CustomersProps> = (props) => {

    interface TypedColumn extends IColumn {
        fieldName: keyof UseListCustomersItem;
    }

    const items = useListCustomers(props.appState.projectId);
    const columns: TypedColumn[] = [
        {
            key: "column1", name: "Klient", fieldName: "name", minWidth: 70, maxWidth: 90, isResizable: true, isCollapsible: true, data: "string",
            onRender: (item: UseListCustomersItem) => {
                return <Link to={`/customers/${item.customerId.projectId}/${item.customerId.entityId}/${item.customerId.entityVersion}`}>{item.name}</Link>;
            },
            isPadded: true
        }
    ];
    return (
        <div className="ms-Grid">
            <HorizontalSeparatorStack >
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm3">
                        <PrimaryButton text="Dodaj nowego klienta" onClick={() => props.history.push(routing.newCustomer)} />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm12">
                        <TextField placeholder="Softowanie: wprowadź fragment nazwy klienta ..." />
                    </div>
                </div>
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm12">
                        <TextField placeholder="Filtrowanie: wprowadź filtr wyszukiwania ..." />
                    </div>
                </div>

                <div className="ms-Grid-row">


                    <Separator alignContent="start"></Separator>

                    <div className="ms-Grid-col ms-sm12">
                        <DetailsList
                            items={items}
                            compact={true}
                            columns={columns}
                            setKey="none"
                            isHeaderVisible={true}
                        />
                    </div>
                </div>

            </HorizontalSeparatorStack>
        </div >
    )
}


export const Customers = connect(mapStateToProps, mapDispatchToProps)(CustomersLocal);