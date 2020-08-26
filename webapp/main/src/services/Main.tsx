import React from "react";
import { Content } from "./Content";
import { ServiceCommandBar } from "./Commands";
import { IconButton, FirstWeekOfYear } from "office-ui-fabric-react";
import { RootState } from "../store/reducers";
import { ServicesState } from "../store/services/types";
import { nextPeriod, previousPeriod } from "../store/viewcontext/actions";
import { connect, ConnectedProps } from "react-redux";
import { Dispatch } from "redux";
import { ViewContextState } from "../store/viewcontext/types";

const mapStateToProps = (state: RootState): ViewContextState => {
  return state.viewContext;
}
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    nextPeriod: () => {
      dispatch(nextPeriod());
    },
    previousPeriod: () => dispatch(previousPeriod())
  }
}

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface MainProps extends PropsFromRedux {
}

const MainView: React.FC<MainProps> = (props) => {


  return (
    <>
      <ServiceCommandBar
        onNextMonthRequested={() => props.nextPeriod()}
        onPreviousMonthRequested={() => props.previousPeriod()}
      />

      { props.date.toString() }

      <Content />
    </>
  );
};

export const Main = connect(mapStateToProps, mapDispatchToProps)(MainView);