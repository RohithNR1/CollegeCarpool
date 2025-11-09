@echo off
setlocal enabledelayedexpansion

REM Optional: If you bundle a JDK folder beside this project, set it here:
REM set JAVA_HOME=%~dp0..\jdk-17
REM if exist "%JAVA_HOME%\bin\java.exe" set PATH=%JAVA_HOME%\bin;%PATH%

REM Clean & make bin
if not exist ..\bin mkdir ..\bin

REM List sources (Windows glob workaround)
del /q sources.txt 2>nul
for /r "..\src" %%f in (*.java) do echo %%f>>sources.txt

REM Compile
javac -d ..\bin -cp "..\lib\*;..\resources" @sources.txt
if errorlevel 1 (
  echo.
  echo *** Compile failed. Fix errors above. ***
  exit /b 1
)

REM Copy resources to bin (so classpath finds FXML & images)
xcopy /e /i /y "..\resources" "..\bin" >nul

REM Run
pushd ..
java --module-path lib --add-modules javafx.controls,javafx.fxml ^
 -cp "bin;lib\*" client.CarpoolClient
popd

