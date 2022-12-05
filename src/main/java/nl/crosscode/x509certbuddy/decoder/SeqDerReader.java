package nl.crosscode.x509certbuddy.decoder;

import java.math.BigInteger;


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
    private byte firstLength;

    private int lengthBytes = 0;
    private byte[] length;
    private int lengthIndex = 0;
    private int valueLength = 0;
    private byte[] value;
    private int valueIndex = 0;

    private byte[] result;

    public void read(byte b) throws Exception {
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

    private void handleExtendedLength(byte b) throws Exception {
        length[lengthIndex++] = b;
        if (lengthBytes>2) {
            throw new Exception("Not a cert"); // Not sure, but use this as an optimisation for now.
        }
        if (lengthIndex==lengthBytes) {
            mode = Mode.VALUE;
            valueLength = new BigInteger(length).intValue();
            value = new byte[valueLength];
        }
    }

    private void handleTag(byte b) {
        tag = b;
        mode=Mode.FIRST_LENGTH;
    }

    private void handleFirstLength(byte b) {
        firstLength = b;
        if (b==0) {
            mode = Mode.ERROR;
            return;
        }
        if (b >= 0) {
            valueLength = b;
            mode= Mode.VALUE;
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

    public byte[] getResult() {
        if (mode!=Mode.DONE) {
            return null;
        }
        if (result==null) {
            int bytes = 2 + lengthBytes + valueLength;
            result = new byte[bytes];
            result[0] = tag;
            result[1] = firstLength;
            if (length!=null && length.length>0) {
                System.arraycopy(length,0,result,2, lengthBytes);
            }
            System.arraycopy(value,0,result,2+lengthBytes,valueLength);
        }
        return result;
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
