# 0.4

* Fixed assertion while writing data
* Fixed use of deprecated Base64 
* Support for reading p7b/p7c
* Update editor when editor is opened or something it typed

# 0.3 - 2022-12-11

* UI improvements, now more in line with IntelliJ
    * Most buttons are removed and replaced with actions
    * You can find those in the context menu of the tree view and/or the ToolWindow menu/title bar
* Updated or added icons
* When dropping files, DER encoded X.509 certificates are now accepted
* Made detail TextPane readonly
* Moved cert read action to editor context menu
* Export PEM added
* Copy and export PEM cert chain added
* Export DER added
* Copy and export all certificates to a json file
* Support for multiple projects

# 0.2 - 2022-12-07

* UI improvements, now more in line with IntelliJ.
  * Most buttons are removed and replaced with actions.
  * You can find those in the context menu of the tree view and/or the ToolWindow menu bar. 
* TextPanes readonly
* Verify certificates
* Parse certificates with escaped line feeds in json strings
* No longer remove certs when reading them from a document
* Remove duplicate certificates from the tree
* Allow removal of single or all certificates from the tree
* Allow drop of files with certificates (PEM/BASE64)

# 0.1