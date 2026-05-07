# Franchise API

API REST reactiva para gestión de franquicias, sucursales y productos, construida con **Spring Boot 3 + WebFlux**, persistencia en **Amazon DynamoDB** y desplegada en **AWS ECS Fargate** mediante **Terraform**.

---

## Tabla de contenidos

- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Prerrequisitos](#prerrequisitos)
- [Ejecución local](#ejecución-local)
- [Despliegue en AWS con Terraform](#despliegue-en-aws-con-terraform)
- [Endpoints](#endpoints)

---

## Arquitectura

El proyecto sigue el patrón de **Clean Architecture** (Arquitectura Hexagonal):

```
franchise/
├── domain/
│   ├── model/            → Entidades del negocio + interfaces Gateway (puertos)
│   └── usecase/          → Lógica de aplicación (casos de uso)
├── infrastructure/
│   ├── driven-adapters/
│   │   └── dynamo-db/    → Adaptador DynamoDB (implementa los gateways)
│   └── entry-points/
│       └── reactive-web/ → Controladores HTTP (WebFlux)
├── applications/
│   └── app-service/      → Ensamblaje, configuración y punto de entrada
├── deployment/
│   └── Dockerfile        → Imagen multi-stage (build + runtime)
└── terraform/            → Infraestructura como código (AWS)
```

```
[ HTTP Client ]
      │
  [ ALB :80 ]
      │
[ ECS Fargate :8080 ]
      │
  [ DynamoDB ] ← IAM Task Role (sin credenciales hardcodeadas)
```

### Tablas DynamoDB

| Tabla | Partition Key | GSI |
|-------|--------------|-----|
| `franchises` | `id` (String) | — |
| `branches` | `id` (String) | `franchiseId-index` (PK: franchiseId) |
| `products` | `id` (String) | `branchId-stock-index` (PK: branchId, SK: stock) |

---

## Tecnologías

- Java 21 + Spring Boot 3.4 (WebFlux — totalmente reactivo)
- AWS DynamoDB (SDK v2 Enhanced Client async)
- AWS ECS Fargate + ALB + ECR
- Terraform >= 1.5
- Docker (build multi-stage)
- Gradle 9 (wrapper incluido)

---

## Prerrequisitos

| Herramienta | Versión mínima |
|-------------|----------------|
| Java JDK | 21 |
| Docker Desktop | cualquiera reciente |
| AWS CLI | v2 |
| Terraform | 1.5+ |
| Cuenta AWS | con permisos para ECS, ECR, DynamoDB, IAM, ALB |

**Configurar credenciales AWS localmente:**

```bash
aws configure
# AWS Access Key ID:     <tu-access-key>
# AWS Secret Access Key: <tu-secret-key>
# Default region:        us-east-1
# Default output format: json
```

---

## Ejecución local

**1. Compilar sin tests:**

```bash
./gradlew :app-service:bootJar -x test
```

**2. Correr la aplicación:**

```bash
./gradlew bootRun -x test
```

> Requiere que las tablas DynamoDB existan en AWS y que las credenciales estén configuradas.

La app queda disponible en:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

**3. Alternativamente con Docker:**

```bash
# Construir imagen
docker build -f deployment/Dockerfile -t franchise:latest .

# Correr contenedor (reemplazar los datos <>)
docker run -d -p 8080:8080 --name franchise \
  -e AWS_REGION=us-east-1 \
  -e AWS_ACCESS_KEY_ID=<tu-access-key> \
  -e AWS_SECRET_ACCESS_KEY=<tu-secret-key> \
  franchise:latest

# Ver logs
docker logs -f franchise
```

---

## Despliegue en AWS con Terraform

### Infraestructura que se crea

| Recurso | Descripción |
|---------|-------------|
| ECR | Repositorio de imágenes Docker |
| ECS Fargate | Cluster + Task (0.25 vCPU / 512 MB) + Service |
| ALB | Application Load Balancer (puerto 80 → 8080) |
| DynamoDB | 3 tablas con GSIs (dentro del free tier) |
| IAM | Roles de ejecución y tarea con permisos mínimos |
| CloudWatch | Log group `/ecs/franchise` con retención de 7 días |
| Security Groups | ECS solo acepta tráfico proveniente del ALB |

### Paso 1 — Crear la infraestructura

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

Al finalizar, Terraform muestra (debes guardar estos datos para los siguientes pasos):

```
alb_url            = "http://franchise-alb-xxxxxxxxxx.us-east-1.elb.amazonaws.com"
ecr_repository_url = "xxxxxxxxxxxx.dkr.ecr.us-east-1.amazonaws.com/franchise"
ecs_cluster_name   = "franchise"
```

### Paso 2 — Construir y subir la imagen a ECR

```bash
# Autenticar Docker con ECR (reemplazar los datos en <>)
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <ecr_repository_url>

# Volver a la raíz del proyecto y construir la imagen
cd ..
docker build -f deployment/Dockerfile -t franchise:latest .

# Etiquetar y subir a ECR (reemplazar los datos en <>)
docker tag franchise:latest <ecr_repository_url>:latest
docker push <ecr_repository_url>:latest

```

### Paso 3 — Desplegar en ECS

```bash
aws ecs update-service --cluster franchise --service franchise --force-new-deployment --region us-east-1
```

### Paso 4 — Verificar el despliegue

```bash
# Estado del servicio
aws ecs describe-services \
  --cluster franchise \
  --services franchise \
  --region us-east-1 \
  --query 'services[0].{Status:status,Running:runningCount,Desired:desiredCount}'

# Los endpoints quedan disponibles en:
http://<alb_url>/

# Probar la aplicación (esperar 2-3 min a que el ALB marque las tareas como healthy)
curl http://<alb_url>/actuator/health
```

Swagger UI disponible en: `http://<alb_url>/swagger-ui.html`

### Destruir la infraestructura

```bash
cd terraform
terraform destroy
```

> **Advertencia:** esto elimina **todos** los recursos AWS incluyendo los datos en DynamoDB.
> Si necesitas conservar los datos, expórtalos primero:
>
> ```bash
> aws dynamodb scan --table-name franchises > franchises-backup.json
> aws dynamodb scan --table-name branches   > branches-backup.json
> aws dynamodb scan --table-name products   > products-backup.json
> ```

---

## Endpoints

### Franchises

| Método | Ruta | Descripción | Código |
|--------|------|-------------|--------|
| `POST` | `/api/franchises` | Crear franquicia | 201 |
| `PATCH` | `/api/franchises/{id}` | Actualizar nombre de franquicia | 200 |
| `GET` | `/api/franchises/{franchiseId}/top-products` | Producto con mayor stock por sucursal | 200 |

### Branches

| Método | Ruta | Descripción | Código |
|--------|------|-------------|--------|
| `POST` | `/api/branches` | Crear sucursal | 201 |
| `PATCH` | `/api/branches/{id}` | Actualizar nombre de sucursal | 200 |

### Products

| Método | Ruta | Descripción | Código |
|--------|------|-------------|--------|
| `POST` | `/api/products` | Crear producto | 201 |
| `DELETE` | `/api/products/{id}` | Eliminar producto | 204 |
| `PATCH` | `/api/products/{id}/stock` | Actualizar stock | 200 |
| `PATCH` | `/api/products/{id}/name` | Actualizar nombre | 200 |

### Utilidades

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/webjars/swagger-ui/index.html` | Documentación interactiva |
| `GET` | `/actuator/health` | Health check |
| `GET` | `/actuator/prometheus` | Métricas Prometheus |
