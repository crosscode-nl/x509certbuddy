Feature: CertRetriever retrieves all certs in a full text

    Scenario: When a full text is presented all certs hidden in there in different forms are found
    Given a text file: "singlebase64cert.txt" with certificates
    When CertRetriever is used to find all certificates
    Then all certificates in file: "singlebase64cert.results" are returned
