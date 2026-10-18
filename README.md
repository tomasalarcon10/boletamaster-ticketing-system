# BoletaMaster Pro: Event Ticketing and Resale Platform

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=flat-square&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Swing-5382A1?style=flat-square)
![JUnit](https://img.shields.io/badge/tests-JUnit-25A162?style=flat-square)

Desktop system for selling and reselling event tickets, built in Java with object-oriented design, a Swing graphical interface, file-based persistence, a resale marketplace with counteroffers and QR-coded ticket printing. Around 9,000 lines of code across 10 packages.

Final project of *Object-Oriented Design and Programming (ISIS1226)*, Universidad de los Andes, developed in a team of three.

---

## Why this project

Ticketing platforms combine almost every hard problem in business software: several user roles with different permissions, money moving between accounts, inventory that can run out, a secondary market and audit logs. Building one from scratch was the best way to practice object-oriented design on a system large enough for design decisions to matter.

## Objectives

1. Model a ticketing business with a clean object-oriented domain: events, venues, seat sections, ticket types, users and transactions.
2. Support three roles with their own workflows: client, organizer and administrator.
3. Add a resale marketplace with offers, counteroffers and a complete transaction log.
4. Deliver a usable graphical interface and printable tickets with dynamic QR codes.

## Features

**Roles**
- **Client:** buys, transfers and resells tickets, recharges a virtual balance, makes counteroffers and prints tickets.
- **Organizer:** creates events, adds sections, creates discounts and proposes venues.
- **Administrator:** approves venues, configures fees, cancels events, processes refunds, moderates the marketplace and generates financial reports.

**Domain**
- Event types: music, cultural, sports and religious.
- Ticket types: basic, numbered, multiple, season pass and deluxe package (inheritance hierarchy).
- Financial layer: transactions, refunds, configurable fees per event type and financial reports.

**Marketplace**
- Resale listings, counteroffers between users, administrative moderation and a full log of every action.

**Interface and printing**
- Swing GUI with a dedicated panel per role, tables and tabs.
- Ticket printing with a dynamically generated QR code (event, ticket ID, event date and print timestamp).
- A printed ticket is locked: it cannot be printed again, transferred or resold.

## Architecture

```
src/
├── eventos/        # Event, Venue, Section (Localidad), Offer
├── tiquetes/       # Ticket hierarchy: basic, multiple, season pass, deluxe
├── usuarios/       # User roles and authentication
├── finanzas/       # Transactions, refunds, fee configuration, financial reports
├── marketplace/    # Resale offers, counteroffers, log registry
├── sistema/        # SistemaVentaTiquetes: central façade of the system
├── persistencia/   # IPersistencia interface and file-based implementation
├── gui/            # Swing panels per role, ticket printing dialog, QR generator
├── Consola/        # Console version of the application
└── tests/          # Unit tests: purchases, marketplace, refunds, users, venues
```

Design documentation (UML class diagrams, interfaces and design document) is included as PDF files in the repository.

## How to run

1. Open the project in Eclipse (or any Java IDE) and add the two ZXing jars in `lib/` to the build path.
2. Run `gui.MainGUI`.
3. Test users: `cliente1 / 123`, `organizador1 / 123`, `admin / 123`.

Recommended flow: the administrator approves a venue, the organizer creates an event and adds sections, then the client recharges the balance, buys and prints a ticket or lists it in the marketplace. Data is saved automatically to `datos_sistema/`.

The background music file used in the original delivery is not included for copyright reasons; the application runs normally without it.

## What this project means for my development

This was the largest system I had built at the time. It taught me how design decisions such as inheritance hierarchies, a façade for the core system and an interface for persistence make a codebase easier to extend. It also gave me my first experience modeling money flows, fees and reporting in software, which connects directly with my interest in financial and enterprise systems.

## Team

Tomás Alarcón, Scovino and Uribe (team project).

---

**Author:** Tomás Alarcón, Systems and Computing Engineering, Universidad de los Andes
