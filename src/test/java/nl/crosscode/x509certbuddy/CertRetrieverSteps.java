package nl.crosscode.x509certbuddy;

import com.intellij.openapi.editor.Editor;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CertRetrieverSteps {
    private final CertRetrieverStepsContext context;
    private ClassLoader classLoader = getClass().getClassLoader();

    public CertRetrieverSteps(CertRetrieverStepsContext context) {
        this.context = context;
    }

    @Given("a text file: {string} with certificates")
    public void aTextFileWithCertificates(String textFile) {

        try (InputStream inputStream = classLoader.getResourceAsStream("nl/crosscode/x509certbuddy/CertRetrieverTestData/"+textFile)){
            context.setText( IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @When("CertRetriever is used to find all certificates")
    public void certretrieverIsUsedToFindAllCertificates() throws CertificateException {
        Editor editor = null;
        CertRetriever certRetriever = new CertRetriever(editor);
        context.setBase64certs(certRetriever.retrieveCerts(context.getText()).stream().map(c-> {
            try {
                return Base64.getEncoder().encodeToString(c.getCertificate().getEncoded());
            } catch (CertificateEncodingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
    }

    @Then("all certificates in file: {string} are returned")
    public void allCertificatesInFileAreReturned(String expectedCertsFile) {
        try (InputStream inputStream = classLoader.getResourceAsStream("nl/crosscode/x509certbuddy/CertRetrieverTestData/"+expectedCertsFile)){
            IOUtils.lineIterator(inputStream,StandardCharsets.UTF_8).forEachRemaining(cert -> {
                assertTrue(context.getBase64certs().stream().anyMatch(c->c.equals(cert)));
            });
            context.setText( IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
