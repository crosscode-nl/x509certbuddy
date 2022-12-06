package nl.crosscode.x509certbuddy.decoder;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Base64;

public class Decoder {
    private final String base64Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private String data = "";
    private Boolean readPadding = false;
    private Boolean done = false;

    private Boolean escapeMode = false;

    private int originalOffset;

    public Decoder(int originalOffset) {
        this.originalOffset = originalOffset;
    }

    public Boolean add(char c) {
        // TODO: Alternative alphabets
        if (done) return true;
        if (escapeMode) {
            String escape = "\\"+c;
            String result = StringEscapeUtils.unescapeJava(escape);
            if (result.length()==1) {
                c = result.charAt(0);
            }
            escapeMode = false;
        } else if (c=='\\') {
            escapeMode = true;
            return false;
        }
        if (c==' '||c=='\n'||c=='\r'||c=='\t') return false;

        if (base64Alphabet.indexOf(c)==-1){
            done= true;
            return true;
        };
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

    public int getOriginalOffset() {
        return originalOffset;
    }
}
