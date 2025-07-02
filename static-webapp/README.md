# Content
Folder contains client codebase part of SinNet PSA application.

- **npm install** install packages
- **npm run generate** generate local graphql models
- **npm start** to run against production backend

## Used articles
* https://daveceddia.com/pluggable-slots-in-react-components/
* https://www.develop1.net/public/post/2020/05/11/pcf-detailslist-layout-with-fluent-ui
* https://reactgo.com/react-hooks-apollo/
* https://docs.microsoft.com/en-us/azure/active-directory/develop/reference-app-manifest
* https://github.com/syncweek-react-aad/react-aad/tree/master/samples/react-typescript

## Static Web App configuration: https://learn.microsoft.com/en-us/azure/static-web-apps/configuration

## Architecture Overview

This project is a client-side part of the SinNet PSA application, implemented as a React single-page application (SPA) with TypeScript. It interacts with a backend via GraphQL APIs and integrates with Azure B2C for authentication.

### Main Technologies
- **React** (TypeScript): UI framework for building the SPA.
- **Redux**: State management for application context and user session.
- **Apollo Client**: Handles GraphQL queries and mutations.
- **Ant Design**: UI component library.
- **Azure B2C**: Authentication and user management.

### Folder Structure
- `src/` – Main source code
  - `api/` – GraphQL API hooks and DTO mappers
  - `app/` – Application logic, views, and submodules (actions, configuration, customer, mainview, reports)
  - `components/` – Reusable UI components
  - `debug/` – Debugging utilities
  - `hooks/` – Custom React hooks
  - `msal/` – Authentication state and logic
  - `reports/` – Report-related UI and logic
  - `services/` – Service-related UI and logic
  - `store/` – Redux store, reducers, actions, sagas, and context
- `public/` – Static assets and HTML entry point
- `build/` – Production build output

### Data Flow
1. **Authentication**: Users log in via Azure B2C. The authentication state is managed in `msal/` and Redux store.
2. **Data Fetching**: UI components use Apollo Client hooks (generated from GraphQL schema) to fetch and mutate data from the backend.
3. **State Management**: Redux is used to manage global state, including authentication, application context, and view state.
4. **UI Rendering**: Components render data and handle user interactions, dispatching actions to Redux or triggering GraphQL operations as needed.

### Key Components
- **App.tsx**: Main application entry point, sets up routing and context providers.
- **NavBar.tsx**: Top navigation bar.
- **Home.tsx**: Home page view.
- **ReportsView.Routed.tsx**: Main reports view, routed via React Router.
- **CustomerView.Edit.tsx**: Customer editing view.
- **ActionView.Edit.tsx**: Service action editing view.

### Build & Run
- `npm install` – Install dependencies
- `npm run generate` – Generate GraphQL models
- `npm start` – Start development server (connects to production backend)

### Backend Integration
- Communicates with backend microservices via GraphQL endpoints.
- Authentication and authorization are handled via Azure B2C.

### Extensibility
- Modular folder structure allows for easy addition of new features and views.
- GraphQL code generation ensures type safety and easy API evolution.

---
