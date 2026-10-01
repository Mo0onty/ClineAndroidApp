#!/usr/bin/env python3
# tools/prepare-assets.py - Prepare assets for APK

import os
import sys
import shutil
import hashlib
import json
from pathlib import Path

# Configuration
APP_DIR = Path(__file__).parent.parent / "app"
ASSETS_DIR = APP_DIR / "src" / "main" / "assets"
GENERATED_DIR = APP_DIR / "build" / "generated" / "standardAssets"
RECOVERY_DIR = APP_DIR / "build" / "generated" / "recoveryAssets"
TOOLS_DIR = Path(__file__).parent
REPORT_FILE = APP_DIR / "build" / "generated" / "standard-assets-report.json"

def main():
    """Main function."""
    print("Preparing assets for Cline Android...")
    print("=" * 50)

    # Create directories
    GENERATED_DIR.mkdir(parents=True, exist_ok=True)
    RECOVERY_DIR.mkdir(parents=True, exist_ok=True)

    # Check required files
    required_files = [
        ASSETS_DIR / "offline-rootfs.bin",
        ASSETS_DIR / "cline-runtime.bin",
    ]

    for file_path in required_files:
        if not file_path.exists():
            print(f"Error: Required file not found: {file_path}")
            sys.exit(1)

    # Copy rootfs
    print("Copying rootfs...")
    shutil.copy2(
        ASSETS_DIR / "offline-rootfs.bin",
        GENERATED_DIR / "offline-rootfs.bin"
    )

    # Copy runtime
    print("Copying runtime...")
    shutil.copy2(
        ASSETS_DIR / "cline-runtime.bin",
        GENERATED_DIR / "cline-runtime.bin"
    )

    # Prepare web compatibility files
    print("Preparing web compatibility files...")
    prepare_web_compat()

    # Prepare language files
    print("Preparing language files...")
    prepare_languages()

    # Prepare plugin files
    print("Preparing plugin files...")
    prepare_plugins()

    # Generate report
    print("Generating report...")
    generate_report()

    print("Assets prepared successfully!")

def prepare_web_compat():
    """Prepare web compatibility files."""
    web_compat_dir = GENERATED_DIR / "web-compat"
    web_compat_dir.mkdir(exist_ok=True)

    # Copy web-compat files from tools
    web_compat_source = TOOLS_DIR / "web-compat"
    if web_compat_source.exists():
        for item in web_compat_source.iterdir():
            if item.is_file():
                shutil.copy2(item, web_compat_dir / item.name)

def prepare_languages():
    """Prepare language files."""
    # Create language patch
    language_patch = {
        "version": 1,
        "languages": ["en", "zh", "ja", "ko"],
        "fallback": "en"
    }

    (GENERATED_DIR / "language-patch.json").write_text(json.dumps(language_patch, indent=2))

def prepare_plugins():
    """Prepare plugin files."""
    # Create plugin manifest
    plugin_manifest = {
        "version": 1,
        "plugins": []
    }

    (GENERATED_DIR / "plugin-manifest.json").write_text(json.dumps(plugin_manifest, indent=2))

def generate_report():
    """Generate asset report."""
    report = {
        "version": 1,
        "assets": [],
        "checksums": {}
    }

    # List all generated assets
    for asset_path in GENERATED_DIR.rglob("*"):
        if asset_path.is_file():
            relative_path = asset_path.relative_to(GENERATED_DIR)
            report["assets"].append(str(relative_path))

            # Calculate checksum
            sha256 = hashlib.sha256()
            with asset_path.open("rb") as f:
                while chunk := f.read(8192):
                    sha256.update(chunk)
            report["checksums"][str(relative_path)] = sha256.hexdigest()

    REPORT_FILE.parent.mkdir(parents=True, exist_ok=True)
    REPORT_FILE.write_text(json.dumps(report, indent=2))

    print(f"Report saved to: {REPORT_FILE}")

if __name__ == "__main__":
    main()
