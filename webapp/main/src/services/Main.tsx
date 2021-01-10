import React from "react";
import { Content } from "./ActionList";
import { ServiceCommandBar } from "./Commands";
import { RootState } from "../store/reducers";
import { connect, ConnectedProps } from "react-redux";
import { Dispatch } from "redux";
import { ViewContextState } from "../store/viewcontext/types";
import { addressProvider } from "../addressProvider";

const mapStateToProps = (state: RootState) => {
  return ({ viewContext: state.viewContext, appState: state.appState });
}
const mapDispatchToProps = (dispatch: Dispatch) => ({});

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface MainProps extends PropsFromRedux {
}

const MainView: React.FC<MainProps> = (props) => {

  const openInNewTab = (url: string) => {
    window.open(url, '_blank');
  }

  return (
    <>
      <ServiceCommandBar getCustomerRaport={() => {
        var projectId = props.appState.projectId;
        var dateFrom = props.viewContext.period.getValue().dateFrom;
        openInNewTab(addressProvider().host + `/raporty/klienci/${projectId}/${dateFrom.year}/${dateFrom.month}`);
      }} />

      { props.viewContext.period.toString()}
      <Content />
    </>
  );
};

export const Main = connect(mapStateToProps, mapDispatchToProps)(MainView);