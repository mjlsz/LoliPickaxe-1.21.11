"""One-shot local build: strip BOMs, javac against the named MC jar, package the mod jar.

Run:  python build_all.py
"""
import glob
import json
import os
import shutil
import subprocess
import zipfile

ROOT = r"F:\ds\lolipickaxe-port\LoliPickaxe"
PORT = r"F:\ds\lolipickaxe-port"
JDK = os.path.join(PORT, "tools", "jdk21", "jdk-21.0.13+9", "bin")
CLASSES = os.path.join(PORT, "build2")
STAGE = os.path.join(ROOT, "build", "stage")
OUT = os.path.join(ROOT, "build", "libs")
JAR = os.path.join(OUT, "lolipickaxe-2.0.0.jar")

# 1. strip UTF-8 BOMs (PowerShell Set-Content adds them)
for path in glob.glob(os.path.join(ROOT, "src", "main", "java", "**", "*.java"), recursive=True):
    data = open(path, "rb").read()
    if data.startswith(b"\xef\xbb\xbf"):
        open(path, "wb").write(data[3:])

# 2. classpath
libs = glob.glob(os.path.join(PORT, "mc", "libs", "**", "*.jar"), recursive=True)
cp = ";".join([
    os.path.join(PORT, "mc", "named"),
    os.path.join(PORT, "tools", "neoforge-universal.jar"),
    os.path.join(PORT, "tools", "bus.jar"),
    os.path.join(PORT, "stubs", "classes"),
    *libs,
])

# 2b. recompile the FML stubs used only for compilation
stub_src = glob.glob(os.path.join(PORT, "stubs", "src", "**", "*.java"), recursive=True)
stub_cp = ";".join([
    os.path.join(PORT, "mc", "named"),
    os.path.join(PORT, "tools", "neoforge-universal.jar"),
    os.path.join(PORT, "tools", "bus.jar"),
])
os.makedirs(os.path.join(PORT, "stubs", "classes"), exist_ok=True)
r = subprocess.run([os.path.join(JDK, "javac.exe"), "-cp", stub_cp, "-d", os.path.join(PORT, "stubs", "classes"), *stub_src],
                   capture_output=True, text=True, encoding="utf-8", errors="replace")
if r.returncode != 0:
    print(r.stdout[-2000:], r.stderr[-2000:])
    raise SystemExit("stub compile failed")

sources = glob.glob(os.path.join(ROOT, "src", "main", "java", "**", "*.java"), recursive=True)
shutil.rmtree(CLASSES, ignore_errors=True)
os.makedirs(CLASSES, exist_ok=True)
env = dict(os.environ, JAVA_TOOL_OPTIONS="-Duser.language=en -Duser.country=US")
result = subprocess.run([os.path.join(JDK, "javac.exe"), "-proc:none", "-nowarn",
                         "-cp", cp, "-d", CLASSES, *sources],
                        capture_output=True, text=True, encoding="utf-8", errors="replace", env=env)
if result.returncode != 0:
    print(result.stdout[-4000:])
    print(result.stderr[-4000:])
    raise SystemExit("compile failed")
print("compiled", len(sources), "sources ->", len(glob.glob(os.path.join(CLASSES, "**", "*.class"), recursive=True)), "classes")

# 3. stage resources
props = {}
for line in open(os.path.join(ROOT, "gradle.properties"), encoding="utf-8"):
    line = line.strip()
    if line and not line.startswith("#") and "=" in line:
        k, v = line.split("=", 1)
        props[k.strip()] = v.strip()

shutil.rmtree(STAGE, ignore_errors=True)
for base, _, files in os.walk(CLASSES):
    for name in files:
        if name.endswith(".class"):
            src = os.path.join(base, name)
            dst = os.path.join(STAGE, os.path.relpath(src, CLASSES))
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            shutil.copyfile(src, dst)

res = os.path.join(ROOT, "src", "main", "resources")
for base, _, files in os.walk(res):
    for name in files:
        src = os.path.join(base, name)
        dst = os.path.join(STAGE, os.path.relpath(src, res))
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copyfile(src, dst)

text = open(os.path.join(ROOT, "src", "main", "templates", "META-INF", "neoforge.mods.toml"), encoding="utf-8").read()
for key, value in props.items():
    text = text.replace("${" + key + "}", value)
os.makedirs(os.path.join(STAGE, "META-INF"), exist_ok=True)
open(os.path.join(STAGE, "META-INF", "neoforge.mods.toml"), "w", encoding="utf-8").write(text)
open(os.path.join(STAGE, "META-INF", "MANIFEST.MF"), "w", encoding="utf-8", newline="\n").write(
    "Manifest-Version: 1.0\nSpecification-Title: LoliPickaxe\nImplementation-Version: 2.0.0\n\n")

os.makedirs(OUT, exist_ok=True)
if os.path.exists(JAR):
    os.remove(JAR)
with zipfile.ZipFile(JAR, "w", zipfile.ZIP_DEFLATED) as z:
    for base, _, files in os.walk(STAGE):
        for name in files:
            full = os.path.join(base, name)
            z.write(full, os.path.relpath(full, STAGE).replace(os.sep, "/"))

print("built", JAR, os.path.getsize(JAR))
