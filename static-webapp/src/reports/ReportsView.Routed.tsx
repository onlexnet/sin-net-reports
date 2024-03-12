import React, { useReducer } from "react"
import { initialState, reducer } from "../store/reducers";
import { ReportsView } from "./ReportsView";

interface ReportsViewRoutedProps {
}


const ReportsViewRoutedLocal: React.FC<ReportsViewRoutedProps> = props => {
    const [state, dispatch] = useReducer(reducer, initialState);

    const projectId = state.appState.projectId;
    const { dateFrom } = state.viewContext.period.getValue();
    return <ReportsView projectId={projectId} from={dateFrom} />

}

export const ReportsViewRouted = ReportsViewRoutedLocal;