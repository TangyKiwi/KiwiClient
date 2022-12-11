package com.tangykiwi.kiwiclient.util.auth;

import com.google.common.hash.Hashing;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class LoginEncrypter {

    public static final String PASS_PHRASE = Hashing.sha256().hashString(
            System.getProperty("user.home")
                    + System.getProperty("os.name")
                    + System.getProperty("os.version")
                    + Runtime.getRuntime().availableProcessors()
                    + System.getProperty("os.arch")
                    + System.getProperty("user.name"),
            StandardCharsets.UTF_8).toString();

    private Cipher dcipher;
    private SecretKey key;

    public LoginEncrypter(String passPhrase) {
        try {
            byte[] pass = passPhrase.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            pass = sha.digest(pass);
            pass = Arrays.copyOf(pass, 16); // use only first 128 bit
            key = new SecretKeySpec(pass, "AES");
            dcipher = Cipher.getInstance("AES");
        } catch (Exception e) {
            System.out.println("Error initing login crypter");
        }
    }

    public String encrypt(String data) throws Exception {
        dcipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] utf8EncryptedData = dcipher.doFinal(data.getBytes());
        return new String(Base64.getEncoder().encode(utf8EncryptedData));
    }

    public String decrypt(String base64EncryptedData) throws Exception {
        dcipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = Base64.getDecoder().decode(base64EncryptedData);
        return new String(dcipher.doFinal(decryptedData), StandardCharsets.UTF_8);
    }

}