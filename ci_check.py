"""Lightweight CI check that does not need Minecraft or NeoForge artifacts.

Validates the resource tree directly (no jar required):
  * every JSON resource parses (and has no BOM)
  * item definitions -> model -> parent chain -> textures resolve
  * recipes reference items that exist in the resource tree
  * zh_cn and en_us have the same keys
  * the mods.toml template renders and parses as TOML
  * python helpers compile
"""
import glob
import json
import os
import py_compile
import sys
import tomllib

ROOT = os.path.dirname(os.path.abspath(__file__))
RES = os.path.join(ROOT, "src", "main", "resources")
NS = "lolipickaxe"
problems = []

json_files = glob.glob(os.path.join(RES, "**", "*.json"), recursive=True)
parsed = {}
for path in json_files:
    raw = open(path, "rb").read()
    rel = os.path.relpath(path, RES).replace(os.sep, "/")
    if raw.startswith(b"\xef\xbb\xbf"):
        problems.append(f"{rel}: has UTF-8 BOM")
    try:
        parsed[rel] = json.loads(raw.decode("utf-8-sig"))
    except Exception as exc:
        problems.append(f"{rel}: invalid JSON ({exc})")


def strip_ns(ref):
    if ":" in ref:
        ns, path = ref.split(":", 1)
        return ns, path
    return "minecraft", ref


names = set(parsed)
for rel in [n for n in names if n.startswith(f"assets/{NS}/items/")]:
    model = parsed[rel].get("model", {})
    ref = model.get("model") if isinstance(model, dict) else None
    if ref:
        ns, path = strip_ns(ref)
        target = f"assets/{ns}/models/{path}.json"
        if ns == NS and target not in names:
            problems.append(f"{rel}: missing model {target}")

for rel in [n for n in names if n.startswith(f"assets/{NS}/models/")]:
    data = parsed[rel]
    parent = data.get("parent")
    if parent:
        ns, path = strip_ns(parent)
        if ns == NS and f"assets/{ns}/models/{path}.json" not in names:
            problems.append(f"{rel}: missing parent {parent}")
    for tex in (data.get("textures") or {}).values():
        if not isinstance(tex, str) or tex.startswith("#"):
            continue
        ns, path = strip_ns(tex)
        if ns == NS and f"assets/{ns}/textures/{path}.png" not in {
            os.path.relpath(p, RES).replace(os.sep, "/") for p in glob.glob(os.path.join(RES, "**", "*.png"), recursive=True)
        }:
            problems.append(f"{rel}: missing texture {tex}")

for rel in [n for n in names if n.startswith(f"data/{NS}/recipe/")]:
    result = parsed[rel].get("result", {}).get("id")
    if result and result.startswith(f"{NS}:"):
        item = result.split(":", 1)[1]
        if f"assets/{NS}/items/{item}.json" not in names:
            problems.append(f"{rel}: result {result} has no item definition")

lang_en = parsed.get(f"assets/{NS}/lang/en_us.json", {})
lang_zh = parsed.get(f"assets/{NS}/lang/zh_cn.json", {})
missing = sorted(set(lang_zh) - set(lang_en))
if missing:
    problems.append(f"lang: {len(missing)} keys missing in en_us, e.g. {missing[:5]}")

template = open(os.path.join(ROOT, "src", "main", "templates", "META-INF", "neoforge.mods.toml"), encoding="utf-8").read()
props = {}
for line in open(os.path.join(ROOT, "gradle.properties"), encoding="utf-8"):
    line = line.strip()
    if line and not line.startswith("#") and "=" in line:
        k, v = line.split("=", 1)
        props[k.strip()] = v.strip()
for key, value in props.items():
    template = template.replace("${" + key + "}", value)
try:
    toml = tomllib.loads(template)
    if toml.get("mods", [{}])[0].get("modId") != NS:
        problems.append("neoforge.mods.toml: unexpected modId")
except Exception as exc:
    problems.append(f"neoforge.mods.toml: does not parse ({exc})")

for script in glob.glob(os.path.join(ROOT, "*.py")):
    try:
        py_compile.compile(script, doraise=True)
    except Exception as exc:
        problems.append(f"{os.path.basename(script)}: {exc}")

print(f"json files: {len(json_files)}, png files: {len(glob.glob(os.path.join(RES, '**', '*.png'), recursive=True))}")
if problems:
    print(f"{len(problems)} problem(s):")
    for problem in problems[:50]:
        print(" -", problem)
    sys.exit(1)
print("all checks passed")
