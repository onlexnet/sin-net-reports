import React from "react";
import { Content } from "./ActionList";
import { ServiceCommandBar } from "./Commands";
import { RootState } from "../store/reducers";
import { connect, ConnectedProps } from "react-redux";
import { Dispatch } from "redux";
import { ViewContextState } from "../store/viewcontext/types";

const mapStateToProps = (state: RootState): ViewContextState => {
  return state.viewContext;
}
const mapDispatchToProps = (dispatch: Dispatch) => ({ });

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface MainProps extends PropsFromRedux {
}

const MainView: React.FC<MainProps> = (props) => {


  return (
    <>
      <ServiceCommandBar />

      { props.period.toString() }
      <Content />
    </>
  );
};

export const Main = connect(mapStateToProps, mapDispatchToProps)(MainView);