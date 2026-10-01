#!/bin/bash
# build.sh - Main build script for Cline Android

set -e

# Configuration
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$APP_DIR/app/build"
TOOLS_DIR="$APP_DIR/tools"
ASSETS_DIR="$APP_DIR/app/src/main/assets"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Functions
function echo_error {
    echo -e "${RED}[ERROR] $1${NC}"
}

function echo_success {
    echo -e "${GREEN}[SUCCESS] $1${NC}"
}

function echo_info {
    echo -e "${YELLOW}[INFO] $1${NC}"
}

function check_dependencies {
    echo_info "Checking dependencies..."

    # Check JDK
    if [ -z "$JAVA_HOME" ]; then
        echo_error "JAVA_HOME is not set"
        exit 1
    fi

    if [ ! -d "$JAVA_HOME" ]; then
        echo_error "Java not found at $JAVA_HOME"
        exit 1
    fi

    # Check Android SDK
    if [ -z "$ANDROID_SDK_ROOT" ]; then
        ANDROID_SDK_ROOT="$ANDROID_HOME"
    fi

    if [ -z "$ANDROID_SDK_ROOT" ]; then
        echo_error "ANDROID_SDK_ROOT or ANDROID_HOME is not set"
        exit 1
    fi

    if [ ! -d "$ANDROID_SDK_ROOT" ]; then
        echo_error "Android SDK not found at $ANDROID_SDK_ROOT"
        exit 1
    fi

    # Check Python
    if [ -z "$CLINE_PYTHON" ]; then
        CLINE_PYTHON="python3"
    fi

    if ! command -v "$CLINE_PYTHON" &> /dev/null; then
        echo_error "Python not found"
        exit 1
    fi

    echo_success "All dependencies are available"
}

function build_rootfs {
    echo_info "Building Ubuntu rootfs..."

    cd "$TOOLS_DIR"

    if [ ! -f "$TOOLS_DIR/build-rootfs.py" ]; then
        echo_error "build-rootfs.py not found"
        exit 1
    fi

    $CLINE_PYTHON "$TOOLS_DIR/build-rootfs.py"

    echo_success "Ubuntu rootfs built"
}

function build_cline_runtime {
    echo_info "Building Cline runtime..."

    cd "$TOOLS_DIR"

    if [ ! -f "$TOOLS_DIR/build-cline-runtime.py" ]; then
        echo_error "build-cline-runtime.py not found"
        exit 1
    fi

    $CLINE_PYTHON "$TOOLS_DIR/build-cline-runtime.py"

    echo_success "Cline runtime built"
}

function prepare_assets {
    echo_info "Preparing assets..."

    cd "$TOOLS_DIR"

    if [ ! -f "$TOOLS_DIR/prepare-assets.py" ]; then
        echo_error "prepare-assets.py not found"
        exit 1
    fi

    $CLINE_PYTHON "$TOOLS_DIR/prepare-assets.py"

    echo_success "Assets prepared"
}

function build_apk {
    local flavor=$1
    local build_type=$2

    echo_info "Building APK (flavor: $flavor, type: $build_type)..."

    cd "$APP_DIR"

    if [ "$flavor" = "standard" ]; then
        if [ "$build_type" = "debug" ]; then
            ./gradlew :app:assembleStandardDebug
        else
            ./gradlew :app:assembleStandardRelease
        fi
    elif [ "$flavor" = "low" ]; then
        if [ "$build_type" = "debug" ]; then
            ./gradlew :app:assembleLowDebug
        else
            ./gradlew :app:assembleLowRelease
        fi
    else
        echo_error "Unknown flavor: $flavor"
        exit 1
    fi

    echo_success "APK built: $flavor-$build_type"
}

function verify_build {
    echo_info "Verifying build..."

    # Check if APK was created
    local apk_path="$BUILD_DIR/outputs/apk"

    if [ ! -d "$apk_path" ]; then
        echo_error "APK output directory not found"
        exit 1
    fi

    # Check for APK files
    if [ "$(find "$apk_path" -name '*.apk' | wc -l)" -eq 0 ]; then
        echo_error "No APK files found in output directory"
        exit 1
    fi

    echo_success "Build verified"
}

# Main execution
function main {
    local target=${1:-"all"}
    local flavor=${2:-"standard"}
    local build_type=${3:-"debug"}

    check_dependencies

    case "$target" in
        "rootfs")
            build_rootfs
            ;;
        "runtime")
            build_cline_runtime
            ;;
        "assets")
            prepare_assets
            ;;
        "apk")
            prepare_assets
            build_apk "$flavor" "$build_type"
            verify_build
            ;;
        "all")
            build_rootfs
            build_cline_runtime
            prepare_assets
            build_apk "$flavor" "$build_type"
            verify_build
            ;;
        *)
            echo_error "Unknown target: $target"
            echo "Usage: $0 [rootfs|runtime|assets|apk|all] [flavor] [build_type]"
            echo "  flavor: standard (default) or low"
            echo "  build_type: debug (default) or release"
            exit 1
            ;;
    esac
}

# Run main
main "$@"
