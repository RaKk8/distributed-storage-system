@echo off
echo ======================================
echo  PRUEBA DE BASE DE DATOS
echo ======================================

echo IMPORTANTE: Asegurate de que el servidor de base de datos este ejecutandose
echo            (ejecuta 02-start-database.bat en otra ventana)
echo.
echo Presiona cualquier tecla para continuar o Ctrl+C para cancelar...
pause > nul

echo Ejecutando cliente de prueba de base de datos...
echo.

cd /d "%~dp0\.."
call mvn exec:java -pl database-server -Dexec.mainClass="com.distribuidos.database.client.DatabaseClient"

echo.
echo Prueba completada.
pause