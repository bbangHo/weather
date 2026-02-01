package org.pknu.weather.common;

import org.junit.jupiter.api.Test;
import org.pknu.weather.member.entity.Member;
import org.pknu.weather.member.repository.MemberRepository;
import org.pknu.weather.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@SpringBootTest
@Transactional(readOnly = true)
class PerfUserJwtTxtExportTest {

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    void perfUserJwtTxtExport() throws Exception {

        Path outputPath = Path.of("src/test/resources/perf_users.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

            // ===== header =====
            writer.write("email\tnickname\ttoken\tlatitude\tlongitude");
            writer.newLine();

            for (int i = 1, id = 1171; i <= 500; i++, id++) {
                String email = "perf_user" + i + "@test.com";
                String token = generateJwtToken(jwtUtil, id, email); // 순수 JWT

                writer.write(
                        email + "\t" +
                                "perf_user" + i + "\t" +
                                token + "\t" +
                                "\t" +
                                "\t"
                );
                writer.newLine();
            }
        }

        System.out.println("✅ perf_users.txt 생성 완료 (엑셀 붙여넣기용)");
    }

    public static String generateJwtToken(JWTUtil jwtUtil, int id, String email) {
        Map<String, Object> claims = Map.of(
                "id", id,
                "email", email
        );
        return "Bearer " + jwtUtil.generateToken(claims, 1);
    }
}
