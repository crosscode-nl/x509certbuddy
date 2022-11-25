import org.jsoup.nodes.BooleanAttribute;

import java.nio.ByteBuffer;


public class SeqDerReader {

    enum Mode {
        TAG,
        FIRST_LENGTH,
        EXTENDED_LENGTH,
        VALUE,
        DONE,
        ERROR
    }

    Mode mode = Mode.TAG;

    private byte tag;
    private int lengthBytes = 0;
    private byte[] length;
    private int lengthIndex = 0;
    private int valueLength = 0;
    private byte[] value;
    private int valueIndex = 0;

    public void read(byte b) {
        switch (mode) {
            case TAG: handleTag(b); return;
            case FIRST_LENGTH: handleFirstLength(b); return;
            case EXTENDED_LENGTH: handleExtendedLength(b); return;
            case VALUE: handleValue(b);
        }
    }

    private void handleValue(byte b) {
        value[valueIndex++] =b;
        if (valueIndex==valueLength) {
            mode = Mode.DONE;
        }
    }

    private void handleExtendedLength(byte b) {
        length[lengthIndex++] = b;
        if (lengthIndex==lengthBytes) {
            mode = Mode.VALUE;
            valueLength = ByteBuffer.wrap(length).getInt();
            value = new byte[valueLength];
        }
    }

    private void handleTag(byte b) {
        tag = b;
        mode=Mode.FIRST_LENGTH;
    }

    private void handleFirstLength(byte b) {
        if (b <=0x7F) {
            valueLength = b;
            mode= Mode.VALUE;
            if (valueLength==0) {
                mode = Mode.ERROR;
                return;
            }
            value = new byte[valueLength];
            return;
        }
        lengthBytes = b & 0x7F;
        if (lengthBytes==0) {
            mode = Mode.ERROR;
            return;
        }
        mode=Mode.EXTENDED_LENGTH;
        length = new byte[lengthBytes];
    }

    public void setEof() {
        if (mode!=Mode.DONE&&mode!=Mode.ERROR) {
            mode = Mode.ERROR;
        }
    }

    public Boolean isDone() {
        return mode==Mode.DONE||mode==Mode.ERROR;
    }

    public Boolean isError() {
        return mode==Mode.ERROR;
    }
}
