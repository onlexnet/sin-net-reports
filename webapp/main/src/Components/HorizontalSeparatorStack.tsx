import { IStackTokens, Stack } from "office-ui-fabric-react";
import React from "react";

const stackTokens: IStackTokens = { childrenGap: 12 };
export const HorizontalSeparatorStack = (props: { children: JSX.Element[] }) => (
    <>
        {React.Children.map(props.children, child => {
            return <Stack tokens={stackTokens}>{child}</Stack>;
        })}
    </>
);