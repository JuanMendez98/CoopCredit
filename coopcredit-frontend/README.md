# CoopCredit - Frontend

Frontend moderno y minimalista para el sistema de gestión de crédito cooperativo CoopCredit.

## Características

- Diseño responsive con Tailwind CSS
- JavaScript Vanilla (sin frameworks)
- Autenticación con JWT
- Gestión completa de afiliados
- Gestión de solicitudes de crédito
- Evaluación de riesgo integrada
- Notificaciones toast
- Manejo de errores robusto

## Requisitos

- Navegador web moderno (Chrome, Firefox, Safari, Edge)
- Backend corriendo en:
  - Credit Service: `http://localhost:8080`
  - Risk Service: `http://localhost:8081`

## Estructura del Proyecto

```
coopcredit-frontend/
├── index.html              # Página de login/registro
├── css/
│   └── main.css           # Estilos personalizados
├── js/
│   ├── api.js             # Cliente HTTP y endpoints
│   ├── auth.js            # Gestión de autenticación
│   └── utils.js           # Funciones auxiliares
└── pages/
    ├── dashboard.html      # Panel principal
    ├── affiliates.html     # Gestión de afiliados
    └── credit-requests.html # Gestión de solicitudes
```

## Instalación y Uso

### Opción 1: Abrir directamente (Recomendado para desarrollo)

1. Simplemente abre el archivo `index.html` en tu navegador
2. Asegúrate de que los servicios backend estén corriendo

### Opción 2: Servidor HTTP local

```bash
# Con Python 3
cd coopcredit-frontend
python3 -m http.server 8000

# Luego abre: http://localhost:8000
```

```bash
# Con Node.js (npx)
cd coopcredit-frontend
npx serve

# Luego abre: http://localhost:3000
```

## Funcionalidades

### Autenticación

#### Registro
- **Endpoint**: `POST /api/auth/register`
- **Campos**:
  - Email
  - Contraseña (mínimo 6 caracteres)
  - Nombre completo
- **Respuesta**: Token JWT

#### Login
- **Endpoint**: `POST /api/auth/login`
- **Campos**:
  - Email
  - Contraseña
- **Respuesta**: Token JWT

### Gestión de Afiliados

#### Crear Afiliado
- **Endpoint**: `POST /api/affiliates`
- **Campos requeridos**:
  - `document`: Documento de identidad (String)
  - `name`: Nombre completo (String)
  - `salary`: Salario mensual (BigDecimal)
  - `affiliationDate`: Fecha de afiliación (ISO 8601, automático)

#### Listar Afiliados
- **Endpoint**: `GET /api/affiliates`
- Muestra todos los afiliados registrados
- Información: documento, nombre, salario, fecha de afiliación, estado

#### Buscar por Documento
- **Endpoint**: `GET /api/affiliates/document/{document}`
- Búsqueda específica por número de documento

### Gestión de Solicitudes de Crédito

#### Crear Solicitud
- **Endpoint**: `POST /api/credit-requests`
- **Campos requeridos**:
  - `affiliateId`: ID del afiliado (Integer)
  - `amount`: Monto solicitado (BigDecimal)
  - `term`: Plazo en meses (Integer)
  - `rate`: Tasa de interés (BigDecimal)

#### Listar Solicitudes
- **Endpoint**: `GET /api/credit-requests`
- Muestra todas las solicitudes con su estado
- Estados: PENDING, APPROVED, REJECTED

#### Ver Detalles
- **Endpoint**: `GET /api/credit-requests/{id}`
- Información completa de la solicitud
- Incluye evaluación de riesgo si está disponible

#### Evaluar Solicitud
- **Endpoint**: `POST /api/credit-requests/{id}/evaluate`
- Evalúa automáticamente la solicitud con Risk Central
- Actualiza el estado según el score de riesgo

## Manejo de Fechas

El sistema utiliza formato ISO 8601 para todas las fechas:
```javascript
// Formato correcto
"2024-01-15T10:30:00"

// Generación automática en JavaScript
new Date().toISOString() // "2025-12-09T20:30:45.123Z"
```

## Manejo de Errores

El frontend maneja automáticamente los errores del backend:

### Errores de Validación (400, 422)
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "errors": {
    "document": ["El documento es requerido"],
    "salary": ["El salario debe ser mayor a 0"]
  }
}
```

### Errores de Autenticación (401)
- Redirige automáticamente al login
- Limpia el token almacenado

### Errores del Servidor (500)
- Muestra mensaje genérico
- Registra el error en consola

## Configuración de API

Si necesitas cambiar las URLs del backend, edita `/js/api.js`:

```javascript
// Cambiar la URL base
class APIClient {
    constructor(baseURL = 'http://localhost:8080/api') {
        this.baseURL = baseURL;
        // ...
    }
}
```

## Características de Seguridad

1. **JWT Token**: Almacenado en localStorage
2. **Expiración**: Verificación automática del token
3. **Headers**: Authorization Bearer en todas las peticiones autenticadas
4. **Validación**: Validación en cliente antes de enviar

## Estilos y UI

- **Framework CSS**: Tailwind CSS (CDN)
- **Tipografía**: System fonts (-apple-system, Segoe UI)
- **Colores**: Paleta minimalista en escala de grises con acentos azules
- **Componentes**:
  - Modales animados
  - Toasts de notificación
  - Tablas responsivas
  - Loading skeletons
  - Empty states

## Navegación

```
index.html (Login/Registro)
    ↓ (autenticado)
pages/dashboard.html
    ├─→ pages/affiliates.html (Gestión de Afiliados)
    └─→ pages/credit-requests.html (Gestión de Solicitudes)
```

## Funciones Auxiliares

### Formateo
```javascript
formatCurrency(5000000)      // "$5,000,000.00 COP"
formatDate("2024-01-15")     // "15 de enero de 2024"
formatDateTime("2024-01-15") // "15 ene 2024, 10:30"
```

### Validación
```javascript
isValidEmail("test@example.com")  // true
isValidPassword("abc123")         // true (mínimo 6 caracteres)
```

### Notificaciones
```javascript
toast.success("Operación exitosa")
toast.error("Error en la operación")
toast.info("Información importante")
```

## Testing

Puedes probar el sistema siguiendo este flujo:

1. **Registrar usuario**
   - Email: `admin@coopcredit.com`
   - Password: `admin123`

2. **Crear afiliado**
   - Documento: `123456789`
   - Nombre: `Juan Pérez`
   - Salario: `5000000`

3. **Crear solicitud de crédito**
   - Afiliado: Seleccionar "Juan Pérez"
   - Monto: `10000000`
   - Plazo: `12` meses
   - Tasa: `5.5`%

4. **Evaluar solicitud**
   - Ir a detalles de la solicitud
   - Click en "Evaluar"
   - Ver resultado de Risk Central

## Troubleshooting

### Error: "Sesión expirada"
- El token JWT ha expirado
- Vuelve a iniciar sesión

### Error: "Error de red" o timeout
- Verifica que los servicios backend estén corriendo
- Revisa las URLs en `api.js`

### Error: "CORS"
- Asegúrate de que el backend tenga CORS habilitado para el origen del frontend

### La página está en blanco
- Abre la consola del navegador (F12)
- Revisa los errores de JavaScript
- Verifica que todos los archivos JS se carguen correctamente

## Desarrollo

Para agregar nuevos endpoints:

1. Agrega el método en `/js/api.js`
2. Úsalo en la página correspondiente
3. Maneja los errores con `handleAPIError()`

```javascript
// Ejemplo: Agregar nuevo endpoint
const myAPI = {
    myMethod(param) {
        return api.get(`/my-endpoint/${param}`);
    }
};
```

## Licencia

© 2025 CoopCredit. Todos los derechos reservados.
