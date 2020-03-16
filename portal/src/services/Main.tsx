import React from "react"
import { Content } from "./Content"
import { CommandBarBasicExample } from "./Commands"

export const Main : React.FC<{}> = (props) => {

    return (
        <>
        <CommandBarBasicExample />
        <Content />
        </>
    )
}