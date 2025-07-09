package com.ngleanhvu.dsa_training_system.security;


import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.*;
import java.security.spec.*;
import java.util.Base64;

public class RsaKeyUtil {

    private static final String PRIVATE_PATH = "keys/private-key.pem";
    private static final String PUBLIC_PATH = "keys/public-key.pem";

    public static RSAPrivateKey getPrivateKey() throws Exception {
        String content = readKeyFromClasspath(PRIVATE_PATH)
                .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] bytes = Base64.getDecoder().decode(content);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static RSAPublicKey getPublicKey() throws Exception {
        String content = readKeyFromClasspath(PUBLIC_PATH)
                .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] bytes = Base64.getDecoder().decode(content);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private static String readKeyFromClasspath(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes());
        }
    }
}
