package nl.crosscode.x509certbuddy;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;


public class SeqDerReaderSteps {

    private final SeqDerReaderContext context;

    public SeqDerReaderSteps(SeqDerReaderContext context) {
        this.context = context;
    }


    @Given("the following binary data in base64: {string}")
    public void the_following_binary_data_in_base64(String base64x509) {
        context.setInput(base64x509);
        // Write code here that turns the phrase above into concrete actionbelly.features
    }

    @When("data is read into SeqDerReader")
    public void dataIsReadIntoSeqDerReader() {
        byte[] input = Base64.getDecoder().decode(context.getInput());
        for (byte i : input) {
            context.getSeqDerReader().read(i);
        }
    }

    @Then("SeqDerReader IsDone is true")
    public void seqderreaderIsDoneIsTrue() {
        assertTrue( context.getSeqDerReader().isDone());
    }

    @And("SeqDerReader IsError is false")
    public void seqderreaderIsErrorIsFalse() {
        assertFalse(context.getSeqDerReader().isError());
    }

    @And("SeqDerReader getResult is: {string}")
    public void seqderreaderGetResultIs(String base64x509) {
        String b64 = Base64.getEncoder().encodeToString(context.getSeqDerReader().getResult());
        assertEquals( base64x509,b64);
    }

    @And("SeqDerReader is signaled EOF")
    public void seqderreaderIsSignaledEOF() {
        context.getSeqDerReader().setEof();
    }

    @Then("SeqDerReader IsDone is false")
    public void seqderreaderIsDoneIsFalse() {
        assertFalse(context.getSeqDerReader().isDone());
    }

    @And("SeqDerReader IsError is true")
    public void seqderreaderIsErrorIsTrue() {
        assertTrue(context.getSeqDerReader().isError());
    }

    @And("SeqDerReader getResult returns null")
    public void seqderreaderGetResultReturnsNull() {
        assertNull(context.getSeqDerReader().getResult());
    }
}
