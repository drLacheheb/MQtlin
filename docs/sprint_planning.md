# MQTT Explorer — Sprint Planning

> Agile sprint plan for building the MQTT Explorer application from zero to production.
> Follows Scrum best practices with 2-week sprints, incremental delivery, and a shippable product at each milestone.

---

## Table of Contents

- [Planning Overview](#planning-overview)
- [Definition of Done (DoD)](#definition-of-done-dod)
- [Story Point Scale](#story-point-scale)
- [Release Roadmap](#release-roadmap)
- [Sprint 0: Project Bootstrap & Architecture](#sprint-0-project-bootstrap--architecture)
- [Sprint 1: Basic Connection & Topic Tree](#sprint-1-basic-connection--topic-tree)
- [Sprint 2: Payload Inspection & Message Publishing](#sprint-2-payload-inspection--message-publishing)
- [Sprint 3: Message History, Diff Viewer & Search](#sprint-3-message-history-diff-viewer--search)
- [Sprint 4: Real-Time Charting & Retained Message Management](#sprint-4-real-time-charting--retained-message-management)
- [Sprint 5: Subscription Management & Connection Profiles](#sprint-5-subscription-management--connection-profiles)
- [Sprint 6: TLS, Security & WebSocket Support](#sprint-6-tls-security--websocket-support)
- [Sprint 7: MQTT 5.0 Protocol Support](#sprint-7-mqtt-50-protocol-support)
- [Sprint 8: Broker Diagnostics & Cloud Integration](#sprint-8-broker-diagnostics--cloud-integration)
- [Sprint 9: Home Automation Integration](#sprint-9-home-automation-integration)
- [Sprint 10: Data Export, Import & Advanced Payload Codecs](#sprint-10-data-export-import--advanced-payload-codecs)
- [Sprint 11: Multi-Broker Workspaces & Device Simulation](#sprint-11-multi-broker-workspaces--device-simulation)
- [Sprint 12: Performance Optimization & UX Polish](#sprint-12-performance-optimization--ux-polish)
- [Sprint 13: Deployment, Packaging & Launch](#sprint-13-deployment-packaging--launch)
- [Risk Register](#risk-register)
- [Sprint Velocity Tracking](#sprint-velocity-tracking)

---

## Planning Overview

| Parameter | Value |
|---|---|
| **Sprint Duration** | 2 weeks (10 working days) |
| **Planned Velocity** | 30–40 story points per sprint |
| **Total Sprints** | 13 sprints + Sprint 0 (bootstrap) |
| **Estimated Timeline** | ~28 weeks (~7 months) |
| **Release Strategy** | 3 milestone releases (Alpha → Beta → GA) |
| **Retrospective** | End of each sprint |
| **Sprint Review / Demo** | End of each sprint — demonstrate working increment |

### Guiding Principles

1. **Shippable Increment** — Every sprint produces a working, testable build
2. **Vertical Slices** — Each sprint delivers end-to-end features (UI + logic + data), not horizontal layers
3. **Must Have First** — "Must Have" stories are scheduled before "Should Have" and "Could Have"
4. **Dependencies Forward** — Foundational features (connection, tree, payload viewer) are built first so later sprints can build on them
5. **Continuous Integration** — All code merged to `main` passes automated tests before sprint close
6. **Feedback Loops** — Internal dogfooding starts after Sprint 2; user feedback incorporated from Sprint 5+

---

## Definition of Done (DoD)

A user story is considered **Done** when ALL of the following are met:

- [ ] Feature is implemented and matches the acceptance criteria
- [ ] Unit tests written and passing (≥80% code coverage for new code)
- [ ] Integration/E2E test written for user-facing workflows
- [ ] No critical or high-severity bugs open
- [ ] Code reviewed and approved by at least one peer
- [ ] UI is responsive and works in both dark and light mode (if applicable)
- [ ] Documentation updated (README, inline comments, user-facing tooltips)
- [ ] Accessibility basics met (keyboard navigation, screen reader labels)
- [ ] Performance acceptable (no visible UI lag for typical usage)
- [ ] Feature merged to `main` branch and deployed to staging/preview

---

## Story Point Scale

Using a **modified Fibonacci scale** for relative estimation:

| Points | Effort | Example |
|---|---|---|
| **1** | Trivial | Toggle a boolean setting, add a tooltip |
| **2** | Small | Add a button with a simple action, display a static value |
| **3** | Medium-Small | Input form with validation, simple list rendering |
| **5** | Medium | CRUD for connection profiles, context menu with actions |
| **8** | Medium-Large | Real-time topic tree with live updates, JSON diff viewer |
| **13** | Large | Full payload viewer with multi-format support, TLS configuration UI |
| **21** | Very Large | Complete charting system, multi-broker tab management |

---

## Release Roadmap

```
Sprint 0-2  ──►  Internal Alpha (Core: Connect + Explore + Inspect)
Sprint 3-6  ──►  Private Beta  (History + Charts + Security + Publish)
Sprint 7-10 ──►  Public Beta   (MQTT 5.0 + Cloud + Home Automation)
Sprint 11-13──►  GA v1.0       (Performance + Multi-Broker + Packaging)
```

| Milestone | After Sprint | Key Deliverables |
|---|---|---|
| **🟡 Alpha** | Sprint 2 | Connect to broker, browse topic tree, view payloads, basic publish |
| **🟠 Beta** | Sprint 6 | History + diff, charts, retained mgmt, TLS/WSS, subscriptions |
| **🔵 Public Beta** | Sprint 10 | MQTT 5.0, cloud presets, HA integration, export/import, codecs |
| **🟢 GA v1.0** | Sprint 13 | Multi-broker, performance, device sim, packaging, auto-update |

---

## Sprint 0: Project Bootstrap & Architecture

> **Sprint Goal:** Set up the project infrastructure, choose the tech stack, and establish development workflows so the team can start building features immediately.

> **Duration:** 1 week (shortened sprint)

> **Points:** N/A (infrastructure — not estimated)

### Decisions Required

| # | Decision | Options to Evaluate |
|---|---|---|
| D-01 | **UI Framework** | Electron + React, Tauri + Svelte, Tauri + React, Wails + Svelte |
| D-02 | **MQTT Client Library** | mqtt.js (Node/Browser), MQTT.js v5, Eclipse Paho |
| D-03 | **State Management** | Zustand, Redux Toolkit, Jotai, Svelte stores |
| D-04 | **Charting Library** | Chart.js, Recharts, uPlot, D3.js |
| D-05 | **Styling Approach** | Tailwind CSS, CSS Modules, Styled Components |
| D-06 | **Testing Framework** | Vitest + Playwright, Jest + Cypress |
| D-07 | **Persistence** | IndexedDB, SQLite (via Tauri), localStorage |

### Tasks

| # | Task | Owner | Status |
|---|---|---|---|
| T-00.01 | Finalize tech stack decisions (D-01 through D-07) | Team | ☐ |
| T-00.02 | Initialize project repository with chosen framework | Dev | ☐ |
| T-00.03 | Set up CI/CD pipeline (lint, test, build on PR) | DevOps | ☐ |
| T-00.04 | Configure linting (ESLint/Prettier or Biome) | Dev | ☐ |
| T-00.05 | Set up automated testing framework | Dev | ☐ |
| T-00.06 | Define project folder structure and architecture | Architect | ☐ |
| T-00.07 | Create component library foundation (design tokens, theme) | Design | ☐ |
| T-00.08 | Set up dark/light theme infrastructure (US-19.01) | Dev | ☐ |
| T-00.09 | Set up dev environment documentation (CONTRIBUTING.md) | Dev | ☐ |
| T-00.10 | Create initial README with project overview | Dev | ☐ |

### Architecture Deliverables

- [ ] Architecture Decision Records (ADRs) for all D-01 through D-07
- [ ] Folder structure documented
- [ ] Empty app shell running with hot-reload
- [ ] CI pipeline green on `main`
- [ ] Theme system (dark/light) toggling

---

## Sprint 1: Basic Connection & Topic Tree

> **Sprint Goal:** A user can connect to an MQTT broker and see topics appear in a live, hierarchical tree view.

> **Capacity:** 34 points

> **Milestone:** 🟡 Alpha (1/2)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-01.01 | Connect to broker by hostname and port | 5 | EP-01 | Must Have | Sprint 0 |
| US-01.05 | Authenticate with username/password | 3 | EP-01 | Must Have | US-01.01 |
| US-01.06 | Set custom Client ID | 2 | EP-01 | Must Have | US-01.01 |
| US-01.10 | Select MQTT protocol version | 3 | EP-01 | Must Have | US-01.01 |
| US-01.11 | Choose transport protocol (TCP/TLS/WS/WSS) | 3 | EP-01 | Must Have | US-01.01 |
| US-01.12 | See real-time connection status | 3 | EP-01 | Must Have | US-01.01 |
| US-02.01 | Hierarchical topic tree structure | 8 | EP-02 | Must Have | US-01.01 |
| US-02.02 | Real-time topic tree updates | 5 | EP-02 | Must Have | US-02.01 |
| US-02.09 | Visual indicators for retained messages | 2 | EP-02 | Must Have | US-02.01 |

### Sprint Deliverables

- [ ] Connection form with host, port, client ID, username/password
- [ ] Protocol version selector (3.1, 3.1.1, 5.0)
- [ ] Transport selector (mqtt://, mqtts://, ws://, wss://)
- [ ] Connect/Disconnect with color-coded status badge
- [ ] Topic tree that auto-populates from `#` subscription
- [ ] Tree updates in real-time without page reload
- [ ] Retained message badge on topic nodes

### Acceptance Test Scenarios

1. Connect to `test.mosquitto.org:1883` → status turns green
2. Topics appear in tree within 2 seconds of connection
3. Tree shows hierarchical structure (e.g., `$SYS/broker/version` nested correctly)
4. Disconnect → status turns red, tree frozen
5. Retained messages show pin icon

---

## Sprint 2: Payload Inspection & Message Publishing

> **Sprint Goal:** A user can click any topic to inspect its payload in multiple formats, and publish messages to any topic.

> **Capacity:** 38 points

> **Milestone:** 🟡 Alpha (2/2) — **Alpha Release**

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-02.04 | Search and filter topic tree by keyword | 5 | EP-02 | Must Have | US-02.01 |
| US-02.07 | Click topic to view payload and metadata | 5 | EP-02 | Must Have | US-02.01 |
| US-03.01 | View payload as formatted JSON | 5 | EP-03 | Must Have | US-02.07 |
| US-03.02 | View payload as raw plain text | 2 | EP-03 | Must Have | US-02.07 |
| US-03.03 | View payload in hex format | 3 | EP-03 | Must Have | US-02.07 |
| US-03.06 | Auto-detect payload format | 3 | EP-03 | Must Have | US-03.01 |
| US-03.07 | Show message metadata (QoS, retain, timestamp) | 3 | EP-03 | Must Have | US-02.07 |
| US-03.08 | Copy topic path, payload, JSON key paths | 2 | EP-03 | Must Have | US-03.01 |
| US-04.01 | Publish message to topic with text payload | 5 | EP-04 | Must Have | US-01.01 |
| US-04.02 | Select QoS level when publishing | 2 | EP-04 | Must Have | US-04.01 |
| US-04.03 | Toggle retain flag when publishing | 1 | EP-04 | Must Have | US-04.01 |
| US-19.01 | Dark mode and light mode | 2 | EP-19 | Must Have | Sprint 0 |

### Sprint Deliverables

- [ ] Topic search bar with real-time filtering
- [ ] Detail panel: payload + metadata on topic click
- [ ] Payload viewer with tabs: JSON (formatted), Raw Text, Hex
- [ ] Auto-format detection (JSON vs text vs binary)
- [ ] Copy buttons for topic, payload, JSON paths
- [ ] Publish panel: topic input, payload editor, QoS selector, retain toggle
- [ ] Dark/light mode toggle

### 🟡 Alpha Release Checklist

- [ ] All Sprint 1 + Sprint 2 stories complete and tested
- [ ] App connects to public test brokers reliably
- [ ] Core loop works: Connect → Browse → Inspect → Publish
- [ ] No critical bugs
- [ ] Internal team starts dogfooding

---

## Sprint 3: Message History, Diff Viewer & Search

> **Sprint Goal:** A user can browse the history of messages on any topic and visually diff consecutive payloads to see what changed.

> **Capacity:** 35 points

> **Milestone:** 🟠 Beta (1/4)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-05.01 | Scrollable message history per topic | 5 | EP-05 | Must Have | US-02.07 |
| US-05.02 | Side-by-side diff of consecutive messages | 8 | EP-05 | Must Have | US-05.01 |
| US-05.04 | JSON-aware structural diff | 5 | EP-05 | Should Have | US-05.02 |
| US-05.05 | Configurable history depth per topic | 3 | EP-05 | Should Have | US-05.01 |
| US-05.06 | Clear history for topic or all topics | 2 | EP-05 | Should Have | US-05.01 |
| US-02.08 | Right-click context menu on topic nodes | 5 | EP-02 | Should Have | US-02.01 |
| US-03.10 | Search within payload (Ctrl+F) | 3 | EP-03 | Should Have | US-03.01 |
| US-04.04 | JSON validation on publish | 2 | EP-04 | Should Have | US-04.01 |
| US-04.05 | "Publish Here" from context menu | 2 | EP-04 | Should Have | US-02.08, US-04.01 |

### Sprint Deliverables

- [ ] Message history panel: time-ordered list with payload previews
- [ ] Side-by-side diff viewer with green/red/yellow highlighting
- [ ] JSON structural diff (key-level changes, not just text diff)
- [ ] History depth configurable in settings (default: 100)
- [ ] Clear history per topic and globally
- [ ] Right-click context menu: Copy Topic, Copy Payload, Publish Here, Delete Retained
- [ ] Ctrl+F search within payload viewer
- [ ] JSON validation with inline errors in publish panel

---

## Sprint 4: Real-Time Charting & Retained Message Management

> **Sprint Goal:** A user can see live charts for numeric sensor data and efficiently manage retained messages including recursive deletion.

> **Capacity:** 37 points

> **Milestone:** 🟠 Beta (2/4)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-06.01 | Live line chart for numeric payloads | 8 | EP-06 | Must Have | US-02.07 |
| US-06.02 | Plot specific JSON numeric fields | 5 | EP-06 | Must Have | US-06.01 |
| US-06.03 | Overlay multiple topics on same chart | 5 | EP-06 | Should Have | US-06.01 |
| US-06.04 | Configurable chart time window | 3 | EP-06 | Should Have | US-06.01 |
| US-06.05 | Pause/resume live chart | 2 | EP-06 | Should Have | US-06.01 |
| US-07.01 | Visual identification of retained messages | 2 | EP-07 | Must Have | US-02.09 |
| US-07.02 | Delete single retained message (one click) | 3 | EP-07 | Must Have | US-07.01 |
| US-07.03 | Recursive delete retained messages in branch | 5 | EP-07 | Must Have | US-07.02 |
| US-07.04 | Preview affected topics before recursive delete | 3 | EP-07 | Should Have | US-07.03 |
| US-19.05 | Structured activity/event log | 3 | EP-19 | Should Have | Sprint 0 |

### Sprint Deliverables

- [ ] Live time-series chart with auto-scaling axes
- [ ] Click JSON numeric field → add to chart
- [ ] Multi-series overlay with color-coded legend
- [ ] Time window presets (1 min, 5 min, 1 hour) + custom
- [ ] Pause/Resume toggle for chart
- [ ] Retained filter: show only retained topics
- [ ] One-click delete retained → publishes empty payload with retain=true
- [ ] Recursive delete with preview dialog and progress indicator
- [ ] Event log panel: connection events, errors, publish/subscribe actions

---

## Sprint 5: Subscription Management & Connection Profiles

> **Sprint Goal:** A user can manage subscriptions with granular control and save/load/export connection profiles for quick broker access.

> **Capacity:** 36 points

> **Milestone:** 🟠 Beta (3/4)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-08.01 | Default `#` subscription on connect | 2 | EP-08 | Must Have | US-01.01 |
| US-08.02 | Add custom subscriptions with QoS | 5 | EP-08 | Must Have | US-01.01 |
| US-08.03 | Unsubscribe from topic filters | 3 | EP-08 | Must Have | US-08.02 |
| US-08.04 | Topic ignore/exclude patterns | 3 | EP-08 | Should Have | US-08.02 |
| US-08.05 | Subscriptions saved per connection profile | 2 | EP-08 | Should Have | US-01.02, US-08.02 |
| US-01.02 | Save connection profiles with custom name | 5 | EP-01 | Must Have | US-01.01 |
| US-01.03 | Edit and delete connection profiles | 3 | EP-01 | Must Have | US-01.02 |
| US-01.04 | Duplicate connection profile | 2 | EP-01 | Should Have | US-01.02 |
| US-01.07 | Configure Keep-Alive interval | 2 | EP-01 | Should Have | US-01.01 |
| US-01.13 | Auto-reconnect on connection drop | 3 | EP-01 | Should Have | US-01.01 |
| US-01.14 | Export/import connection profiles | 3 | EP-01 | Should Have | US-01.02 |
| US-02.10 | Expand/collapse all tree nodes | 2 | EP-02 | Should Have | US-02.01 |
| US-02.12 | Subtopic count per branch | 1 | EP-02 | Should Have | US-02.01 |

### Sprint Deliverables

- [ ] Subscription panel: add, list, unsubscribe topic filters with QoS
- [ ] Default `#` with option to customize per profile
- [ ] Ignore list for noisy topics (e.g., `$SYS/#`)
- [ ] Connection profile CRUD: save, edit, delete, duplicate
- [ ] Profile sidebar with one-click connect
- [ ] Export/import profiles as JSON
- [ ] Keep-alive configuration and auto-reconnect
- [ ] Expand All / Collapse All / Expand to Depth N
- [ ] Subtopic count badges on parent nodes

---

## Sprint 6: TLS, Security & WebSocket Support

> **Sprint Goal:** A user can securely connect to any broker using TLS, mTLS, client certificates, and WebSocket transports.

> **Capacity:** 34 points

> **Milestone:** 🟠 Beta (4/4) — **Beta Release**

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-10.01 | Connect over TLS/SSL | 5 | EP-10 | Must Have | US-01.01 |
| US-10.02 | Custom CA certificate file | 5 | EP-10 | Must Have | US-10.01 |
| US-10.03 | Client certificate + private key (mTLS) | 8 | EP-10 | Must Have | US-10.01 |
| US-10.04 | Toggle certificate validation | 2 | EP-10 | Should Have | US-10.01 |
| US-10.05 | Configure SNI | 2 | EP-10 | Should Have | US-10.01 |
| US-10.06 | WebSocket (WS/WSS) connections | 5 | EP-10 | Must Have | US-01.01 |
| US-01.08 | Configure LWT | 3 | EP-01 | Should Have | US-01.01 |
| US-01.09 | Clean Session / Clean Start + Session Expiry | 2 | EP-01 | Should Have | US-01.01 |
| US-12.07 | Ping latency display | 2 | EP-12 | Should Have | US-01.01 |

### Sprint Deliverables

- [ ] TLS connection with system truststore validation
- [ ] CA certificate file picker (PEM, CRT, DER)
- [ ] mTLS: client cert + private key + passphrase
- [ ] "Reject Unauthorized" toggle with warning
- [ ] SNI configuration
- [ ] WebSocket path and custom HTTP headers
- [ ] LWT configuration form (topic, payload, QoS, retain)
- [ ] Clean Session / Clean Start toggle
- [ ] PINGREQ/PINGRESP latency display

### 🟠 Beta Release Checklist

- [ ] All Sprint 1–6 stories complete and tested
- [ ] Secure connections working (TLS, mTLS, WSS)
- [ ] Profile management fully functional
- [ ] Core feature set stable: Connect → Browse → Inspect → Publish → History → Chart → Retained Mgmt
- [ ] Beta shared with early testers / community
- [ ] Feedback collection mechanism active

---

## Sprint 7: MQTT 5.0 Protocol Support

> **Sprint Goal:** A user can leverage MQTT 5.0 features including User Properties, Reason Codes, Message Expiry, and Request-Response patterns.

> **Capacity:** 35 points

> **Milestone:** 🔵 Public Beta (1/4)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-09.01 | View User Properties on received messages | 5 | EP-09 | Must Have | US-02.07 |
| US-09.02 | Attach User Properties when publishing | 5 | EP-09 | Must Have | US-04.01 |
| US-09.03 | Human-readable Reason Codes + Reason Strings | 5 | EP-09 | Must Have | US-01.01 |
| US-09.04 | Message Expiry Interval on publish | 3 | EP-09 | Should Have | US-04.01 |
| US-09.05 | Payload Format Indicator + Content Type | 3 | EP-09 | Should Have | US-04.01 |
| US-09.06 | Request-Response pattern (Response Topic + Correlation Data) | 8 | EP-09 | Should Have | US-04.01, US-08.02 |
| US-04.09 | MQTT 5.0 properties section in publish panel | 3 | EP-04 | Should Have | US-04.01 |
| US-04.06 | Save publish templates | 3 | EP-04 | Should Have | US-04.01 |

### Sprint Deliverables

- [ ] User Properties table in message detail panel
- [ ] Key-value editor for User Properties in publish panel
- [ ] Reason Code decoder (hex → human-readable description)
- [ ] Message Expiry Interval input + TTL display on received messages
- [ ] Payload Format Indicator dropdown + Content Type text input
- [ ] Request-Response mode: auto-subscribe to Response Topic, match by Correlation Data
- [ ] Unified MQTT 5.0 properties section in publish panel
- [ ] Publish template save/load/one-click re-publish

---

## Sprint 8: Broker Diagnostics & Cloud Integration

> **Sprint Goal:** A broker admin can monitor broker health via `$SYS` topics, and IoT developers can quickly connect to AWS IoT Core, Azure IoT Hub, and other cloud brokers.

> **Capacity:** 36 points

> **Milestone:** 🔵 Public Beta (2/4)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-12.01 | `$SYS` broker dashboard | 8 | EP-12 | Should Have | US-02.01 |
| US-12.02 | Connected clients count | 2 | EP-12 | Should Have | US-12.01 |
| US-12.03 | Message throughput rates | 3 | EP-12 | Should Have | US-12.01 |
| US-12.04 | Broker uptime and version | 2 | EP-12 | Should Have | US-12.01 |
| US-12.05 | Toggle `$SYS` in main topic tree | 2 | EP-12 | Should Have | US-12.01 |
| US-11.01 | AWS IoT Core connection preset | 5 | EP-11 | Should Have | US-10.03 |
| US-11.02 | Azure IoT Hub connection preset | 5 | EP-11 | Should Have | US-10.01 |
| US-11.05 | Local Mosquitto preset | 2 | EP-11 | Should Have | US-01.02 |
| US-04.07 | Publish history (resend previous messages) | 3 | EP-04 | Should Have | US-04.01 |
| US-02.05 | MQTT wildcard filter in search bar | 3 | EP-02 | Should Have | US-02.04 |
| US-19.02 | Resizable and dockable panels | 5 | EP-19 | Should Have | Sprint 0 |

### Sprint Deliverables

- [ ] Dedicated `$SYS` dashboard: clients, messages, bytes, uptime, memory
- [ ] Throughput sparkline charts and peak indicators
- [ ] `$SYS` toggle (show/hide in main tree)
- [ ] AWS IoT Core wizard: endpoint, certs, ALPN config
- [ ] Azure IoT Hub wizard: SAS token generator, username format
- [ ] Local Mosquitto one-click preset
- [ ] Publish history with re-send capability
- [ ] MQTT wildcard support (`+`, `#`) in topic search
- [ ] Resizable panel dividers for all main panels

---

## Sprint 9: Home Automation Integration

> **Sprint Goal:** Home automation users can browse, inspect, and manage Home Assistant discovery topics, Zigbee2MQTT devices, and Tasmota devices with specialized tools.

> **Capacity:** 33 points

> **Milestone:** 🔵 Public Beta (3/4)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-13.01 | Browse HA MQTT discovery topics | 5 | EP-13 | Should Have | US-02.01 |
| US-13.02 | Identify orphaned HA discovery topics | 5 | EP-13 | Should Have | US-13.01 |
| US-13.03 | Browse Zigbee2MQTT device states and bridge info | 5 | EP-13 | Should Have | US-02.01 |
| US-13.04 | Send commands to Zigbee2MQTT devices | 3 | EP-13 | Should Have | US-04.01 |
| US-13.07 | Device availability status (online/offline) | 3 | EP-13 | Should Have | US-02.01 |
| US-07.05 | Clean Orphaned Discovery Topics tool | 5 | EP-07 | Could Have | US-13.02, US-07.03 |
| US-02.03 | Message count and rate per topic | 3 | EP-02 | Should Have | US-02.01 |
| US-03.05 | View payloads as formatted XML | 2 | EP-03 | Should Have | US-03.01 |
| US-03.09 | View large payloads without truncation | 2 | EP-03 | Should Have | US-03.01 |

### Sprint Deliverables

- [ ] "Home Assistant" view: parsed discovery topics grouped by device
- [ ] Orphan detection: cross-reference availability topics, flag stale entities
- [ ] Zigbee2MQTT view: device list from `bridge/devices`, live state display
- [ ] Quick-publish commands to Z2M devices (toggle, brightness, color templates)
- [ ] Device availability badges: green (online), red (offline), grey (unknown)
- [ ] One-click orphaned discovery topic purge tool
- [ ] Message rate badges (msg/sec) on topic nodes
- [ ] XML payload viewer with syntax highlighting
- [ ] Large payload lazy rendering (no truncation)

---

## Sprint 10: Data Export, Import & Advanced Payload Codecs

> **Sprint Goal:** Users can export message data for external analysis, and IIoT engineers can decode binary payloads (Sparkplug B, Protobuf).

> **Capacity:** 34 points

> **Milestone:** 🔵 Public Beta (4/4) — **Public Beta Release**

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-15.01 | Export message history as CSV | 3 | EP-15 | Should Have | US-05.01 |
| US-15.02 | Export message history as JSON | 3 | EP-15 | Should Have | US-05.01 |
| US-15.03 | Export chart data as CSV | 2 | EP-15 | Should Have | US-06.01 |
| US-15.05 | Export/import connection profiles | 3 | EP-15 | Should Have | US-01.02 |
| US-06.07 | Export chart as CSV or PNG | 3 | EP-06 | Should Have | US-06.01 |
| US-14.01 | Decode Sparkplug B payloads | 8 | EP-14 | Should Have | US-03.01 |
| US-14.02 | Load custom `.proto` files for Protobuf decoding | 8 | EP-14 | Should Have | US-03.01 |
| US-03.04 | View payloads as Base64 | 2 | EP-03 | Should Have | US-03.01 |
| US-05.03 | Compare any two messages (non-consecutive diff) | 2 | EP-05 | Should Have | US-05.02 |

### Sprint Deliverables

- [ ] Export history: CSV and JSON with configurable time range
- [ ] Export chart data: CSV (timestamp + values) and PNG/SVG image
- [ ] Connection profile export/import (JSON, sensitive data exclusion option)
- [ ] Sparkplug B decoder: parse Protobuf, display metrics, resolve aliases
- [ ] Custom Protobuf decoder: load `.proto` → select message type → decode
- [ ] Base64 payload view mode
- [ ] Select any two messages for ad-hoc diff comparison

### 🔵 Public Beta Release Checklist

- [ ] All Sprint 1–10 stories complete and tested
- [ ] MQTT 5.0 features working with compliant brokers (EMQX, HiveMQ)
- [ ] Cloud broker presets tested against live services
- [ ] Home Automation features tested with Home Assistant + Zigbee2MQTT
- [ ] Export/import workflows verified
- [ ] Binary decoding working for Sparkplug B payloads
- [ ] Public beta published to GitHub Releases / website
- [ ] Bug tracker / feedback form linked in app

---

## Sprint 11: Multi-Broker Workspaces & Device Simulation

> **Sprint Goal:** Users can connect to multiple brokers simultaneously in tabs, and developers can simulate IoT devices for testing.

> **Capacity:** 35 points

> **Milestone:** 🟢 GA (1/3)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-16.01 | Multiple broker tabs (side-by-side) | 13 | EP-16 | Should Have | US-01.01 |
| US-16.02 | Open new tab without disconnecting others | 3 | EP-16 | Should Have | US-16.01 |
| US-17.01 | Publish on timed interval (sensor simulation) | 5 | EP-17 | Should Have | US-04.01 |
| US-17.02 | Payload templates with dynamic variables | 5 | EP-17 | Should Have | US-17.01 |
| US-19.03 | Keyboard shortcuts for common actions | 3 | EP-19 | Should Have | Sprint 0 |
| US-02.13 | Bookmark/favorite topics | 3 | EP-02 | Could Have | US-02.01 |
| US-05.07 | Pin/freeze topic value for comparison | 3 | EP-05 | Could Have | US-05.01 |

### Sprint Deliverables

- [ ] Tabbed interface: one broker per tab, independent state
- [ ] "New Connection" tab without affecting existing tabs
- [ ] Interval publisher: configurable topic, payload, interval, QoS
- [ ] Template engine: `{{timestamp}}`, `{{random(min,max)}}`, `{{counter}}`, `{{uuid}}`
- [ ] Keyboard shortcuts with cheat sheet (`?` key)
- [ ] Topic bookmarks with persistent favorites panel
- [ ] Pin/freeze current value for later comparison

---

## Sprint 12: Performance Optimization & UX Polish

> **Sprint Goal:** The app handles 10K+ topics and 1K+ msg/sec smoothly, and the UI is polished with final UX enhancements.

> **Capacity:** 36 points

> **Milestone:** 🟢 GA (2/3)

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-18.01 | Handle 10K+ topics without UI freeze | 8 | EP-18 | Must Have | US-02.01 |
| US-18.02 | Handle 1K+ msg/sec without dropping frames | 8 | EP-18 | Must Have | US-02.02 |
| US-18.03 | Configurable message buffer limits | 3 | EP-18 | Should Have | US-05.01 |
| US-18.04 | Rate-limiting warning on busy brokers | 3 | EP-18 | Should Have | US-01.01 |
| US-18.05 | Real-time performance metrics (memory, rate) | 3 | EP-18 | Should Have | Sprint 0 |
| US-19.07 | Customizable topic tree display | 3 | EP-19 | Could Have | US-02.01 |
| US-06.06 | Sparkline mini-charts in topic tree | 5 | EP-06 | Could Have | US-06.01 |
| US-02.11 | Sort topic tree (A-Z, activity, recent) | 3 | EP-02 | Could Have | US-02.01 |

### Sprint Deliverables

- [ ] Virtual scrolling / lazy rendering for topic tree (10K+ nodes)
- [ ] Message batching and throttled UI updates (1K+ msg/sec)
- [ ] Ring buffer with configurable max messages per topic
- [ ] High-rate warning dialog on `#` subscription
- [ ] Status bar: memory usage, msg/sec, total topics
- [ ] Tree display customization: font size, density, inline payload length
- [ ] Sparkline mini-charts next to numeric topic nodes
- [ ] Topic tree sorting options

### Performance Benchmarks

| Metric | Target |
|---|---|
| Topic tree render (10K topics) | < 500ms initial, 60fps scroll |
| Message processing (1K msg/sec) | < 5% dropped UI frames |
| Memory (10K topics, 100 msg history each) | < 800MB |
| Cold start time | < 3 seconds |

---

## Sprint 13: Deployment, Packaging & Launch

> **Sprint Goal:** The app is packaged for all platforms, auto-update works, and the project is ready for GA release.

> **Capacity:** 32 points

> **Milestone:** 🟢 GA (3/3) — **v1.0 Release**

### User Stories

| ID | Story | Points | Epic | Priority | Dependencies |
|---|---|---|---|---|---|
| US-20.01 | Windows, macOS, Linux installers | 8 | EP-20 | Must Have | All sprints |
| US-20.02 | Docker / web browser deployment | 8 | EP-20 | Should Have | All sprints |
| US-20.03 | Auto-update mechanism | 5 | EP-20 | Should Have | US-20.01 |
| US-20.04 | Lightweight and fast launch (< 3 sec) | 3 | EP-20 | Should Have | US-18.01 |
| US-20.05 | macOS signing and notarization | 3 | EP-20 | Should Have | US-20.01 |
| US-19.06 | Tooltips and onboarding hints | 3 | EP-19 | Could Have | Sprint 0 |
| US-15.04 | Export topic tree structure | 2 | EP-15 | Could Have | US-02.01 |

### Sprint Deliverables

- [ ] Windows installer (.exe) + portable version
- [ ] macOS .dmg (Apple Silicon + Intel), signed and notarized
- [ ] Linux: .AppImage, .deb, Snap
- [ ] Docker image with web UI (WebSocket connections)
- [ ] Auto-update check with changelog popup
- [ ] Launch time optimized (< 3 sec cold start)
- [ ] First-launch onboarding tour
- [ ] Topic tree export (JSON, flat text)

### 🟢 GA v1.0 Release Checklist

- [ ] All "Must Have" stories complete (100%)
- [ ] All "Should Have" stories complete (≥90%)
- [ ] Zero critical/high bugs open
- [ ] Performance benchmarks met
- [ ] All platform installers built and tested
- [ ] Auto-update pipeline operational
- [ ] Documentation complete (README, user guide, API docs)
- [ ] Website / landing page live
- [ ] GitHub Release published with changelog
- [ ] Community channels set up (GitHub Discussions / Discord)

---

## Backlog: Unscheduled "Could Have" Stories

> These stories are intentionally left unscheduled and can be pulled into any sprint if capacity allows, or scheduled for post-GA releases.

| ID | Story | Points | Epic |
|---|---|---|---|
| US-02.06 | Regex filter in topic search | 3 | EP-02 |
| US-04.08 | Publish raw binary/hex data | 3 | EP-04 |
| US-08.06 | MQTT 5.0 shared subscriptions | 3 | EP-08 |
| US-08.07 | MQTT 5.0 subscription options (No Local, Retain Handling) | 3 | EP-08 |
| US-09.07 | Topic Alias resolution | 3 | EP-09 |
| US-09.08 | Flow Control / Receive Maximum display | 2 | EP-09 |
| US-10.07 | TLS connection details (cipher, cert chain) | 3 | EP-10 |
| US-11.03 | HiveMQ Cloud preset | 2 | EP-11 |
| US-11.04 | EMQX Cloud preset | 2 | EP-11 |
| US-12.06 | ACL testing with credential switching | 5 | EP-12 |
| US-13.05 | Tasmota topic browser | 3 | EP-13 |
| US-13.06 | Mock sensor data publisher for HA | 3 | EP-13 |
| US-14.03 | CBOR payload decoder | 3 | EP-14 |
| US-14.04 | MessagePack payload decoder | 3 | EP-14 |
| US-14.05 | Custom decoder plugin API | 8 | EP-14 |
| US-16.03 | Draggable tab arrangement + split view | 5 | EP-16 |
| US-16.04 | Save/restore workspace layout | 5 | EP-16 |
| US-17.03 | Multi-device concurrent simulation | 5 | EP-17 |
| US-17.04 | Auto-responder rules (mock device logic) | 5 | EP-17 |
| US-17.05 | Scripted test scenarios | 8 | EP-17 |
| US-19.04 | Command palette (Ctrl+Shift+P) | 5 | EP-19 |
| US-19.08 | Notification alerts on topic/value thresholds | 5 | EP-19 |

---

## Risk Register

| # | Risk | Impact | Probability | Mitigation |
|---|---|---|---|---|
| R-01 | **Performance degradation at scale** — Electron/WebView memory issues with 10K+ topics | High | Medium | Evaluate Tauri (Rust backend) in Sprint 0. Implement virtual scrolling early. Budget Sprint 12 for optimization. |
| R-02 | **MQTT 5.0 library gaps** — Chosen MQTT client library may not fully support MQTT 5.0 features | Medium | Medium | Spike MQTT 5.0 features during Sprint 0. Validate User Properties, Reason Codes, and Request-Response before committing. |
| R-03 | **Cloud broker auth complexity** — AWS IoT ALPN/SigV4 and Azure SAS tokens may be complex to implement | Medium | Low | Allocate 5 points each for AWS and Azure. Prepare fallback documentation for manual configuration. |
| R-04 | **Sparkplug B Protobuf complexity** — Binary decoding with metric alias resolution is non-trivial | Medium | Medium | Use existing `sparkplug-payload` library. Start with basic decoding, add alias resolution iteratively. |
| R-05 | **Scope creep** — "Could Have" stories pulled into sprints at the expense of "Must Have" | High | Medium | Strict sprint goal adherence. Product Owner gates all scope additions. Backlog grooming every sprint. |
| R-06 | **Cross-platform packaging issues** — macOS notarization, Linux desktop integration, Windows code signing | Medium | High | Start packaging research in Sprint 0. Test packaging on all platforms by Sprint 6 (Beta). |

---

## Sprint Velocity Tracking

> Update this table at the end of each sprint to track actual vs. planned velocity.

| Sprint | Planned Points | Completed Points | Velocity | Carryover | Notes |
|---|---|---|---|---|---|
| Sprint 0 | N/A | N/A | N/A | — | Bootstrap |
| Sprint 1 | 34 | — | — | — | |
| Sprint 2 | 38 | — | — | — | 🟡 Alpha |
| Sprint 3 | 35 | — | — | — | |
| Sprint 4 | 37 | — | — | — | |
| Sprint 5 | 36 | — | — | — | |
| Sprint 6 | 34 | — | — | — | 🟠 Beta |
| Sprint 7 | 35 | — | — | — | |
| Sprint 8 | 36 | — | — | — | |
| Sprint 9 | 33 | — | — | — | |
| Sprint 10 | 34 | — | — | — | 🔵 Public Beta |
| Sprint 11 | 35 | — | — | — | |
| Sprint 12 | 36 | — | — | — | |
| Sprint 13 | 32 | — | — | — | 🟢 GA v1.0 |

---

## Ceremony Schedule

| Ceremony | When | Duration | Purpose |
|---|---|---|---|
| **Sprint Planning** | Day 1 of sprint | 2 hours | Commit to sprint goal and stories |
| **Daily Standup** | Every day | 15 minutes | Blockers, progress, coordination |
| **Backlog Grooming** | Mid-sprint (Day 5) | 1 hour | Estimate upcoming stories, clarify AC |
| **Sprint Review / Demo** | Last day of sprint | 1 hour | Demo working increment to stakeholders |
| **Sprint Retrospective** | Last day of sprint | 45 minutes | What went well, what to improve |

---

*Last updated: 2026-08-28*
*Document version: 1.0*

