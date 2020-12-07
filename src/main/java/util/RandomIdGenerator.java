package util;

import java.security.SecureRandom;

public class RandomIdGenerator {

    static SecureRandom secureRandom = new SecureRandom();
    public static String getId()
    {
        return String.valueOf(secureRandom.nextInt());
    }
}
