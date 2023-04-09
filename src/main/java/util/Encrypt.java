package util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class Encrypt {

    public static String generateHash() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;
        return DigestUtils.md5Hex(RandomStringUtils.random(length, useLetters, useNumbers));
    }

    // Encrypts a string using a Caesar cipher
    public static String encrypt(String input, int shift) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                c = (char) (base + (c - base + shift) % 26);
            }
            else if (Character.isDigit(c)) {
                c = (char) ('0' + (c - '0' + shift) % 10);
            }
            encrypted.append(c);
        }
        return encrypted.toString();
    }

    // Decrypts a string using a Caesar cipher
    public static String decrypt(String input, int shift) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                c = (char) (base + (c - base - shift + 26) % 26);
            }
            else if (Character.isDigit(c)) {
                c = (char) ('0' + (c - '0' - shift + 10) % 10);
            }
            decrypted.append(c);
        }
        return decrypted.toString();
    }
}
