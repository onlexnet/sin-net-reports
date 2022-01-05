import React from "react"
import { RouteComponentProps } from "react-router-dom";
import { ReportsView } from "./ReportsView";


interface ReportsViewRoutedProps extends RouteComponentProps<{ year: string, month: string }> {
}


export const ReportsViewRouted: React.FC<ReportsViewRoutedProps> = props => {
    const { year, month } = props.match.params;
    const yearAsInt = parseInt(year);
    const monthAsInt = parseInt(month);

    return <ReportsView year={yearAsInt} month={monthAsInt} />

}