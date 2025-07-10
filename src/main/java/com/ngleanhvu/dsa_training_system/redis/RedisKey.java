package com.ngleanhvu.dsa_training_system.redis;

public class RedisKey {
    public static String generateBlackListKey(String jti) {
        return "blacklist:" + jti;
    }

    public static String generateRefreshKey(String authId, String jti)  {
        return "refresh:" + authId + ":jti:" + jti;
    }
}
