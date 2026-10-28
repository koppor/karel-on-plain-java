"""Align pipe tables in Markdown files: pad every cell to the column width so that the pipes line up (markdownlint MD060 style aligned)."""
import pathlib, re, sys

def split_row(line):
    s = line.strip()
    if s.startswith("|"): s = s[1:]
    if s.endswith("|"): s = s[:-1]
    cells, cur, i = [], "", 0
    while i < len(s):
        ch = s[i]
        if ch == "\\" and i + 1 < len(s):
            cur += s[i:i + 2]; i += 2; continue
        if ch == "|":
            cells.append(cur.strip()); cur = ""
        else:
            cur += ch
        i += 1
    cells.append(cur.strip())
    return cells

def is_sep(cells):
    return all(re.fullmatch(r":?-{1,}:?", c) for c in cells) and cells

def align(block):
    rows = [split_row(l) for l in block]
    width = max(len(r) for r in rows)
    rows = [r + [""] * (width - len(r)) for r in rows]
    sep_idx = next((i for i, r in enumerate(rows) if is_sep(r)), None)
    cols = [max(len(r[c]) for i, r in enumerate(rows) if i != sep_idx) for c in range(width)]
    cols = [max(w, 3) for w in cols]
    out = []
    for i, r in enumerate(rows):
        if i == sep_idx:
            cells = []
            for c, spec in enumerate(r):
                left = spec.startswith(":"); right = spec.endswith(":")
                dash = "-" * (cols[c] - left - right)
                cells.append((":" if left else "") + dash + (":" if right else ""))
            out.append("| " + " | ".join(cells) + " |")
        else:
            out.append("| " + " | ".join(r[c].ljust(cols[c]) for c in range(width)) + " |")
    return out

def process(path):
    lines = path.read_text(encoding="utf-8").split("\n")
    result, block, in_code = [], [], False
    for line in lines + [""]:
        if line.strip().startswith("```"):
            in_code = not in_code
        if not in_code and line.strip().startswith("|") and line.strip().endswith("|"):
            block.append(line); continue
        if block:
            result.extend(align(block) if len(block) >= 2 else block); block = []
        result.append(line)
    text = "\n".join(result[:-1]) + ("\n" if lines[-1] == "" else "")
    changed = text != path.read_text(encoding="utf-8")
    path.write_text(text, encoding="utf-8")
    return changed

for p in sys.argv[1:]:
    print(p, "aligned" if process(pathlib.Path(p)) else "unchanged")
