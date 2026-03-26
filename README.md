# Talep Yönetim API'si

Bu proje, Java Spring Boot ile geliştirilmiş bir talep yönetim API'sidir. Talepler oluşturulabilir, listelenebilir, tekil olarak görüntülenebilir, durumları güncellenebilir ve silinebilir.

## Kullanılan Teknolojiler

- Java 21
- Spring Boot 3.5.12
- Spring Web
- Spring Data JPA
- Spring Validation
- H2 In-Memory Database
- Lombok
- springdoc OpenAPI / Swagger UI
- JUnit 5, Mockito, MockMvc

## Çalıştırma

Gereksinimler:

- Java 21
- Maven Wrapper veya Maven 3.9+

Varsayılan olarak uygulama `8080` portunda çalışır.

API temel adresi:

- `http://localhost:8080/api/requests`

Swagger UI:

- `http://localhost:8080/swagger-ui/index.html`

OpenAPI JSON:

- `http://localhost:8080/v3/api-docs`

H2 Console:

- `http://localhost:8080/h2-console`

Not: Bu proje bir REST API uygulamasıdır. Bu nedenle kök `http://localhost:8080` adresinde ayrıca bir kullanıcı arayüzü veya sayfa dönmesi zorunlu değildir.

### Windows

Önce proje kök klasörüne geçin:

```powershell
cd requestmanagementapi
```

Projeyi başlatmak için:

```powershell
./mvnw.cmd spring-boot:run
```

Testleri çalıştırmak için:

```powershell
./mvnw.cmd clean test
```
Jar dosyasını üretmek için:

./mvnw.cmd clean package
```
Üretilen jar dosyasını çalıştırmak için:

java -jar .\target\requestmanagementapi-0.0.1-SNAPSHOT.jar
```

Eğer `8080` portu doluysa uygulamayı alternatif bir portta başlatabilirsiniz:

```powershell
./mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=18081"
```

Bu durumda adresler şu şekilde olur:

- API: `http://localhost:18081/api/requests`
- Swagger UI: `http://localhost:18081/swagger-ui/index.html`
- H2 Console: `http://localhost:18081/h2-console`

### macOS / Linux

Önce proje kök klasörüne geçin:

```bash
cd requestmanagementapi
```

Projeyi başlatmak için:

```bash
./mvnw spring-boot:run
```

Testleri çalıştırmak için:

```bash
./mvnw clean test
```
Jar dosyasını üretmek için:

./mvnw clean package
```
Üretilen jar dosyasını çalıştırmak için:

java -jar ./target/requestmanagementapi-0.0.1-SNAPSHOT.jar
```

Alternatif port örneği:

```bash
./mvnw "-Dspring-boot.run.arguments=--server.port=18081" spring-boot:run
```

Alternatif port kullanıldığında adresler buna göre değişir. Örneğin API adresi `http://localhost:18081/api/requests`, Swagger UI adresi `http://localhost:18081/swagger-ui/index.html` olur.

## H2 Bağlantı Bilgileri

- JDBC URL: `jdbc:h2:mem:requestdb`
- Username: `sa`
- Password: boş

## Veri Modeli

Her talep şu alanları içerir:

- `id`
- `title`
- `description`
- `requesterName`
- `priority` (`Low`, `Medium`, `High`)
- `status` (`Open`, `InProgress`, `Completed`, `Cancelled`)
- `createdAt`
- `updatedAt`

## İş Kuralları

- `Completed` durumundaki bir talep tekrar `Open` durumuna alınamaz.
- `Cancelled` durumundaki bir talep `Completed` durumuna geçirilemez.
- `title` boş olamaz.
- `description` en fazla 500 karakter olabilir.
- `priority` yalnızca tanımlı değerlerden biri olabilir.

## Endpoint Özeti

### 1. Yeni talep oluştur

- Method: `POST`
- URL: `/api/requests`

Örnek request:

```json
{
  "title": "VPN erişimi",
  "description": "Evden çalışma için VPN erişimi gerekli",
  "requesterName": "Efe",
  "priority": "High"
}
```

Örnek response:

```json
{
  "id": 1,
  "title": "VPN erişimi",
  "description": "Evden çalışma için VPN erişimi gerekli",
  "requesterName": "Efe",
  "priority": "High",
  "status": "Open",
  "createdAt": "2026-03-24T12:00:00",
  "updatedAt": "2026-03-24T12:00:00"
}
```

### 2. Talepleri listele

- Method: `GET`
- URL: `/api/requests`
- Opsiyonel filtreler:
  - `status`
  - `priority`
  - `requesterName`

Örnek filtre kullanımı:

```text
GET /api/requests?status=Open&priority=High&requesterName=efe
```

Örnek response:

```json
[
  {
    "id": 1,
    "title": "VPN erişimi",
    "description": "Evden çalışma için VPN erişimi gerekli",
    "requesterName": "Efe",
    "priority": "High",
    "status": "Open",
    "createdAt": "2026-03-24T12:00:00",
    "updatedAt": "2026-03-24T12:00:00"
  }
]
```

### 3. Tek talep getir

- Method: `GET`
- URL: `/api/requests/{id}`

Örnek response:

```json
{
  "id": 1,
  "title": "VPN erişimi",
  "description": "Evden çalışma için VPN erişimi gerekli",
  "requesterName": "Efe",
  "priority": "High",
  "status": "Open",
  "createdAt": "2026-03-24T12:00:00",
  "updatedAt": "2026-03-24T12:00:00"
}
```

### 4. Talep durumu güncelle

- Method: `PUT`
- URL: `/api/requests/{id}/status`

Örnek request:

```json
{
  "status": "Completed"
}
```

Örnek response:

```json
{
  "id": 1,
  "title": "VPN erişimi",
  "description": "Evden çalışma için VPN erişimi gerekli",
  "requesterName": "Efe",
  "priority": "High",
  "status": "Completed",
  "createdAt": "2026-03-24T12:00:00",
  "updatedAt": "2026-03-24T12:30:00"
}
```

### 5. Talebi sil

- Method: `DELETE`
- URL: `/api/requests/{id}`

Örnek response:

```text
204 No Content
```

## Hata Response Yapısı

Örnek validation hatası:

```json
{
  "timestamp": "2026-03-24T12:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/requests",
  "validationErrors": {
    "title": "Title must not be blank"
  }
}
```

Örnek business rule hatası:

```json
{
  "timestamp": "2026-03-24T12:45:00",
  "status": 409,
  "error": "Conflict",
  "message": "A completed request cannot be moved back to Open",
  "path": "/api/requests/1/status",
  "validationErrors": null
}
```

Örnek enum hatası:

```json
{
  "timestamp": "2026-03-24T12:50:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid value for 'status'. Allowed values: Open, InProgress, Completed, Cancelled",
  "path": "/api/requests?status=Started",
  "validationErrors": null
}
```