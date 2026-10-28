# Builds Karel-v<version>.zip: the complete IntelliJ project students open (template in intellij/ + src/karel/Karel.java + src/FirstKarelProgram.java).
# No top-level folder in the ZIP, so that "Extract all" yields exactly one folder named like the ZIP (Karel-v1.3.1); IntelliJ shows that
# folder name as the project name. Usage: .\make_zip.ps1   (or .\make_zip.ps1 -Name KarelExercise01 for the legacy name)
param([string]$Name = "Karel")
$name = $Name
$k = $PSScriptRoot
# ZIP name carries the engine version (OK 2026-10-28)
$version = (Select-String -Path "$k\src\karel\Karel.java" -Pattern 'VERSION = "([0-9.]+)"').Matches[0].Groups[1].Value
$zipName = "$name-v$version"
$stage = Join-Path $env:TEMP "$name-stage"
Remove-Item -Recurse -Force $stage -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force "$stage\src\karel" | Out-Null
# template files carry the placeholder KarelExercise01; rename to the ZIP name
foreach ($f in Get-ChildItem -Recurse -File "$k\intellij") {
    $rel = $f.FullName.Substring("$k\intellij\".Length).Replace("KarelExercise01", $name)
    $target = Join-Path $stage $rel
    New-Item -ItemType Directory -Force (Split-Path $target) | Out-Null
    (Get-Content $f.FullName -Raw).Replace("KarelExercise01", $name) | Set-Content -NoNewline -Encoding utf8 $target
}
Copy-Item "$k\src\karel\Karel.java" "$stage\src\karel\"
Copy-Item "$k\src\FirstKarelProgram.java" "$stage\src\"
Remove-Item "$k\$name-v*.zip" -ErrorAction SilentlyContinue
Add-Type -AssemblyName System.IO.Compression.FileSystem
[IO.Compression.ZipFile]::CreateFromDirectory($stage, "$k\$zipName.zip")
"$zipName.zip built"
