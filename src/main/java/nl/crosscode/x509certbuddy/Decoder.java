package nl.crosscode.x509certbuddy;

import java.util.Arrays;
import java.util.Base64;

public class Decoder {
    private final String base64Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private String data = "";
    private Boolean readPadding = false;
    private Boolean done = false;

    public Boolean add(char c) {
        // TODO: Alternative alphabets
        if (done) return true;
        if (base64Alphabet.indexOf(c)==-1) return false;
        if (readPadding && c!='=') {
            done = true;
            return true;
        }
        if (c=='=') readPadding = true;
        data+=c;
        return false;
    }

    public byte[] tryDecode() {
        return Base64.getDecoder().decode(data);
    }

    Boolean isDone() {
        return done;
    }
}
