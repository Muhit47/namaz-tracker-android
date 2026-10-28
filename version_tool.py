#!/usr/bin/env python3
"""
Namaz Tracker - Version Management & Auto-Increment Tool
Handles version synchronization across version.properties, AppVersionHelper.kt, and CI/CD.

Usage:
    python version_tool.py status
    python version_tool.py bump-build
    python version_tool.py bump-patch
    python version_tool.py bump-minor
    python version_tool.py bump-major
    python version_tool.py set 3.0.0 17
    python version_tool.py install-hook
"""

import sys
import os
import re

if sys.stdout.encoding != 'utf-8':
    try:
        sys.stdout.reconfigure(encoding='utf-8')
    except Exception:
        pass

ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
PROPS_FILE = os.path.join(ROOT_DIR, "version.properties")
HELPER_FILE = os.path.join(ROOT_DIR, "app", "src", "main", "java", "com", "example", "util", "AppVersionHelper.kt")

def read_properties():
    props = {
        "VERSION_MAJOR": "3",
        "VERSION_MINOR": "0",
        "VERSION_PATCH": "0",
        "VERSION_BUILD": "17"
    }
    if os.path.exists(PROPS_FILE):
        with open(PROPS_FILE, "r", encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    k, v = line.split("=", 1)
                    props[k.strip()] = v.strip()
    return props

def write_properties(props):
    content = f"""# Namaz Tracker Application Versioning
# Single source of truth for dynamic auto-versioning
VERSION_MAJOR={props['VERSION_MAJOR']}
VERSION_MINOR={props['VERSION_MINOR']}
VERSION_PATCH={props['VERSION_PATCH']}
VERSION_BUILD={props['VERSION_BUILD']}
"""
    with open(PROPS_FILE, "w", encoding="utf-8") as f:
        f.write(content)
    sync_helper(props)

def sync_helper(props):
    if not os.path.exists(HELPER_FILE):
        return
    with open(HELPER_FILE, "r", encoding="utf-8") as f:
        content = f.read()

    v_name = f"{props['VERSION_MAJOR']}.{props['VERSION_MINOR']}.{props['VERSION_PATCH']}"
    v_code = props['VERSION_BUILD']

    content = re.sub(
        r'(BuildConfig\.VERSION_NAME\s*\r?\n\s*}\s*catch\s*\([^\)]+\)\s*{\s*)"[^"]+"',
        r'\g<1>"' + v_name + '"',
        content
    )
    content = re.sub(
        r'(BuildConfig\.VERSION_CODE\s*\r?\n\s*}\s*catch\s*\([^\)]+\)\s*{\s*)\d+',
        r'\g<1>' + str(v_code),
        content
    )

    with open(HELPER_FILE, "w", encoding="utf-8") as f:
        f.write(content)

def show_status():
    p = read_properties()
    name = f"{p['VERSION_MAJOR']}.{p['VERSION_MINOR']}.{p['VERSION_PATCH']}"
    code = p['VERSION_BUILD']
    print(f"==================================================")
    print(f"  🕌 Namaz Tracker Version Information")
    print(f"==================================================")
    print(f"  Version Name : v{name}")
    print(f"  Build Code   : {code}")
    print(f"  Major.Minor  : {p['VERSION_MAJOR']}.{p['VERSION_MINOR']}")
    print(f"  Patch Level  : {p['VERSION_PATCH']}")
    print(f"==================================================")

def bump(mode="build"):
    p = read_properties()
    major = int(p["VERSION_MAJOR"])
    minor = int(p["VERSION_MINOR"])
    patch = int(p["VERSION_PATCH"])
    build = int(p["VERSION_BUILD"])

    if mode in ("build", "bump", "bump-build"):
        build += 1
    elif mode in ("patch", "bump-patch"):
        patch += 1
        build += 1
    elif mode in ("minor", "bump-minor"):
        minor += 1
        patch = 0
        build += 1
    elif mode in ("major", "bump-major"):
        major += 1
        minor = 0
        patch = 0
        build += 1
    else:
        print(f"Unknown bump mode: {mode}")
        return

    p["VERSION_MAJOR"] = str(major)
    p["VERSION_MINOR"] = str(minor)
    p["VERSION_PATCH"] = str(patch)
    p["VERSION_BUILD"] = str(build)

    write_properties(p)
    print(f"Updated successfully to v{major}.{minor}.{patch} (Build {build})")

def set_version(ver_str, build_str=None):
    parts = ver_str.lstrip("v").split(".")
    if len(parts) < 2:
        print("Invalid version format. Expected e.g. 3.0.0")
        return
    major = parts[0]
    minor = parts[1]
    patch = parts[2] if len(parts) > 2 else "0"

    p = read_properties()
    p["VERSION_MAJOR"] = major
    p["VERSION_MINOR"] = minor
    p["VERSION_PATCH"] = patch
    if build_str:
        p["VERSION_BUILD"] = str(build_str)

    write_properties(p)
    print(f"Version explicitly set to v{major}.{minor}.{patch} (Build {p['VERSION_BUILD']})")

def install_hook():
    git_dir = os.path.join(ROOT_DIR, ".git")
    if not os.path.exists(git_dir):
        print("Note: .git directory not found in workspace. When git is initialized, rerun this command.")
        return
    hooks_dir = os.path.join(git_dir, "hooks")
    os.makedirs(hooks_dir, exist_ok=True)
    hook_path = os.path.join(hooks_dir, "pre-commit")

    hook_script = """#!/bin/sh
# Auto-bump build number before commit
python version_tool.py bump-build
git add version.properties app/src/main/java/com/example/util/AppVersionHelper.kt
"""
    with open(hook_path, "w", encoding="utf-8", newline="\n") as f:
        f.write(hook_script)
    print(f"Git pre-commit hook installed at {hook_path}")

def main():
    if len(sys.argv) < 2 or sys.argv[1] == "status":
        show_status()
    elif sys.argv[1].startswith("bump"):
        bump(sys.argv[1])
    elif sys.argv[1] == "set":
        ver = sys.argv[2] if len(sys.argv) > 2 else "3.0.0"
        bld = sys.argv[3] if len(sys.argv) > 3 else None
        set_version(ver, bld)
    elif sys.argv[1] == "install-hook":
        install_hook()
    else:
        print("Invalid arguments. Use status, bump-build, bump-patch, bump-minor, bump-major, set, or install-hook.")

if __name__ == "__main__":
    main()
