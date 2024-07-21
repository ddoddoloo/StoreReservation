package com.example.storereservation.global.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordUtils {

    /**
     * 두 비밀번호가 동일한지 확인
     * @param pw1 첫 번째 비밀번호
     * @param pw2 두 번째 비밀번호
     * @return 두 비밀번호가 동일하면 true, 그렇지 않으면 false
     */
    public static boolean validatePlainTextPassword(String pw1, String pw2) {
        return pw1 != null && pw1.equals(pw2);
    }

    /**
     * 평문 비밀번호와 해시된 비밀번호가 동일한지 확인
     * @param plainText 평문 비밀번호
     * @param hashed 해시된 비밀번호
     * @return 비밀번호가 동일하면 true, 그렇지 않으면 false
     */
    public static boolean equals(String plainText, String hashed) {
        if (plainText == null || plainText.isEmpty()) {
            return false;
        }
        if (hashed == null || hashed.isEmpty()) {
            return false;
        }

        return BCrypt.checkpw(plainText, hashed);
    }

    /**
     * 평문 비밀번호를 해시화
     * @param plainText 평문 비밀번호
     * @return 해시된 비밀번호
     */
    public static String encPassword(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return "";
        }
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }
}
