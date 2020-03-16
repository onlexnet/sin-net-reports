import React from "react";
import ReactDOM from "react-dom";
import { App } from "./App";
import { Fabric } from "office-ui-fabric-react";
import * as serviceWorker from "./serviceWorker";

import 'office-ui-fabric-react/dist/css/fabric.css';

import { initializeIcons } from '@uifabric/icons';
initializeIcons();

ReactDOM.render(
  <Fabric>
    <App />
  </Fabric>,
  document.getElementById("app")
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
