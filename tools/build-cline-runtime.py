#!/usr/bin/env python3
# tools/build-cline-runtime.py - Build Cline runtime package

import os
import sys
import subprocess
import tarfile
import hashlib
import shutil
import json
import tempfile
from pathlib import Path

# Configuration
RUNTIME_DIR = Path(__file__).parent.parent / "tools" / "cline-runtime"
OUTPUT_DIR = Path(__file__).parent.parent / "app" / "src" / "main" / "assets"
RUNTIME_FILE = OUTPUT_DIR / "cline-runtime.bin"
CHECKSUM_FILE = OUTPUT_DIR / "cline-runtime.bin.sha256"

# Cline version
CLINE_VERSION = "0.1.0"

# Node.js version
NODE_VERSION = "20.11.1"

def run_command(cmd, cwd=None, check=True, env=None):
    """Run a shell command."""
    print(f"Running: {cmd}")
    result = subprocess.run(
        cmd,
        shell=True,
        cwd=cwd,
        capture_output=True,
        text=True,
        env=env
    )
    if check and result.returncode != 0:
        print(f"Error: {result.stderr}")
        sys.exit(1)
    return result

def build_runtime():
    """Build Cline runtime package."""
    print("Building Cline runtime...")

    with tempfile.TemporaryDirectory() as temp_dir:
        temp_path = Path(temp_dir)
        runtime_build_dir = temp_path / "cline-runtime"

        # Create runtime directory structure
        runtime_build_dir.mkdir()
        bin_dir = runtime_build_dir / "bin"
        lib_dir = runtime_build_dir / "lib"
        include_dir = runtime_build_dir / "include"
        node_dir = runtime_build_dir / "node"
        cline_dir = runtime_build_dir / "cline"

        bin_dir.mkdir()
        lib_dir.mkdir()
        include_dir.mkdir()
        node_dir.mkdir()
        cline_dir.mkdir()

        # Install Node.js
        print("Installing Node.js...")
        install_nodejs(node_dir)

        # Install Cline
        print("Installing Cline...")
        install_cline(cline_dir, node_dir)

        # Create startup script
        print("Creating startup script...")
        create_startup_script(bin_dir, node_dir, cline_dir)

        # Create package manifest
        print("Creating package manifest...")
        create_manifest(runtime_build_dir)

        # Create tar.gz
        print("Creating runtime archive...")
        create_runtime_archive(runtime_build_dir)

    print("Cline runtime built successfully!")

def install_nodejs(node_dir: Path):
    """Install Node.js."""
    # Download Node.js binary
    node_url = f"https://nodejs.org/dist/v{NODE_VERSION}/node-v{NODE_VERSION}-linux-arm64.tar.xz"
    node_archive = Path(tempfile.gettempdir()) / "node.tar.xz"

    print(f"Downloading Node.js from {node_url}")
    run_command(f"curl -L -o {node_archive} {node_url}")

    # Extract
    print("Extracting Node.js...")
    run_command(f"tar -xf {node_archive} -C {node_dir.parent}")

    # Move to node directory
    extracted_dir = node_dir.parent / f"node-v{NODE_VERSION}-linux-arm64"
    for item in extracted_dir.iterdir():
        if item.is_dir():
            shutil.copytree(item, node_dir / item.name, dirs_exist_ok=True)
        else:
            shutil.copy2(item, node_dir / item.name)

    # Clean up
    shutil.rmtree(extracted_dir)
    node_archive.unlink()

    # Verify
    node_binary = node_dir / "bin" / "node"
    if not node_binary.exists():
        raise RuntimeError("Node.js binary not found")

    # Test
    result = run_command(f"{node_binary} --version")
    print(f"Node.js version: {result.stdout.strip()}")

def install_cline(cline_dir: Path, node_dir: Path):
    """Install Cline agent."""
    # Set PATH to include Node.js
    env = os.environ.copy()
    node_bin = node_dir / "bin"
    env["PATH"] = f"{node_bin}:{env.get('PATH', '')}"

    # Install Cline using npm
    print("Installing Cline via npm...")
    run_command(
        f"{node_bin / 'npm'} install -g @cline/agent@{CLINE_VERSION}",
        env=env
    )

    # Copy Cline to runtime directory
    # Find where npm installed it
    npm_global = Path.home() / ".npm-global"
    if not npm_global.exists():
        # Try alternative locations
        npm_global = Path("/usr/local")
        if not (npm_global / "lib" / "node_modules" / "@cline" / "agent").exists():
            raise RuntimeError("Cline installation not found")

    cline_source = npm_global / "lib" / "node_modules" / "@cline" / "agent"
    if cline_source.exists():
        shutil.copytree(cline_source, cline_dir, dirs_exist_ok=True)
    else:
        # Try another location
        cline_source = Path("/usr/local") / "lib" / "node_modules" / "@cline" / "agent"
        if cline_source.exists():
            shutil.copytree(cline_source, cline_dir, dirs_exist_ok=True)
        else:
            raise RuntimeError("Cline installation not found")

    # Install additional dependencies
    print("Installing additional dependencies...")
    dependencies = [
        "express", "cors", "body-parser", "socket.io",
        "axios", "chalk", "ora", "commander"
    ]

    for dep in dependencies:
        run_command(
            f"{node_bin / 'npm'} install {dep}",
            cwd=cline_dir,
            env=env
        )

def create_startup_script(bin_dir: Path, node_dir: Path, cline_dir: Path):
    """Create startup script."""
    startup_script = bin_dir / "cline"
    startup_script.write_text(f"""#!/bin/bash

# Cline startup script
export NODE_PATH={cline_dir / 'node_modules'}:{node_dir / 'lib' / 'node_modules'}
export PATH={node_dir / 'bin'}:$PATH
export HOME=$(pwd)
export CLINE_HOME=$HOME/.cline

# Create directories if they don't exist
mkdir -p $CLINE_HOME
mkdir -p $CLINE_HOME/plugins
mkdir -p $CLINE_HOME/cache

# Start Cline
{node_dir / 'bin' / 'node'} {cline_dir / 'bin' / 'cline.js'} "$@"
""")

    # Make executable
    os.chmod(startup_script, 0o755)

def create_manifest(runtime_build_dir: Path):
    """Create runtime manifest."""
    manifest = {
        "version": "1.0",
        "name": "cline-runtime",
        "description": "Cline AI coding agent runtime",
        "clineVersion": CLINE_VERSION,
        "nodeVersion": NODE_VERSION,
        "arch": "arm64",
        "os": "linux",
        "createdAt": str(Path(__file__).stat().st_mtime),
        "files": []
    }

    # List all files
    for root, dirs, files in os.walk(runtime_build_dir):
        for file in files:
            file_path = Path(root) / file
            relative_path = file_path.relative_to(runtime_build_dir)
            manifest["files"].append(str(relative_path))

    manifest_file = runtime_build_dir / "manifest.json"
    manifest_file.write_text(json.dumps(manifest, indent=2))

def create_runtime_archive(runtime_build_dir: Path):
    """Create compressed runtime archive."""
    # Create tar.gz
    tar_path = OUTPUT_DIR / "cline-runtime.tar.gz"

    with tarfile.open(tar_path, "w:gz") as tar:
        tar.add(runtime_build_dir, arcname=".", recursive=True)

    # Convert to .bin (just rename for now)
    shutil.move(tar_path, RUNTIME_FILE)

    # Create checksum
    create_checksum()

def create_checksum():
    """Create SHA-256 checksum file."""
    sha256 = hashlib.sha256()
    with RUNTIME_FILE.open("rb") as f:
        while chunk := f.read(8192):
            sha256.update(chunk)

    checksum = sha256.hexdigest()
    CHECKSUM_FILE.write_text(checksum)

    print(f"Checksum: {checksum}")
    print(f"Saved to: {CHECKSUM_FILE}")

def main():
    """Main function."""
    print("Cline Runtime Builder")
    print("=" * 50)

    # Create output directory
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    build_runtime()

if __name__ == "__main__":
    main()
