package com.ngleanhvu.dsa_training_system.redis;

public class RedisKey {
    public static String generateBlackListKey(String jti) {
        return "blacklist:" + jti;
    }

    public static String generateRefreshKey(String authId, String jti)  {
        return "refresh:" + authId + ":jti:" + jti;
    }

    public static String generateEmailConfirmKey(String token) {
        return "email_confirm:" + token;
    }

    public static String generateDifficultiesKey() {
        return "difficulties";
    }

    public static String generateTopicKey() {
        return "topics";
    }

    public static String generateTagKey() {
        return "tags";
    }

    public static String generateForgotPasswordKey(String email) {
        return "forgot_password:" + email;
    }
    public static String generateResetPasswordKey(String otp) {
        return "reset_password:"+otp;
    }
}
