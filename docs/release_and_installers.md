# MQtlin Release & Installer Guide

> Comprehensive guide for packaging, building installers locally, and publishing multi-platform releases for **MQtlin** across Windows, macOS, and Linux.

---

## 📦 Supported Installer Formats

| Platform | Format | Description |
| :--- | :--- | :--- |
| **🪟 Windows** | `.exe` (Setup Wizard) | Custom Modern Dark Setup Wizard built via Inno Setup. Includes desktop & start menu shortcuts, install path selection, and clean uninstaller. |
| **🪟 Windows** | `.msi` (Windows Installer) | Native MSI installer built via WiX Toolset. Standard for enterprise & silent deployments. |
| **🪟 Windows** | `.zip` (Portable) | Self-contained zip with embedded JRE — extract and run `MQtlin.exe` with zero installation. |
| **🍎 macOS** | `.dmg` (Disk Image) | Native macOS drag-and-drop installer with embedded JRE for Apple Silicon & Intel Macs. |
| **🍎 macOS** | `.zip` (Portable) | Standalone portable `MQtlin.app` zip bundle. |
| **🐧 Linux** | `.deb` | Native Debian/Ubuntu package for `dpkg` / `apt` software centers. |
| **🐧 Linux** | `.AppImage` | Universal single-file portable executable that runs across Ubuntu, Fedora, Arch, Debian, openSUSE, etc. |
| **🐧 Linux** | `.flatpak` | Sandboxed universal Linux bundle installable on any modern distro and Flathub. |
| **🐧 Linux** | `.tar.gz` (Portable) | Portable Linux archive containing standalone binaries. |
| **☕ Universal** | `.jar` | Standalone Fat/Uber JAR runnable anywhere with `java -jar MQtlin.jar`. |

---

## 🛠️ Local Build Commands (Windows)

### 1. Build Custom Dark Windows Setup Wizard (`.exe`)
```powershell
# Compiles distributable and builds installer in one step with dynamic versioning
.\gradlew.bat :desktopApp:packageInnoSetup

# Result: desktopApp\build\compose\binaries\main\setup\MQtlin-Setup-0.1.0-beta.1.exe
```

### 2. Build Native Windows MSI Installer
```powershell
.\gradlew.bat :desktopApp:packageMsi
# Result: desktopApp\build\compose\binaries\main\msi\MQtlin-0.1.0.msi
```

### 3. Build Standalone Universal JAR
```powershell
.\gradlew.bat :desktopApp:packageUberJarForCurrentOS
# Result: desktopApp\build\compose\jars\MQtlin-windows-x64-0.1.0.jar
```

---

## 🚀 How to Publish a Multi-Platform Release (GitHub Actions)

MQtlin has an automated GitHub Actions CI/CD release workflow (`.github/workflows/release.yml`) that builds native installers for **all three operating systems in parallel**:

### Steps to Release:

1. **Commit all changes & ensure tests pass:**
   ```bash
   git add .
   git commit -m "chore(release): prepare v1.0.0"
   git push origin main
   ```

2. **Create and push a version tag:**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

3. **What GitHub Actions will automatically do:**
   - 🪟 Boots `windows-latest` runner $\rightarrow$ builds `MQtlin-Setup-1.0.0.exe`, `MQtlin-1.0.0.msi`, `MQtlin-windows-portable.zip`.
   - 🍎 Boots `macos-latest` runner $\rightarrow$ builds `MQtlin-1.0.0.dmg`, `MQtlin-macOS-portable.zip`.
   - 🐧 Boots `ubuntu-latest` runner $\rightarrow$ builds `MQtlin-1.0.0.deb`, `MQtlin-1.0.0-x86_64.AppImage`, `MQtlin-linux-portable.tar.gz`.
   - 🔒 Computes SHA-256 `checksums.txt` for all assets.
   - 🎉 Publishes a new **GitHub Release** with all installers attached!

