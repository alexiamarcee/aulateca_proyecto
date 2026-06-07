package com.aulateca.util;

import org.mindrot.jbcrypt.BCrypt;

/** Utilidades para hash y verificación de contraseñas con BCrypt. */
public final class PasswordUtil {

    private static final int ROUNDS = 12;

    private PasswordUtil() {}

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(ROUNDS));
    }

    public static boolean matches(String plain, String stored) {
        if (isHashed(stored)) {
            return BCrypt.checkpw(plain, stored);
        }
        return plain.equals(stored);
    }

    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith("$2");
    }
}
