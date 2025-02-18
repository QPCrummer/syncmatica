package ch.endte.syncmatica.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class SyncmaticaUtil {

    static final int[] ILLEGAL_CHARS = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};
    static final String ILLEGAL_PATTERNS = "(^(con|prn|aux|nul|com[0-9]|lpt[0-9])(\\..*)?$)|(^\\.\\.*$)";

    private SyncmaticaUtil() {
        // NOT USED
    }

    public static UUID createChecksum(final InputStream fis) throws NoSuchAlgorithmException, IOException {
        // source StackOverflow
        final byte[] buffer = new byte[4096]; // 4096 is the most common cluster size
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                messageDigest.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return UUID.nameUUIDFromBytes(messageDigest.digest());
    }
    // taken from stackoverflow

    static {
        Arrays.sort(ILLEGAL_CHARS);
    }

    public static String sanitizeFileName(final String badFileName) {
        final StringBuilder sanitized = new StringBuilder();
        final int len = badFileName.codePointCount(0, badFileName.length());

        for (int i = 0; i < len; i++) {
            final int c = badFileName.codePointAt(i);
            if (Arrays.binarySearch(ILLEGAL_CHARS, c) < 0) {
                sanitized.appendCodePoint(c);
                if (sanitized.length() == 255) { //make sure .length stays below 255
                    break;
                }
            }
        }
        // ^ sanitizes unique characters
        // v sanitizes entire patterns
        return sanitized.toString().replaceAll(ILLEGAL_PATTERNS, "_");
    }
}
