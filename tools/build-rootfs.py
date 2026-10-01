#!/usr/bin/env python3
# tools/build-rootfs.py - Build Ubuntu 24.04 rootfs for Cline

import os
import sys
import subprocess
import tarfile
import hashlib
import shutil
import tempfile
import json
from pathlib import Path

# Configuration
UBUNTU_VERSION = "24.04"
ARCH = "arm64"
OUTPUT_DIR = Path(__file__).parent.parent / "app" / "src" / "main" / "assets"
ROOTFS_FILE = OUTPUT_DIR / "offline-rootfs.bin"
CHECKSUM_FILE = OUTPUT_DIR / "offline-rootfs.bin.sha256"

# Packages to include
BASE_PACKAGES = [
    "bash", "coreutils", "findutils", "grep", "sed", "awk", "tar", "gzip",
    "curl", "wget", "git", "vim-tiny", "nano", "less", "man-db",
    "ca-certificates", "openssl", "openssh-client", "iproute2", "net-tools",
    "procps", "psmisc", "lsof", "strace", "tmux", "screen",
    "python3", "python3-pip", "python3-venv", "python3-dev",
    "build-essential", "gcc", "g++", "make", "cmake", "autoconf", "automake",
    "libtool", "pkg-config", "libssl-dev", "zlib1g-dev", "libbz2-dev",
    "libreadline-dev", "libsqlite3-dev", "libffi-dev", "liblzma-dev",
    "nodejs", "npm"
]

CLINE_PACKAGES = [
    "unzip", "jq", "ripgrep", "fd-find", "bat"
]

# Create output directory
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

def run_command(cmd, cwd=None, check=True):
    """Run a shell command."""
    print(f"Running: {cmd}")
    result = subprocess.run(
        cmd,
        shell=True,
        cwd=cwd,
        capture_output=True,
        text=True
    )
    if check and result.returncode != 0:
        print(f"Error: {result.stderr}")
        sys.exit(1)
    return result

def build_rootfs():
    """Build Ubuntu rootfs using debootstrap."""
    print("Building Ubuntu rootfs...")

    with tempfile.TemporaryDirectory() as temp_dir:
        temp_path = Path(temp_dir)
        rootfs_dir = temp_path / "ubuntu-rootfs"

        # Create rootfs directory
        rootfs_dir.mkdir()

        # Use debootstrap to create minimal Ubuntu
        print("Creating minimal Ubuntu system...")
        run_command(
            f"sudo debootstrap --arch={ARCH} --variant=minbase "
            f"{UBUNTU_VERSION} {rootfs_dir} http://ports.ubuntu.com/ubuntu-ports"
        )

        # Configure apt
        print("Configuring apt...")
        apt_sources = rootfs_dir / "etc" / "apt" / "sources.list"
        apt_sources.write_text(
            "deb http://ports.ubuntu.com/ubuntu-ports noble main restricted universe multiverse\n"
            "deb http://ports.ubuntu.com/ubuntu-ports noble-updates main restricted universe multiverse\n"
            "deb http://ports.ubuntu.com/ubuntu-ports noble-security main restricted universe multiverse\n"
        )

        # Mount proc, sys, dev
        print("Mounting special filesystems...")
        run_command(f"sudo mount -t proc /proc {rootfs_dir / 'proc'}")
        run_command(f"sudo mount -t sysfs /sys {rootfs_dir / 'sys'}")
        run_command(f"sudo mount -o bind /dev {rootfs_dir / 'dev'}")
        run_command(f"sudo mount -t devpts /dev/pts {rootfs_dir / 'dev' / 'pts'}")

        try:
            # Chroot and install packages
            print("Installing base packages...")
            install_cmd = (
                f"sudo chroot {rootfs_dir} /bin/bash -c \""
                f"export DEBIAN_FRONTEND=noninteractive; "
                f"apt-get update; "
                f"apt-get install -y --no-install-recommends {' '.join(BASE_PACKAGES)}; "
                f"apt-get clean; "
                f"rm -rf /var/lib/apt/lists/*; "
                f"rm -rf /tmp/*; "
                f"rm -rf /var/tmp/*; "
                f"exit\""
            )
            run_command(install_cmd)

            # Install Cline-specific packages
            print("Installing Cline packages...")
            install_cline_cmd = (
                f"sudo chroot {rootfs_dir} /bin/bash -c \""
                f"export DEBIAN_FRONTEND=noninteractive; "
                f"apt-get install -y --no-install-recommends {' '.join(CLINE_PACKAGES)}; "
                f"apt-get clean; "
                f"rm -rf /var/lib/apt/lists/*; "
                f"exit\""
            )
            run_command(install_cline_cmd)

            # Configure environment
            print("Configuring environment...")
            configure_environment(rootfs_dir)

            # Create tar.gz
            print("Creating rootfs archive...")
            create_rootfs_archive(rootfs_dir)

        finally:
            # Unmount
            print("Unmounting...")
            run_command(f"sudo umount -R {rootfs_dir}")

    print("Rootfs built successfully!")

def configure_environment(rootfs_dir: Path):
    """Configure the Ubuntu environment."""
    etc_dir = rootfs_dir / "etc"

    # Create /root directory
    root_dir = rootfs_dir / "root"
    root_dir.mkdir(exist_ok=True)

    # Configure bash
    bashrc = root_dir / ".bashrc"
    bashrc.write_text("""\
        export PS1='\\u@\\h:\\w\\$ '
        export PATH=$PATH:/usr/local/bin
        export HOME=/root
        export USER=root
        cd $HOME

        # Aliases
        alias ls='ls --color=auto'
        alias ll='ls -la'
        alias la='ls -A'
        alias l='ls -CF'

        # Node.js
        export NODE_PATH=/usr/local/lib/node_modules

        # Python
        export PYTHONUNBUFFERED=1
    """.strip())

    # Configure profile
    profile = etc_dir / "profile"
    profile.write_text("""\
        export PATH=$PATH:/usr/local/bin
    """.strip())

    # Configure hosts
    hosts = etc_dir / "hosts"
    hosts.write_text("127.0.0.1 localhost\n")

    # Configure resolv.conf
    resolv_conf = etc_dir / "resolv.conf"
    resolv_conf.write_text("nameserver 8.8.8.8\nnameserver 8.8.4.4\n")

    # Create necessary directories
    (rootfs_dir / "tmp").mkdir(exist_ok=True)
    (rootfs_dir / "var" / "tmp").mkdir(exist_ok=True)
    (rootfs_dir / "var" / "run").mkdir(exist_ok=True)
    (rootfs_dir / "var" / "log").mkdir(exist_ok=True)

    # Set permissions
    run_command(f"sudo chmod 1777 {rootfs_dir / 'tmp'}")
    run_command(f"sudo chmod 755 {rootfs_dir / 'var' / 'tmp'}")
    run_command(f"sudo chmod 755 {rootfs_dir / 'var' / 'run'}")
    run_command(f"sudo chmod 755 {rootfs_dir / 'var' / 'log'}")

def create_rootfs_archive(rootfs_dir: Path):
    """Create compressed rootfs archive."""
    # Create tar.gz
    tar_path = OUTPUT_DIR / "offline-rootfs.tar.gz"

    with tarfile.open(tar_path, "w:gz") as tar:
        tar.add(rootfs_dir, arcname=".", recursive=True)

    # Convert to .bin (just rename for now)
    shutil.move(tar_path, ROOTFS_FILE)

    # Create checksum
    create_checksum()

def create_checksum():
    """Create SHA-256 checksum file."""
    sha256 = hashlib.sha256()
    with ROOTFS_FILE.open("rb") as f:
        while chunk := f.read(8192):
            sha256.update(chunk)

    checksum = sha256.hexdigest()
    CHECKSUM_FILE.write_text(checksum)

    print(f"Checksum: {checksum}")
    print(f"Saved to: {CHECKSUM_FILE}")

def main():
    """Main function."""
    print("Ubuntu RootFS Builder for Cline Android")
    print("=" * 50)

    # Check if running as root
    if os.geteuid() != 0:
        print("Warning: This script requires root privileges for debootstrap")
        print("Some operations may fail without sudo")

    build_rootfs()

if __name__ == "__main__":
    main()
