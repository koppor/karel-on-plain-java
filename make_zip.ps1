# Builds KarelExercise<NN>-Karel-v<version>.zip: complete IntelliJ project (template in intellij/ + src/karel/Karel.java + src/FirstKarelProgram.java).
# No top-level folder in the ZIP, so that "Extract All" in Explorer yields exactly one folder named like the ZIP.
# Usage: .\make_zip.ps1            -> KarelExercise02.zip (current exercise)
#        .\make_zip.ps1 -Exercise 01
param([string]$Exercise = "02")
$name = "KarelExercise$Exercise"
$k = $PSScriptRoot
# ZIP name carries the engine version (OK 2026-10-28): KarelExercise02-Karel-v1.3.0.zip; Extract all then yields a folder of that name
$version = (Select-String -Path "$k\src\karel\Karel.java" -Pattern 'VERSION = "([0-9.]+)"').Matches[0].Groups[1].Value
$zipName = "$name-Karel-v$version"
$stage = Join-Path $env:TEMP "$name-stage"
Remove-Item -Recurse -Force $stage -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force "$stage\src\karel" | Out-Null
# template files carry the placeholder KarelExercise01; rename for the current exercise
foreach ($f in Get-ChildItem -Recurse -File "$k\intellij") {
    $rel = $f.FullName.Substring("$k\intellij\".Length).Replace("KarelExercise01", $name)
    $target = Join-Path $stage $rel
    New-Item -ItemType Directory -Force (Split-Path $target) | Out-Null
    (Get-Content $f.FullName -Raw).Replace("KarelExercise01", $name) | Set-Content -NoNewline -Encoding utf8 $target
}
Copy-Item "$k\src\karel\Karel.java" "$stage\src\karel\"
Copy-Item "$k\src\FirstKarelProgram.java" "$stage\src\"
Remove-Item "$k\$name*.zip" -ErrorAction SilentlyContinue
Add-Type -AssemblyName System.IO.Compression.FileSystem
[IO.Compression.ZipFile]::CreateFromDirectory($stage, "$k\$zipName.zip")
"$zipName.zip built"
