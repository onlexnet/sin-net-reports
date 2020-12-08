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
import { RootState } from "../../store/reducers";
import { SessionState } from "../../store/session/types";
import { CustomerView } from "../customer/CustomerView";
import { Customers } from "../customers/Customers";

const mapStateToProps = (state: RootState): SessionState => {
    return state.auth;
}
const mapDispatchToProps = (dispatch: Dispatch) => {
    return {
    }
}
const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface Props extends PropsFromRedux {
}

const LocalView: React.FC<Props> = (props) => {

    const { data, loading, error } = useAvailableProjectsQuery();

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

    const project = availableProjects[0];
    return (
        <Router>
            <div className="ms-Grid" dir="ltr">
                <div className="ms-Grid-row">
                    <div className="ms-Grid-col ms-sm6 ms-md4 ms-lg2">
                        <Route path="/" component={NavBasicExample} />
                    </div>
                    <div className="ms-Grid-col ms-sm6 ms-md8 ms-lg10">
                        <Route path={routing.services} component={Main} />
                        <Route path={routing.customers} component={Customers} />
                        <Route path={routing.reports} component={Reports} />
                        <Route path={routing.newCustomer} component={CustomerView} />
                        <Route path={routing.debug} render={(localProps) => <Debug {...props} />} />
                        <Route path="/" exact component={Home} />
                    </div>
                </div>
            </div>
        </Router>);

}

export const MainView = connect(mapStateToProps, mapDispatchToProps)(LocalView);