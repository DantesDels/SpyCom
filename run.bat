@echo off
if /i "%1"=="server" (
    gradle runServer
) else if /i "%1"=="client" (
    gradle runClient
) else if "%1"=="" (
    gradle runClient
) else (
    echo Usage: run [server^|client]
    exit /b 1
)
