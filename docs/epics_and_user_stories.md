# MQTT Explorer — Epics & User Stories

> A comprehensive product backlog for building a full-featured MQTT Explorer application.
> Derived from research into the original MQTT Explorer app, community feedback, competitor analysis, and real-world IoT/home automation workflows.

---

## Table of Contents

- [Personas](#personas)
- [EP-01: Connection Management & Profiles](#ep-01-connection-management--profiles)
- [EP-02: Topic Tree Exploration & Navigation](#ep-02-topic-tree-exploration--navigation)
- [EP-03: Message Inspection & Payload Viewer](#ep-03-message-inspection--payload-viewer)
- [EP-04: Message Publishing](#ep-04-message-publishing)
- [EP-05: Message History & Diff Viewer](#ep-05-message-history--diff-viewer)
- [EP-06: Real-Time Charting & Data Visualization](#ep-06-real-time-charting--data-visualization)
- [EP-07: Retained Message Management](#ep-07-retained-message-management)
- [EP-08: Subscription Management & Filtering](#ep-08-subscription-management--filtering)
- [EP-09: MQTT 5.0 Advanced Features](#ep-09-mqtt-50-advanced-features)
- [EP-10: Security, TLS & Authentication](#ep-10-security-tls--authentication)
- [EP-11: Cloud Broker Integration](#ep-11-cloud-broker-integration)
- [EP-12: Broker Monitoring & Diagnostics](#ep-12-broker-monitoring--diagnostics)
- [EP-13: Home Automation Integration](#ep-13-home-automation-integration)
- [EP-14: Advanced Payload Codecs & Binary Decoding](#ep-14-advanced-payload-codecs--binary-decoding)
- [EP-15: Data Export & Import](#ep-15-data-export--import)
- [EP-16: Multi-Broker & Workspace Management](#ep-16-multi-broker--workspace-management)
- [EP-17: Device Simulation & Automated Testing](#ep-17-device-simulation--automated-testing)
- [EP-18: Performance & Scalability](#ep-18-performance--scalability)
- [EP-19: User Experience & Interface](#ep-19-user-experience--interface)
- [EP-20: Deployment & Platform Support](#ep-20-deployment--platform-support)
- [Priority Summary](#priority-summary)

---

## Personas

| Persona | Description |
|---|---|
| **IoT Developer** | Embedded/firmware engineer building and debugging IoT devices (ESP32, STM32, Raspberry Pi). Needs to verify telemetry, test commands, and validate LWT behavior. |
| **Home Automation Enthusiast** | Home Assistant / Zigbee2MQTT / Tasmota user who manages smart home devices over MQTT. Needs to diagnose entities, clean up ghost devices, and test automations. |
| **Backend Engineer** | Cloud/distributed systems developer monitoring microservice communication over MQTT. Needs high-throughput monitoring, payload schema validation, and load balancing verification. |
| **Broker Administrator** | DevOps/SRE responsible for MQTT broker health, security, and access control. Needs broker diagnostics, ACL testing, and performance metrics. |
| **IIoT Engineer** | Industrial IoT engineer working with SCADA/PLC systems, Sparkplug B, and factory-floor telemetry. Needs complex namespace navigation and binary payload decoding. |

---

## EP-01: Connection Management & Profiles

> **Goal:** Enable users to quickly connect to any MQTT broker with saved, reusable connection profiles.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-01.01 | As a **user**, I want to connect to an MQTT broker by entering a hostname and port, so that I can start exploring topics. | - Input fields for host, port, client ID<br>- Default port pre-filled (1883 for TCP, 8883 for TLS)<br>- Connect/Disconnect button with status indicator | Must Have |
| US-01.02 | As a **user**, I want to save connection profiles with a custom name, so that I can quickly reconnect to frequently used brokers. | - Save profile with name, host, port, credentials, protocol<br>- Profiles listed in a sidebar or dropdown<br>- One-click connect from saved profile | Must Have |
| US-01.03 | As a **user**, I want to edit and delete saved connection profiles, so that I can keep my profile list current. | - Edit button opens profile form pre-filled with existing values<br>- Delete button with confirmation dialog<br>- Changes persist across app restarts | Must Have |
| US-01.04 | As a **user**, I want to duplicate an existing connection profile, so that I can quickly create variations (e.g., same broker, different credentials). | - "Duplicate" option in profile context menu<br>- Creates a copy with "(Copy)" appended to the name<br>- All settings cloned | Should Have |
| US-01.05 | As a **user**, I want to authenticate with username and password, so that I can connect to secured brokers. | - Username and password fields in connection form<br>- Password field masked by default with show/hide toggle<br>- Credentials saved (optionally) with the profile | Must Have |
| US-01.06 | As a **user**, I want to set a custom Client ID, so that I can identify my session on the broker and avoid conflicts. | - Client ID input field<br>- Auto-generate random Client ID button<br>- Warn if Client ID may conflict with another active session | Must Have |
| US-01.07 | As a **user**, I want to configure the Keep-Alive interval, so that I can control how frequently the client pings the broker. | - Numeric input for keep-alive (seconds)<br>- Default value: 60 seconds<br>- Visual ping/pong latency indicator when connected | Should Have |
| US-01.08 | As a **user**, I want to configure a Last Will and Testament (LWT), so that the broker notifies other subscribers if my client disconnects unexpectedly. | - LWT section in connection form with: topic, payload, QoS, retain flag<br>- Optional Will Delay Interval (MQTT 5.0)<br>- LWT verified by intentionally killing connection | Should Have |
| US-01.09 | As a **user**, I want to choose between Clean Session (MQTT 3.1.1) or Clean Start + Session Expiry (MQTT 5.0), so that I can control session persistence behavior. | - Toggle for Clean Session (3.1.1) or Clean Start (5.0)<br>- Session Expiry Interval input (seconds) for MQTT 5.0<br>- Tooltip explaining each option | Should Have |
| US-01.10 | As a **user**, I want to select the MQTT protocol version (3.1, 3.1.1, 5.0), so that I connect with the correct protocol for my broker. | - Dropdown selector for protocol version<br>- Default: Auto-negotiate or 3.1.1<br>- UI adapts to show/hide MQTT 5.0-specific settings | Must Have |
| US-01.11 | As a **user**, I want to choose the transport protocol (TCP, TLS, WebSocket, Secure WebSocket), so that I can connect over the appropriate channel. | - Protocol selector: `mqtt://`, `mqtts://`, `ws://`, `wss://`<br>- Port auto-updates based on selection<br>- WebSocket path field shown when WS/WSS selected | Must Have |
| US-01.12 | As a **user**, I want to see the real-time connection status (connecting, connected, disconnected, error), so that I know the state of my session. | - Color-coded status badge (green/yellow/red/grey)<br>- Status text with timestamp of last state change<br>- Auto-reconnect option with configurable retry interval | Must Have |
| US-01.13 | As a **user**, I want the app to auto-reconnect when the connection drops, so that I don't lose visibility during transient network failures. | - Auto-reconnect toggle (default: on)<br>- Configurable retry interval and max retries<br>- Visual indicator showing reconnection attempts | Should Have |
| US-01.14 | As a **user**, I want to export and import connection profiles, so that I can migrate settings between machines or share them with teammates. | - Export all profiles to JSON file<br>- Import profiles from JSON file<br>- Option to export individual profiles<br>- Sensitive data (passwords, keys) optionally excluded | Should Have |

---

## EP-02: Topic Tree Exploration & Navigation

> **Goal:** Provide a hierarchical, interactive tree view of all MQTT topics on the broker for intuitive exploration and discovery.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-02.01 | As a **user**, I want to see all MQTT topics organized in a hierarchical tree structure, so that I can understand the broker's topic namespace at a glance. | - Topics parsed by `/` delimiter into parent/child nodes<br>- Tree is collapsible/expandable<br>- Leaf nodes show the latest payload value | Must Have |
| US-02.02 | As a **user**, I want the topic tree to update in real-time as messages arrive, so that I always see the current state of each topic. | - Tree nodes update value in-place (no scrolling log)<br>- Node briefly highlights/flashes on update<br>- No UI freeze during rapid updates | Must Have |
| US-02.03 | As a **user**, I want to see message count and message rate (msg/sec) per topic node, so that I can identify chatty or silent topics. | - Message counter badge on each node<br>- Messages-per-second indicator for active topics<br>- Cumulative counts for parent nodes (recursive) | Should Have |
| US-02.04 | As a **user**, I want to search and filter the topic tree by keyword, so that I can quickly find specific topics in a large namespace. | - Search bar above the topic tree<br>- Filters tree in real-time as user types<br>- Matching text highlighted in topic names<br>- Non-matching branches hidden/collapsed | Must Have |
| US-02.05 | As a **user**, I want to filter topics using MQTT wildcards (`+` and `#`), so that I can use familiar MQTT patterns to narrow the tree. | - Support `+` (single-level) and `#` (multi-level) in the search bar<br>- Example: `home/+/temperature` matches `home/kitchen/temperature` and `home/bedroom/temperature` | Should Have |
| US-02.06 | As a **user**, I want to filter topics using regex patterns, so that I can perform complex searches on topic names. | - Regex toggle in the search bar<br>- Standard regex syntax supported<br>- Invalid regex shows inline error | Could Have |
| US-02.07 | As a **user**, I want to click on a topic node to view its latest payload, metadata (QoS, retain flag, timestamp), and history, so that I can inspect message details. | - Clicking a node opens a detail panel<br>- Shows: topic path, payload, QoS, retain flag, timestamp<br>- Detail panel resizable and dockable | Must Have |
| US-02.08 | As a **user**, I want to right-click a topic node for a context menu, so that I can perform common actions (copy topic, publish to topic, delete retained, subscribe/unsubscribe). | - Context menu with actions: Copy Topic Path, Copy Payload, Publish Here, Delete Retained, Subscribe, Unsubscribe<br>- Keyboard shortcuts for common actions | Should Have |
| US-02.09 | As a **user**, I want to see visual indicators on topic nodes for retained messages, so that I can distinguish retained vs. live messages in the tree. | - Retained message icon/badge on applicable nodes<br>- Optional filter to show only retained topics<br>- Color differentiation (e.g., pin icon or colored dot) | Must Have |
| US-02.10 | As a **user**, I want to expand or collapse all nodes in the tree at once, so that I can quickly navigate deep topic hierarchies. | - "Expand All" and "Collapse All" buttons<br>- "Expand to Depth N" option<br>- Remember expansion state per session | Should Have |
| US-02.11 | As a **user**, I want to sort the topic tree alphabetically or by message activity, so that I can organize the view based on my needs. | - Sort options: A-Z, Z-A, Most Active, Least Active, Most Recent<br>- Sort persists until changed<br>- Sortable at any level of the tree | Could Have |
| US-02.12 | As a **user**, I want to see the total number of unique topics and subtopics for each branch, so that I can gauge the size of topic namespaces. | - Subtopic count badge on parent nodes<br>- Tooltip showing recursive topic count<br>- Summary statistics in the status bar | Should Have |
| US-02.13 | As a **user**, I want to bookmark/favorite specific topics, so that I can quickly jump to important topics without searching. | - Star/bookmark icon on topic nodes<br>- Bookmarked topics listed in a "Favorites" panel<br>- Bookmarks persist across sessions | Could Have |

---

## EP-03: Message Inspection & Payload Viewer

> **Goal:** Allow users to inspect message payloads in multiple formats with syntax highlighting and auto-detection.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-03.01 | As a **user**, I want to view message payloads as formatted, syntax-highlighted JSON, so that I can easily read structured data. | - Auto-detect JSON payloads<br>- Collapsible/expandable JSON tree view<br>- Syntax coloring for keys, values, types<br>- Copy formatted JSON button | Must Have |
| US-03.02 | As a **user**, I want to view payloads as raw plain text, so that I can inspect non-JSON text messages. | - Raw text tab/mode in payload viewer<br>- Monospace font rendering<br>- Word wrap toggle | Must Have |
| US-03.03 | As a **user**, I want to view payloads in hexadecimal format, so that I can inspect binary message content byte by byte. | - Hex viewer with address offsets<br>- Side-by-side hex + ASCII display<br>- Byte-level selection and copy | Must Have |
| US-03.04 | As a **user**, I want to view payloads as Base64-encoded data, so that I can inspect encoded binary content. | - Base64 display mode<br>- Decode Base64 to raw bytes option<br>- Copy encoded/decoded content | Should Have |
| US-03.05 | As a **user**, I want to view payloads as formatted XML, so that I can inspect XML-based messages with proper indentation. | - Auto-detect XML payloads<br>- Syntax-highlighted XML with indentation<br>- Collapsible XML tree view | Should Have |
| US-03.06 | As a **user**, I want the payload viewer to auto-detect the format (JSON, XML, plain text, binary), so that I don't have to manually switch modes. | - Auto-detection based on payload content analysis<br>- Respects MQTT 5.0 `Content-Type` property if present<br>- Manual override always available | Must Have |
| US-03.07 | As a **user**, I want to see message metadata alongside the payload (QoS level, retain flag, topic, timestamp, packet ID), so that I have full context for every message. | - Metadata bar above/beside payload viewer<br>- QoS badge (0, 1, 2), retain indicator, timestamp<br>- Packet size in bytes | Must Have |
| US-03.08 | As a **user**, I want to copy the topic path, full payload, or specific JSON keys to my clipboard, so that I can use them in my code or configuration. | - Copy Topic button<br>- Copy Payload (raw) button<br>- Right-click on JSON key → "Copy Key Path" (e.g., `data.sensors[0].temperature`)<br>- Right-click on JSON value → "Copy Value" | Must Have |
| US-03.09 | As a **user**, I want to view large payloads without truncation, so that I don't miss data in verbose messages. | - No hard payload size limit (or configurable limit)<br>- Lazy rendering for very large payloads (>100KB)<br>- Warning banner for exceptionally large payloads | Should Have |
| US-03.10 | As a **user**, I want to search within a payload, so that I can find specific keys or values in large JSON/XML documents. | - Ctrl+F search within the payload viewer<br>- Highlight all matches<br>- Navigate between matches (next/previous) | Should Have |

---

## EP-04: Message Publishing

> **Goal:** Enable users to compose and publish MQTT messages to any topic with full control over QoS, retain flag, and payload format.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-04.01 | As a **user**, I want to publish a message to a specific topic with a text payload, so that I can send commands or test data to devices/services. | - Topic input field with autocomplete from known topics<br>- Payload text editor (multi-line)<br>- Publish button with success/failure feedback | Must Have |
| US-04.02 | As a **user**, I want to select the QoS level (0, 1, 2) when publishing, so that I can control delivery guarantees. | - QoS dropdown/radio: 0, 1, 2<br>- Default: QoS 0<br>- Tooltip explaining each level | Must Have |
| US-04.03 | As a **user**, I want to toggle the retain flag when publishing, so that I can control whether the broker stores the message. | - Retain checkbox (default: unchecked)<br>- Visual warning when retain is enabled<br>- Tooltip explaining retain behavior | Must Have |
| US-04.04 | As a **user**, I want to publish JSON payloads with syntax validation, so that I don't accidentally send malformed data. | - JSON validation on publish (optional toggle)<br>- Inline error highlighting for invalid JSON<br>- Auto-format/prettify JSON button | Should Have |
| US-04.05 | As a **user**, I want to publish a message directly from the topic tree context menu ("Publish Here"), so that I can quickly send to a known topic without retyping. | - "Publish Here" in right-click context menu<br>- Opens publish panel with topic pre-filled<br>- Focus goes to payload editor | Should Have |
| US-04.06 | As a **user**, I want to save frequently used publish templates (topic + payload + QoS + retain), so that I can quickly resend common messages. | - Save as Template button in publish panel<br>- Templates listed in a sidebar/dropdown<br>- One-click publish from template<br>- Edit/delete templates | Should Have |
| US-04.07 | As a **user**, I want to view my publish history, so that I can resend or modify previously published messages. | - Scrollable list of recently published messages<br>- Click to re-populate the publish form<br>- Clear history option | Should Have |
| US-04.08 | As a **user**, I want to publish raw binary/hex data, so that I can test binary protocols and payloads. | - Hex input mode in publish panel<br>- Byte-by-byte hex entry or paste<br>- File upload option for binary payloads | Could Have |
| US-04.09 | As a **user**, I want to set MQTT 5.0 properties when publishing (User Properties, Message Expiry, Content Type, Response Topic, Correlation Data), so that I can test advanced MQTT 5.0 features. | - Expandable "MQTT 5.0 Properties" section in publish panel<br>- Key-value editor for User Properties<br>- Input fields for Message Expiry, Content Type, Response Topic, Correlation Data | Should Have |

---

## EP-05: Message History & Diff Viewer

> **Goal:** Maintain a rolling history of messages per topic and provide visual diffing to highlight changes between consecutive payloads.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-05.01 | As a **user**, I want to see a scrollable history of all messages received on a selected topic, so that I can review past values and detect patterns. | - Time-ordered list of payloads for the selected topic<br>- Each entry shows: timestamp, payload preview, QoS, retain flag<br>- Configurable history depth (e.g., last 100 messages) | Must Have |
| US-05.02 | As a **user**, I want to compare two consecutive messages side-by-side with color-coded diffs, so that I can instantly see what changed. | - Side-by-side or inline diff view<br>- Added lines/keys highlighted in green<br>- Removed lines/keys highlighted in red<br>- Changed values highlighted in yellow/orange | Must Have |
| US-05.03 | As a **user**, I want to select any two messages from the history to compare, so that I can diff non-consecutive payloads. | - Checkbox or click to select two messages<br>- "Compare Selected" button<br>- Diff view opens with the two selected messages | Should Have |
| US-05.04 | As a **user**, I want to view JSON diffs at the key level (structural diff), so that I can see which specific JSON fields changed rather than a text-level diff. | - JSON-aware diff that understands key/value structure<br>- Shows: added keys, removed keys, changed values<br>- Nested key paths displayed (e.g., `sensors.temperature: 22.1 → 23.4`) | Should Have |
| US-05.05 | As a **user**, I want to configure the maximum history depth per topic, so that I can balance memory usage with history availability. | - Settings option for max messages per topic (default: 100)<br>- Oldest messages evicted when limit reached<br>- Global and per-topic override options | Should Have |
| US-05.06 | As a **user**, I want to clear the message history for a specific topic or all topics, so that I can start fresh during debugging sessions. | - "Clear History" in topic context menu<br>- "Clear All History" in toolbar/menu<br>- Confirmation dialog before clearing | Should Have |
| US-05.07 | As a **user**, I want to pin/freeze a topic's current value, so that I can compare it against future updates without losing it. | - "Pin Value" button that snapshots the current payload<br>- Pinned value persists in a panel even as new messages arrive<br>- Compare pinned value vs. latest value | Could Have |

---

## EP-06: Real-Time Charting & Data Visualization

> **Goal:** Provide live, zero-configuration time-series graphs for numeric MQTT payloads, enabling instant visual monitoring without external tools.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-06.01 | As a **user**, I want to see a live line chart of numeric payload values over time, so that I can monitor sensor readings visually. | - Auto-detect numeric payloads (plain numbers or JSON numeric values)<br>- Real-time line chart that updates with each message<br>- X-axis: timestamp, Y-axis: value<br>- Auto-scaling axes | Must Have |
| US-06.02 | As a **user**, I want to plot specific numeric fields from JSON payloads, so that I can chart nested values like `temperature` or `humidity`. | - Click on a JSON numeric field to add it to the chart<br>- Support dot-notation paths (e.g., `data.sensors.temperature`)<br>- Legend showing each plotted field | Must Have |
| US-06.03 | As a **user**, I want to overlay multiple topics/fields on the same chart, so that I can compare related metrics (e.g., indoor vs. outdoor temperature). | - "Add to Chart" action from multiple topic nodes<br>- Color-coded series with legend<br>- Up to 10+ concurrent series | Should Have |
| US-06.04 | As a **user**, I want to configure the chart time window (last 1 min, 5 min, 1 hour, custom), so that I can zoom in on recent data or see longer trends. | - Time window selector (preset and custom)<br>- Scrollable/draggable time axis for historical review<br>- Auto-scroll toggle (follows latest data) | Should Have |
| US-06.05 | As a **user**, I want to pause and resume the live chart, so that I can freeze the display to analyze a specific moment. | - Pause/Resume button on chart<br>- While paused, data continues to buffer<br>- Resuming shows buffered data or jumps to current | Should Have |
| US-06.06 | As a **user**, I want to see sparkline mini-charts inline in the topic tree, so that I can get a quick visual of value trends without opening a full chart. | - Small inline sparkline next to numeric topic nodes<br>- Shows last N values as a tiny graph<br>- Clickable to open full chart view | Could Have |
| US-06.07 | As a **user**, I want to export chart data as CSV or PNG image, so that I can use the data in reports or external analysis tools. | - "Export as CSV" button (timestamp + values)<br>- "Export as PNG/SVG" button (chart image)<br>- Configurable export time range | Should Have |

---

## EP-07: Retained Message Management

> **Goal:** Provide powerful tools to identify, inspect, and purge retained messages — the most requested feature in the MQTT community.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-07.01 | As a **user**, I want to visually identify all retained messages in the topic tree, so that I can distinguish stored state from live messages. | - Retained message badge/icon on topic nodes (e.g., 📌 pin icon)<br>- Filter to show "Retained Only" topics<br>- Count of total retained messages in status bar | Must Have |
| US-07.02 | As a **user**, I want to delete a single retained message with one click, so that I can clear stale data from the broker. | - "Delete Retained" button in topic detail panel<br>- Publishes empty payload with `retain: true` to the topic<br>- Topic node updates to reflect cleared state | Must Have |
| US-07.03 | As a **user**, I want to recursively delete all retained messages under a topic branch, so that I can clean up entire device namespaces at once. | - "Delete Retained (Recursive)" in branch context menu<br>- Confirmation dialog showing count of affected topics<br>- Progress indicator during batch deletion<br>- Summary of deleted topics upon completion | Must Have |
| US-07.04 | As a **user**, I want to preview which topics will be affected before performing a recursive retained delete, so that I don't accidentally purge important data. | - Dry-run / preview mode showing list of topics to be purged<br>- Checkboxes to exclude specific topics<br>- "Proceed" / "Cancel" buttons after review | Should Have |
| US-07.05 | As a **Home Automation enthusiast**, I want a dedicated "Clean Orphaned Discovery Topics" tool, so that I can remove ghost entities from Home Assistant without manual topic hunting. | - Scan `homeassistant/#` for retained config topics<br>- Cross-reference with availability topics to identify orphans<br>- One-click purge of orphaned discovery topics<br>- Report of cleaned entities | Could Have |

---

## EP-08: Subscription Management & Filtering

> **Goal:** Give users fine-grained control over which topics they subscribe to, enabling efficient operation on busy brokers.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-08.01 | As a **user**, I want the app to subscribe to `#` (all topics) by default on connect, so that I can immediately discover the full broker namespace. | - Default subscription: `#`<br>- Option to change default in connection profile<br>- Warning for high-volume brokers | Must Have |
| US-08.02 | As a **user**, I want to add custom topic subscriptions with specific QoS levels, so that I can focus on relevant topics and reduce noise. | - "Add Subscription" panel with topic filter and QoS selector<br>- Multiple concurrent subscriptions supported<br>- Active subscriptions listed with unsubscribe option | Must Have |
| US-08.03 | As a **user**, I want to unsubscribe from specific topic filters, so that I can stop receiving messages I no longer need. | - "Unsubscribe" button per active subscription<br>- Messages from unsubscribed topics stop appearing<br>- Tree nodes optionally removed or greyed out | Must Have |
| US-08.04 | As a **user**, I want to configure topic ignore/exclude patterns, so that I can block noisy topics (e.g., `$SYS/#`) from cluttering the tree. | - Ignore list in connection profile settings<br>- Support wildcard patterns<br>- Ignored topics hidden from tree and don't consume memory | Should Have |
| US-08.05 | As a **user**, I want to configure subscription topics per connection profile, so that different brokers can have different default subscriptions. | - Subscription list saved as part of connection profile<br>- Loaded automatically on connect<br>- Editable after connection | Should Have |
| US-08.06 | As a **user**, I want to subscribe to MQTT 5.0 shared subscriptions (`$share/{group}/{filter}`), so that I can test load-balanced consumer patterns. | - Shared subscription input in subscription panel<br>- Group name and topic filter fields<br>- Visual indicator showing shared subscription status | Could Have |
| US-08.07 | As a **user**, I want to set MQTT 5.0 subscription options (No Local, Retain As Published, Retain Handling), so that I can fine-tune subscription behavior. | - Checkboxes/dropdowns for each MQTT 5.0 subscription option<br>- Options shown only when MQTT 5.0 is selected<br>- Tooltips explaining each option | Could Have |

---

## EP-09: MQTT 5.0 Advanced Features

> **Goal:** Fully support MQTT 5.0 protocol features that are missing from most existing MQTT clients.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-09.01 | As a **user**, I want to view User Properties (key-value pairs) attached to received messages, so that I can inspect metadata sent alongside payloads. | - "User Properties" section in message detail panel<br>- Table view of key-value string pairs<br>- Copyable keys and values | Must Have |
| US-09.02 | As a **user**, I want to attach User Properties when publishing messages, so that I can send metadata with my test messages. | - Key-value editor in publish panel (add/remove rows)<br>- Support multiple properties with the same key<br>- Properties included in the PUBLISH packet | Must Have |
| US-09.03 | As a **user**, I want to see human-readable Reason Codes and Reason Strings in connection and packet responses, so that I understand exactly why operations succeed or fail. | - Decode numeric reason codes to descriptive text<br>- Display Reason String from broker if provided<br>- Show in: CONNACK, PUBACK, SUBACK, DISCONNECT popups/logs | Must Have |
| US-09.04 | As a **user**, I want to set a Message Expiry Interval when publishing, so that I can test time-limited message delivery. | - Numeric input for Message Expiry (seconds) in publish panel<br>- Display remaining TTL on received messages (if broker provides it)<br>- Expired messages visually marked | Should Have |
| US-09.05 | As a **user**, I want to specify a Payload Format Indicator and Content Type when publishing, so that subscribers can auto-detect payload encoding. | - Dropdown for Payload Format: Unspecified (0) / UTF-8 (1)<br>- Text input for Content Type (e.g., `application/json`, `application/protobuf`)<br>- Received Content Type displayed in message metadata | Should Have |
| US-09.06 | As a **user**, I want to use the Request-Response pattern with Response Topic and Correlation Data, so that I can test MQTT 5.0 RPC workflows. | - "Request-Response" mode in publish panel<br>- Inputs for Response Topic and Correlation Data<br>- Auto-subscribes to Response Topic on send<br>- Matches incoming responses by Correlation Data<br>- Displays request/response pairs together | Should Have |
| US-09.07 | As a **user**, I want to see Topic Alias resolution in the UI, so that aliased topics are displayed by their full name, not their numeric alias. | - Transparent alias decoding (user sees full topic name)<br>- Optional "Show Aliases" debug mode displaying alias numbers<br>- Support for both client-to-broker and broker-to-client aliases | Could Have |
| US-09.08 | As a **user**, I want to see Flow Control information (Receive Maximum), so that I understand the in-flight message limits negotiated with the broker. | - Display `Receive Maximum` from CONNACK<br>- Show current in-flight QoS 1/2 message count<br>- Warning when approaching the limit | Could Have |

---

## EP-10: Security, TLS & Authentication

> **Goal:** Support all common MQTT security mechanisms including TLS, mTLS, and certificate management.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-10.01 | As a **user**, I want to connect over TLS/SSL, so that my MQTT traffic is encrypted. | - `mqtts://` protocol option<br>- Default port 8883 for TLS<br>- Connection validates server certificate against system truststore | Must Have |
| US-10.02 | As a **user**, I want to provide a custom CA certificate file, so that I can connect to brokers using self-signed or private CA certificates. | - File picker for CA certificate (PEM, CRT, DER formats)<br>- Certificate details displayed after loading (issuer, expiry, subject)<br>- Saved with connection profile | Must Have |
| US-10.03 | As a **user**, I want to provide a client certificate and private key for mutual TLS (mTLS), so that I can authenticate to brokers that require client certificates. | - File pickers for: Client Certificate, Client Key<br>- Passphrase input for encrypted private keys<br>- Support for PEM, PKCS#8, PKCS#12 (.p12/.pfx) formats | Must Have |
| US-10.04 | As a **user**, I want to toggle certificate validation on/off, so that I can connect to development brokers with self-signed certificates. | - "Reject Unauthorized" checkbox (default: on)<br>- Warning banner when validation is disabled<br>- Setting saved per connection profile | Should Have |
| US-10.05 | As a **user**, I want to configure Server Name Indication (SNI), so that I can connect to multi-tenant cloud brokers correctly. | - SNI hostname input field<br>- Auto-filled from broker hostname by default<br>- Override option for custom SNI values | Should Have |
| US-10.06 | As a **user**, I want to connect via WebSocket (WS/WSS), so that I can reach brokers behind web proxies or on port 443. | - `ws://` and `wss://` protocol options<br>- WebSocket path input (e.g., `/mqtt`)<br>- Custom HTTP headers support (e.g., `Authorization: Bearer ...`) | Must Have |
| US-10.07 | As a **user**, I want to see TLS connection details (cipher suite, protocol version, certificate chain), so that I can verify the security of my connection. | - "Connection Info" panel showing: TLS version, cipher suite<br>- Certificate chain viewer (subject, issuer, validity dates)<br>- Warning for weak ciphers or expiring certificates | Could Have |

---

## EP-11: Cloud Broker Integration

> **Goal:** Provide guided setup and presets for connecting to major cloud IoT platforms.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-11.01 | As an **IoT developer**, I want a connection preset/wizard for AWS IoT Core, so that I can connect without manually configuring ALPN, mTLS, and custom endpoints. | - "AWS IoT Core" preset in connection profile<br>- Guides: endpoint URL, CA cert, device cert, private key<br>- Auto-configures port 8883 mTLS or port 443 with ALPN `x-amzn-mqtt-ca`<br>- Notes QoS 2 is unsupported | Should Have |
| US-11.02 | As an **IoT developer**, I want a connection preset/wizard for Azure IoT Hub, so that I can connect using SAS tokens or X.509 certificates. | - "Azure IoT Hub" preset<br>- SAS token generator (input: key, device ID, hub name, expiry)<br>- Auto-configures username format and port 8883<br>- Documents fixed topic patterns (telemetry, C2D, twin) | Should Have |
| US-11.03 | As a **user**, I want a connection preset for HiveMQ Cloud, so that I can quickly connect with username/password over TLS. | - "HiveMQ Cloud" preset<br>- Auto-configures TLS port 8883<br>- Username/password authentication fields | Could Have |
| US-11.04 | As a **user**, I want a connection preset for EMQX Cloud, so that I can quickly connect to managed EMQX instances. | - "EMQX Cloud" preset<br>- TLS configuration with deployment endpoint<br>- Username/password or token-based auth | Could Have |
| US-11.05 | As a **user**, I want a connection preset for Mosquitto (local), so that I can connect to a local development broker with one click. | - "Local Mosquitto" preset<br>- Default: `localhost:1883`, no auth<br>- Option to add credentials if configured | Should Have |

---

## EP-12: Broker Monitoring & Diagnostics

> **Goal:** Provide broker health monitoring via `$SYS` topics and connection diagnostics.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-12.01 | As a **broker admin**, I want to view `$SYS/#` broker statistics in a dedicated dashboard, so that I can monitor broker health without cluttering the topic tree. | - Separate "$SYS Dashboard" panel/tab<br>- Subscribes to `$SYS/#` automatically<br>- Organized display: Clients, Messages, Bytes, Uptime, Memory | Should Have |
| US-12.02 | As a **broker admin**, I want to see the number of connected clients, so that I can monitor broker capacity. | - `$SYS/broker/clients/connected` displayed prominently<br>- Live-updating value<br>- Historical chart option | Should Have |
| US-12.03 | As a **broker admin**, I want to see message throughput rates (messages/sec sent and received), so that I can detect traffic spikes or anomalies. | - `$SYS/broker/messages/received` and `.../sent` rates<br>- Real-time counter with sparkline chart<br>- Peak rate indicator | Should Have |
| US-12.04 | As a **broker admin**, I want to see broker uptime and version, so that I can verify the broker is running and up-to-date. | - Display `$SYS/broker/uptime` and `$SYS/broker/version`<br>- Formatted uptime (e.g., "3 days 12h 45m") | Should Have |
| US-12.05 | As a **broker admin**, I want to toggle `$SYS` topics on/off in the main topic tree, so that they don't clutter the application namespace when I'm debugging devices. | - Toggle: "Show $SYS Topics" (default: off in main tree)<br>- When off, $SYS topics only visible in dedicated dashboard<br>- Configurable per connection profile | Should Have |
| US-12.06 | As a **user**, I want to test broker ACLs by connecting with different credentials and verifying topic access, so that I can validate security rules. | - Quick-switch between credentials without full reconnect<br>- Log showing "Subscribe Denied" / "Publish Denied" results<br>- Side-by-side comparison of permissions for different users | Could Have |
| US-12.07 | As a **user**, I want to see ping latency to the broker, so that I can diagnose network issues. | - Display PINGREQ → PINGRESP round-trip time (ms)<br>- Running average and peak latency<br>- Warning when latency exceeds threshold | Should Have |

---

## EP-13: Home Automation Integration

> **Goal:** Provide specialized tools for Home Assistant, Zigbee2MQTT, Tasmota, and ESPHome users to debug and manage their smart home MQTT ecosystems.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-13.01 | As a **Home Automation enthusiast**, I want to browse and inspect Home Assistant MQTT discovery topics (`homeassistant/#`), so that I can see all auto-discovered entities and their configurations. | - Dedicated "HA Discovery" view or filter preset<br>- Parses discovery JSON to show: entity name, type, state topic, availability topic<br>- Groups by device | Should Have |
| US-13.02 | As a **Home Automation enthusiast**, I want to identify orphaned/stale Home Assistant discovery topics, so that I can find ghost entities that no longer have active devices. | - Scan retained discovery topics under `homeassistant/#`<br>- Cross-reference with availability topics<br>- Mark topics as "Active" or "Orphaned/Stale"<br>- Filter to show only orphaned topics | Should Have |
| US-13.03 | As a **Home Automation enthusiast**, I want to browse Zigbee2MQTT device states and bridge info, so that I can monitor my Zigbee network. | - Filter preset for `zigbee2mqtt/#`<br>- Parse and display `zigbee2mqtt/bridge/devices` as a device list<br>- Show device attributes: friendly name, model, manufacturer, link quality | Should Have |
| US-13.04 | As a **Home Automation enthusiast**, I want to send commands to Zigbee2MQTT devices (e.g., `{"state": "TOGGLE"}`), so that I can test device control without using Home Assistant. | - Quick-publish to `zigbee2mqtt/<device>/set`<br>- Pre-built command templates for common actions (toggle, brightness, color)<br>- Response verification from state topic | Should Have |
| US-13.05 | As a **Home Automation enthusiast**, I want to browse Tasmota device topics (`tele/`, `stat/`, `cmnd/`), so that I can monitor and control Tasmota-flashed devices. | - Filter preset for Tasmota topic patterns<br>- Group by device name across `tele/`, `stat/`, `cmnd/` prefixes<br>- Display formatted telemetry (uptime, WiFi signal, sensor readings) | Could Have |
| US-13.06 | As a **Home Automation enthusiast**, I want to publish mock sensor data to test Home Assistant automations, so that I can verify triggers without physically manipulating sensors. | - "Simulate Sensor" quick-action<br>- Input: topic, value, unit<br>- Preset templates for temperature, humidity, motion, door contact<br>- Publish with appropriate retain flag | Could Have |
| US-13.07 | As a **Home Automation enthusiast**, I want to check device availability status (online/offline), so that I can quickly identify disconnected devices. | - Availability topic monitoring per device<br>- Color-coded status: green (online), red (offline), grey (unknown)<br>- Notification when device goes offline | Should Have |

---

## EP-14: Advanced Payload Codecs & Binary Decoding

> **Goal:** Support decoding of binary and industrial payload formats beyond plain text and JSON.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-14.01 | As an **IIoT engineer**, I want to decode Sparkplug B payloads, so that I can inspect industrial MQTT metrics from SCADA/PLC systems. | - Built-in Sparkplug B Protobuf decoder<br>- Display metric name, value, type, timestamp, alias<br>- Handle NBIRTH/NDEATH/DBIRTH/DDEATH/NDATA/DDATA message types<br>- Resolve metric aliases using birth certificate context | Should Have |
| US-14.02 | As a **developer**, I want to load custom `.proto` files to decode Protocol Buffer payloads, so that I can inspect binary-encoded messages from my services. | - File picker to load `.proto` schema files<br>- Select message type for decoding<br>- Decoded fields displayed in structured tree view<br>- Cache loaded schemas per connection profile | Should Have |
| US-14.03 | As a **developer**, I want to decode CBOR payloads, so that I can inspect Concise Binary Object Representation data from constrained IoT devices. | - Auto-detect CBOR binary format<br>- Decode and display as JSON-like tree<br>- Support CBOR tags and nested structures | Could Have |
| US-14.04 | As a **developer**, I want to decode MessagePack payloads, so that I can inspect efficient binary serializations. | - Auto-detect MessagePack format<br>- Decode and display as JSON-like tree<br>- Handle all MessagePack types | Could Have |
| US-14.05 | As a **developer**, I want to register custom payload decoders via plugins or scripts, so that I can support proprietary binary formats. | - Plugin/script API for custom decoders<br>- Input: raw bytes, Output: structured display object<br>- JavaScript or Python decoder functions<br>- Register decoder per topic pattern | Could Have |

---

## EP-15: Data Export & Import

> **Goal:** Enable users to export message data, connection profiles, and session state for external analysis and portability.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-15.01 | As a **user**, I want to export message history for selected topics as CSV, so that I can analyze data in spreadsheets or external tools. | - "Export as CSV" option per topic or multi-select<br>- Columns: timestamp, topic, payload, QoS, retain<br>- Configurable time range for export | Should Have |
| US-15.02 | As a **user**, I want to export message history as JSON, so that I can programmatically process historical data. | - "Export as JSON" option<br>- Array of message objects with full metadata<br>- Pretty-printed or minified option | Should Have |
| US-15.03 | As a **user**, I want to export chart/graph data as CSV, so that I can use time-series data in external analysis tools. | - "Export Chart Data" button on chart panel<br>- Columns: timestamp, series name, value<br>- Matches the visible chart time window | Should Have |
| US-15.04 | As a **user**, I want to export the current topic tree structure, so that I can document or diff my broker's topic namespace. | - "Export Topic Tree" option<br>- Formats: JSON tree structure, flat text list, YAML<br>- Include/exclude payload values option | Could Have |
| US-15.05 | As a **user**, I want to export and import connection profiles as a portable file, so that I can share broker configs with my team. | - Export: JSON file with all profile settings<br>- Import: Load profiles from JSON file<br>- Merge or replace existing profiles<br>- Option to exclude sensitive data (passwords, keys) | Should Have |

---

## EP-16: Multi-Broker & Workspace Management

> **Goal:** Allow users to connect to multiple brokers simultaneously and manage them in tabs or workspaces.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-16.01 | As a **user**, I want to connect to multiple brokers simultaneously in separate tabs, so that I can monitor Dev, Staging, and Production environments side-by-side. | - Tabbed interface with one broker per tab<br>- Each tab has its own independent topic tree and state<br>- Tab shows broker name and connection status<br>- No cross-tab interference | Should Have |
| US-16.02 | As a **user**, I want to open a new broker tab without disconnecting existing ones, so that I can add connections on the fly. | - "New Connection" tab/button<br>- Existing tabs remain active and receiving messages<br>- Independent connect/disconnect per tab | Should Have |
| US-16.03 | As a **user**, I want to drag and arrange broker tabs, so that I can organize my workspace. | - Draggable tabs for reordering<br>- Optional split view (side-by-side panes)<br>- Close tab with confirmation if connected | Could Have |
| US-16.04 | As a **user**, I want to save and restore my workspace layout (open tabs, subscriptions, selected topics), so that I can resume my session after restarting the app. | - "Save Workspace" option<br>- Auto-save workspace state on close<br>- "Restore Last Session" on startup<br>- Named workspace profiles | Could Have |

---

## EP-17: Device Simulation & Automated Testing

> **Goal:** Enable users to simulate IoT devices and automate MQTT testing scenarios without writing code.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-17.01 | As a **developer**, I want to publish messages on a timed interval, so that I can simulate a sensor publishing periodic telemetry. | - Interval publisher: topic, payload, QoS, retain, interval (ms/sec)<br>- Start/Stop controls<br>- Counter showing number of messages sent | Should Have |
| US-17.02 | As a **developer**, I want to define payload templates with dynamic variables (timestamp, random values, incrementing counters), so that I can generate realistic test data. | - Template syntax: `{{timestamp}}`, `{{random(min, max)}}`, `{{counter}}`, `{{uuid}}`<br>- Preview rendered payload before starting<br>- Custom variable definitions | Should Have |
| US-17.03 | As a **developer**, I want to simulate multiple virtual devices publishing concurrently, so that I can load-test my broker and subscribers. | - Create N virtual publishers with configurable topics and intervals<br>- Concurrent execution<br>- Aggregate statistics: total msg/sec, success/fail counts | Could Have |
| US-17.04 | As a **developer**, I want to create auto-responder rules (when message X is received on topic A, publish message Y to topic B), so that I can mock device behavior. | - Rule editor: trigger topic (with wildcards), condition (optional), response topic, response payload<br>- Enable/disable individual rules<br>- Execution log showing triggered responses | Could Have |
| US-17.05 | As a **developer**, I want to run a scripted test scenario (sequence of publish/subscribe/wait/assert steps), so that I can automate regression testing of my MQTT system. | - Scenario editor with step types: Publish, Subscribe, Wait, Assert (value equals, contains, regex)<br>- Run scenario with pass/fail results<br>- Save and load scenario files<br>- Export results as test report | Could Have |

---

## EP-18: Performance & Scalability

> **Goal:** Ensure the application remains responsive and stable even when connected to high-volume brokers with thousands of topics and messages per second.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-18.01 | As a **user**, I want the app to handle 10,000+ topics without UI freezing, so that I can explore large industrial brokers. | - Virtual scrolling / lazy rendering for topic tree<br>- Smooth scrolling and interaction at 10K+ nodes<br>- Memory usage stays under reasonable bounds (< 1GB for 10K topics) | Must Have |
| US-18.02 | As a **user**, I want the app to handle 1,000+ messages/sec without dropping frames, so that I can monitor high-throughput brokers. | - Message batching and throttled UI updates<br>- Background message processing off the UI thread<br>- Configurable UI update rate (e.g., 10 FPS, 30 FPS) | Must Have |
| US-18.03 | As a **user**, I want configurable message buffer limits, so that I can prevent excessive memory consumption on long-running sessions. | - Settings: Max messages per topic, Max total messages in memory<br>- Ring buffer behavior (oldest evicted first)<br>- Memory usage indicator in status bar | Should Have |
| US-18.04 | As a **user**, I want the app to gracefully handle connection to `#` on a busy broker with a rate-limiting warning, so that I'm informed before the app becomes sluggish. | - Detection of high message rate on initial connection<br>- Warning dialog: "This broker is sending >X msg/sec. Consider using a more specific subscription."<br>- Option to auto-throttle or switch to specific subscriptions | Should Have |
| US-18.05 | As a **user**, I want to see real-time performance metrics (memory usage, message rate, CPU), so that I can monitor the app's resource consumption. | - Status bar showing: memory usage, messages/sec, total topics<br>- Performance dashboard accessible from menu<br>- Warning indicators when approaching limits | Should Have |

---

## EP-19: User Experience & Interface

> **Goal:** Deliver a polished, intuitive, and customizable user interface.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-19.01 | As a **user**, I want a dark mode and light mode, so that I can use the app comfortably in different lighting conditions. | - Toggle between dark and light themes<br>- System theme auto-detection option<br>- Consistent styling across all panels | Must Have |
| US-19.02 | As a **user**, I want resizable and dockable panels (topic tree, payload viewer, chart, publish panel), so that I can arrange the layout to suit my workflow. | - Draggable panel dividers<br>- Panels can be resized, minimized, or detached<br>- Layout persists across sessions | Should Have |
| US-19.03 | As a **user**, I want keyboard shortcuts for common actions, so that I can work efficiently without relying on the mouse. | - Shortcuts for: Connect/Disconnect, Publish, Search, Copy Topic/Payload, Navigate Tree<br>- Shortcut cheat sheet accessible via `?` or Help menu<br>- Customizable keybindings | Should Have |
| US-19.04 | As a **user**, I want a command palette (Ctrl+Shift+P), so that I can quickly access any action by name. | - Command palette overlay with fuzzy search<br>- Lists all available actions and their shortcuts<br>- Quick navigation to topics by name | Could Have |
| US-19.05 | As a **user**, I want to see a structured activity/event log, so that I can trace connection events, errors, and publish/subscribe actions. | - Scrollable log panel with timestamps<br>- Log levels: Info, Warning, Error<br>- Filterable by level and source<br>- Copy and export log entries | Should Have |
| US-19.06 | As a **user**, I want contextual tooltips and onboarding hints, so that I can learn the app's features without external documentation. | - Tooltips on all buttons and icons<br>- First-launch guided tour of key features<br>- "What's this?" help mode | Could Have |
| US-19.07 | As a **user**, I want to customize the topic tree display (show/hide payloads inline, truncation length, font size), so that I can optimize readability for my use case. | - Settings: Inline payload preview (on/off), max preview length, font size<br>- Tree density option (compact/comfortable/spacious)<br>- Color-code topics by custom rules | Could Have |
| US-19.08 | As a **user**, I want notification alerts when specific topics receive messages or values cross thresholds, so that I'm alerted to important events without constantly watching. | - Alert rules: topic match, value condition (>, <, ==, contains)<br>- System notification (OS-level) and in-app badge<br>- Sound alert option<br>- Snooze and mute controls | Could Have |

---

## EP-20: Deployment & Platform Support

> **Goal:** Make the application available across all major desktop platforms, with optional web and containerized deployments.

### User Stories

| ID | Story | Acceptance Criteria | Priority |
|---|---|---|---|
| US-20.01 | As a **user**, I want to install the app on Windows, macOS, and Linux, so that I can use it on my preferred operating system. | - Windows: Installer (.exe) + portable version<br>- macOS: .dmg with Apple Silicon and Intel support<br>- Linux: .AppImage, .deb, Snap | Must Have |
| US-20.02 | As a **user**, I want to run the app in a web browser (via Docker or standalone server), so that I can access it from any device on my network. | - Docker image with web UI<br>- Accessible via `http://localhost:PORT`<br>- WebSocket-based broker connections<br>- Feature parity with desktop (or clearly documented limitations) | Should Have |
| US-20.03 | As a **user**, I want the app to auto-update, so that I always have the latest features and security fixes. | - Auto-update check on launch (configurable)<br>- Notification when update is available<br>- One-click update with changelog display<br>- Option to disable auto-updates | Should Have |
| US-20.04 | As a **user**, I want the app to be lightweight and fast to launch, so that I can quickly start debugging without waiting. | - Cold start time < 3 seconds<br>- Minimal idle memory footprint (< 150MB)<br>- No splash screen delays | Should Have |
| US-20.05 | As a **macOS user**, I want the app to be properly signed and notarized, so that I don't get Gatekeeper/quarantine warnings. | - Apple Developer ID signing<br>- Notarized with Apple<br>- No manual `xattr -cr` or `spctl` workaround needed | Should Have |

---

## Priority Summary

| Priority | Count | Description |
|---|---|---|
| **Must Have** | ~30 | Core functionality required for an MVP — connecting, exploring, inspecting, publishing, and basic performance. |
| **Should Have** | ~45 | Important features that differentiate from basic clients — history, charting, MQTT 5.0, security, home automation, export. |
| **Could Have** | ~25 | Nice-to-have features for power users — scripting, custom codecs, advanced UI, device simulation. |

> **Total: ~100 user stories across 20 epics**

---

## Research Sources

- [MQTT Explorer](https://mqtt-explorer.com/) by Thomas Nordquist — official app and documentation
- [MQTT Explorer GitHub](https://github.com/thomasnordquist/MQTT-Explorer) — issues, feature requests, community forks
- [MQTTX](https://mqttx.app/) by EMQX — primary competitor analysis
- Reddit communities: r/homeassistant, r/esp32, r/IOT, r/MQTT
- Home Assistant MQTT Discovery documentation
- Zigbee2MQTT, Tasmota, ESPHome official documentation
- MQTT 5.0 specification (OASIS Standard)
- Sparkplug B specification (Eclipse Tahu)
- AWS IoT Core, Azure IoT Hub connection documentation

