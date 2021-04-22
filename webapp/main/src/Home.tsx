import React from "react";
import packageJson from '../package.json';


export const Home: React.FC<{}> = (props) => {

    return (
        <div>
        <h1>Witaj w systemie ewidencji usług.</h1>
        <p>version: {packageJson.version}</p>
        </div>
    )
}
