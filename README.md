# Sistema de Almacenamiento Distribuido

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Ready-green.svg)](https://spring.io/projects/spring-boot)
[![H2 Database](https://img.shields.io/badge/H2-In--Memory-yellow.svg)](https://www.h2database.com/)

Un sistema de almacenamiento distribuido desarrollado como proyecto académico para la materia de Sistemas Distribuidos. Implementa comunicación mediante múltiples protocolos: RMI, TCP Sockets y SOAP/HTTPS.

## 🏗️ Arquitectura

```
┌─────────────────┐    SOAP/HTTPS    ┌──────────────────────┐
│  Cliente SOAP   │◄─────────────────►│  Servidor Principal  │
└─────────────────┘                  │   (Spring Boot)      │
                                     └──────────┬───────────┘
                                                │
                         ┌──────────────────────┼──────────────────────┐
                         │ RMI                  │ TCP                  │
                         ▼                      ▼                      │
            ┌─────────────────────┐    ┌─────────────────┐             │
            │  Nodos Storage      │    │  Base de Datos  │             │
            │  ┌─────┬─────┬─────┐│    │     (H2)        │             │
            │  │Node1│Node2│Node3││    │   Puerto 9001   │             │
            │  └─────┴─────┴─────┘│    └─────────────────┘             │
            └─────────────────────┘                                    │
                         ▲                                             │
                         └─────────────────────────────────────────────┘
```

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 11 o superior
- Maven 3.6 o superior
- Puerto 9001 disponible para base de datos

### Instalación y Compilación
```bash
# Compilar todos los módulos
mvn clean install
```

### Ejecución del Sistema

#### 1. Iniciar Servidor de Base de Datos
```bash
mvn exec:java -pl database-server
```

#### 2. Ejecutar Pruebas de Autenticación
```bash
mvn exec:java -pl shared
```

#### 3. Ejecutar Pruebas TCP (en nueva terminal)
```bash
mvn exec:java -pl shared -Dexec.mainClass="com.distribuidos.test.DatabaseClientTest"
```

## 📁 Estructura del Proyecto

```
Prototipe_1/
├── shared/                          # Componentes compartidos
│   ├── src/main/java/com/distribuidos/shared/
│   │   ├── model/                   # Entidades JPA
│   │   ├── rmi/                     # Interfaces RMI
│   │   ├── tcp/                     # Protocolo TCP
│   │   ├── soap/                    # Interfaces SOAP
│   │   └── service/                 # Servicios compartidos
├── database-server/                 # Servidor TCP de BD
│   └── src/main/java/com/distribuidos/database/
│       ├── config/                  # Configuración H2
│       ├── service/                 # Servicios de BD
│       └── handler/                 # Manejo TCP
├── storage-nodes/                   # Nodos RMI (en desarrollo)
├── application-server/              # Servidor Spring Boot (en desarrollo)
├── client-backend/                  # Cliente SOAP (en desarrollo)
└── scripts/                         # Scripts de automatización
```

## 📊 Estado del Desarrollo

### ✅ Completado (40%)
- [x] Modelo de datos JPA
- [x] Sistema de autenticación
- [x] Servidor de base de datos TCP
- [x] Protocolo de comunicación TCP
- [x] Pruebas unitarias básicas

### 🔄 En Desarrollo (60%)
- [ ] Nodos de almacenamiento RMI (3 nodos)
- [ ] Servidor principal Spring Boot
- [ ] Cliente backend SOAP
- [ ] Lógica de replicación distribuida
- [ ] Pruebas de integración completas

## 🧪 Pruebas

### Usuarios de Prueba
```
Username: admin    | Password: admin123 | Email: admin@sistema.com
Username: juan     | Password: juan123  | Email: juan@usuario.com  
Username: maria    | Password: maria123 | Email: maria@usuario.com
```

Ver [TESTING_RESULTS.md](TESTING_RESULTS.md) para resultados detallados de todas las pruebas.

## 🗃️ Base de Datos

### Esquema H2
- **users** - Gestión de usuarios
- **directories** - Estructura jerárquica de directorios  
- **files** - Metadatos de archivos
- **nodes** - Nodos de almacenamiento
- **file_replicas** - Réplicas distribuidas
- **permissions** - Control de acceso

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

**Nota:** Este es un prototipo académico enfocado en demostrar conceptos de sistemas distribuidos.
- ✅ Reporte de consumo de espacio

## Cómo Ejecutar
1. Compilar el proyecto: `mvn clean install`
2. Ejecutar servidor de base de datos: `scripts/start-database.bat`
3. Ejecutar 3 nodos de almacenamiento: `scripts/start-nodes.bat`
4. Ejecutar servidor de aplicación: `scripts/start-server.bat`
5. Ejecutar cliente backend: `scripts/start-client.bat`

## Tecnologías
- Java 11+
- Spring Boot 2.7+ (SOAP Web Services)
- H2 Database (preparado para MySQL)
- RMI para comunicación servidor-nodos
- TCP Sockets para comunicación servidor-BD
- Maven para gestión de dependencias
- H2 Database
- RMI para comunicación entre componentes
- Maven para gestión de dependencias