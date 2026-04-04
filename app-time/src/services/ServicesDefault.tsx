import { toast } from "sonner";
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
        toast.success('Plik został pobrany pomyślnie');
      } catch (error) {
        toast.error('Błąd podczas pobierania pliku: ' + (error as Error).message);
      }
    }
  }, [downloadData]);

  React.useEffect(() => {
    if (downloadError) {
      toast.error('Błąd podczas pobierania pliku: ' + downloadError.message);
    }
  }, [downloadError]);

  const handleExcelExport = () => {
    if (downloadLoading) {
      toast.info('Pobieranie w toku...');
      return;
    }

    if (!props.appState.empty) {
      const periodValue = props.viewContext.period.getValue();
      downloadFile({
        variables: {
          projectId: props.appState.projectId,
          year: periodValue.dateFrom.year,
          month: periodValue.dateFrom.month,
        },
      });
      toast.info('Inicjowanie pobierania pliku...');
    } else {
      toast.error('Nie wybrano projektu');
    }
  };

  return (
    <div className="flex h-full flex-col">
      <div className="flex h-full w-full flex-col pr-4 md:pr-16 lg:pr-64">
        <header className="border-b bg-background px-4 py-3">
          <ServiceCommandBar
            onReportsViewRequested={() => {
              const url = routing.reports;
              props.history.push(url);
            }}
            onExcelExportRequested={handleExcelExport}
          />
        </header>
        <main className="flex-1 overflow-hidden px-2.5 py-2.5">
          <span className="text-sm">
            {"Miesiąc: " + props.viewContext.period.toString()}
          </span>
          <div style={{ height: "100%", overflowY: "auto", overflowX: "auto" }}>
            <Content />
          </div>
        </main>
      </div>
    </div>
  );
};

export const ServicesDefault = connect(mapStateToProps, mapDispatchToProps)(MainView);
