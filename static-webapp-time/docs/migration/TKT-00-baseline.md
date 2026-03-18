# TKT-00 — Baseline techniczny i smoke scope

Data: 2026-03-18
Repo: `static-webapp-time`

## 1) Baseline techniczny

### Środowisko
- Node.js: `v22.12.0`
- npm: `10.9.0`

### Instalacja zależności
- Komenda: `npm ci`
- Status: ✅ sukces
- Uwagi:
  - Wystąpiły ostrzeżenia deprecacji pakietów transytywnych.
  - `npm audit`: 51 podatności (16 low, 9 moderate, 25 high, 1 critical) — poza zakresem TKT-00.

### Build baseline
- Komenda: `npm run build`
- Status: ✅ sukces
- Uwagi:
  - Build kończy się ostrzeżeniami ESLint o nieużywanych zmiennych/importach.
  - Ostrzeżenia source-map dla zależności (`graphql-request`, `totp-generator`).
  - Ostrzeżenie CRA/Babel o `@babel/plugin-proposal-private-property-in-object` (pakiet pośredni).
  - Artefakt build wygenerowany poprawnie.

### Test baseline
- Komenda: `CI=true npm test -- --watch=false`
- Status: ✅ sukces
- Wynik:
  - Test Suites: `7 passed, 7 total`
  - Tests: `20 passed, 20 total`
- Uwagi:
  - Pojawia się ostrzeżenie o worker process force-exit (możliwy test leak/open handles), bez wpływu na status PASS.
  - Pojawiają się deprecation warnings modułu `punycode`.

## 2) Krytyczne ścieżki smoke (scope)

> Cel: odtworzyć najważniejsze przepływy biznesowe po każdej fazie migracji UI.

### Smoke-01 — Logowanie
- Wejście: `src/app/App.tsx`, `src/app/AppInProgress.tsx`, `src/app/AppAuthenticated.tsx`, `src/app/AppTestLogin.tsx`
- Kryterium PASS:
  - Użytkownik przechodzi do stanu zalogowanego (MSAL lub test login).
  - Aplikacja renderuje widok po autoryzacji bez błędów UI.
- Status TKT-00: ⏳ do wykonania manualnego

### Smoke-02 — Listy/tabele (services/customers/reports)
- Wejście: `src/services/ActionList.tsx`, `src/app/customers/Customers.tsx`, `src/app/reports/Reports.tsx`
- Kryterium PASS:
  - Dane list/tabel renderują się.
  - Sortowanie/filtrowanie/paginacja (jeśli dostępne) działają bez regresji.
- Status TKT-00: ⏳ do wykonania manualnego

### Smoke-03 — Edycja klienta
- Wejście: `src/app/customer/CustomerView.tsx` (+ routed edit/new)
- Kryterium PASS:
  - Formularz ładuje dane i pozwala zapisać/edytować bez zmiany logiki.
  - Disabled/read-only/focus navigation działają poprawnie.
- Status TKT-00: ⏳ do wykonania manualnego

### Smoke-04 — Raporty
- Wejście: `src/reports/ReportsView.tsx`, `src/app/reports/Reports.tsx`
- Kryterium PASS:
  - Widok raportowy renderuje się poprawnie.
  - Akcje raportowe uruchamiają się bez błędów UI.
- Status TKT-00: ⏳ do wykonania manualnego

### Smoke-05 — Pobranie pliku
- Wejście: `src/services/ServicesDefault.tsx`, `src/api/useDownloadFile.ts`, `src/utils/fileDownloadUtils.ts`
- Kryterium PASS:
  - Akcja pobrania zwraca i zapisuje plik poprawnie.
  - Obsługa błędów nie blokuje UI.
- Status TKT-00: ⏳ do wykonania manualnego

## 3) Strategia rolloutu / rollbacku

### Rollout
- Potwierdzone: `1 ticket = 1 PR`.
- Kolejność: `TKT-00 -> TKT-01 -> (TKT-02 + TKT-03) -> TKT-04 -> TKT-05 -> TKT-06 -> TKT-07 -> TKT-08`.
- Zasada: nie łączyć migracji UI z refaktorem logiki biznesowej/API.

### Rollback
- Rollback przez revert pojedynczego PR (per ticket/faza).
- Dla ekranów krytycznych utrzymać możliwość szybkiego wycofania zmian widoku.

## 4) Exit criteria TKT-00
- ✅ Baseline build/test uruchomiony i zapisany.
- ✅ Scope smoke testów spisany i powiązany z entrypointami.
- ✅ Strategia rolloutu i rollbacku zapisana.
- ⏳ Pozostało: manualne odhaczenie smoke checklisty przez zespół (środowisko aplikacyjne).

## 5) Status gotowości do TKT-01
- Status: ✅ **Ready for TKT-01 (technicznie)**
- Warunek domknięcia operacyjnego TKT-00: ręczne wykonanie checklisty smoke-01..05 i zapis PASS/FAIL.
