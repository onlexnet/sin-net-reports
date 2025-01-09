import { Typography, Layout } from "antd";
import React from "react";
import { connect, ConnectedProps } from "react-redux";
import { RouteComponentProps } from "react-router-dom";
import { Dispatch } from "redux";
import { routing } from "../Routing";
import { RootState } from "../store/reducers";
import { Content } from "./ActionList";
import { ServiceCommandBar } from "./Commands";

const { Text } = Typography;
const { Header, Content: AntContent } = Layout;

const mapStateToProps = (state: RootState) => {
  return ({ viewContext: state.viewContext, appState: state.appState });
}
const mapDispatchToProps = (dispatch: Dispatch) => ({});

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface MainProps extends PropsFromRedux, RouteComponentProps {
}

const MainView: React.FC<MainProps> = (props) => {

  return (
    <Layout style={{ height: "100%" }}>
      <Header>
        <ServiceCommandBar
          onReportsViewRequested={() => {
            const url = routing.reports;
            props.history.push(url);
          }}
        />
      </Header>
      <AntContent style={{ padding: "10px" }}>
        <Text>
          {"Miesiąc: " + props.viewContext.period.toString()}
        </Text>
        <div style={{ height: "100%", overflowY: "auto", overflowX: "auto" }}>
          <Content />
        </div>
      </AntContent>
    </Layout>
  );
};

export const ServicesDefault = connect(mapStateToProps, mapDispatchToProps)(MainView);
