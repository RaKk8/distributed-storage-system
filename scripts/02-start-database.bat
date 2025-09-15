@echo off
echo ======================================
echo  SERVIDOR DE BASE DE DATOS
echo ======================================

echo Iniciando servidor de base de datos en puerto 9001...
echo Presiona Ctrl+C para detener el servidor
echo.

cd /d "%~dp0\.."
call mvn exec:java -pl database-server -Dexec.mainClass="com.distribuidos.database.DatabaseServer"

pause