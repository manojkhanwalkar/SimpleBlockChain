package util;

import java.security.SecureRandom;

public class RandomIdGenerator {

    static SecureRandom secureRandom = new SecureRandom();
    public static int getId()
    {
        return secureRandom.nextInt();
    }
}
