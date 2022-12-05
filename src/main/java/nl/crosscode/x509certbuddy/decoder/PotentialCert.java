package nl.crosscode.x509certbuddy.decoder;

public class PotentialCert {
    private byte[] potentialCert;
    private int offset;

    public PotentialCert(byte[] potentialCert, int offset) {
        this.potentialCert = potentialCert;
        this.offset = offset;
    }

    public byte[] getPotentialCert() {
        return potentialCert;
    }

    public int getOffset() {
        return offset;
    }
}
