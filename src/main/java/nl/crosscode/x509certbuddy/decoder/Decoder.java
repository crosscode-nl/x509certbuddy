package nl.crosscode.x509certbuddy.decoder;

import java.util.Base64;
import java.lang.String;

public class Decoder {
    private final String base64Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private String data = "";
    private Boolean readPadding = false;

    private int count = 0;

    private Boolean done = false;

    private Boolean escapeMode = false;

    private final int originalOffset;

    public Decoder(int originalOffset) {
        this.originalOffset = originalOffset;
    }

    public Boolean add(char c) {
        // TODO: Alternative alphabets
        if (done) return true;
        count++;
        if (escapeMode) {
            String escape = "\\"+c;
            String result = escape.translateEscapes();
            if (result.length()==1) {
                c = result.charAt(0);
            }
            escapeMode = false;
        } else if (c=='\\') {
            escapeMode = true;
            return false;
        }
        if (c==' '||c=='\n'||c=='\r'||c=='\t') {
            return false;
        }

        if (base64Alphabet.indexOf(c)==-1){
            done= true;
            count--;
            return true;
        };
        if (readPadding && c!='=') {
            count--;
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

    public boolean isOffsetInsideRange(int offset) {
       return offset>=originalOffset && offset<originalOffset+count;
    }
}
