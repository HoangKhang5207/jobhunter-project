# 📌 JobHunter

> Ứng dụng RESTful API giúp người dùng tìm và quản lý công việc, xây dựng bằng Java Spring Framework và bảo mật với JWT.

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.0-brightgreen)

## 📑 Table of Contents

* [🔍 Giới thiệu](#-giới-thiệu)
* [✨ Tính năng](#-tính-năng)
* [🏗️ Kiến trúc & Công nghệ](#️-kiến-trúc--công-nghệ)
* [🚀 Cài đặt & Chạy thử](#-cài-đặt--chạy-thử)
* [🎬 Demo & Ví dụ sử dụng](#-demo--ví-dụ-sử-dụng)
* [🤝 Contributing](#-contributing)
* [📬 Liên hệ](#-liên-hệ)

---

## 🔍 Giới thiệu

JobHunter là RESTful API backend, cho phép:

* Đăng ký, đăng nhập và phân quyền người dùng qua JWT.
* Tạo, sửa, xóa và tìm kiếm việc làm (CRUD).
* Quản lý hồ sơ ứng viên và lưu công việc yêu thích.

Ứng dụng hướng tới các developer muốn tích hợp nhanh vào hệ thống tìm việc hoặc làm nền cho mobile app.

---

## ✨ Tính năng

* **Auth & Authorization**: Đăng ký, đăng nhập, refresh token, phân quyền ROLE\_USER / ROLE\_ADMIN.
* **Job Management**: CRUD công việc với các trường: title, description, location, salary, company,…
* **Profile**: Ứng viên có thể tạo hồ sơ, cập nhật thông tin cá nhân.
* **Search & Filter**: Tìm kiếm công việc theo keyword, location, mức lương.
* **Favorites**: Lưu công việc yêu thích để truy cập nhanh.

---

## 🏗️ Kiến trúc & Công nghệ

| Thành phần     | Công nghệ                           |
| -------------- | ----------------------------------- |
| **Backend**    | Java 21, Spring Boot 3.4            |
| **Security**   | Spring Security, JWT                |
| **Database**   | MySQL                               |
| **Build**      | Gradle                              |

---

## 🚀 Cài đặt & Chạy thử

1. **Clone repository**

   ```bash
   git clone https://github.com/HoangKhang5207/jobhunter-project.git
   cd jobhunter-project
   ```

---

## 🎬 Demo & Ví dụ sử dụng

### 1. Đăng ký / Đăng nhập

```bash
# Đăng ký
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123","email":"a@b.com"}'

# Đăng nhập
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123"}'
```

### 2. Tạo công việc (Admin)

```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Java Developer","description":"...","location":"HCM","salary":"15M"}'
```

### 3. Tìm việc

```
GET http://localhost:8080/api/jobs?keyword=Java&location=HCM
```

---

## 🧪 Testing

Chạy toàn bộ unit & integration tests:

```bash
mvn test
```

* **Framework**: JUnit 5, Mockito
* **Coverage**: 85%+ (mục tiêu)

---

## 🤝 Contributing

1. Fork repo
2. Tạo nhánh feature:

   ```bash
   git checkout -b feature/YourFeature
   ```
3. Commit & push
4. Mở Pull Request mô tả rõ thay đổi

---

## 📬 Liên hệ

* **Email**: [hoangkhang5207@example.com](mailto:hoangkhang16112003@gmail.com)
* **LinkedIn**: [https://www.linkedin.com/in/khang-nguyen-2k3](https://www.linkedin.com/in/khang-nguyen-2k3/)
* **GitHub**: [https://github.com/HoangKhang5207](https://github.com/HoangKhang5207)

---

## 🚀 Cấu hình environment & Chạy ứng dụng

2. **Cấu hình environment**
   Tạo file `application.properties` trong `src/main/resources/` với các biến sau:

   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/jobhunter
   jwt.secret=YourJWTSecretKey
   jwt.expiration=3600000
   ```

3. **Cài dependencies & build**

   ```bash
   mvn clean install
   ```

4. **Chạy ứng dụng**

   ```bash
   mvn spring-boot:run
   ```

   \=> API chạy mặc định trên `http://localhost:8080`

---
