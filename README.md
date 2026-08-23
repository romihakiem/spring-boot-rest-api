# REST API Skeleton — Spring Boot + MySQL + JWT

Skeleton REST API standar untuk Java Spring Boot dengan autentikasi JWT,
CRUD lengkap, pagination, dan graceful shutdown.

## Tech Stack
- Java 17
- Spring Boot 3.2.5 (Web, Data JPA, Security, Validation)
- MySQL 8
- JWT (jjwt 0.12.x)
- Lombok
- Maven

## Struktur Folder

```
src/main/java/com/skeleton/api/
├── ApiApplication.java          # entry point
├── config/
│   ├── SecurityConfig.java      # security filter chain, CORS, password encoder
│   └── GracefulShutdownConfig.java
├── security/
│   ├── CustomUserDetails.java
│   ├── CustomUserDetailsService.java
│   └── JwtAuthenticationFilter.java
├── controller/
│   ├── AuthController.java      # register, login, me
│   ├── UserController.java      # admin: list/detail/delete user
│   └── ItemController.java      # full CRUD item
├── service/
│   ├── AuthService.java / impl/AuthServiceImpl.java
│   ├── UserService.java / impl/UserServiceImpl.java
│   └── ItemService.java / impl/ItemServiceImpl.java
├── repository/
│   ├── UserRepository.java
│   └── ItemRepository.java
├── entity/
│   ├── User.java
│   ├── Role.java                 (ADMIN, USER)
│   ├── Item.java
│   └── ItemStatus.java           (ACTIVE, INACTIVE)
├── dto/
│   ├── request/  (RegisterRequest, LoginRequest, ItemRequest)
│   └── response/ (ApiResponse, PageResponse, JwtResponse, UserResponse, ItemResponse)
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── UnauthorizedException.java
└── util/
    ├── JwtUtil.java              # generate/parse/validate token
    ├── ResponseUtil.java         # ApiResponse + ResponseEntity helpers
    ├── PaginationUtil.java       # safe Pageable builder
    └── PasswordUtil.java         # BCrypt hash/verify
```

## Setup

1. Buat database MySQL:
   ```sql
   CREATE DATABASE skeleton_db;
   ```
2. Sesuaikan kredensial di `src/main/resources/application.yml`
   (`spring.datasource.username` / `password`) dan ganti `jwt.secret`
   dengan secret production Anda sendiri (minimal 256-bit / 32 karakter).
3. Jalankan:
   ```bash
   mvn spring-boot:run
   ```
   Tabel `users` dan `items` otomatis dibuat oleh Hibernate (`ddl-auto: update`).

## Autentikasi

Semua endpoint di `/api/items/**` dan `/api/users/**` butuh header:
```
Authorization: Bearer <token>
```
`/api/users/**` khusus role `ADMIN`.

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Romi Amirul","email":"romi@example.com","password":"secret123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"romi@example.com","password":"secret123"}'
```
Response berisi `data.token` yang dipakai untuk request selanjutnya.

### Me (current user)
```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <token>"
```

## Item CRUD

| Method | Endpoint            | Keterangan                                   |
|--------|----------------------|-----------------------------------------------|
| POST   | /api/items            | Buat item baru (owner = user login)          |
| GET    | /api/items             | List item (pagination + search + filter)     |
| GET    | /api/items/{id}        | Detail item                                  |
| PUT    | /api/items/{id}        | Update item (hanya owner atau admin)         |
| DELETE | /api/items/{id}        | Hapus item (hanya owner atau admin)          |

Query params untuk `GET /api/items`:
- `page` (default 0), `size` (default 10, max 100)
- `sortBy` (default `id`), `direction` (`asc`/`desc`, default `desc`)
- `search` — cari berdasarkan nama item
- `category` — filter kategori exact match

Contoh:
```bash
curl "http://localhost:8080/api/items?page=0&size=10&search=laptop&category=Elektronik" \
  -H "Authorization: Bearer <token>"
```

## Format Response

Semua response dibungkus `ApiResponse`:
```json
{
  "success": true,
  "message": "Items fetched successfully",
  "data": {
    "content": [ ... ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false
  },
  "timestamp": "2026-08-21T10:00:00Z"
}
```

Error response:
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": { "email": "Email must be valid" },
  "timestamp": "2026-08-21T10:00:00Z"
}
```

## Graceful Shutdown

Diaktifkan lewat `application.yml`:
```yaml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 20s
```
Saat aplikasi menerima sinyal stop (SIGTERM / Ctrl+C), Tomcat berhenti
menerima request baru tapi tetap menyelesaikan request yang sedang berjalan
sampai maksimal 20 detik sebelum context benar-benar ditutup.
`GracefulShutdownConfig` menambahkan logging pada proses ini dan menjadi
tempat untuk membersihkan resource tambahan (thread pool custom, scheduler,
koneksi eksternal, dll) lewat `@PreDestroy`.

## Catatan

- Password tidak pernah dikembalikan di response mana pun — DTO
  (`UserResponse`) tidak memiliki field password sama sekali.
- Role disimpan sebagai enum (`ADMIN`, `USER`) dan dipetakan ke Spring
  Security authority `ROLE_ADMIN` / `ROLE_USER`.
- `ItemRepository` menggunakan `JpaSpecificationExecutor` sehingga filter
  search/category mudah dikembangkan lebih lanjut (misalnya range harga,
  filter status, dll) tanpa mengubah signature method.
