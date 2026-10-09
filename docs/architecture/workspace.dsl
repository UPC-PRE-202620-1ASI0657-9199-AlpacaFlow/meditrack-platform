workspace "MediTrack" "Arquitectura de software de MediTrack (AlpacaFlow) - Fundamentos de Arquitectura de Software 1ASI0657" {

    !identifiers hierarchical

    model {
        # ------------------------------------------------------------------
        # Personas
        # ------------------------------------------------------------------
        allegado = person "Allegado" "Familiar que monitorea a distancia al adulto mayor y recibe alertas."
        medico = person "Médico" "Personal médico de clínica que da seguimiento a los adultos mayores asignados."
        cuidador = person "Cuidador" "Personal de casa de reposo que supervisa a varios residentes por turno."
        adminOrg = person "Administrador de organización" "Gestiona la clínica o casa de reposo: personal, adultos mayores y asignaciones."
        adultoMayor = person "Adulto mayor" "Porta el parche inteligente. No interactúa con la plataforma."

        # ------------------------------------------------------------------
        # Sistemas externos
        # ------------------------------------------------------------------
        parche = softwareSystem "Parche inteligente (simulado)" "Dispositivo IoT que mide signos vitales y detecta caídas. En esta etapa se emula con un simulador de software." "External"
        acsEmail = softwareSystem "Azure Communication Services Email" "Servicio gestionado de envío de correos de alerta y de credenciales." "External"

        # ------------------------------------------------------------------
        # Sistema MediTrack
        # ------------------------------------------------------------------
        meditrack = softwareSystem "MediTrack" "Plataforma de monitoreo remoto de signos vitales y movilidad de adultos mayores." {

            spa = container "Aplicación Web" "SPA para allegados, médicos, cuidadores y administradores: dashboard, signos vitales, alertas y gestión." "Angular, Azure Static Web Apps" "Web"

            gateway = container "API Gateway" "Punto único de entrada: enruta peticiones, valida JWT y aplica rate limiting." "Spring Cloud Gateway, Azure Container Apps"

            iam = container "IAM Service" "Registro, autenticación, roles y emisión de tokens JWT." "Java 21, Spring Boot, Azure Container Apps"
            subscription = container "Subscription Service" "Planes Free, Premium y Enterprise y funcionalidades habilitadas por plan." "Java 21, Spring Boot, Azure Container Apps"
            organization = container "Organization Service" "Clínicas, casas de reposo, médicos, cuidadores y asignaciones de adultos mayores." "Java 21, Spring Boot, Azure Container Apps"
            patient = container "Patient Service" "Adultos mayores, perfiles clínicos básicos y vínculo con allegados." "Java 21, Spring Boot, Azure Container Apps"
            device = container "Device Service" "Registro de parches y su vinculación con adultos mayores." "Java 21, Spring Boot, Azure Container Apps"

            monitoring = container "Monitoring Service" "Ingesta y consulta de signos vitales, evaluación de reglas y generación de alertas." "Java 21, Spring Boot, Azure Container Apps" {
                telemetryConsumer = component "Telemetry Consumer" "Consume las lecturas desde el endpoint compatible con Event Hubs de IoT Hub, con checkpoints por partición." "Azure SDK EventProcessorClient" "Iteracion2"
                validator = component "Reading Validator" "Valida rangos físicos y formato, normaliza unidades y descarta duplicados (deviceId + timestamp)." "Spring Bean" "Iteracion2"
                deviceDirectory = component "Device Directory" "Read model local deviceId -> adulto mayor y organización, actualizado por eventos DeviceAssigned." "Spring Bean, caché en memoria" "Iteracion2"
                ruleEngine = component "Alert Rule Engine" "Evalúa umbrales por tipo de signo vital y detecta caídas (patrón Strategy)." "Spring Bean" "Iteracion2"
                vitalRepository = component "Vital Sign Repository" "Persiste y consulta lecturas en tablas particionadas por fecha." "Spring Data JPA" "Iteracion2"
                alertRepository = component "Alert Repository" "Persiste alertas y su ciclo de vida." "Spring Data JPA" "Iteracion2"
                alertPublisher = component "Alert Publisher" "Publica el evento AlertRaised en el topic de alertas." "Spring Cloud Azure Service Bus" "Iteracion2"
                vitalsController = component "Vital Signs Controller" "GET/POST /api/v1/devices/{deviceId}/heart-rates | oxygens | blood-pressures | temperatures" "Spring REST Controller" "Iteracion2"
                alertsController = component "Alerts Controller" "GET /api/v1/alerts, /api/v1/alerts/{alertId}, /api/v1/devices/{deviceId}/alerts; PATCH /api/v1/alerts/{alertId}/status" "Spring REST Controller" "Iteracion2"
                assignmentListener = component "Device Assignment Listener" "Escucha DeviceAssigned / DeviceUnassigned y actualiza el Device Directory." "Spring Cloud Azure Service Bus" "Iteracion2"
            }

            notification = container "Notification Service" "Entrega de alertas en tiempo real al navegador y por correo electrónico." "Java 21, Spring Boot, Azure Container Apps" {
                alertListener = component "Alert Event Listener" "Consume AlertRaised desde la suscripción del topic de alertas." "Spring Cloud Azure Service Bus" "Iteracion2"
                recipientResolver = component "Recipient Resolver" "Determina destinatarios (allegados, médico o cuidador asignado) desde un read model alimentado por eventos." "Spring Bean" "Iteracion2"
                realtimeNotifier = component "Real-time Notifier" "Envía la alerta al grupo de la organización y de los allegados." "Azure Web PubSub SDK" "Iteracion2"
                emailNotifier = component "Email Notifier" "Envía correo para alertas críticas y caídas." "Azure Communication Services SDK" "Iteracion2"
                notificationLog = component "Notification Log Repository" "Registra cada entrega para trazabilidad y auditoría." "Spring Data JPA" "Iteracion2"
            }

            serviceBus = container "Azure Service Bus" "Bus de eventos de dominio entre microservicios (topics y suscripciones, con dead-letter queue)." "Azure Service Bus, AMQP" "Queue"

            iotHub = container "Azure IoT Hub" "Broker MQTT gestionado con identidad por dispositivo; expone un endpoint compatible con Event Hubs." "Azure IoT Hub, MQTT" "Queue,Iteracion2"
            webPubSub = container "Azure Web PubSub" "Push de alertas en tiempo real al navegador mediante WebSocket." "Azure Web PubSub" "Iteracion2"
            checkpointStore = container "Checkpoint Store" "Guarda el avance de lectura por partición para reanudar sin pérdida." "Azure Blob Storage" "Database,Iteracion2"

            iamDb = container "IAM DB" "Usuarios, credenciales (hash) y roles." "Azure Database for MySQL" "Database"
            subscriptionDb = container "Subscription DB" "Planes y suscripciones." "Azure Database for MySQL" "Database"
            organizationDb = container "Organization DB" "Organizaciones, personal y asignaciones." "Azure Database for MySQL" "Database"
            patientDb = container "Patient DB" "Adultos mayores y allegados (datos sensibles cifrados)." "Azure Database for MySQL" "Database"
            deviceDb = container "Device DB" "Parches y vinculaciones." "Azure Database for MySQL" "Database"
            monitoringDb = container "Monitoring DB" "Lecturas de signos vitales (particionadas por fecha) y alertas." "Azure Database for MySQL 8" "Database"
            notificationDb = container "Notification DB" "Registro de notificaciones enviadas." "Azure Database for MySQL" "Database"
        }

        # ------------------------------------------------------------------
        # Relaciones de contexto
        # ------------------------------------------------------------------
        adultoMayor -> parche "Porta"
        allegado -> meditrack.spa "Consulta signos vitales y recibe alertas" "HTTPS"
        medico -> meditrack.spa "Da seguimiento a pacientes asignados" "HTTPS"
        cuidador -> meditrack.spa "Supervisa residentes y atiende alertas" "HTTPS"
        adminOrg -> meditrack.spa "Gestiona personal, adultos mayores y asignaciones" "HTTPS"

        parche -> meditrack "Envía lecturas de signos vitales y eventos de caída" "" "Contexto"

        # Iteración 1: el parche envía lecturas por REST (TS23-TS30)
        parche -> meditrack.gateway "Envía lecturas (Iteración 1)" "HTTPS/JSON" "Iteracion1"
        # Iteración 2: el parche publica por MQTT en IoT Hub
        parche -> meditrack.iotHub "Publica lecturas y eventos de caída" "MQTT/TLS" "Iteracion2"

        # SPA y Gateway
        meditrack.spa -> meditrack.gateway "Consume API REST" "HTTPS/JSON"
        meditrack.gateway -> meditrack.iam "Enruta /api/v1/auth, /users" "HTTPS"
        meditrack.gateway -> meditrack.subscription "Enruta /api/v1/plans" "HTTPS"
        meditrack.gateway -> meditrack.organization "Enruta /api/v1/organizations" "HTTPS"
        meditrack.gateway -> meditrack.patient "Enruta /api/v1/senior-citizens, /relatives" "HTTPS"
        meditrack.gateway -> meditrack.device "Enruta /api/v1/devices" "HTTPS"
        meditrack.gateway -> meditrack.monitoring "Enruta signos vitales y alertas" "HTTPS"

        # Persistencia (Database per Service)
        meditrack.iam -> meditrack.iamDb "Lee/escribe" "JDBC"
        meditrack.subscription -> meditrack.subscriptionDb "Lee/escribe" "JDBC"
        meditrack.organization -> meditrack.organizationDb "Lee/escribe" "JDBC"
        meditrack.patient -> meditrack.patientDb "Lee/escribe" "JDBC"
        meditrack.device -> meditrack.deviceDb "Lee/escribe" "JDBC"
        meditrack.notification -> meditrack.notificationDb "Lee/escribe" "JDBC"

        # Eventos de dominio
        meditrack.iam -> meditrack.serviceBus "Publica UserRegistered, AdminAccountCreated / consume OrganizationRegistered" "AMQP"
        meditrack.organization -> meditrack.serviceBus "Publica OrganizationRegistered, PatientAssigned" "AMQP"
        meditrack.patient -> meditrack.serviceBus "Publica SeniorCitizenRegistered, RelativeLinked" "AMQP"
        meditrack.device -> meditrack.serviceBus "Publica DeviceAssigned" "AMQP"
        meditrack.subscription -> meditrack.serviceBus "Consume UserRegistered" "AMQP"
        meditrack.notification -> meditrack.serviceBus "Consume AlertRaised, AdminAccountCreated y eventos de asignación" "AMQP"
        meditrack.notification -> acsEmail "Envía correos" "HTTPS"

        # Iteración 1: Monitoring como contenedor monolítico simple
        meditrack.monitoring -> meditrack.monitoringDb "Lee/escribe lecturas y alertas" "JDBC"
        meditrack.monitoring -> meditrack.serviceBus "Publica AlertRaised / consume DeviceAssigned" "AMQP"

        # ------------------------------------------------------------------
        # Iteración 2: componentes de Monitoring Service
        # ------------------------------------------------------------------
        meditrack.monitoring.telemetryConsumer -> meditrack.iotHub "Lee lecturas" "AMQP (Event Hubs-compatible)"
        meditrack.monitoring.telemetryConsumer -> meditrack.checkpointStore "Guarda checkpoints" "HTTPS"
        meditrack.monitoring.telemetryConsumer -> meditrack.monitoring.validator "Entrega lectura"
        meditrack.monitoring.validator -> meditrack.monitoring.deviceDirectory "Resuelve adulto mayor y organización"
        meditrack.monitoring.validator -> meditrack.monitoring.vitalRepository "Persiste lectura válida"
        meditrack.monitoring.validator -> meditrack.monitoring.ruleEngine "Evalúa lectura"
        meditrack.monitoring.ruleEngine -> meditrack.monitoring.alertRepository "Registra alerta"
        meditrack.monitoring.ruleEngine -> meditrack.monitoring.alertPublisher "Solicita publicación"
        meditrack.monitoring.alertPublisher -> meditrack.serviceBus "Publica AlertRaised" "AMQP"
        meditrack.monitoring.assignmentListener -> meditrack.serviceBus "Consume DeviceAssigned" "AMQP"
        meditrack.monitoring.assignmentListener -> meditrack.monitoring.deviceDirectory "Actualiza"
        meditrack.monitoring.vitalRepository -> meditrack.monitoringDb "Lee/escribe" "JDBC"
        meditrack.monitoring.alertRepository -> meditrack.monitoringDb "Lee/escribe" "JDBC"
        meditrack.gateway -> meditrack.monitoring.vitalsController "Enruta signos vitales" "HTTPS"
        meditrack.gateway -> meditrack.monitoring.alertsController "Enruta alertas" "HTTPS"
        meditrack.monitoring.vitalsController -> meditrack.monitoring.vitalRepository "Consulta"
        meditrack.monitoring.alertsController -> meditrack.monitoring.alertRepository "Consulta"

        # ------------------------------------------------------------------
        # Iteración 2: componentes de Notification Service
        # ------------------------------------------------------------------
        meditrack.notification.alertListener -> meditrack.serviceBus "Consume AlertRaised" "AMQP"
        meditrack.notification.alertListener -> meditrack.notification.recipientResolver "Solicita destinatarios"
        meditrack.notification.alertListener -> meditrack.notification.realtimeNotifier "Notifica en tiempo real"
        meditrack.notification.alertListener -> meditrack.notification.emailNotifier "Notifica por correo (críticas y caídas)"
        meditrack.notification.alertListener -> meditrack.notification.notificationLog "Registra entrega"
        meditrack.notification.realtimeNotifier -> meditrack.webPubSub "Envía a grupos" "HTTPS"
        meditrack.notification.emailNotifier -> acsEmail "Envía correo" "HTTPS"
        meditrack.notification.notificationLog -> meditrack.notificationDb "Escribe" "JDBC"
        meditrack.webPubSub -> meditrack.spa "Entrega alerta" "WebSocket"
    }

    views {
        systemContext meditrack "L1-Contexto" "MediTrack - Diagrama de contexto" {
            include *
            include adultoMayor
            exclude relationship.tag==Iteracion1
            autoLayout lr
        }

        container meditrack "L2-Contenedores-Iteracion1" "Iteración 1 - Arquitectura base de microservicios (contenedores)" {
            include *
            exclude element.tag==Iteracion2
            exclude relationship.tag==Iteracion2
            exclude adultoMayor
            autoLayout lr
        }

        container meditrack "L2-Contenedores-Iteracion2" "Iteración 2 - Contenedores tras incorporar el pipeline de telemetría y alertas" {
            include *
            exclude relationship.tag==Iteracion1
            exclude adultoMayor
            autoLayout lr
        }

        component meditrack.monitoring "L3-Componentes-Monitoring" "Iteración 2 - Componentes del Monitoring Service" {
            include *
            autoLayout lr
        }

        component meditrack.notification "L3-Componentes-Notification" "Iteración 2 - Componentes del Notification Service" {
            include *
            autoLayout lr
        }

        dynamic meditrack.monitoring "Dinamico-Alerta-Critica" "Iteración 2 - Flujo de una alerta crítica (de la lectura al dashboard)" {
            parche -> meditrack.iotHub "Publica lectura anómala o evento de caída"
            meditrack.monitoring.telemetryConsumer -> meditrack.iotHub "Lee el mensaje de la partición"
            meditrack.monitoring.telemetryConsumer -> meditrack.monitoring.validator "Entrega lectura"
            meditrack.monitoring.validator -> meditrack.monitoring.deviceDirectory "Resuelve adulto mayor y organización"
            meditrack.monitoring.validator -> meditrack.monitoring.vitalRepository "Persiste lectura"
            meditrack.monitoring.validator -> meditrack.monitoring.ruleEngine "Evalúa reglas"
            meditrack.monitoring.ruleEngine -> meditrack.monitoring.alertRepository "Registra alerta (RAISED)"
            meditrack.monitoring.ruleEngine -> meditrack.monitoring.alertPublisher "Solicita publicación"
            meditrack.monitoring.alertPublisher -> meditrack.serviceBus "Publica AlertRaised"
            meditrack.notification -> meditrack.serviceBus "Consume AlertRaised"
            meditrack.notification -> meditrack.webPubSub "Envía alerta a los grupos destinatarios"
            meditrack.webPubSub -> meditrack.spa "Muestra alerta en el dashboard"
            meditrack.notification -> acsEmail "Envía correo de alerta"
            autoLayout lr
        }

        styles {
            element "Person" {
                shape Person
                background #08427B
                color #ffffff
            }
            element "Software System" {
                background #1168BD
                color #ffffff
            }
            element "External" {
                background #999999
                color #ffffff
            }
            element "Container" {
                background #438DD5
                color #ffffff
            }
            element "Component" {
                background #85BBF0
                color #000000
            }
            element "Database" {
                shape Cylinder
            }
            element "Queue" {
                shape Pipe
            }
            element "Web" {
                shape WebBrowser
            }
        }
    }
}
