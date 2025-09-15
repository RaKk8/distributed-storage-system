@echo off
echo ======================================
echo  PRUEBAS COMPLETAS DEL SISTEMA
echo ======================================

echo Este script ejecuta todas las pruebas disponibles
echo.

echo 1. Compilando proyecto...
call "%~dp0\01-compile.bat"

if %ERRORLEVEL% neq 0 (
    echo ERROR en compilacion. Deteniendo pruebas.
    pause
    exit /b 1
)

echo.
echo 2. Probando servicio de autenticacion...
call "%~dp0\03-test-auth.bat"

echo.
echo ======================================
echo  INSTRUCCIONES PARA PRUEBA DE BD
echo ======================================
echo.
echo Para probar la base de datos:
echo 1. Abre una nueva ventana de comandos
echo 2. Ejecuta: 02-start-database.bat
echo 3. Cuando veas "Esperando conexiones de clientes..."
echo 4. Ejecuta en otra ventana: 04-test-database.bat
echo.
echo ¡Todas las pruebas basicas completadas!
echo.
pause