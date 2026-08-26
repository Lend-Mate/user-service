# User Service

HemenKirala kullanıcı kayıt, giriş, JWT üretimi ve kullanıcı yönetimi işlemlerini sağlayan Spring Boot servisi.

---

## İçindekiler
- [Genel Bakış](#genel-bakış)
- [Mimari](#mimari)
- [Teknolojiler](#teknolojiler)
- [Veritabanı](#veritabanı)
- [API Endpoints](#api-endpoints)
- [Servisler Arası İletişim](#servisler-arası-i̇letişim)
- [Kurulum](#kurulum)
- [Ortam Değişkenleri](#ortam-değişkenleri)
- [Testler](#testler)

---

## Genel Bakış

`user-service`, LendMate platformundaki kullanıcı hesaplarının yaşam döngüsünü yönetir.

- Kullanıcı kaydı ve e-posta/şifre ile giriş
- 30 dakika geçerli JWT üretimi
- Rol ve permission tabanlı endpoint yetkilendirmesi
- Kullanıcı profil ve kiralama geçmişi endpoint'leri için API yüzeyi
- Admin kullanıcı yönetimi endpoint'leri
- PostgreSQL kalıcılığı ve Flyway migration'ları

Servis varsayılan olarak `8081` portunda çalışır. Profil, kiralama ve admin endpoint'lerinin bazıları şu an iskelet implementasyondur; gerçek profil/kiralama işlemleri henüz tamamlanmamıştır.

---

## Mimari

### Katmanlar

- **Controller:** HTTP isteklerini karşılar (`AuthController`, `UserController`).
- **Service:** Kullanıcı işlemleri ve JWT üretimini yürütür.
- **Repository:** Spring Data JPA ile `users` tablosuna erişir.
- **Entity/DTO/Mapper:** Veritabanı modeli, istek/yanıt modelleri ve dönüşümler.
- **Security:** Stateless Spring Security, gateway header doğrulaması ve permission kontrolü.
- **Migration:** Flyway ile veritabanı şema versiyonlama.

### Klasör Yapısı

```text
src/main/java/com/lendmate/userservice/
├── config/          # CORS ve uygulama konfigürasyonu
├── controller/      # Auth ve kullanıcı HTTP endpoint'leri
├── dto/             # Request/response modelleri
├── entity/          # User, Role ve Permission
├── mapper/          # Entity-DTO dönüşümleri
├── repository/      # JPA repository'leri
├── security/        # SecurityConfig, gateway filter, Swagger ayarları
└── service/         # Servis arayüzleri ve implementasyonları
```

---

## Teknolojiler

| Teknoloji | Versiyon | Kullanım Amacı |
|---|---|---|
| Java | 21 | Uygulama çalışma zamanı |
| Spring Boot | 3.5.14 | Uygulama çatısı |
| Spring Web | Spring Boot ile yönetilir | REST API |
| Spring Security | Spring Boot ile yönetilir | Kimlik doğrulama ve yetkilendirme |
| Spring Data JPA / Hibernate | Spring Boot ile yönetilir | ORM ve veri erişimi |
| PostgreSQL | Runtime | Üretim veritabanı |
| Flyway | Spring Boot ile yönetilir | Şema migration'ları |
| JJWT | 0.11.5 | JWT üretimi ve doğrulaması |
| Springdoc OpenAPI | 2.8.16 | Swagger UI ve OpenAPI dokümantasyonu |
| Spring Kafka | Spring Boot ile yönetilir | Bağımlılık mevcut; bu serviste event akışı tanımlı değil |
| OpenTelemetry / Actuator | 2.6.0 / Spring Boot ile yönetilir | Gözlemlenebilirlik ve health endpoint'leri |

---

## Veritabanı

### Tablolar

#### `users`

| Kolon | Açıklama |
|---|---|
| `id` | Birincil anahtar |
| `first_name`, `last_name` | Kullanıcı adı bilgileri |
| `username` | Benzersiz kullanıcı adı |
| `email` | Benzersiz e-posta adresi; girişte kullanılır |
| `password` | BCrypt ile hash'lenmiş şifre |
| `role` | Kullanıcı rolü |
| `phone` | Opsiyonel, benzersiz telefon numarası |
| `profile_image` | Opsiyonel profil görseli |
| `last_login_at` | Son giriş zamanı |
| `is_deleted`, `is_verified` | Soft delete ve doğrulama durumları |
| `created_at`, `updated_at` | Kayıt zamanları |

Migration geçmişindeki son role constraint `ADMIN` ve `USER` değerlerini kabul eder. `V1` dosyasındaki `LENDER` ve `CUSTOMER` değerleri `V2` ile değiştirilmiştir.

---

## API Endpoints

### Auth
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `GET` | `/auth/health` | Servis sağlık kontrolü | Yok |
| `POST` | `/auth/register` | Yeni kullanıcı oluşturur; şifre BCrypt ile hash'lenir | Yok |
| `POST` | `/auth/login` | E-posta ve şifreyi doğrular, JWT döner | Yok |

### Kullanıcı
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `GET` | `/user/profile` | Kendi profilini getirir; mevcut yanıt placeholder'dır | `PROFILE_READ` |
| `PUT` | `/user/profile` | Kendi profilini günceller; mevcut yanıt placeholder'dır | `PROFILE_WRITE` |
| `DELETE` | `/user/profile` | Kendi hesabını siler; mevcut yanıt placeholder'dır | `PROFILE_DELETE` |
| `GET` | `/user/rentals` | Kullanıcının kiralama geçmişi; mevcut yanıt placeholder'dır | `RENTAL_READ` |

### Admin ve internal
| Method | Endpoint | Açıklama | Auth |
|---|---|---|---|
| `GET` | `/user/admin/users` | Tüm kullanıcıları listeler; mevcut yanıt placeholder'dır | `USER_READ` |
| `GET` | `/user/admin/users/{id}` | ID ile kullanıcı getirir | `USER_READ` |
| `DELETE` | `/user/admin/users/{id}` | Kullanıcıyı soft delete yapar | `USER_DELETE` |
| `PUT` | `/user/admin/users/{id}/role` | Kullanıcı rolünü günceller; mevcut yanıt placeholder'dır | `ADMIN_PANEL` |
| `GET` | `/user/internal/{id}` | Kullanıcı detayını internal servisler için döner | Oturum gerekli |
| `GET` | `/user/internal/{id}/email` | ID ile e-posta adresi getirir | Oturum gerekli |

Swagger UI: `/swagger-ui/index.html`  
OpenAPI JSON: `/v3/api-docs`

---

## Servisler Arası İletişim

### Feign Client (Senkron)

Bu projede Feign client tanımı bulunmuyor. Kiralama geçmişi endpoint'i için Rental Service çağrısı henüz implement edilmemiştir.

### Kafka Events (Asenkron)

Kafka bağımlılığı `pom.xml` içinde bulunuyor ancak bu serviste producer, consumer veya event topic tanımı bulunmuyor.

### Gateway iletişimi

`GatewayAuthFilter`, gateway tarafından gönderilen `X-User-Role` ve `X-User-Email` header'larını okur. Rol geçerli olduğunda role ait permission'ları Spring Security context'ine ekler. `gateway.security.enabled=false` ayarıyla tüm permission'lar verilebilir; varsayılan değer `true`'dur.

---

## Kurulum

### Gereksinimler

- Java 21
- Maven 3.9+ veya projeyle birlikte gelen Maven Wrapper
- PostgreSQL
- Docker ve Docker Compose (container ile çalıştırma için)
- Stage/prod profillerinde erişilebilir Spring Cloud Config Server
### Çalıştırma

Yerel Maven çalıştırması:

```bash
./mvnw spring-boot:run
```

Docker ile local compose:

```bash
docker network create lendmate-net
docker compose -f docker-compose-local.yml up --build
```

Local compose dosyası PostgreSQL'in `postgres` hostname'iyle aynı external Docker network üzerinde çalışmasını bekler. Üretim compose dosyası `DB_PASSWORD` ve ortak LendMate servislerine erişim gerektirir.

---

## Ortam Değişkenleri

| Değişken | Açıklama | Örnek |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Aktif Spring profili | `dev` |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC bağlantısı | `jdbc:postgresql://postgres:5432/user_service_db` |
| `SPRING_DATASOURCE_USERNAME` | Veritabanı kullanıcı adı | `lendmate` |
| `SPRING_DATASOURCE_PASSWORD` | Veritabanı şifresi | `********` |
| `CONFIG_SERVER_URL` | Spring Cloud Config Server adresi | `http://config-server:8888` |
| `gateway.security.enabled` | Gateway header güvenliğini açar/kapatır | `true` |
| `JAVA_OPTS` | JVM seçenekleri | `-XX:MaxRAMPercentage=60.0` |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | OpenTelemetry collector adresi | `http://otel-collector:4318` |
| `OTEL_SERVICE_NAME` | Telemetry servis adı | `user-service` |

---

## Testler

Testleri çalıştırmak için:

```bash
./mvnw test
```

Mevcut testler `JwtServiceImpl` için token üretimi, claim'ler, geçerlilik ve süresi dolmuş token davranışını; `UserServiceImpl` için kullanıcı oluşturma akışını kapsar. Test profili `src/test/resources/application-test.yml` üzerinden çalışır.
