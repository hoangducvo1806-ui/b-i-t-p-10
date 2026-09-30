package vn.edu.hcmute.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Service xử lý các thao tác với JSON Web Token (JWT) sử dụng thư viện Nimbus JOSE + JWT.
 * Tuân thủ các tiêu chuẩn RFC 7515 (JWS), RFC 7519 (JWT).
 */
public class JwtNimbusService {

    // Khóa bí mật (Secret Key) cho thuật toán HMAC-SHA256 (HS256).
    // Theo chuẩn RFC 7518, khóa cho HS256 bắt buộc phải có độ dài tối thiểu 256 bits (32 bytes).
    public static final String DEFAULT_SECRET_KEY = "DayLaKhoaBiMatRatDaiVaBaoMatChoBaiTapJWT123456789";

    // Thời gian hiệu lực mặc định của Token: 1 giờ (3600000 ms)
    public static final long DEFAULT_EXPIRATION_TIME = 3600 * 1000L;

    private final String secretKey;
    private final long expirationTime;

    public JwtNimbusService() {
        this(DEFAULT_SECRET_KEY, DEFAULT_EXPIRATION_TIME);
    }

    public JwtNimbusService(String secretKey, long expirationTime) {
        if (secretKey == null || secretKey.getBytes().length < 32) {
            throw new IllegalArgumentException("Secret key phải có độ dài tối thiểu 32 bytes (256 bits) cho thuật toán HS256!");
        }
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    /**
     * 1. TẠO VÀ KÝ JWT (GENERATE TOKEN)
     *
     * @param username Định danh người dùng (subject)
     * @param role     Vai trò người dùng (ADMIN, USER...)
     * @return Chuỗi JWT compact serialization (Header.Payload.Signature)
     */
    public String generateToken(String username, String role) {
        return generateToken(username, role, null);
    }

    /**
     * Tạo JWT với các claims tùy biến bổ sung
     */
    public String generateToken(String username, String role, Map<String, Object> extraClaims) {
        try {
            // Bước 1: Khởi tạo JWS Header (Thuật toán HS256, loại token là JWT)
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                    .type(JOSEObjectType.JWT)
                    .build();

            // Bước 2: Thiết lập Payload (Claims)
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + this.expirationTime);

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(username)                          // sub
                    .issuer("hcmute.edu.vn")                    // iss
                    .issueTime(now)                             // iat
                    .expirationTime(expiryDate)                 // exp
                    .claim("role", role)                        // Custom claim: role
                    .claim("university", "HCMUTE - UTE");       // Custom claim: trường

            if (extraClaims != null) {
                for (Map.Entry<String, Object> entry : extraClaims.entrySet()) {
                    claimsBuilder.claim(entry.getKey(), entry.getValue());
                }
            }

            JWTClaimsSet claimsSet = claimsBuilder.build();

            // Bước 3: Đóng gói Header và Claims vào SignedJWT
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // Bước 4: Tạo bộ ký số HMAC với khóa bí mật
            JWSSigner signer = new MACSigner(this.secretKey.getBytes());

            // Bước 5: Ký số
            signedJWT.sign(signer);

            // Bước 6: Trả về chuỗi JWT đã tuần tự hóa (Compact Serialization)
            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi trong quá trình tạo và ký JWT: " + e.getMessage(), e);
        }
    }

    /**
     * 2. XÁC THỰC TÍNH HỢP LỆ CỦA TOKEN (VERIFY TOKEN)
     *
     * @param token Chuỗi JWT
     * @return true nếu chữ ký hợp lệ và token chưa hết hạn; false nếu giả mạo hoặc hết hạn
     */
    public boolean verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Xác thực chữ ký với secret key
            JWSVerifier verifier = new MACVerifier(this.secretKey.getBytes());
            boolean isSignatureValid = signedJWT.verify(verifier);
            if (!isSignatureValid) {
                return false;
            }

            // Kiểm tra thời hạn hết hạn (Expiration Time)
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expirationTime == null || !new Date().after(expirationTime);

        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    /**
     * 3. TRÍCH XUẤT CÁC CLAIMS TỪ TOKEN (EXTRACT CLAIMS)
     *
     * @param token Chuỗi JWT
     * @return Đối tượng JWTClaimsSet chứa toàn bộ thông tin
     */
    public JWTClaimsSet extractClaims(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet();
    }

    /**
     * Trích xuất Username (Subject)
     */
    public String extractUsername(String token) throws ParseException {
        return extractClaims(token).getSubject();
    }

    /**
     * Trích xuất Role
     */
    public String extractRole(String token) throws ParseException {
        Object role = extractClaims(token).getClaim("role");
        return role != null ? role.toString() : null;
    }

    /**
     * Kiểm tra Token đã hết hạn chưa
     */
    public boolean isTokenExpired(String token) {
        try {
            Date exp = extractClaims(token).getExpirationTime();
            return exp != null && new Date().after(exp);
        } catch (ParseException e) {
            return true;
        }
    }
}
