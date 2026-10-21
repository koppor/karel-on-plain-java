# Builds KarelExercise01.zip: complete IntelliJ project (template in intellij/ + src/karel/Karel.java + src/FirstKarelProgram.java + src/KarelExercise01.java).
# No top-level folder in the ZIP, so that "Extract All" in Explorer yields exactly one folder KarelExercise01.
$k = $PSScriptRoot
$stage = Join-Path $env:TEMP "KarelExercise01-stage"
Remove-Item -Recurse -Force $stage -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force "$stage\src\karel" | Out-Null
Copy-Item -Recurse -Force "$k\intellij\.idea" "$stage\.idea"
Copy-Item "$k\intellij\KarelExercise01.iml" $stage
Copy-Item "$k\src\karel\Karel.java" "$stage\src\karel\"
Copy-Item "$k\src\FirstKarelProgram.java" "$stage\src\"
Copy-Item "$k\src\KarelExercise01.java" "$stage\src\"
Remove-Item "$k\KarelExercise01.zip" -ErrorAction SilentlyContinue
Add-Type -AssemblyName System.IO.Compression.FileSystem
[IO.Compression.ZipFile]::CreateFromDirectory($stage, "$k\KarelExercise01.zip")
"KarelExercise01.zip built"