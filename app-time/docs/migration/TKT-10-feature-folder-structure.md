# TKT-10 – Feature Folder Structure: Rules and Migration Plan

## Goal

Standardise the source folder structure according to the **feature-based** pattern (vertical slices per domain feature), eliminating file scattering and ambiguous naming.

---

## Rules

### 1. Feature folder
Every view / domain feature has one dedicated folder under `src/features/`.  
The folder contains **all** files related to that feature: components, tests, types, and `.graphql` files.

```
src/features/customers/
  Customers.tsx
  Customers.Routed.tsx
  Customers.test.tsx
  Customers.graphql
  CustomerView.tsx
  CustomerView.Edit.tsx
  CustomerView.Routed.Edit.tsx
  CustomerView.Routed.New.tsx
  index.ts
```

### 2. File naming
Format: `FeatureName.Variant.tsx`

| Variant | Meaning |
|---|---|
| `FeatureName.tsx` | Main view component |
| `FeatureName.Routed.tsx` | Wraps the view inside `<Route>` / handles URL params |
| `FeatureName.Edit.tsx` | Edit form view |
| `FeatureName.New.tsx` | Create-new-record view |
| `FeatureName.test.tsx` | Unit / integration tests |
| `FeatureName.graphql` | GraphQL queries and mutations for the feature |

### 3. `index.ts` per feature
Each feature folder exposes its public API through `index.ts`.  
Cross-feature imports must go through `index.ts` only — never directly into an internal file.

```ts
// src/features/customers/index.ts
export { default as CustomersView } from './Customers';
export { default as CustomerEditView } from './CustomerView.Edit';
```

### 4. `.Routed.*` files live next to the view
Routing wrapper files (`*.Routed.tsx`) belong in the feature folder, not in the global router.  
The global `Routing.ts` defines only URL path constants (strings).

### 5. `src/` folder responsibilities

| Folder | Contents |
|---|---|
| `src/features/` | Domain views and feature logic (feature-based) |
| `src/components/` | Stateless, reusable UI components |
| `src/store/` | Redux: reducers, actions, sagas |
| `src/api/` | GraphQL hooks, DTO mappers |
| `src/config/` | Runtime configuration, themes, build info |
| `src/layout/` | App shell: NavBar, Home, NotFound |
| `src/msal/` | Azure B2C authentication logic |
| `src/utils/` | Pure helper functions |

### 6. Don'ts
- Do not place views directly in `src/app/` or in the `src/` root.
- Do not create two folders for one feature (e.g. `customer/` and `customers/`).
- Do not import from inside another feature bypassing its `index.ts`.
- Do not mix configuration files (`RuntimeConfig`, `ThemeMode`) with view components.

---

## Target structure

```
src/
├── features/
│   ├── actions/          # service action list + edit form
│   ├── customers/        # customer list + edit + new
│   ├── reports/          # reports view
│   ├── dashboard/        # main dashboard (MainView)
│   └── auth/             # authentication flows (Authenticated/Unauthenticated/...)
├── components/
│   ├── ui/
│   └── table/
├── layout/               # NavBar, Home, NotFound
├── config/               # RuntimeConfig, BuildInfo, ThemeMode
├── store/
├── api/
├── msal/
├── utils/
├── App.tsx
├── Routing.ts
└── index.tsx
```

---

## Migration plan

### Step 1 – Preparation

- [ ] Verify all tests pass before starting (`npm test`)
- [ ] Create branch `refactor/feature-folder-structure`

---

### Step 2 – `src/config/` (configuration)

Move from `src/app/configuration/` to `src/config/`:

| From | To |
|---|---|
| `src/app/configuration/RuntimeConfig.ts` | `src/config/RuntimeConfig.ts` |
| `src/app/configuration/BuildInfo.ts` | `src/config/BuildInfo.ts` |
| `src/app/configuration/ThemeMode.ts` | `src/config/ThemeMode.ts` |
| `src/app/configuration/ApplicationInsights.ts` | `src/config/ApplicationInsights.ts` |
| `src/app/configuration/Configuration.ts` | `src/config/Configuration.ts` |

Update all imports referencing `./configuration/*` or `../configuration/*`.

---

### Step 3 – `src/layout/` (app shell)

Move from `src/`:

| From | To |
|---|---|
| `src/NavBar.tsx` | `src/layout/NavBar.tsx` |
| `src/Home.tsx` | `src/layout/Home.tsx` |
| `src/NotFound.tsx` | `src/layout/NotFound.tsx` |

---

### Step 4 – `src/features/dashboard/` (main view)

Move from `src/app/mainview/`:

| From | To |
|---|---|
| `src/app/mainview/MainView.tsx` | `src/features/dashboard/MainView.tsx` |
| `src/app/mainview/MainView.MultipleProjects.tsx` | `src/features/dashboard/MainView.MultipleProjects.tsx` |
| `src/app/mainview/MainView.NoProjects.tsx` | `src/features/dashboard/MainView.NoProjects.tsx` |
| `src/app/mainview/MainView.graphql` | `src/features/dashboard/MainView.graphql` |

Add `src/features/dashboard/index.ts`.

---

### Step 5 – `src/features/auth/` (authentication)

Move from `src/app/`:

| From | To |
|---|---|
| `src/app/AppAuthenticated.tsx` | `src/features/auth/AppAuthenticated.tsx` |
| `src/app/AppUnauthenticated.tsx` | `src/features/auth/AppUnauthenticated.tsx` |
| `src/app/AppTestLogin.tsx` | `src/features/auth/AppTestLogin.tsx` |
| `src/app/AppInProgress.tsx` | `src/features/auth/AppInProgress.tsx` |
| `src/app/LocalDateView.tsx` | `src/features/auth/LocalDateView.tsx` *(or `src/components/` if reused elsewhere)* |

Add `src/features/auth/index.ts`.

---

### Step 6 – `src/features/customers/` (customers)

Merge `src/app/customers/` and `src/app/customer/` into one folder:

| From | To |
|---|---|
| `src/app/customers/Customers.tsx` | `src/features/customers/Customers.tsx` |
| `src/app/customers/Customers.Routed.tsx` | `src/features/customers/Customers.Routed.tsx` |
| `src/app/customers/Customers.graphql` | `src/features/customers/Customers.graphql` |
| `src/app/customers/Customers.test.tsx` | `src/features/customers/Customers.test.tsx` |
| `src/app/customer/CustomerView.tsx` | `src/features/customers/CustomerView.tsx` |
| `src/app/customer/CustomerView.Edit.tsx` | `src/features/customers/CustomerView.Edit.tsx` |
| `src/app/customer/CustomerView.Routed.Edit.tsx` | `src/features/customers/CustomerView.Routed.Edit.tsx` |
| `src/app/customer/CustomerView.Routed.New.tsx` | `src/features/customers/CustomerView.Routed.New.tsx` |
| `src/app/customer/CustomerView.graphql` | `src/features/customers/CustomerView.graphql` |
| `src/app/customer/NewContactItem.tsx` | `src/features/customers/NewContactItem.tsx` |
| `src/app/customer/View.NewSecret.tsx` | `src/features/customers/View.NewSecret.tsx` |
| `src/app/customer/View.UserPasswordItem.tsx` | `src/features/customers/View.UserPasswordItem.tsx` |
| `src/app/customer/View.UserPasswordItemEx.tsx` | `src/features/customers/View.UserPasswordItemEx.tsx` |
| `src/app/customer/SecretsTimestamp.ts` | `src/features/customers/SecretsTimestamp.ts` |

Add `src/features/customers/index.ts`.

---

### Step 7 – `src/features/actions/` (service actions)

Merge `src/services/` and `src/app/actions/`:

| From | To |
|---|---|
| `src/services/ActionList.tsx` | `src/features/actions/ActionList.tsx` |
| `src/services/ActionList.DatePicker.tsx` | `src/features/actions/ActionList.DatePicker.tsx` |
| `src/services/ActionList.Duration.tsx` | `src/features/actions/ActionList.Duration.tsx` |
| `src/services/ActionList.test.tsx` | `src/features/actions/ActionList.test.tsx` |
| `src/services/ServicesDefault.tsx` | `src/features/actions/ActionsDefault.tsx` |
| `src/services/ServiceListModel.ts` | `src/features/actions/ActionListModel.ts` |
| `src/services/Commands.tsx` | `src/features/actions/Commands.tsx` |
| `src/services/Commands.css` | `src/features/actions/Commands.css` |
| `src/services/actions.graphql` | `src/features/actions/actions.graphql` |
| `src/services/Api.graphql` | `src/features/actions/Api.graphql` |
| `src/app/actions/ActionView.Edit.tsx` | `src/features/actions/ActionView.Edit.tsx` |
| `src/app/actions/ActionView.Routed.Edit.tsx` | `src/features/actions/ActionView.Routed.Edit.tsx` |
| `src/app/actions/ActionView.Edit.CustomerView.tsx` | `src/features/actions/ActionView.Edit.CustomerView.tsx` |
| `src/app/actions/CustomerComboBox.tsx` | `src/features/actions/CustomerComboBox.tsx` |
| `src/app/actions/FilteredComboBox.tsx` | `src/features/actions/FilteredComboBox.tsx` |

Add `src/features/actions/index.ts`.

---

### Step 8 – `src/features/reports/` (reports)

Merge `src/reports/` and `src/app/reports/`:

| From | To |
|---|---|
| `src/reports/ReportsView.tsx` | `src/features/reports/ReportsView.tsx` |
| `src/reports/ReportsView.Routed.tsx` | `src/features/reports/ReportsView.Routed.tsx` |
| `src/reports/PeriodSelector.tsx` | `src/features/reports/PeriodSelector.tsx` |
| `src/reports/ReportsView.test.tsx` | `src/features/reports/ReportsView.test.tsx` |
| `src/app/reports/Reports.tsx` | *(verify — remove if duplicate, otherwise merge)* |

Add `src/features/reports/index.ts`.

---

### Step 9 – Verification

- [ ] Run `npm run build` — zero compilation errors
- [ ] Run `npm test` — all tests green
- [ ] Run `npm run generate` — GraphQL models regenerated successfully
- [ ] Delete empty legacy folders: `src/app/actions/`, `src/app/customer/`, `src/app/customers/`, `src/app/mainview/`, `src/app/reports/`, `src/app/configuration/`, `src/services/`, `src/reports/`
- [ ] Code review: confirm no cross-feature imports bypass `index.ts`

---

### Step order (dependencies)

```
Step 2 (config)
    └─> Step 4 (dashboard) — uses RuntimeConfig
    └─> Step 5 (auth)      — uses RuntimeConfig

Step 3 (layout)
    └─> Step 5 (auth)      — App.tsx imports NavBar

Steps 6, 7, 8 — independent, can be done in parallel
    └─> Step 9 (verification) — after all of the above
```
