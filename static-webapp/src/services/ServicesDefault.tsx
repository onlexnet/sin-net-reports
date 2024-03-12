import { Label, Stack } from "@fluentui/react";
import React, { useReducer } from "react";
import { RouteComponentProps } from "react-router-dom";
import { routing } from "../Routing";
import { Content } from "./ActionList";
import { ServiceCommandBar } from "./Commands";
import { initialState, reducer } from "../store/reducers";


interface MainProps extends RouteComponentProps {
}

const MainView: React.FC<MainProps> = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);

  return (
    <Stack styles={{
      root: { width: "100%", height: "100%" }
    }}>
      <Stack.Item>
        <ServiceCommandBar
          onReportsViewRequested={() => {
            const url = routing.reports;
            props.history.push(url);
          }}
           />
      </Stack.Item>
      <Stack.Item  >
        <div style={{ padding: "10px" }}>
          <Label >
            {"Miesiąc: " + state.viewContext.period.toString()}
          </Label>
        </div>
      </Stack.Item>
      <Stack.Item verticalFill
        styles={{
          root: {
            height: "100%",
            overflowY: "auto",
            overflowX: "auto",
          },
        }}>
        <Content />
      </Stack.Item>

    </Stack>
  );
};

export const ServicesDefault = MainView;
