## Plan: Pełna migracja antd do shadcn

Pełna migracja UI z Ant Design do shadcn/ui w istniejącej aplikacji CRA+TypeScript, bez pozostawiania antd. Najpierw konfiguracja infrastruktury (Tailwind, shadcn, Radix/lucide), potem migracja komponentów bazowych, następnie trudne obszary (tabele i date picker) oraz usunięcie zależności antd z finalną walidacją.

**Steps**
1. Faza 0 — Baseline i przygotowanie (blokuje dalsze kroki)
   - Uruchomić snapshot stanu wyjściowego: npm run build i npm test.
   - Spisać krytyczne ścieżki smoke testu: logowanie, listy, edycja klienta, raporty, pobranie pliku.
   - Ustalić strategię rolloutu: PR-y per faza i kolejność merge.
2. Faza 1 — Infrastruktura i bootstrap (blokuje dalsze kroki)
   - Dodać Tailwind do CRA (tailwindcss, postcss, autoprefixer) i podłączyć style globalne.
   - Zainicjalizować shadcn/ui dla TypeScript i ustawić aliasy/importy zgodnie z aktualną konfiguracją projektu.
   - Zweryfikować aktualną składnię CLI shadcn w dniu wdrożenia i dopiero potem wykonać init/add.
   - Dodać utilities ekosystemu shadcn (class-variance-authority, clsx, tailwind-merge) oraz lucide-react i sonner.
   - Usunąć globalny provider antd z entrypointu i zastąpić go bazową strukturą z Toaster.
3. Faza 2 — Komponenty bazowe i layout (możliwe równolegle po 1)
   - 2A (równolegle z 2B): migrować Button, Input, Select, Checkbox, Separator, podstawową typografię.
   - 2B (równolegle z 2A): migrować Row/Col/Space/Layout na układ oparty o Tailwind utility classes i obecne kontenery.
   - Ujednolicić klasy i warianty poprzez lokalne wrappery tam, gdzie komponenty powtarzają wzorce.
4. Faza 3 — Formularze domenowe (zależy od 2)
   - Zmigrować duże formularze z customer/actions/reports i zachować istniejące handlery stanu (bez zmiany logiki biznesowej).
   - Zamienić importy Form/Form.Item z antd na semantyczną strukturę formularzy opartą o komponenty shadcn.
   - Zweryfikować obsługę disabled/read-only i zgodność keyboard/focus.
5. Faza 4 — Data grid i tabela (zależy od 2, równolegle z 3)
   - Wykonać krótki spike (1–2 dni) na adapter tabel: mapowanie kolumn, sortowanie, filtrowanie i paginacja.
   - Po spike zatwierdzić finalny adapter i schemat migracji ekranów tabelarycznych.
   - Wdrożyć TanStack Table + prymitywy tabeli shadcn jako wspólny adapter tabel.
   - Odwzorować aktualne kolumny, sortowanie i filtrowanie z ekranów services/customers/reports.
   - Zachować obecne kontrakty danych i callbacki, ograniczyć zmiany do warstwy widoku.
6. Faza 5 — DatePicker i popovery (zależy od 2)
   - Zaimplementować date picker w stylu shadcn (Calendar + Popover) i podmienić obecny komponent daty.
   - Utrzymać zgodność formatów dat oraz integrację z dayjs/obecnym modelem danych.
7. Faza 6 — Nawigacja, ikony, powiadomienia i finalne cleanup (zależy od 3/4/5)
   - Zamienić @ant-design/icons na lucide-react w nawigacji i akcjach.
   - Zamienić antd message na sonner toast (success/error/info) i dodać spójny punkt użycia.
   - Usunąć wszystkie importy antd/@ant-design/icons oraz zależności z package.json.
8. Faza 7 — Stabilizacja i walidacja końcowa (zależy od 6)
   - Uruchomić testy jednostkowe i smoke testy kluczowych ścieżek (logowanie, lista, edycja, raporty).
   - Wykonać ręczną weryfikację regresji UI/UX na ekranach krytycznych.
   - Potwierdzić brak pozostałych referencji antd i gotowość do release.

**Definition of Done (per faza)**
- Faza 0: baseline build/test zapisany i uzgodniona lista smoke testów.
- Faza 1: Tailwind + shadcn działają, entrypoint bez ConfigProvider, app się buduje.
- Faza 2: brak importów bazowych komponentów (Button/Input/Select/Checkbox) z antd.
- Faza 3: formularze domenowe działają bez regresji logiki i z zachowaną dostępnością klawiaturową.
- Faza 4: wszystkie tabele używają adaptera TanStack + shadcn, bez importów Table z antd.
- Faza 5: wszystkie flow dat używają nowego date pickera i zachowują formaty.
- Faza 6: brak @ant-design/icons i message z antd; toasty działają globalnie.
- Faza 7: npm run build i testy przechodzą, globalne wyszukanie antd|@ant-design/icons zwraca 0 wyników.

**Relevant files**
- /workspaces/sin-net-reports/static-webapp-time/package.json — zależności do dodania/usunięcia (antd -> shadcn stack).
- /workspaces/sin-net-reports/static-webapp-time/src/index.tsx — usunięcie ConfigProvider i dodanie Toaster/root providers.
- /workspaces/sin-net-reports/static-webapp-time/src/index.css — dyrektywy Tailwind i globalne tokeny stylów.
- /workspaces/sin-net-reports/static-webapp-time/src/NavBar.tsx — migracja Menu/ikon.
- /workspaces/sin-net-reports/static-webapp-time/src/services/Commands.tsx — migracja menu akcji i ikon.
- /workspaces/sin-net-reports/static-webapp-time/src/services/ServicesDefault.tsx — migracja message -> toast.
- /workspaces/sin-net-reports/static-webapp-time/src/services/ActionList.tsx — migracja tabeli i komponentów wspierających.
- /workspaces/sin-net-reports/static-webapp-time/src/services/ActionList.DatePicker.tsx — migracja date picker.
- /workspaces/sin-net-reports/static-webapp-time/src/app/customer/CustomerView.tsx — największy formularz, checkbox/select/input.
- /workspaces/sin-net-reports/static-webapp-time/src/app/actions/ActionView.Edit.tsx — formularz edycji akcji.
- /workspaces/sin-net-reports/static-webapp-time/src/app/actions/FilteredComboBox.tsx — adapter select.
- /workspaces/sin-net-reports/static-webapp-time/src/reports/ReportsView.tsx — przyciski i widok raportowy.

**Verification**
1. Techniczna: npm install, npm run build, npm test.
2. Statyczna: wyszukanie globalne importów antd i @ant-design/icons musi dać zero wyników.
3. Funkcjonalna: manualny smoke test flow logowania, list akcji, edycji klienta, raportów i pobierania plików.
4. UX/A11y: focus order, keyboard navigation, disabled states, kontrast i czytelność formularzy/tabel.
5. Wydajność: brak istotnej regresji renderu list i tabel względem baseline z Fazy 0.

**Decisions**
- Zakres obejmuje pełną migrację 100% z usunięciem antd.
- Tabele migrują teraz na TanStack Table + shadcn table primitives (bez odkładania).
- DatePicker migruje teraz (bez etapu późniejszego).
- Logika biznesowa i kontrakty API są poza zakresem zmian; zmieniana jest warstwa UI.

**Further Considerations**
1. Rekomendacja: wdrażać PR-ami per faza (1, 2, 3+4, 5, 6+7), aby ograniczyć ryzyko regresji i uprościć review.
2. Rekomendacja: utrzymać jeden wspólny adapter tabeli i jeden adapter select dla spójności wzorców w całej aplikacji.
3. Ryzyko czasowe: największe koszty są w CustomerView.tsx i tabelach — uwzględnić dodatkowy bufor testowy.

**Rollback / Fallback**
- Każdą fazę dostarczać osobnym PR z jasnym zakresem, aby rollback był możliwy przez revert konkretnego PR.
- Dla ekranów krytycznych utrzymać krótkookresową możliwość cofnięcia poprzez szybki revert zmian widoku.
- Nie łączyć migracji UI z refaktorem logiki biznesowej ani API w tym samym PR.

---

## Backlog ticketów

### EPIC: Migracja UI z Ant Design do shadcn/ui (100%)
Cel: pełna migracja warstwy UI na shadcn/ui + Tailwind, bez pozostawiania antd i @ant-design/icons.

### TKT-00 — Baseline techniczny i smoke scope
**Opis**
- Przygotować punkt odniesienia przed migracją.
**Zakres**
- Uruchomić npm run build i npm test na stanie wyjściowym.
- Spisać listę krytycznych ścieżek smoke: logowanie, listy, edycja klienta, raporty, pobieranie pliku.
**Akceptacja (AC)**
- Wyniki baseline zapisane i dostępne dla zespołu.
- Lista smoke testów zatwierdzona.
**Zależności**
- Brak.

### TKT-01 — Setup Tailwind + shadcn + provider root
**Opis**
- Dodać infrastrukturę pod shadcn i odłączyć globalny provider antd.
**Zakres**
- Konfiguracja Tailwind/PostCSS.
- Inicjalizacja shadcn/ui dla TS.
- Dodanie clsx, class-variance-authority, tailwind-merge, lucide-react, sonner.
- Usunięcie ConfigProvider i dodanie globalnego toastera.
**Akceptacja (AC)**
- Aplikacja uruchamia się i buduje po setupie.
- Brak regresji na ekranie startowym.
**Zależności**
- TKT-00.

### TKT-02 — Migracja komponentów bazowych (Button/Input/Select/Checkbox)
**Opis**
- Przepiąć najczęściej używane kontrolki na shadcn.
**Zakres**
- Button, Input, Select, Checkbox, Separator, podstawowa typografia.
- Ujednolicenie najczęstszych wariantów przez lokalne wrappery (jeśli potrzebne).
**Akceptacja (AC)**
- Brak importów tych komponentów z antd.
- Kluczowe formularze działają bez zmian logiki.
**Zależności**
- TKT-01.

### TKT-03 — Migracja layoutu (Row/Col/Space/Layout)
**Opis**
- Zastąpić układ antd układem opartym o Tailwind utility.
**Zakres**
- Refaktor Row/Col/Space/Layout w widokach aplikacji.
- Zachowanie obecnych proporcji i responsywności.
**Akceptacja (AC)**
- Brak importów layoutowych z antd.
- Brak regresji wizualnej na głównych ekranach.
**Zależności**
- TKT-01.

### TKT-04 — Formularze domenowe (customer/actions/reports)
**Opis**
- Migracja największych formularzy, bez zmian biznesowych.
**Zakres**
- CustomerView, ActionView.Edit, pokrewne formularze i comboboxy.
- Zachowanie callbacków, disabled/read-only i focus handling.
**Akceptacja (AC)**
- Formularze zapisują/edytują dane jak przed migracją.
- Keyboard navigation i focus są poprawne.
**Zależności**
- TKT-02, TKT-03.

### TKT-05 — Spike tabel (TanStack adapter)
**Opis**
- Krótki spike architektury wspólnego adaptera tabel.
**Zakres**
- POC mapowania kolumn, sortowania, filtrowania, paginacji.
- Decyzja o finalnym interfejsie adaptera.
**Akceptacja (AC)**
- Udokumentowana decyzja techniczna i gotowy szablon adaptera.
- Potwierdzenie, że adapter pokrywa wymagane przypadki użycia.
**Zależności**
- TKT-01.

### TKT-06 — Migracja tabel produkcyjnych
**Opis**
- Wdrożenie TanStack Table + prymitywy tabeli shadcn na ekranach docelowych.
**Zakres**
- Ekrany tabelaryczne w services/customers/reports.
- Migracja callbacków sort/filter bez zmian kontraktów danych.
**Akceptacja (AC)**
- Brak importów Table z antd.
- Sortowanie i filtrowanie działają zgodnie z baseline.
**Zależności**
- TKT-05, TKT-03.

### TKT-07 — DatePicker + popovery
**Opis**
- Podmiana DatePicker na rozwiązanie zgodne z shadcn.
**Zakres**
- Calendar + Popover oraz integracja z aktualnym modelem dat.
- Zachowanie formatów i przepływów formularzy.
**Akceptacja (AC)**
- Brak importów DatePicker z antd.
- Wszystkie scenariusze dat przechodzą smoke test.
**Zależności**
- TKT-02.

### TKT-08 — Ikony, toasty i finalny cleanup
**Opis**
- Domknięcie migracji i usunięcie pozostałości po antd.
**Zakres**
- Zamiana @ant-design/icons na lucide-react.
- Zamiana message na sonner toast.
- Usunięcie antd i @ant-design/icons z zależności.
**Akceptacja (AC)**
- Globalne wyszukiwanie antd|@ant-design/icons zwraca 0 wyników.
- Build/test przechodzą, smoke test bez regresji krytycznych flow.
- Brak istotnej regresji wydajności list/tabel względem baseline.
**Zależności**
- TKT-04, TKT-06, TKT-07.

### Strategia wdrożenia
- Jeden ticket = jeden PR, bez mieszania zmian UI i logiki biznesowej.
- Kolejność rekomendowana: TKT-00 -> 01 -> (02 + 03) -> 04 -> 05 -> 06 -> 07 -> 08.
- Rollback przez revert pojedynczego PR.
