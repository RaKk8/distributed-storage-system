@echo off
echo ======================================
echo  PRUEBA DE AUTENTICACION
echo ======================================

echo Ejecutando pruebas del servicio de autenticacion...
echo.

cd /d "%~dp0\.."
call mvn exec:java -pl shared -Dexec.mainClass="com.distribuidos.shared.test.AuthenticationTest"

echo.
echo Prueba completada.
pause