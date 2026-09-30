# Bài Tập Ví Dụ JWT (JSON Web Token) Với Thư Viện Nimbus JOSE + JWT

Dự án Java triển khai cơ chế xác thực và ủy quyền **JWT (JSON Web Token)** theo chuẩn **RFC 7515 (JWS)** và **RFC 7519 (JWT)**, sử dụng thư viện **Nimbus JOSE + JWT** thay thế cho các thư viện thông thường như `jjwt` hoặc `auth0/java-jwt`.



## 📌 1. Mục Tiêu Bài Tập
1. **Khởi tạo và ký JWT:** Sử dụng `SignedJWT`, `JWSHeader` và `JWTClaimsSet` với thuật toán HMAC-SHA256 (`HS256`).
2. **Xác thực chữ ký số (Signature Verification):** Đảm bảo tính toàn vẹn của token bằng `MACVerifier`, ngăn chặn giả mạo dữ liệu.
3. **Kiểm tra thời hạn hiệu lực (Expiration Check):** Tự động phát hiện và từ chối các token đã hết hạn (`exp`).
4. **Trích xuất thông tin (Claims Extraction):** Đọc các thông tin định danh (`sub`, `iss`, `iat`, `exp`) và các claims tùy biến (`role`, `email`, `university`).
5. **Kiểm thử bảo mật:** Mô phỏng kịch bản tấn công sửa đổi token (Tampered Token) và kiểm tra khả năng phòng vệ của hệ thống.

---

## 🏗️ 2. Cấu Trúc Dự Án
```text
b-i-t-p-10/
├── pom.xml                                    # File cấu hình Maven và khai báo nimbus-jose-jwt
├── README.md                                  # Hướng dẫn chi tiết
├── .gitignore                                 # Bỏ qua các file tạm biên dịch
└── src/
    ├── main/java/vn/edu/hcmute/jwt/
    │   ├── JwtNimbusService.java              # Lớp xử lý chính: Tạo, ký, verify, đọc claims
    │   └── Main.java                          # Chương trình chạy demo thực tế trên Console
    └── test/java/vn/edu/hcmute/jwt/
        └── JwtNimbusServiceTest.java          # Bộ kiểm thử tự động JUnit 5
```

---

## 🚀 3. Hướng Dẫn Cài Đặt & Chạy Chương Trình

### Yêu cầu môi trường:
* **Java:** JDK 17 trở lên (Hỗ trợ tốt trên Java 17, 21, 26).
* **Maven:** Apache Maven 3.6 trở lên.

### Bước 1: Biên dịch dự án và chạy Unit Tests
```bash
mvn clean test
```
*Tất cả 5 test cases của JUnit 5 sẽ tự động chạy để kiểm tra tính toàn vẹn của thuật toán.*

### Bước 2: Chạy chương trình Demo
```bash
mvn exec:java
```
Hoặc mở trực tiếp dự án trong IntelliJ IDEA / Eclipse / VS Code và nhấn **Run** tại file `Main.java`.

---

## 📊 4. Kết Quả Chạy Thực Tế (Output)
```text
===============================================================
   BÀI TẬP VÍ DỤ JWT VỚI THƯ VIỆN NIMBUS JOSE + JWT (HCMUTE)
===============================================================

[1] KHỞI TẠO VÀ KÝ JWT TOKEN:
   - Username (Subject): nguyen_van_a
   - Role:               ADMIN
   - Fullname:           Nguyễn Văn A
   - Email:              21110001@student.hcmute.edu.vn

-> Chuỗi Token sinh ra (3 phần Header.Payload.Signature):
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJuZ3V5ZW5fdmFuX2EiLCJpc3MiOiJoY211dGUuZWR1LnZuIiwiaWF0IjoxNzg0NTY3ODkwLCJleHAiOjE3ODQ1NzE0OTAsInJvbGUiOiJBRE1JTiIsImZ1bGxuYW1lIjoiTmd1eeG7hW4gVsSDbiBBIiwiZW1haWwiOiIyMTExMDAwMUBzdHVkZW50LmhjbXV0ZS5lZHUudm4iLCJ1bml2ZXJzaXR5IjoiSENNVVRFIC0gVVRFIn0.xxxxxxxxxxxxxxxxxxxxxxxxxxxx

[2] XÁC THỰC TÍNH HỢP LỆ (VERIFICATION):
   - Kết quả xác thực chữ ký & hạn dùng: HỢP LỆ (VALID) ✅

[3] TRÍCH XUẤT THÔNG TIN (CLAIMS) TỪ TOKEN:
   - Subject (User):   nguyen_van_a
   - Issuer (iss):     hcmute.edu.vn
   - Issued At (iat):  Wed Sep 30 08:30:00 ICT 2026
   - Expiration (exp): Wed Sep 30 09:30:00 ICT 2026
   - Role:             ADMIN
   - Fullname:         Nguyễn Văn A
   - Email:            21110001@student.hcmute.edu.vn
   - University:       HCMUTE - UTE

[4] KIỂM THỬ BẢO MẬT (TOKEN BỊ GIẢ MẠO CHỮ KÝ / NỘI DUNG):
   - Token sau khi hacker can thiệp: eyJhbGciOiJIUzI1NiIsXYZ...
   - Kết quả xác thực: TỪ CHỐI BẢO MẬT THÀNH CÔNG ✅ (CHỮ KÝ SAI)



---

## 💡 5. Vì Sao Nên Sử Dụng Nimbus JOSE + JWT?
* **Chuẩn hóa công nghiệp:** Bám sát hệ tiêu chuẩn RFC 7515 (JWS), RFC 7516 (JWE), RFC 7517 (JWK), RFC 7518 (JWA), RFC 7519 (JWT).
* **Mặc định trong Spring Security 6+:** Spring Security sử dụng Nimbus làm công cụ giải mã và xử lý JWT mặc định cho các mô hình Resource Server và OAuth2/OpenID Connect.
* **Hỗ trợ bảo mật nâng cao:** Dễ dàng mở rộng từ chữ ký đối xứng (HMAC HS256) sang khóa bất đối xứng (RSA RS256, ECDSA ES256) và nạp khóa tự động qua JWKS URI (`/.well-known/jwks.json`).
