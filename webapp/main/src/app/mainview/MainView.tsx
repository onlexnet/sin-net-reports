import _ from "lodash";
import React from "react";
import { connect, ConnectedProps } from "react-redux";
import { HashRouter as Router, Route } from "react-router-dom";
import { Dispatch } from "redux";
import { useAvailableProjectsQuery } from "../../Components/.generated/components";
import { Debug } from "../../debug/Debug";
import { Home } from "../../Home";
import { NavBasicExample } from "../../NavBar";
import { Reports } from "../../reports/Reports";
import { routing } from "../../Routing";
import { Main } from "../../services";
import { AppContextAction } from "../../store/appcontext/types";
import { RootState } from "../../store/reducers";
import { ActionViewRoutedEdit } from "../actions/ActionView.Routed.Edit";
import { CustomerViewRoutedEdit } from "../customer/CustomerView.Routed.Edit";
import { CustomerViewNew } from "../customer/CustomerView.Routed.New";
import { Customers } from "../customers/Customers";

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

    const { data, loading, error } = useAvailableProjectsQuery();

    if (error) {
    return (<p>Error: {JSON.stringify(error)}</p>);
    }

    if (!data) {
        return (<p>Loading projects ...</p>);
    }

    const { availableProjects } = data;
    if (availableProjects.length == 0 ) {
        return (<p>No available projects. Please contact with your app provider.</p>);
    }

    if (availableProjects.length > 1 ) {
        return (<p>Too many available projects. Please contact with your app provider.</p>);
    }

    if (props.appState.empty) {
        const projectId = availableProjects[0];
        props.setProject(projectId);
        return (<p>Set application context ....</p>);
    }

    return (
        <Router>
            <div className="ms-Grid" dir="ltr">
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm6 ms-md4 ms-lg2">
                        <Route path="/" component={NavBasicExample} />
                    </div>
                    <div className="ms-Grid-col ms-sm6 ms-md8 ms-lg10">
                        <Route path={routing.editAction} component={ActionViewRoutedEdit} />
                        <Route path={routing.services} component={Main} exact={true} />
                        <Route path={routing.reports} component={Reports} />
                        <Route path={routing.editCustomer} component={CustomerViewRoutedEdit} />
                        <Route path={routing.newCustomer} component={CustomerViewNew} />
                        <Route path={routing.customers} component={Customers} exact={true} />
                        <Route path={routing.debug} render={(localProps) => <Debug {...props} />} />
                        <Route path="/" exact component={Home} />
                    </div>
                </div>
            </div>
        </Router>);

}

export const MainView = connect(mapStateToProps, mapDispatchToProps)(LocalView);