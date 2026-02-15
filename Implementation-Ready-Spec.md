# Implementation‑Ready Spec – Kassabuch

## 1) Ziel
Android‑App (Deutsch) zur Verwaltung von Einnahmen/Ausgaben mit AMS‑Auszahlung (Tagsatz × Kalendertage, Auszahlung für Vormonat).

## 2) Plattform
- Android, **minSdk 26**, targetSdk 34
- Sprache: Deutsch
- Offline‑First

## 3) Validierungsregeln
- **Pflichtfelder**: Kategorie, Betrag, Datum
- Betrag > 0
- Datum darf nicht leer sein

## 4) Datenmodell (final)

### IncomeCategory
- id (PK, Long)
- name (String)
- defaultType (Enum: one_time | recurring)

### Income
- id (PK, Long)
- categoryId (FK → IncomeCategory)
- amount (Decimal)
- date (LocalDate)
- type (Enum: one_time | recurring)
- note (String, nullable)

### ExpenseCategory
- id (PK, Long)
- name (String)

### Expense
- id (PK, Long)
- categoryId (FK → ExpenseCategory)
- amount (Decimal)
- date (LocalDate)
- type (Enum: fixed | variable)
- note (String, nullable)

### FixedExpenseRule
- id (PK, Long)
- categoryId (FK → ExpenseCategory)
- amount (Decimal)
- validFrom (LocalDate)
- validTo (LocalDate, nullable)

### DailyRateHistory
- id (PK, Long)
- rateAmount (Decimal)
- validFrom (LocalDate)
- validTo (LocalDate, nullable)

### PayoutSchedule
- id (PK, Long)
- month (YearMonth)
- payoutDate (LocalDate)

### MonthlyBudget
- id (PK, Long)
- month (YearMonth)
- amount (Decimal)

**Indices/Constraints**
- PayoutSchedule: unique (month)
- MonthlyBudget: unique (month)
- FixedExpenseRule: index (categoryId, validFrom)
- DailyRateHistory: index (validFrom)

## 5) Seed‑Daten (First Run)

### Einnahmen‑Kategorien
- Lohn (default: einmalig)
- Sozialleistungen (default: wiederkehrend)

### Ausgaben‑Kategorien
- Lebensmittel
- Wohnen/Miete
- Alimente
- Heizen/Strom
- Internet/Telefon
- Mobilität/Transport
- Gesundheit/Medikamente
- Kleidung
- Freizeit
- Versicherungen
- Google Play
- Sonstiges

### Auszahlungstermine (Überweisung)
- Feb 2026 → 03.03.2026
- Mär 2026 → 03.04.2026
- Apr 2026 → 06.05.2026
- Mai 2026 → 03.06.2026
- Jun 2026 → 03.07.2026
- Jul 2026 → 05.08.2026
- Aug 2026 → 04.09.2026
- Sep 2026 → 06.10.2026
- Okt 2026 → 05.11.2026
- Nov 2026 → 03.12.2026
- Dez 2026 → 05.01.2027

## 6) Berechnungslogik (final)
- **AMS‑Betrag** = Tagsatz (gültig im Vormonat) × Kalendertage des Vormonats
- Auszahlungstermin aus PayoutSchedule für den **Vormonat**
- Monats‑Saldo = Einnahmen − Ausgaben
- Budget‑Warnung: Ausgaben > Budget

## 7) Fixkosten‑Regel
- Fixkosten werden **als eigene Regel** verwaltet
- Änderung = neuer Datensatz mit validFrom
- Alte Regel erhält validTo

## 8) Benachrichtigungen
- Trigger: **2 Tage vor Auszahlung**
- Uhrzeit: **14:00**
- Kein Termin → keine Benachrichtigung

## 9) UI‑Navigation
- Bottom‑Navigation: Dashboard | Einnahmen | Ausgaben | Mehr
- „Mehr“: Kategorien, Auszahlungstermine, Budget

## 10) Initiale Werte
- Tagsatz initial **leer** (manuell eingeben)

---

## Umsetzungshinweis
Diese Spezifikation ist vollständig für eine KI‑basierte Implementierung.
