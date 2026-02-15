# Implementation Prompt – Kassabuch

You are a senior Android engineer. Implement the Kassabuch Android app using Kotlin + Jetpack Compose, Room, and WorkManager. Follow the spec exactly.

## Context
Project path: /home/christian/projects/Kassabuch
Min SDK: 26, Target SDK: 34
Language: German
Offline‑first

## Requirements (must follow)
1) Use the data model and logic from `Implementation-Ready-Spec.md`.
2) Use Bottom Navigation with tabs: Dashboard | Einnahmen | Ausgaben | Mehr.
3) “Mehr” includes: Kategorien, Auszahlungstermine, Budget.
4) Tagsatz is initially empty and manually editable with history.
5) AMS amount = Tagsatz (valid in previous month) × number of calendar days in previous month.
6) Payout schedule is manual and seeded with 2026 dates (Überweisung only).
7) Notifications: 2 days before payout at 14:00 (WorkManager).
8) Validation: category, amount, date are required, amount > 0.
9) Fix expenses must keep history (validFrom/validTo).

## Seed Data
- Income categories: Lohn (one_time), Sozialleistungen (recurring)
- Expense categories: Lebensmittel, Wohnen/Miete, Alimente, Heizen/Strom, Internet/Telefon, Mobilität/Transport, Gesundheit/Medikamente, Kleidung, Freizeit, Versicherungen, Google Play, Sonstiges
- Payout schedule 2026 (Überweisung):
  - 2026-02 → 2026-03-03
  - 2026-03 → 2026-04-03
  - 2026-04 → 2026-05-06
  - 2026-05 → 2026-06-03
  - 2026-06 → 2026-07-03
  - 2026-07 → 2026-08-05
  - 2026-08 → 2026-09-04
  - 2026-09 → 2026-10-06
  - 2026-10 → 2026-11-05
  - 2026-11 → 2026-12-03
  - 2026-12 → 2027-01-05

## Deliverables
- Fully working Android app structure in this repo
- Room DB, DAOs, repositories, ViewModels
- Compose UI screens with German strings
- Seed logic on first run
- Notification scheduling

## Notes
- Do not add web scraping or auto-fetch of payout dates.
- Use local storage only.

Start implementation now.