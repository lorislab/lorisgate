package org.lorislab.lorisgate.domain.utils;

import java.util.Base64;

public class Base64Utils {

    private Base64Utils() {
    }

    public static String base64Url(byte[] b) {
        if (b.length > 1 && b[0] == 0) {
            byte[] c = new byte[b.length - 1];
            System.arraycopy(b, 1, c, 0, c.length);
            b = c;
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}
