<p align="center">
  <img src="docs/assets/favicon.svg" alt="MQtlin Logo" width="160" />
</p>

<h1 align="center">MQtlin</h1>

<p align="center">
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.4+-7F52FF?style=flat&logo=kotlin&logoColor=white" alt="Kotlin"></a>
  <a href="https://www.jetbrains.com/lp/compose-multiplatform/"><img src="https://img.shields.io/badge/Compose-Multiplatform-4285F4?style=flat&logo=jetpackcompose&logoColor=white" alt="Compose Multiplatform"></a>
  <a href="https://mqtt.org"><img src="https://img.shields.io/badge/MQTT-3.1.1%20%7C%205.0-660066?style=flat&logo=mqtt&logoColor=white" alt="MQTT Standard"></a>
  <a href="https://github.com/drlacheheb/MQtlin/releases"><img src="https://img.shields.io/badge/Target-Windows%20%7C%20macOS%20%7C%20Linux-2ea44f?style=flat" alt="Desktop Platforms"></a>
  <a href="https://ko-fi.com/drlacheheb"><img src="https://img.shields.io/badge/Support-Buy%20Me%20a%20Coffee-FF5E5B?style=flat&logo=kofi&logoColor=white" alt="Ko-fi"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPLv3-blue.svg?style=flat" alt="License"></a>
</p>

<p align="center">
  <em>A modern, high-performance MQTT explorer and topic hierarchy visualizer built with Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP). Explore broker namespaces as structured trees, inspect multi-format payloads in real time, and publish messages with zero bloat.</em>
</p>

---

## What It Does

Traditional MQTT clients treat high-frequency message traffic like an unreadable flat chat stream. **MQtlin** transforms raw broker streams into a structured, live-updating namespace hierarchy:

1. **Hierarchical Topic Tree**: Organizes MQTT topics into an expandable/collapsible directory tree based on `/` delimiters.
2. **In-Place Live Updates**: Topics update values in-place with visual activity indicators without chaotic auto-scrolling logs.
3. **Multi-Format Payload Inspection**: Auto-detects and formats JSON (collapsible tree), plain text, hexadecimal byte arrays, and binary data.
4. **Full Protocol Fidelity**: Native support for both **MQTT 3.1.1** and **MQTT 5.0** (User Properties, Reason Codes, Message Expiry).
5. **Universal Message Publishing**: Publish commands and telemetry with fine-grained QoS (0, 1, 2), retain flags, and JSON validation.
6. **Native Cross-Platform Desktop**: Responsive UI powered by Skia hardware acceleration for Windows, macOS, and Linux from a single Kotlin codebase.



## Tech Stack

| Layer | Technology | Description |
| :--- | :--- | :--- |
| **UI Framework** | **Compose Multiplatform** | Modern declarative UI powered by Skia 2D rendering |
| **MQTT Engine** | **HiveMQ MQTT Client** | High-throughput Netty-based JVM MQTT 3.1.1 and 5.0 client |
| **Navigation** | **Decompose** | Component-tree navigation with isolated lifecycles and split panes |
| **State Management** | **MVI / Kotlin StateFlow** | Unidirectional data flow and reactive coroutines |
| **Dependency Injection** | **Koin 4.x** | Pure Kotlin lightweight multiplatform dependency injection |
| **Persistence** | **kotlinx.serialization** | Local human-readable JSON profile & settings storage |
| **Networking** | **Netty & HiveMQ Engine** | High-performance TCP, TLS, and WebSocket broker transport |
| **Serialization** | **kotlinx.serialization** | Fast, zero-reflection JSON and binary codecs |

---

## Quick Start

### 1. Prerequisites
* **Java Development Kit (JDK)**: JDK 21 LTS (JetBrains Runtime JBR 21 or Eclipse Temurin recommended).
* **Git** installed on your system.

### 2. Clone the Repository
```bash
git clone https://github.com/drlacheheb/MQtlin.git
cd MQtlin
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

## Privacy Policy

**MQtlin** values user privacy and security:
- **Zero Telemetry:** MQtlin does not track, collect, store, or transmit any personal data, analytics, or usage metrics.
- **Local Data Storage:** All connection profiles, broker credentials, settings, and payload logs remain 100% on your local machine (`~/.mqtlin/`).
- **Direct Broker Transport:** All network sockets and connections are established strictly between your machine and your target MQTT broker.

---

## Support the Project

If you find MQtlin helpful for your IoT development, smart home management, or broker debugging, consider supporting its open-source development:

<p align="left">
  <a href="https://ko-fi.com/drlacheheb" target="_blank" rel="noopener noreferrer">
    <img src="https://storage.ko-fi.com/cdn/kofi3.png?v=3" alt="Buy Me a Coffee at ko-fi.com" height="40" />
  </a>
</p>

---

## License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**. See the [LICENSE](LICENSE) file for details.