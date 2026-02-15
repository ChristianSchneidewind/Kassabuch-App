# Kassabuch – One‑Pager

## Ziel
Android‑App zur Verwaltung von Einnahmen und Ausgaben mit Fokus auf AMS‑Auszahlung (Tagsatz × Kalendertage). Auszahlung erfolgt im aktuellen Monat für den Vormonat.

## Zielgruppe
Privater Nutzer (single user), später evtl. Teilen/Sync.

## Kernfunktionen (MVP)
- Einnahmen & Ausgaben erfassen
- Tagsatz + AMS‑Berechnung
- Auszahlungstermine (Überweisung) anzeigen
- Fixkosten‑Historie
- Kategorienverwaltung
- Monatsbudget + Monatsübersicht

## Besonderheiten
- Auszahlungstermine werden manuell gepflegt (jährlich)
- Benachrichtigung 2 Tage vorher um 14:00
- Offline‑First

## Tech‑Stack
- Android (Kotlin + Jetpack Compose)
- Room (SQLite)
- WorkManager (Benachrichtigung)

## Roadmap
- MVP (lokal)
- v1.0 (Benachrichtigung, Budget‑Warnung, Tagsatz‑Statistik)
- v1.1 (Export, Backup, Sync‑Vorbereitung)
