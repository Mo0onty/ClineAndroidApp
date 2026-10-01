# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v1.0.0] - 2024-01-00

### Added
- Complete Material Design 3 Expressive architecture implementation
- Full Kotlin + Jetpack Compose UI layer
- Design system with DesignTokens, Theme, Components, Animations
- Core logic: ClineController, ConfigStore, RuntimeHostPorts, ProotBootstrap, ContainerRuntime, WebProcessManager, TerminalProcess
- Data layer: KeyVault (API key encryption), BackupManager, PluginRepository, BackupRepository
- Bridge layer: LocalNetworkAccess, AppBridge, AdbBridge
- Recovery layer: RecoveryController, RecoveryRuntime, RecoveryService
- UI components: All screens (Welcome, Main, Chat, Terminal, Plugins, Settings, Backup), activities, fragments, adapters
- Android resources: colors.xml, themes.xml, styles.xml, strings.xml, dimens.xml, drawables, layouts
- Asset files: offline-rootfs.bin, cline-runtime.bin, recovery-agent.js
- Build system: settings.gradle, build.gradle (root and app), gradle.properties, build.sh
- Python build scripts: build-rootfs.py, build-cline-runtime.py, prepare-assets.py, prepare-recovery-assets.py
- CI/CD: GitHub Actions workflows (build.yml, test.yml)
- Documentation: README.md, docs/ARCHITECTURE.md, docs/BUILD.md, docs/DEVELOPMENT.md, CONTRIBUTING.md, CODE_OF_CONDUCT.md
- Testing infrastructure: Unit tests and instrumentation tests

### Changed
- Updated color palette from Material Design 3 purple to Cline AI official green (#00D4AA, #00B8A3, #00E5B8)
- Fixed import paths in core files
- Moved TerminalProcess.kt from runtime/ to core/ package

### Fixed
- Import path issues in ClineController.kt, ClineApp.kt, BackupManager.kt
- GitHub Actions workflow to use relative keystore path with base64 decoding

## [v1.1.0] - 2024-01-00

### Added
- New features to be documented upon release

## [v1.2.0] - 2024-01-00

### Added
- Additional features to be documented upon release
