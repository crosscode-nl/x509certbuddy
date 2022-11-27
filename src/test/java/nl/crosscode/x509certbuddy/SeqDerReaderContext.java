package nl.crosscode.x509certbuddy;

public class SeqDerReaderContext {
    private String input;

    private final SeqDerReader seqDerReader = new SeqDerReader();

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public SeqDerReader getSeqDerReader() {
        return seqDerReader;
    }
}
