import { Loader2 } from "lucide-react";
import _ from "lodash";
import React from "react";
import { connect, ConnectedProps } from "react-redux";
import { HashRouter as Router, Route } from "react-router-dom";
import { Dispatch } from "redux";
import { useAvailableProjectsQuery } from "../../components/.generated/components";
import { Debug } from "../../debug/Debug";
import { Home } from "../../Home";
import { NavBar } from "../../NavBar";
import { ReportsViewRouted } from "../../reports/ReportsView.Routed";
import { routing } from "../../Routing";
import { ServicesDefault } from "../../services";
import { AppContextAction } from "../../store/appcontext/types";
import { RootState } from "../../store/reducers";
import { ActionViewRoutedEdit } from "../actions/ActionView.Routed.Edit";
import { CustomerViewRoutedEdit } from "../customer/CustomerView.Routed.Edit";
import { CustomerViewNew } from "../customer/CustomerView.Routed.New";
import { CustomersRoutedConnectedView } from "../customers/Customers.Routed";
import { MainViewMultipleProjects } from "./MainView.MultipleProjects";
import { MainViewNoProjects } from "./MainView.NoProjects";

const mapStateToProps = (state: RootState) => {
    return { auth: state.auth, appState: state.appState };
}
const mapDispatchToProps = (dispatch: Dispatch) => {
    return {
        setProject: (projectId: string) => {
            const action: AppContextAction = {
                type: 'APPCONTEXT_READY',
                projectId
            }
            dispatch(action);
        }
    }
}
const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface Props extends PropsFromRedux {
}

const LocalView: React.FC<Props> = (props) => {

    const { data, error } = useAvailableProjectsQuery();

    if (error) {
        return (<p>Error: {JSON.stringify(error)}</p>);
    }

    if (!data) {
        return (
            <div className="flex min-h-screen w-full items-start justify-center pt-8">
                <div className="flex items-center">
                    <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
                    <span className="ml-2 text-muted-foreground">Ładowanie przydatnych projektów .....</span>
                </div>
            </div>);
    }

    const { list } = data.Projects;

    const availableProjects = _.chain(list)
        .map(it => ({ name: it?.name ?? "default id", id: it?.entity?.entityId  ?? "default id"}))
        .value();

    if (availableProjects.length === 0) {
        return <MainViewNoProjects />;
    }

    if (availableProjects.length > 1 && props.appState.empty) {
        return (
            <div className="flex min-h-screen w-full items-start justify-center pt-8">
                <MainViewMultipleProjects projects={availableProjects} projectSelected={id => props.setProject(id)} />
            </div>
        );
    }

    if (availableProjects.length === 1 && props.appState.empty) {
        const project = availableProjects[0];
        props.setProject(project.id);
        return (<p>Set application context ....</p>);
    }

    return (
        <div className="h-screen">
            <Router>
                <div className="flex h-full">
                    <aside className="w-64 shrink-0 border-r bg-background">
                        <Route path="/" component={NavBar}/>
                    </aside>
                    <main className="min-w-0 flex-1 overflow-auto">
                        <Route path={routing.editAction} component={ActionViewRoutedEdit} />
                        <Route path={routing.actions} component={ServicesDefault} exact={true} />
                        <Route path={routing.reports} component={ReportsViewRouted} />
                        <Route path={routing.editCustomer} component={CustomerViewRoutedEdit} />
                        <Route path={routing.newCustomer} component={CustomerViewNew} />
                        <Route path={routing.customers} component={CustomersRoutedConnectedView} exact={true} />
                        <Route path={routing.debug} render={(localProps) => <Debug {...props} />} />
                        <Route path="/" exact component={Home} />
                    </main>
                </div>
            </Router>
        </div>);

}

export const MainView = connect(mapStateToProps, mapDispatchToProps)(LocalView);
