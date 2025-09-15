#!/bin/bash

# Script de Validación Completa del Sistema
# Ejecuta todas las pruebas y genera reportes de estado

echo "🚀 INICIANDO VALIDACIÓN COMPLETA DEL SISTEMA"
echo "============================================="
echo ""

# Función para mostrar el estado de un comando
check_status() {
    if [ $? -eq 0 ]; then
        echo "✅ $1: EXITOSO"
    else
        echo "❌ $1: FALLÓ"
        exit 1
    fi
}

# 1. Compilación completa
echo "📦 Compilando todos los módulos..."
mvn clean install -q
check_status "Compilación Maven"

echo ""
echo "🧪 EJECUTANDO SUITE DE PRUEBAS"
echo "================================"

# 2. Pruebas de autenticación
echo ""
echo "👤 Ejecutando pruebas de autenticación..."
mvn exec:java -pl shared -q
check_status "Pruebas de Autenticación"

# 3. Iniciar servidor de BD en background
echo ""
echo "🗄️ Iniciando servidor de base de datos..."
mvn exec:java -pl database-server > database-server.log 2>&1 &
SERVER_PID=$!
echo "Servidor iniciado con PID: $SERVER_PID"

# Esperar a que el servidor se inicie
echo "⏳ Esperando 5 segundos para que el servidor se inicialice..."
sleep 5

# Verificar que el servidor esté corriendo
if ps -p $SERVER_PID > /dev/null; then
    echo "✅ Servidor de base de datos: CORRIENDO"
else
    echo "❌ Servidor de base de datos: FALLÓ AL INICIAR"
    exit 1
fi

# 4. Pruebas de comunicación TCP
echo ""
echo "🔌 Ejecutando pruebas de comunicación TCP..."
mvn exec:java -pl shared -Dexec.mainClass="com.distribuidos.test.DatabaseClientTest" -q
check_status "Pruebas TCP"

# 5. Detener servidor
echo ""
echo "🛑 Deteniendo servidor de base de datos..."
kill $SERVER_PID
sleep 2

echo ""
echo "📊 RESUMEN DE VALIDACIÓN"
echo "========================"
echo "✅ Compilación: EXITOSA"
echo "✅ Autenticación: VALIDADA"
echo "✅ Servidor TCP: OPERACIONAL"
echo "✅ Comunicación BD: FUNCIONAL"
echo ""
echo "🎉 TODAS LAS PRUEBAS PASARON EXITOSAMENTE"
echo "📋 Ver TESTING_RESULTS.md para detalles completos"
echo ""
echo "🚀 Estado del proyecto: 40% completado"
echo "📅 Próximos pasos: Implementar nodos RMI"