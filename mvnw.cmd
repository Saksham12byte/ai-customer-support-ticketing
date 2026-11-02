@REM ----------------------------------------------------------------------------
@REM Maven Wrapper script for Windows (CMD)
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set "PROJECT_DIR=%~dp0"
if defined JAVA_HOME (
  set "JAVACMD=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVACMD=java"
)

where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
  mvn %*
) else (
  echo Maven is required to run this project. Please install Maven from https://maven.apache.org
)
