Feature: CertRetriever retrieves all certs in a full text

  Scenario Outline: When a full text is presented all certs hidden in there in different forms are found
    Given a text file: "<filename>.txt" with certificates
    When CertRetriever is used to find all certificates
    Then all certificates in file: "<filename>.results" are returned
    Examples:
      | filename                  |
      | single_base64_cert        |
      | quoted_single_base64_cert |
      | dual_base64_cert          |
      | quoted_dual_base64_cert   |
      | concat_dual_base64_cert   |
      | pem_dual_base64_cert   |

