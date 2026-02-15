# Dev-Notes — Kassabuch (Android)

## Ziel
Android‑App zur Erfassung von Einnahmen und Ausgaben mit Fokus auf AMS‑Auszahlung (Tagsatz × Kalendertage). Auszahlung im aktuellen Monat gilt für den **Vormonat**.

## Kernregeln
- App‑Sprache: **Deutsch**
- Währung: EUR
- Berechnung nach **Kalendertagen** (anteilig)
- Auszahlungstermine werden **manuell** gepflegt (jährlich)
- Start: **Auszahlungstermine ab Februar 2026**
- Auszahlung **nur per Überweisung** (Posttermine werden nicht erfasst)
- Tagsatz ist **manuell bearbeitbar** und historisiert (Statistik auf Abruf)
- Monatsbudget: **ja**
- Benachrichtigung: **2 Tage vor Auszahlungstermin**

---

## Einnahmen
**Fixe Kategorien (Start):**
- Lohn
- Sozialleistungen

**Erweiterung:**
- Beliebige Kategorien möglich
- Kategorie kann als **einmalig** oder **wiederkehrend** markiert werden

**Spezialfall AMS:**
- Eingabe: Tagsatz
- Berechnung: Tagsatz × Kalendertage des Vormonats
- Tagsatz‑Änderungen im Monat werden anteilig berücksichtigt (Historie)
- Anzeige: Auszahlungstermin + Betrag

---

## Ausgaben
**Kategorien (bestätigt):**
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

**Fix vs. variabel:**
- Fixe Ausgaben sind bearbeitbar
- Änderungen gelten ab einem Datum (Historie bleibt korrekt)

---

## Kategorien
- Kategorien können hinzugefügt und umbenannt werden
- Löschen ist **nicht erlaubt** (Datenintegrität)

---

## Auszahlungstermine (Überweisung)
Quelle: https://finanzrechner.at/arbeitslosengeld/ams-auszahlungstermine (Stand 11.02.2026)

**Termine ab Februar 2026:**
- Februar 2026 → **03.03.2026**
- März 2026 → **03.04.2026**
- April 2026 → **06.05.2026**
- Mai 2026 → **03.06.2026**
- Juni 2026 → **03.07.2026**
- Juli 2026 → **05.08.2026**
- August 2026 → **04.09.2026**
- September 2026 → **06.10.2026**
- Oktober 2026 → **05.11.2026**
- November 2026 → **03.12.2026**
- Dezember 2026 → **05.01.2027**

Hinweis: Auszahlung im aktuellen Monat gilt für den **Vormonat**.

---

## Datenhaltung
**Phase 1:** lokal (kein Server)

**Phase 2 (in ~2 Monaten):** Umstellung auf Server‑Sync/Backup

---

## Screens (MVP)
1. **Dashboard**
   - AMS‑Betrag für Vormonat
   - Auszahlungstermin
   - Monats‑Saldo

2. **Einnahmen**
   - Liste + Hinzufügen
   - Tagsatz bearbeiten

3. **Ausgaben**
   - Liste + Hinzufügen
   - Fix vs. variabel sichtbar

4. **Kategorien**
   - Einnahmen/Ausgaben Kategorien verwalten

5. **Auszahlungstermine**
   - Jahresliste + Edit

---

## Meilensteine
**M1 – Konzept & Datenmodell**
- Monatslogik (Vormonat)
- Tagsatz‑Historie
- Kategorien final

**M2 – MVP‑UX**
- Dashboard, Einnahmen, Ausgaben

**M3 – Berechnungslogik**
- Tagsatz × Kalendertage
- Zuordnung Vormonat → Auszahlung

**M4 – Lokale Speicherung**
- Persistenz

**M5 – Qualität**
- Edge‑Cases (Schaltjahr, Monatswechsel)

---

## Risiken & Gegenmaßnahmen
- **Fehlerhafte Auszahlungstermine** → jährliche manuelle Pflege + Prüfansicht
- **Monatslogik unklar** → überall Label: „Auszahlung für Vormonat“
- **Fixkosten‑Änderungen verfälschen Historie** → gültig‑ab/gültig‑bis

---

## Offene Entscheidungen
- Export/Backup (CSV/Excel) **später geplant**
- Mehrbenutzer‑Funktionalität später möglich, aktuell **privat**

---

## Aktueller Stand
- Planung abgeschlossen (Phasen 1–13 dokumentiert)
- Projektstruktur in `/home/christian/projects/Kassabuch` angelegt
- Implementation‑Ready Spec + Implementation‑Prompt vorhanden
- Emulator & Android‑Setup funktionsfähig

## Nächster Schritt
- **GitHub Issues anlegen** und der Reihe nach abarbeiten (morgen)
