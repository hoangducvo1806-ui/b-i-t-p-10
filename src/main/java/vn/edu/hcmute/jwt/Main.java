package vn.edu.hcmute.jwt;

import com.nimbusds.jwt.JWTClaimsSet;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   BÀI TẬP VÍ DỤ JWT VỚI THƯ VIỆN NIMBUS JOSE + JWT (HCMUTE)");
        System.out.println("===============================================================\n");

        JwtNimbusService jwtService = new JwtNimbusService();

        // 1. Tạo JWT Token
        String username = "nguyen_van_a";
        String role = "ADMIN";
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", "21110001@student.hcmute.edu.vn");
        extraClaims.put("fullname", "Nguyễn Văn A");

        System.out.println("[1] KHỞI TẠO VÀ KÝ JWT TOKEN:");
        System.out.println("   - Username (Subject): " + username);
        System.out.println("   - Role:               " + role);
        System.out.println("   - Fullname:           " + extraClaims.get("fullname"));
        System.out.println("   - Email:              " + extraClaims.get("email"));

        String token = jwtService.generateToken(username, role, extraClaims);
        System.out.println("\n-> Chuỗi Token sinh ra (3 phần Header.Payload.Signature):");
        System.out.println(token + "\n");

        // 2. Xác thực Token hợp lệ
        System.out.println("[2] XÁC THỰC TÍNH HỢP LỆ (VERIFICATION):");
        boolean isValid = jwtService.verifyToken(token);
        System.out.println("   - Kết quả xác thực chữ ký & hạn dùng: " + (isValid ? "HỢP LỆ (VALID) ✅" : "KHÔNG HỢP LỆ ❌") + "\n");

        // 3. Trích xuất Claims từ Token
        if (isValid) {
            try {
                System.out.println("[3] TRÍCH XUẤT THÔNG TIN (CLAIMS) TỪ TOKEN:");
                JWTClaimsSet claims = jwtService.extractClaims(token);
                System.out.println("   - Subject (User):   " + claims.getSubject());
                System.out.println("   - Issuer (iss):     " + claims.getIssuer());
                System.out.println("   - Issued At (iat):  " + claims.getIssueTime());
                System.out.println("   - Expiration (exp): " + claims.getExpirationTime());
                System.out.println("   - Role:             " + claims.getClaim("role"));
                System.out.println("   - Fullname:         " + claims.getClaim("fullname"));
                System.out.println("   - Email:            " + claims.getClaim("email"));
                System.out.println("   - University:       " + claims.getClaim("university") + "\n");
            } catch (Exception e) {
                System.err.println("Lỗi trích xuất claims: " + e.getMessage());
            }
        }

        // 4. Kiểm thử bảo mật: Token bị giả mạo / thay đổi nội dung (Tampered Token)
        System.out.println("[4] KIỂM THỬ BẢO MẬT (TOKEN BỊ GIẢ MẠO CHỮ KÝ / NỘI DUNG):");
        // Thay đổi 1 ký tự trong chuỗi Token
        String tamperedToken = token.substring(0, 20) + "XYZ" + token.substring(23);
        System.out.println("   - Token sau khi hacker can thiệp: " + tamperedToken.substring(0, 35) + "...");
        boolean isTamperedValid = jwtService.verifyToken(tamperedToken);
        System.out.println("   - Kết quả xác thực: " + (isTamperedValid ? "HỢP LỆ ❌ (NGUY HIỂM)" : "TỪ CHỐI BẢO MẬT THÀNH CÔNG ✅ (CHỮ KÝ SAI)"));

        System.out.println("\n===============================================================");
        System.out.println("   HOÀN THÀNH BÀI TẬP VÍ DỤ JWT NIMBUS THÀNH CÔNG!");
        System.out.println("===============================================================");
    }
}
