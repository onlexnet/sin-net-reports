import React from "react";
import { createRoot } from "react-dom/client";
import * as serviceWorker from "./serviceWorker";
import "./index.css";

import { Provider } from "react-redux";
import { store } from "./store/store";
import App from "./app/App";

import { Toaster } from "sonner";
import { initializeApplicationInsights } from "./app/configuration/ApplicationInsights";
import { loadRuntimeConfig } from "./app/configuration/RuntimeConfig";

// Initialize application
const initializeApp = async () => {
  try {
    // Load runtime configuration first
    await loadRuntimeConfig();
    
    // Initialize Application Insights
    initializeApplicationInsights();
    
    // Render the application
    const appElement = document.getElementById("app");
    if (!appElement) {
      throw new Error("App root element not found");
    }

    const root = createRoot(appElement);
    root.render(
      <Provider store={store}>
        <App />
        <Toaster richColors position="top-right" />
      </Provider>
    );
  } catch (error) {
    console.error('Failed to initialize application:', error);
    // Show error message to user
    const appElement = document.getElementById("app");
    if (appElement) {
      appElement.innerHTML = `
        <div style="padding: 20px; text-align: center;">
          <h1>Application Error</h1>
          <p>Failed to load application configuration. Please try refreshing the page.</p>
          <pre>${error}</pre>
        </div>
      `;
    }
  }
};

// Start the application
initializeApp();

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
