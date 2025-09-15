@echo off
echo === Iniciando pruebas de comunicacion TCP ===

REM Verificar si el servidor de base de datos está corriendo
echo Verificando servidor de base de datos...
netstat -an | findstr ":9001" >nul
if %errorlevel% equ 0 (
    echo ✅ Servidor de base de datos detectado en puerto 9001
) else (
    echo ❌ Servidor de base de datos no detectado en puerto 9001
    echo Por favor inicie el servidor primero con: mvn exec:java -pl database-server
    pause
    exit /b 1
)

echo.
echo === Ejecutando pruebas del cliente TCP ===

REM Ejecutar las pruebas usando Maven con configuración específica
mvn -q exec:java -pl shared -Dexec.mainClass="com.distribuidos.test.DatabaseClientTest" -Dexec.cleanupDaemonThreads=false

echo.
echo === Pruebas completadas ===
pause