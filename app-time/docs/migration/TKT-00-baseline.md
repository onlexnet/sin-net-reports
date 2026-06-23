# TKT-00 — Technical Baseline and Smoke Scope

Date: 2026-03-18
Repo: `static-webapp-time`

## 1) Technical baseline

### Environment
- Node.js: `v22.12.0`
- npm: `10.9.0`

### Dependency installation
- Command: `npm ci`
- Status: ✅ success
- Notes:
  - Deprecation warnings from transitive packages appeared.
  - `npm audit`: 51 vulnerabilities (16 low, 9 moderate, 25 high, 1 critical) — out of scope for TKT-00.

### Build baseline
- Command: `npm run build`
- Status: ✅ success
- Notes:
  - Build completes with ESLint warnings about unused variables/imports.
  - Source-map warnings for dependencies (`graphql-request`, `totp-generator`).
  - CRA/Babel warning about `@babel/plugin-proposal-private-property-in-object` (transitive package).
  - Build artefact generated correctly.

### Test baseline
- Command: `CI=true npm test -- --watch=false`
- Status: ✅ success
- Result:
  - Test Suites: `7 passed, 7 total`
  - Tests: `20 passed, 20 total`
- Notes:
  - Warning about worker process force-exit appears (possible test leak / open handles), no impact on PASS status.
  - Deprecation warnings from the `punycode` module appear.

## 2) Critical smoke paths (scope)

> Goal: reproduce the most important business flows after each UI migration phase.

### Smoke-01 — Login
- Entry points: `src/app/App.tsx`, `src/app/AppInProgress.tsx`, `src/app/AppAuthenticated.tsx`, `src/app/AppTestLogin.tsx`
- PASS criteria:
  - User reaches the authenticated state (MSAL or test login).
  - Application renders the post-authorisation view without UI errors.
- Status TKT-00: ⏳ pending manual execution

### Smoke-02 — Lists / tables (services / customers / reports)
- Entry points: `src/services/ActionList.tsx`, `src/app/customers/Customers.tsx`, `src/app/reports/Reports.tsx`
- PASS criteria:
  - List/table data renders correctly.
  - Sorting / filtering / pagination (where available) work without regression.
- Status TKT-00: ⏳ pending manual execution

### Smoke-03 — Customer edit
- Entry points: `src/app/customer/CustomerView.tsx` (+ routed edit/new)
- PASS criteria:
  - Form loads data and allows saving/editing without logic changes.
  - Disabled / read-only / focus navigation work correctly.
- Status TKT-00: ⏳ pending manual execution

### Smoke-04 — Reports
- Entry points: `src/reports/ReportsView.tsx`, `src/app/reports/Reports.tsx`
- PASS criteria:
  - Report view renders correctly.
  - Report actions execute without UI errors.
- Status TKT-00: ⏳ pending manual execution

### Smoke-05 — File download
- Entry points: `src/services/ServicesDefault.tsx`, `src/api/useDownloadFile.ts`, `src/utils/fileDownloadUtils.ts`
- PASS criteria:
  - Download action returns and saves the file correctly.
  - Error handling does not block the UI.
- Status TKT-00: ⏳ pending manual execution

## 3) Rollout / rollback strategy

### Rollout
- Confirmed: `1 ticket = 1 PR`.
- Order: `TKT-00 -> TKT-01 -> (TKT-02 + TKT-03) -> TKT-04 -> TKT-05 -> TKT-06 -> TKT-07 -> TKT-08`.
- Rule: do not mix UI migration with business logic / API refactoring.

### Rollback
- Rollback by reverting a single PR (per ticket / phase).
- For critical screens, keep the ability to quickly revert view changes.

## 4) Exit criteria TKT-00
- ✅ Baseline build/test executed and recorded.
- ✅ Smoke test scope written and linked to entry points.
- ✅ Rollout and rollback strategy recorded.
- ⏳ Remaining: manual sign-off of the smoke checklist by the team (application environment).

## 5) Readiness for TKT-01
- Status: ✅ **Ready for TKT-01 (technically)**
- Condition for operational closure of TKT-00: manual execution of smoke-01..05 checklist and recording PASS/FAIL.
