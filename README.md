<p align="center">
  <img src="docs/assets/logo.svg" alt="Mqtlin Logo" width="180" />
</p>

<h1 align="center">Mqtlin</h1>

<p align="center">
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.4+-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin"></a>
  <a href="https://www.jetbrains.com/lp/compose-multiplatform/"><img src="https://img.shields.io/badge/Compose-Multiplatform-4285F4?style=flat&logo=jetpackcompose&logoColor=white" alt="Compose Multiplatform"></a>
  <a href="https://mqtt.org"><img src="https://img.shields.io/badge/MQTT-3.1.1%20%7C%205.0-660066?style=flat&logo=mqtt&logoColor=white" alt="MQTT Standard"></a>
  <a href="https://github.com/drlacheheb/Mqtlin/releases"><img src="https://img.shields.io/badge/Target-Windows%20%7C%20macOS%20%7C%20Linux-2ea44f?style=flat" alt="Desktop Platforms"></a>
  <a href="https://ko-fi.com/drlacheheb"><img src="https://img.shields.io/badge/Support-Buy%20Me%20a%20Coffee-FF5E5B?style=flat&logo=kofi&logoColor=white" alt="Ko-fi"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg?style=flat" alt="License"></a>
</p>

<p align="center">
  <em>A modern, high-performance MQTT explorer and topic hierarchy visualizer built with Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP). Explore broker namespaces as structured trees, inspect multi-format payloads in real time, and publish messages with zero bloat.</em>
</p>

---

## What It Does

Traditional MQTT clients treat high-frequency message traffic like an unreadable flat chat stream. **Mqtlin** transforms raw broker streams into a structured, live-updating namespace hierarchy:

1. **Hierarchical Topic Tree**: Organizes MQTT topics into an expandable/collapsible directory tree based on `/` delimiters.
2. **In-Place Live Updates**: Topics update values in-place with visual activity indicators without chaotic auto-scrolling log hell.
3. **Multi-Format Payload Inspection**: Auto-detects and formats JSON (collapsible tree), plain text, hexadecimal byte arrays, and binary data.
4. **Full Protocol Fidelity**: Native support for both **MQTT 3.1.1** and **MQTT 5.0** (User Properties, Reason Codes, Message Expiry).
5. **Universal Message Publishing**: Publish commands and telemetry with fine-grained QoS (0, 1, 2), retain flags, and JSON validation.
6. **Native Cross-Platform Desktop**: Ultra-responsive UI powered by Skia hardware acceleration for Windows, macOS, and Linux from a single Kotlin codebase.

---

## Alpha Version Roadmap (Sprint 1 & Sprint 2)

Here is the progress tracker for the core features scheduled for the upcoming **Alpha release**:

### 🔌 Connection Management
- [ ] Connect to broker via hostname and port (`US-01.01`)
- [ ] Username & password authentication (`US-01.05`)
- [ ] Custom Client ID configuration & random ID generator (`US-01.06`)
- [ ] MQTT protocol version selector: 3.1, 3.1.1, 5.0 (`US-01.10`)
- [ ] Transport selector: TCP (`mqtt://`), TLS (`mqtts://`), WS (`ws://`), WSS (`wss://`) (`US-01.11`)
- [ ] Real-time connection status indicator with color-coded badge (`US-01.12`)

### 🌳 Topic Tree Exploration
- [ ] Hierarchical topic tree view with live in-place updates (`US-02.01`, `US-02.02`)
- [ ] Visual indicators and badges for retained messages (`US-02.09`)
- [ ] Real-time keyword search and topic tree filtering (`US-02.04`)
- [ ] Topic selection to view payload and message metadata (`US-02.07`)

### 🔍 Payload Viewer & Inspection
- [ ] Formatted, syntax-highlighted collapsible JSON tree viewer (`US-03.01`)
- [ ] Raw plain-text viewer with word-wrap toggle (`US-03.02`)
- [ ] Hexadecimal / binary viewer with address offsets (`US-03.03`)
- [ ] Auto-detection of payload format (JSON vs. Text vs. Hex) (`US-03.06`)
- [ ] Message metadata display: QoS level, retain flag, timestamp, payload size (`US-03.07`)
- [ ] Quick copy utilities for topic path, raw payload, and JSON key paths (`US-03.08`)

### 📤 Message Publishing
- [ ] Publish message panel with topic autocomplete and multi-line editor (`US-04.01`)
- [ ] QoS level selection: QoS 0, QoS 1, QoS 2 (`US-04.02`)
- [ ] Retain flag toggle with visual indicator (`US-04.03`)

### 🎨 UI & Design System
- [ ] Dark mode and light mode theme support with system auto-detection (`US-19.01`)
- [x] Project architecture and dependency configuration (Sprint 0)

---

## Tech Stack

| Layer | Technology | Description |
| :--- | :--- | :--- |
| **UI Framework** | **Compose Multiplatform** | Modern declarative UI powered by Skia 2D rendering |
| **MQTT Engine** | **HiveMQ MQTT Client** | High-throughput Netty-based JVM MQTT 3.1.1 & 5.0 client |
| **Navigation** | **Decompose** | Component-tree navigation with isolated lifecycles and split panes |
| **State Management** | **MVI / Kotlin StateFlow** | Unidirectional data flow and reactive coroutines |
| **Dependency Injection** | **Koin 4.x** | Pure Kotlin lightweight multiplatform dependency injection |
| **Persistence** | **SQLDelight & DataStore** | Compile-time safe SQLite database & typed settings storage |
| **Networking** | **Ktor Client 3.x** | Multiplatform HTTP and WebSocket networking |
| **Serialization** | **kotlinx.serialization** | Fast, zero-reflection JSON and binary codecs |

---

## Quick Start

### 1. Prerequisites
* **Java Development Kit (JDK)**: JDK 21 LTS (JetBrains Runtime JBR 21 or Eclipse Temurin recommended).
* **Git** installed on your system.

### 2. Clone the Repository
```bash
git clone https://github.com/drlacheheb/Mqtlin.git
cd Mqtlin
```

### 3. Run the Desktop Application

* **Standard Run:**
```bash
# Windows
.\gradlew.bat :desktopApp:run

# macOS / Linux
./gradlew :desktopApp:run
```

* **Hot Reload Mode (Instant UI updates):**
```bash
# Windows
.\gradlew.bat :desktopApp:hotRun --auto

# macOS / Linux
./gradlew :desktopApp:hotRun --auto
```

### 4. Run Tests
```bash
# Windows
.\gradlew.bat test

# macOS / Linux
./gradlew test
```

### 5. Package Native Desktop Installers
```bash
# Windows (.msi / .exe)
.\gradlew.bat packageDistributionForCurrentOS

# macOS (.dmg)
./gradlew packageDmg

# Linux (.deb)
./gradlew packageDeb
```

---

## Support the Project

If you find Mqtlin helpful for your IoT development, smart home management, or broker debugging, consider supporting its open-source development:

<p align="left">
  <a href="https://ko-fi.com/drlacheheb" target="_blank" rel="noopener noreferrer">
    <img src="https://storage.ko-fi.com/cdn/kofi3.png?v=3" alt="Buy Me a Coffee at ko-fi.com" height="40" />
  </a>
</p>

---

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.