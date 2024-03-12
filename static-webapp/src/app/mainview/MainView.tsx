import { Spinner, Stack } from "@fluentui/react";
import _ from "lodash";
import React, { useReducer } from "react";
import { HashRouter as Router, Route } from "react-router-dom";
import { useAvailableProjectsQuery } from "../../Components/.generated/components";
import { Debug } from "../../debug/Debug";
import { Home } from "../../Home";
import { NavBar } from "../../NavBar";
import { ReportsViewRouted } from "../../reports/ReportsView.Routed";
import { routing } from "../../Routing";
import { ServicesDefault } from "../../services";
import { AppContextAction } from "../../store/appcontext/types";
import { RootState, initialState, reducer } from "../../store/reducers";
import { ActionViewRoutedEdit } from "../actions/ActionView.Routed.Edit";
import { CustomerViewRoutedEdit } from "../customer/CustomerView.Routed.Edit";
import { CustomerViewNew } from "../customer/CustomerView.Routed.New";
import { CustomersRoutedConnectedView } from "../customers/Customers.Routed";
import { MainViewMultipleProjects } from "./MainView.MultipleProjects";
import { MainViewNoProjects } from "./MainView.NoProjects";

interface Props {
}

const LocalView: React.FC<Props> = (props) => {
    const [state, dispatch] = useReducer(reducer, initialState);
    const { data, error } = useAvailableProjectsQuery();

    const setProject = (projectId: string) => {
        const action: AppContextAction = {
            type: 'APPCONTEXT_READY',
            projectId
        }
        dispatch(action);
    }

    if (error) {
        return (<p>Error: {JSON.stringify(error)}</p>);
    }

    if (!data) {
        return (
            <Stack>
                <Stack.Item align="center">
                    <Spinner label="Ładowanie przydatnych projektów ....." ariaLive="assertive" labelPosition="right" />
                </Stack.Item>
            </Stack>);
    }

    const { list } = data.Projects;

    const availableProjects = _.chain(list)
        .map(it => ({ name: it?.name ?? "default id", id: it?.entity?.entityId  ?? "default id"}))
        .value();

    if (availableProjects.length === 0) {
        return <MainViewNoProjects />;
    }

    if (availableProjects.length > 1 && state.appState.empty) {
        return <MainViewMultipleProjects projects={availableProjects} projectSelected={id => setProject(id)} />;
    }

    if (availableProjects.length === 1 && state.appState.empty) {
        const project = availableProjects[0];
        setProject(project.id);
        return (<p>Set application context ....</p>);
    }

    return (
        <div style={{ height: "100vh" }}  >
            <Router>
                <Stack horizontal styles={{ root: { height: "100%" } }}>
                    <Stack.Item>
                        <Route path="/" component={NavBar} />
                    </Stack.Item>
                    <Stack.Item styles={{ root: { width: "100%", padding: "10" } }}>
                        <Route path={routing.editAction} component={ActionViewRoutedEdit} />
                        <Route path={routing.actions} component={ServicesDefault} exact={true} />
                        <Route path={routing.reports} component={ReportsViewRouted} />
                        <Route path={routing.editCustomer} component={CustomerViewRoutedEdit} />
                        <Route path={routing.newCustomer} component={CustomerViewNew} />
                        <Route path={routing.customers} component={CustomersRoutedConnectedView} exact={true} />
                        <Route path={routing.debug} render={(localProps) => <Debug {...props} />} />
                        <Route path="/" exact component={Home} />
                    </Stack.Item>
                </Stack>
            </Router>
        </div>);

}

export const MainView = LocalView;
