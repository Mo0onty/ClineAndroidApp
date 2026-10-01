#!/usr/bin/env python3
# tools/prepare-recovery-assets.py - Prepare recovery assets for APK

import os
import sys
import shutil
import hashlib
import json
from pathlib import Path

# Configuration
APP_DIR = Path(__file__).parent.parent / "app"
TOOLS_DIR = Path(__file__).parent
GENERATED_DIR = APP_DIR / "build" / "generated" / "standardAssets"
RECOVERY_DIR = APP_DIR / "build" / "generated" / "recoveryAssets"
RECOVERY_RUNTIME_DIR = TOOLS_DIR / "recovery-runtime"
REPORT_FILE = APP_DIR / "build" / "generated" / "recovery-assets-report.json"

def main():
    """Main function."""
    print("Preparing recovery assets for Cline Android...")
    print("=" * 50)

    # Create directories
    RECOVERY_DIR.mkdir(parents=True, exist_ok=True)

    # Check required files
    required_files = [
        GENERATED_DIR / "offline-rootfs.bin",
        GENERATED_DIR / "cline-runtime.bin",
    ]

    for file_path in required_files:
        if not file_path.exists():
            print(f"Error: Required file not found: {file_path}")
            sys.exit(1)

    # Copy standard assets to recovery
    print("Copying standard assets to recovery...")
    for asset_path in GENERATED_DIR.iterdir():
        if asset_path.is_file():
            shutil.copy2(asset_path, RECOVERY_DIR / asset_path.name)

    # Prepare recovery runtime
    print("Preparing recovery runtime...")
    prepare_recovery_runtime()

    # Prepare recovery configuration
    print("Preparing recovery configuration...")
    prepare_recovery_config()

    # Generate report
    print("Generating report...")
    generate_report()

    print("Recovery assets prepared successfully!")

def prepare_recovery_runtime():
    """Prepare recovery runtime files."""
    recovery_runtime_dir = RECOVERY_DIR / "recovery-runtime"
    recovery_runtime_dir.mkdir(exist_ok=True)

    # Copy recovery runtime files from tools
    if RECOVERY_RUNTIME_DIR.exists():
        for item in RECOVERY_RUNTIME_DIR.iterdir():
            if item.is_file():
                shutil.copy2(item, recovery_runtime_dir / item.name)

def prepare_recovery_config():
    """Prepare recovery configuration."""
    recovery_config = {
        "version": 1,
        "recovery_mode": "safe",
        "runtime": {
            "type": "proot",
            "arch": "arm64-v8a",
            "minimal": True
        },
        "features": {
            "network": False,
            "plugins": False,
            "backup": True,
            "restore": True
        }
    }

    (RECOVERY_DIR / "recovery-config.json").write_text(json.dumps(recovery_config, indent=2))

def generate_report():
    """Generate asset report."""
    report = {
        "version": 1,
        "assets": [],
        "checksums": {}
    }

    # List all recovery assets
    for asset_path in RECOVERY_DIR.rglob("*"):
        if asset_path.is_file():
            relative_path = asset_path.relative_to(RECOVERY_DIR)
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
