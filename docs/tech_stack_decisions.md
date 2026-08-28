# MQTT Explorer — Tech Stack Decisions

> Technology choices for the MQTT Explorer application built with Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP).
> Based on ecosystem research conducted August 2026, covering community adoption, maturity, and real-world recommendations.

---

## Table of Contents

- [Stack Overview](#stack-overview)
- [Core Platform](#core-platform)
- [MQTT Client Library](#mqtt-client-library)
- [Navigation Framework](#navigation-framework)
- [State Management](#state-management)
- [Dependency Injection](#dependency-injection)
- [Persistence & Storage](#persistence--storage)
- [Networking & HTTP](#networking--http)
- [Serialization](#serialization)
- [Charting & Data Visualization](#charting--data-visualization)
- [Testing](#testing)
- [Desktop Packaging & Distribution](#desktop-packaging--distribution)
- [Version Matrix](#version-matrix)
- [Architecture Diagram](#architecture-diagram)

---

## Stack Overview

| Category | Decision | Maturity |
|---|---|---|
| **Language** | Kotlin 2.1.x | 🟢 Mature |
| **UI Framework** | Compose Multiplatform 1.7.x+ | 🟢 Stable |
| **Build System** | Gradle (Kotlin DSL) | 🟢 Mature |
| **MQTT Client** | HiveMQ MQTT Client 1.3.x | 🟢 Enterprise-grade |
| **Navigation** | Decompose 3.2.x+ | 🟢 Mature |
| **State Management** | Decompose Components + StateFlow (MVI pattern) | 🟢 Mature |
| **DI Framework** | Koin 4.x | 🟢 De-facto standard |
| **Persistence (Data)** | SQLDelight | 🟢 Very mature |
| **Persistence (Settings)** | Jetpack DataStore Preferences | 🟢 Stable |
| **Networking** | Ktor Client 3.x | 🟢 Standard |
| **Serialization** | kotlinx.serialization | 🟢 Mature |
| **Charting** | Custom Canvas (real-time) + Vico (rich charts) | 🟢/🟡 |
| **Testing** | kotlin.test + Kotest + Turbine + Mokkery | 🟢 Mature |
| **Packaging** | Compose Gradle Plugin (jpackage) | 🟢 Standard |

---

## Core Platform

### Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP)

| Aspect | Detail |
|---|---|
| **Primary Target** | Desktop (JVM) — Windows, macOS, Linux |
| **Stretch Target** | Web (Kotlin/WASM) for browser-based access |
| **UI Rendering** | Skia-backed hardware-accelerated 2D pipeline |
| **Design System** | Material 3 (adaptive components) |

**Why KMP + CMP:**
- Single codebase for all desktop platforms
- Kotlin coroutines are a natural fit for async MQTT message streams
- Strong typing prevents payload handling bugs
- Compose declarative UI makes complex layouts (split panes, trees, tabs) manageable
- Growing ecosystem with JetBrains & Google co-investment

---

## MQTT Client Library

### ✅ Decision: HiveMQ MQTT Client

| Library | MQTT 5.0 | KMP Native | Performance | Maturity | Verdict |
|---|---|---|---|---|---|
| **HiveMQ MQTT Client** | ✅ Full spec | JVM only | Netty-based, highest throughput | 🟢 Enterprise-grade | **✅ Selected** |
| KMQTT | ✅ Full spec | ✅ Pure KMP | Good | 🟡 Community-driven | Backup for KMP-native needs |
| Eclipse Paho | ✅ (separate artifact) | JVM only | Callback-based, dated | 🔴 Legacy/maintenance | ❌ Rejected |
| MQTTastic-Client-KMP | ✅ | ✅ Pure KMP | Zero-copy parser | 🟢 Modern & active | Future consideration |
| DitchOoM/mqtt | ✅ | ✅ Pure KMP | Good | 🟡 Stable | Future consideration |

**Why HiveMQ:**
- **Netty 4.1.x backbone** — non-blocking I/O, high throughput, backpressure management
- **Complete MQTT 5.0** — User Properties, Reason Codes, Topic Aliases, Shared Subscriptions, Request/Response, Flow Control, Session/Message Expiry
- **Robust reconnection** — exponential backoff, automatic keep-alive management
- **TLS/mTLS/WebSocket** — production-grade security handling out of the box
- Since CMP Desktop runs on JVM, there's no penalty for using a JVM library
- Bridges cleanly to Kotlin Coroutines via `asFlow()` / reactive-streams extensions

**Architecture Note:** Define an abstract `MqttClient` interface in `commonMain`, with a HiveMQ-backed implementation in `desktopMain`. This keeps the door open for pure KMP MQTT clients (KMQTT / MQTTastic) if web/iOS targets are added later.

---

## Navigation Framework

### ✅ Decision: Decompose

| Framework | Desktop Fit | Multi-pane | Tabs | Testability | Learning Curve | Verdict |
|---|---|---|---|---|---|---|
| **Decompose 3.2.x** | ✅ Outstanding | ✅ Native | ✅ Native | ✅ Highest | High | **✅ Selected** |
| Official Compose Nav 2.8.x | Moderate | Requires custom | Basic | Moderate | Low | ❌ Too mobile-centric |
| Voyager 1.1.x | Good | Limited | ✅ Built-in | Moderate | Low | ❌ Outgrown by complex layouts |
| Circuit (Slack) | Good | Moderate | Moderate | High | Moderate | Future consideration |

**Why Decompose:**
- MQTT Explorer is **not a mobile app with linear screens** — it's a complex desktop tool with:
  - Multi-broker tabs
  - Split panes (topic tree ← → payload viewer)
  - Dockable panels (chart, publish, log)
  - Modal dialogs (connection config, delete confirmation)
  - Independent lifecycle per broker connection
- Decompose models navigation as a **tree of components** — each component owns its own child stack, slots (dialogs), and pages (tabs)
- Business logic and MQTT subscriptions live inside the Component, persisting cleanly across UI recompositions and window resizes
- **100% unit-testable** navigation flows without the Compose runtime

**Key Decompose Concepts for Our App:**
```
RootComponent
├── ConnectionListComponent (sidebar)
├── WorkspaceComponent (tab container)
│   ├── BrokerTabComponent[0] (Dev broker)
│   │   ├── TopicTreeComponent
│   │   ├── PayloadViewerComponent
│   │   ├── ChartComponent
│   │   └── PublishComponent
│   └── BrokerTabComponent[1] (Production broker)
│       └── ...
└── DialogSlot (Connection Editor, Delete Confirmation, Settings)
```

---

## State Management

### ✅ Decision: MVI with StateFlow inside Decompose Components

| Approach | Predictability | Debuggability | Complexity | Verdict |
|---|---|---|---|---|
| **Decompose Components + StateFlow (MVI)** | ✅ Highest | ✅ Time-travel ready | Moderate | **✅ Selected** |
| AndroidX ViewModel + StateFlow | Good | Good | Low | Good for simple screens |
| MVIKotlin (full framework) | ✅ Highest | ✅ Built-in time-travel | High | Optional enhancement |

**Pattern:**
```
User Action (Intent)
    → Component processes intent
        → Updates MutableStateFlow<UiState>
            → Compose UI observes via collectAsState()
                → Renders updated UI
```

**Why this approach:**
- `StateFlow<UiState>` is the universal state primitive in KMP — no library lock-in
- Immutable `data class` / `sealed interface` state models prevent accidental mutation
- Decompose Components own the `CoroutineScope` — MQTT message streams, reconnection logic, and throttling all live here
- Each broker tab has its own independent state and lifecycle
- **Upgrade path:** Can adopt MVIKotlin's formal Store (Intent → Executor → Message → Reducer) for critical state machines (connection lifecycle, packet diffing) later without rewriting the architecture

---

## Dependency Injection

### ✅ Decision: Koin 4.x

| Framework | Compile-time Safe | Boilerplate | KMP Support | Community (2026) | Verdict |
|---|---|---|---|---|---|
| **Koin 4.x** | Runtime (+ `checkModules()` test) | Lowest | ✅ Seamless | ~75% adoption | **✅ Selected** |
| kotlin-inject | ✅ Compile-time (KSP) | Moderate | ✅ Good | ~20% adoption | Overkill for this project |
| Kodein-DI | Runtime | Low | ✅ Good | Niche/legacy | ❌ Declining |
| Manual DI | ✅ Compile-time | High | ✅ Perfect | Common for small apps | ❌ Too much wiring |

**Why Koin:**
- **De-facto standard** in the KMP/CMP community (~75% adoption in 2026)
- Pure Kotlin DSL — no code generation, no annotation processing, no KSP build overhead
- Out-of-the-box integrations: `koin-compose`, `koin-compose-viewmodel`
- Safety net: add a single `checkModules()` unit test to catch missing bindings at test time
- Lightweight runtime footprint — ideal for desktop apps

---

## Persistence & Storage

### ✅ Decision: SQLDelight (data) + DataStore (settings)

#### Relational Data (Connection Profiles, Message History, Bookmarks)

| Solution | Approach | KMP Desktop | Compile-time Safety | Verdict |
|---|---|---|---|---|
| **SQLDelight** | SQL-first (`.sq` files) | ✅ Native (sqlite-driver) | ✅ Very high | **✅ Selected** |
| Room KMP 3.x | Annotation-first (@Entity, @Dao) | ✅ Native | ✅ High (KSP) | Good, but heavier build |
| Realm Kotlin | Object-store / NoSQL | ✅ | Moderate | ❌ Uncertain roadmap |

**Why SQLDelight:**
- Write pure SQL → compiler validates syntax and schema at build time → generates type-safe Kotlin interfaces
- Zero ORM reflection overhead — lightweight and fast
- No Android baggage — pure KMP from the ground up
- Built-in `Flow` reactivity for live-updating queries (e.g., connection profile list)
- Small footprint: bundled SQLite via `sqlite-driver`

#### App Settings (Theme, Window Size, Last Workspace, Preferences)

| Solution | Use Case | Verdict |
|---|---|---|
| **Jetpack DataStore Preferences** | Key-value app settings | **✅ Selected** |
| File I/O + kotlinx-serialization | Document blobs | For export/import files |

---

## Networking & HTTP

### ✅ Decision: Ktor Client 3.x

| Solution | KMP Support | WebSockets | Coroutine-native | Verdict |
|---|---|---|---|---|
| **Ktor Client 3.x** | ✅ All targets | ✅ First-class | ✅ Built on coroutines | **✅ Selected** |
| OkHttp | JVM/Android only | ✅ | Requires wrapping | ❌ Not multiplatform |

**Why Ktor:**
- **Undisputed standard** for KMP networking
- First-class WebSocket support (`ktor-client-websockets`) — needed for WSS broker connections
- Engine selection: `CIO` for desktop (pure Kotlin coroutine I/O)
- Content negotiation with kotlinx.serialization built-in
- SSE support if needed for future features

**Note:** Ktor is used for HTTP operations (cloud broker auth, REST APIs, update checks). The MQTT protocol itself is handled by HiveMQ MQTT Client directly.

---

## Serialization

### ✅ Decision: kotlinx.serialization

**Status:** Uncontested standard for KMP. Zero-reflection, compiler-plugin-driven.

**Configuration:**
```kotlin
val AppJson = Json {
    ignoreUnknownKeys = true   // Forward-compatible with new broker fields
    isLenient = true           // Handle quirky IoT device payloads
    coerceInputValues = true   // Graceful null → default coercion
    encodeDefaults = true      // Always include all fields in exports
    prettyPrint = false        // Compact for storage, pretty-print in UI
}
```

**Usage in MQTT Explorer:**
- Parse MQTT JSON payloads for structured viewing
- Serialize/deserialize connection profiles for storage and export
- Encode publish templates and workspace state
- Handle Home Assistant discovery config JSON

---

## Charting & Data Visualization

### ✅ Decision: Custom Canvas (real-time) + Vico (rich interactive charts)

| Approach | Use Case | FPS | Customizability | Verdict |
|---|---|---|---|---|
| **Custom Canvas (`drawWithCache`)** | Real-time streaming (msg/sec, live telemetry) | 60-120 FPS | Unlimited | **✅ Selected (real-time)** |
| **Vico** | Rich interactive charts (history, $SYS dashboard) | High | Very high | **✅ Selected (interactive)** |
| KoalaPlot | Declarative analytical charts | Moderate | High | Future consideration |

**Real-time charting strategy:**
1. **Ring buffer** — fixed-capacity circular buffer for data points (no GC pauses)
2. **Decouple ingestion from rendering** — `conflate()` or `sample(16.milliseconds)` on the data flow
3. **`Modifier.drawWithCache`** — cache `Path` objects, recalculate only on data/viewport change
4. **Direct Skia Canvas** — `drawScope.drawIntoCanvas` for hardware-accelerated line rendering

**Vico for interactive charts:**
- Zoom/pan gestures, tooltips, legends
- Animated transitions and entry effects
- Time-series history charts for $SYS dashboard and message history
- Material 3 theme integration

---

## Testing

### ✅ Decision: kotlin.test + Kotest + Turbine + Mokkery

| Category | Tool | Purpose |
|---|---|---|
| **Test Runner** | `kotlin.test` | Common multiplatform test annotations and runners |
| **Assertions** | Kotest (`kotest-assertions-core`) | Expressive matchers, table-driven tests, property-based testing |
| **Flow/Coroutine Testing** | Turbine (`app.cash.turbine`) + `kotlinx-coroutines-test` | `flow.test { ... }` for ViewModel and reactive stream testing |
| **Mocking** | Mokkery | KSP-based multiplatform mocking (replaces MockK for KMP) |
| **Compose UI Testing** | `org.jetbrains.compose.ui:ui-test` | `runComposeUiTest { ... }` for desktop UI verification |
| **DI Validation** | Koin `checkModules()` | Verify all dependency bindings at test time |

**Testing strategy:**
- Unit tests in `commonTest` — runs on all targets
- UI tests using `runComposeUiTest` — headless on CI via `xvfb`
- Flow tests with Turbine for all reactive state (MQTT streams, connection state)
- `checkModules()` test to catch Koin injection errors

---

## Desktop Packaging & Distribution

### ✅ Decision: Compose Gradle Plugin (jpackage)

| Tool | Cross-OS Build | Auto-Updates | Code Signing | Cost | Verdict |
|---|---|---|---|---|---|
| **Compose Gradle Plugin** | ❌ (per-OS build) | ❌ (manual) | Manual config | Free | **✅ Selected (MVP)** |
| Hydraulic Conveyor | ✅ Single runner | ✅ Built-in | ✅ Automated | Commercial | Future upgrade |

**Build tasks:**
```bash
# Windows
./gradlew packageMsi

# macOS
./gradlew packageDmg

# Linux
./gradlew packageDeb
```

**CI/CD Strategy:**
- GitHub Actions matrix: `windows-latest`, `macos-latest`, `ubuntu-latest`
- Each runner builds its own platform installer
- Artifacts uploaded to GitHub Releases

**Future upgrade path:** Migrate to Hydraulic Conveyor for cross-OS builds from a single runner and built-in auto-update support (Sprint 13 / post-GA).

---

## Version Matrix

| Technology | Version | Artifact |
|---|---|---|
| Kotlin | 2.1.x | `org.jetbrains.kotlin` |
| Compose Multiplatform | 1.7.x / 1.8.x | `org.jetbrains.compose` |
| HiveMQ MQTT Client | 1.3.3+ | `com.hivemq:hivemq-mqtt-client` |
| Decompose | 3.2.x / 3.3.x | `com.arkivanov.decompose:decompose` |
| Koin | 4.0.x+ | `io.insert-koin:koin-core` / `koin-compose` |
| SQLDelight | 2.x | `app.cash.sqldelight` |
| DataStore | 1.1.x | `androidx.datastore:datastore-preferences` |
| Ktor Client | 3.x | `io.ktor:ktor-client-cio` |
| kotlinx.serialization | 1.7.x | `org.jetbrains.kotlinx:kotlinx-serialization-json` |
| Vico | Latest | `com.patrykandpatrick.vico:compose-m3` |
| Kotest | 5.x | `io.kotest:kotest-assertions-core` |
| Turbine | 1.x | `app.cash.turbine:turbine` |
| Mokkery | Latest | `dev.mokkery:mokkery-gradle` |

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                        Compose Multiplatform UI                      │
│                                                                      │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────┐  ┌──────────────┐  │
│  │ Topic Tree   │  │ Payload      │  │ Chart   │  │ Publish      │  │
│  │ View         │  │ Viewer       │  │ Panel   │  │ Panel        │  │
│  └──────┬──────┘  └──────┬───────┘  └────┬────┘  └──────┬───────┘  │
│         │                │               │               │          │
│  ┌──────┴────────────────┴───────────────┴───────────────┴───────┐  │
│  │              Decompose Navigation (Component Tree)            │  │
│  │                                                               │  │
│  │  RootComponent                                                │  │
│  │  ├── ConnectionListComponent                                  │  │
│  │  ├── WorkspaceComponent (Child Pages = broker tabs)           │  │
│  │  │   ├── BrokerTabComponent[0]                                │  │
│  │  │   │   ├── TopicTreeComponent  (StateFlow<TreeState>)       │  │
│  │  │   │   ├── PayloadComponent    (StateFlow<PayloadState>)    │  │
│  │  │   │   ├── ChartComponent      (StateFlow<ChartState>)      │  │
│  │  │   │   └── PublishComponent    (StateFlow<PublishState>)    │  │
│  │  │   └── BrokerTabComponent[1]                                │  │
│  │  └── DialogSlot (Settings, Connection Editor)                 │  │
│  └───────────────────────────┬───────────────────────────────────┘  │
│                              │                                      │
└──────────────────────────────┼──────────────────────────────────────┘
                               │
┌──────────────────────────────┼──────────────────────────────────────┐
│                     Domain / Business Logic                         │
│                                                                      │
│  ┌────────────────┐  ┌──────────────┐  ┌─────────────────────────┐  │
│  │ MqttRepository  │  │ TopicStore   │  │ ConnectionProfileRepo   │  │
│  │ (interface)     │  │ (tree mgmt)  │  │ (CRUD profiles)         │  │
│  └────────┬───────┘  └──────────────┘  └─────────────────────────┘  │
│           │                                                          │
└───────────┼──────────────────────────────────────────────────────────┘
            │
┌───────────┼──────────────────────────────────────────────────────────┐
│           │              Data / Infrastructure                       │
│           │                                                          │
│  ┌────────┴───────┐  ┌──────────────┐  ┌─────────────────────────┐  │
│  │ HiveMQ MQTT    │  │ SQLDelight   │  │ DataStore Preferences   │  │
│  │ Client (JVM)   │  │ (profiles,   │  │ (theme, window state,   │  │
│  │                │  │  history,    │  │  settings)              │  │
│  │ MQTT 3.1.1/5.0 │  │  bookmarks) │  │                         │  │
│  │ TCP/TLS/WS/WSS │  │              │  │                         │  │
│  └────────────────┘  └──────────────┘  └─────────────────────────┘  │
│                                                                      │
│  ┌────────────────┐  ┌──────────────┐                               │
│  │ Ktor Client    │  │ kotlinx.     │  Koin 4.x (DI wiring)        │
│  │ (HTTP, update  │  │ serialization│                               │
│  │  checks)       │  │ (JSON codec) │                               │
│  └────────────────┘  └──────────────┘                               │
└──────────────────────────────────────────────────────────────────────┘
```

---

## Research Sources

- HiveMQ MQTT Client GitHub & documentation
- KMQTT, MQTTastic-Client-KMP, DitchOoM/mqtt repositories
- Decompose documentation by Arkadiy Ivanov
- Koin 4.x official documentation (Kotzilla)
- SQLDelight documentation (Cash App)
- Vico charting library documentation
- JetBrains Compose Multiplatform packaging guide
- Reddit r/Kotlin, r/KotlinMultiplatform community discussions (2025–2026)
- KotlinConf 2025 talks on KMP architecture
- Hydraulic Conveyor documentation

---

*Last updated: 2026-08-28*
*Document version: 1.0*

