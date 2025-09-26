import { Typography, Layout, message } from "antd";
import React from "react";
import { connect, ConnectedProps } from "react-redux";
import { RouteComponentProps } from "react-router-dom";
import { Dispatch } from "redux";
import { routing } from "../Routing";
import { RootState } from "../store/reducers";
import { Content } from "./ActionList";
import { ServiceCommandBar } from "./Commands";
import { useDownloadFile } from "../api/useDownloadFile";
import { downloadBase64File } from "../utils/fileDownloadUtils";

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
  const { downloadFile, loading: downloadLoading, error: downloadError, data: downloadData } = useDownloadFile();

  React.useEffect(() => {
    if (downloadData) {
      try {
        downloadBase64File({
          fileName: downloadData.fileName,
          content: downloadData.content,
          contentType: downloadData.contentType,
        });
        message.success('Plik został pobrany pomyślnie');
      } catch (error) {
        message.error('Błąd podczas pobierania pliku: ' + (error as Error).message);
      }
    }
  }, [downloadData]);

  React.useEffect(() => {
    if (downloadError) {
      message.error('Błąd podczas pobierania pliku: ' + downloadError.message);
    }
  }, [downloadError]);

  const handleExcelExport = () => {
    if (downloadLoading) {
      message.info('Pobieranie w toku...');
      return;
    }

    if (!props.appState.empty) {
      downloadFile({
        variables: {
          projectId: props.appState.projectId,
        },
      });
      message.info('Inicjowanie pobierania pliku...');
    } else {
      message.error('Nie wybrano projektu');
    }
  };

  return (
    <Layout style={{ height: "100%" }}>
      <Header style={{ background: "#fff" }}>
        <ServiceCommandBar
          onReportsViewRequested={() => {
        const url = routing.reports;
        props.history.push(url);
          }}
          onExcelExportRequested={handleExcelExport}
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
