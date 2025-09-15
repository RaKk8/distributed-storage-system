@echo off
echo ======================================
echo  COMPILACION DEL PROYECTO
echo ======================================

echo Compilando el proyecto...
call mvn clean compile

if %ERRORLEVEL% neq 0 (
    echo ERROR: Fallo en la compilacion
    pause
    exit /b 1
)

echo ✅ Compilacion exitosa
echo.
pause