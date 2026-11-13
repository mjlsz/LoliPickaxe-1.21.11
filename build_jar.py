"""Build the mod jar without Gradle.

The NeoForm toolchain is unreachable from this machine, so the project is compiled
directly against a deobfuscated Minecraft 1.21.11 jar + the published NeoForge
universal jar (both use Mojang official names, which is what NeoForge runs with).

Usage:  python build_jar.py
"""
import os
import shutil
import zipfile

ROOT = r"F:\ds\lolipickaxe-port\LoliPickaxe"
CLASSES = r"F:\ds\lolipickaxe-port\build2"
OUT = os.path.join(ROOT, "build", "libs")
STAGE = os.path.join(ROOT, "build", "stage")
JAR = os.path.join(OUT, "lolipickaxe-2.0.0.jar")

props = {}
with open(os.path.join(ROOT, "gradle.properties"), encoding="utf-8") as f:
    for line in f:
        line = line.strip()
        if line and not line.startswith("#") and "=" in line:
            k, v = line.split("=", 1)
            props[k.strip()] = v.strip()

shutil.rmtree(STAGE, ignore_errors=True)
os.makedirs(os.path.join(STAGE, "META-INF"), exist_ok=True)

# classes
for base, _, files in os.walk(CLASSES):
    for name in files:
        if not name.endswith(".class"):
            continue
        src = os.path.join(base, name)
        rel = os.path.relpath(src, CLASSES)
        dst = os.path.join(STAGE, rel)
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copyfile(src, dst)

# resources
res = os.path.join(ROOT, "src", "main", "resources")
for base, _, files in os.walk(res):
    for name in files:
        src = os.path.join(base, name)
        rel = os.path.relpath(src, res)
        dst = os.path.join(STAGE, rel)
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copyfile(src, dst)

# mods.toml with placeholders expanded
template = os.path.join(ROOT, "src", "main", "templates", "META-INF", "neoforge.mods.toml")
with open(template, encoding="utf-8") as f:
    text = f.read()
for key, value in props.items():
    text = text.replace("${" + key + "}", value)
with open(os.path.join(STAGE, "META-INF", "neoforge.mods.toml"), "w", encoding="utf-8") as f:
    f.write(text)

with open(os.path.join(STAGE, "META-INF", "MANIFEST.MF"), "w", encoding="utf-8", newline="\n") as f:
    f.write("Manifest-Version: 1.0\nSpecification-Title: LoliPickaxe\nImplementation-Version: 2.0.0\n\n")

os.makedirs(OUT, exist_ok=True)
if os.path.exists(JAR):
    os.remove(JAR)
with zipfile.ZipFile(JAR, "w", zipfile.ZIP_DEFLATED) as z:
    for base, _, files in os.walk(STAGE):
        for name in files:
            full = os.path.join(base, name)
            rel = os.path.relpath(full, STAGE).replace(os.sep, "/")
            z.write(full, rel)

print("built", JAR, os.path.getsize(JAR), "bytes")
