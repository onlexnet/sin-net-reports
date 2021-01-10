import React from "react";
import { Content } from "./ActionList";
import { ServiceCommandBar } from "./Commands";
import { RootState } from "../store/reducers";
import { connect, ConnectedProps } from "react-redux";
import { Dispatch } from "redux";
import { ViewContextState } from "../store/viewcontext/types";
import { addressProvider } from "../addressProvider";

const mapStateToProps = (state: RootState): ViewContextState => {
  return state.viewContext;
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
        openInNewTab(addressProvider().host + "/raporty/klienci");
      }} />

      { props.period.toString()}
      <Content />
    </>
  );
};

export const Main = connect(mapStateToProps, mapDispatchToProps)(MainView);