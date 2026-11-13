"""Validate the built jar's resources: models, textures, item definitions, recipes, lang."""
import json
import os
import posixpath
import sys
import zipfile

JAR = r"F:\ds\lolipickaxe-port\LoliPickaxe\build\libs\lolipickaxe-2.0.0.jar"
NS = "lolipickaxe"
z = zipfile.ZipFile(JAR)
names = set(z.namelist())
problems = []


def read_json(path):
    return json.loads(z.read(path).decode("utf-8"))


def strip_ns(ref):
    if ":" in ref:
        ns, path = ref.split(":", 1)
        return ns, path
    return "minecraft", ref


# 1. every assets/lolipickaxe/items/*.json must point at an existing model
for name in [n for n in names if n.startswith(f"assets/{NS}/items/") and n.endswith(".json")]:
    data = read_json(name)
    model = data.get("model", {})
    ref = model.get("model") if isinstance(model, dict) else None
    if ref:
        ns, path = strip_ns(ref)
        model_path = f"assets/{ns}/models/{path}.json"
        if model_path not in names:
            problems.append(f"{name}: missing model {model_path}")

# 2. every model's textures must exist
for name in [n for n in names if n.startswith(f"assets/{NS}/models/") and n.endswith(".json")]:
    data = read_json(name)
    for key, ref in (data.get("textures") or {}).items():
        if not isinstance(ref, str) or ref.startswith("#"):
            continue
        ns, path = strip_ns(ref)
        tex = f"assets/{ns}/textures/{path}.png"
        if tex not in names:
            problems.append(f"{name}: missing texture {tex}")

# 3. parent models must exist (vanilla parents are skipped)
for name in [n for n in names if n.startswith(f"assets/{NS}/models/") and n.endswith(".json")]:
    parent = read_json(name).get("parent")
    if parent and ":" not in parent:
        parent = "minecraft:" + parent
    if parent and not parent.startswith("minecraft:"):
        ns, path = strip_ns(parent)
        if f"assets/{ns}/models/{path}.json" not in names:
            problems.append(f"{name}: missing parent {parent}")

# 4. recipes: referenced items must be known mod ids or minecraft namespace
for name in [n for n in names if n.startswith(f"data/{NS}/recipe/") and n.endswith(".json")]:
    data = read_json(name)
    result = data.get("result", {}).get("id")
    if result and result.startswith(f"{NS}:"):
        item_id = result.split(":", 1)[1]
        if f"assets/{NS}/items/{item_id}.json" not in names:
            problems.append(f"{name}: result item {result} has no item definition")

# 5. lang keys: every key used in code should exist in en_us
en = read_json(f"assets/{NS}/lang/en_us.json")
zh = read_json(f"assets/{NS}/lang/zh_cn.json")
missing_en = sorted(set(zh) - set(en))
if missing_en:
    problems.append(f"lang: {len(missing_en)} keys missing from en_us, e.g. {missing_en[:5]}")

print(f"checked {len(names)} entries")
if problems:
    print(f"{len(problems)} problems:")
    for p in problems[:40]:
        print(" -", p)
    sys.exit(1)
print("no problems found")

