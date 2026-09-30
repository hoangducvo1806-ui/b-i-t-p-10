package vn.edu.hcmute.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class JwtNimbusServiceTest {

    private JwtNimbusService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtNimbusService();
    }

    @Test
    @DisplayName("Tạo Token thành công và có đủ 3 phần phân cách bởi dấu chấm")
    void testGenerateToken() {
        String token = jwtService.generateToken("user123", "STUDENT");
        assertNotNull(token);
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT phải gồm 3 phần: Header, Payload, Signature");
    }

    @Test
    @DisplayName("Xác thực Token hợp lệ trả về true")
    void testVerifyValidToken() {
        String token = jwtService.generateToken("admin", "ADMIN");
        boolean isValid = jwtService.verifyToken(token);
        assertTrue(isValid, "Token vừa tạo với khóa bí mật hợp lệ phải được verify thành công");
    }

    @Test
    @DisplayName("Xác thực Token bị sửa đổi phải trả về false")
    void testVerifyTamperedToken() {
        String token = jwtService.generateToken("admin", "ADMIN");
        String tampered = token.substring(0, 10) + "ABC" + token.substring(13);
        boolean isValid = jwtService.verifyToken(tampered);
        assertFalse(isValid, "Token bị can thiệp chữ ký phải bị từ chối");
    }

    @Test
    @DisplayName("Trích xuất thông tin Subject và Role chính xác từ Token")
    void testExtractClaims() throws ParseException {
        String token = jwtService.generateToken("student_hcmute", "STUDENT");
        JWTClaimsSet claims = jwtService.extractClaims(token);

        assertEquals("student_hcmute", claims.getSubject());
        assertEquals("STUDENT", claims.getClaim("role"));
        assertEquals("hcmute.edu.vn", claims.getIssuer());
    }

    @Test
    @DisplayName("Token hết hạn phải trả về verify false")
    void testExpiredToken() {
        // Tạo service với thời gian hết hạn là -1000ms (đã quá hạn)
        JwtNimbusService expiredService = new JwtNimbusService(JwtNimbusService.DEFAULT_SECRET_KEY, -1000L);
        String expiredToken = expiredService.generateToken("old_user", "USER");

        assertFalse(expiredService.verifyToken(expiredToken), "Token đã hết hạn không được xem là hợp lệ");
        assertTrue(expiredService.isTokenExpired(expiredToken), "isTokenExpired phải trả về true");
    }
}
