import React from "react";
import ReactDOM from "react-dom";
import * as serviceWorker from "./serviceWorker";

import { Provider } from "react-redux";
import { store } from "./store/store";
import App from "./app/App";

import { ConfigProvider, theme } from "antd";

const lightToken = theme.getDesignToken({ algorithm: theme.defaultAlgorithm }); 

ReactDOM.render(
  <ConfigProvider theme={{ token: lightToken }}>
    <Provider store={store}>
        <App />
    </Provider>
  </ConfigProvider>,
  document.getElementById("app")
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
