# 0.9.0 - 2024-04-16

* Removed dependency on org.apache.commons-text
* Upgraded to Java 17
* Minimal supported version is now 2023.2.1
* Started migration to Kotlin

# 0.8.2 - 2024-03-26

* Bumped supported version range

# 0.8.1 - 2023-12-06

* Bumped supported version range
* Use org.apache.commons:commons-text instead of org.apache.commons:lang for StringEscapeUtils

# 0.7 - 2023-07-28

* Bumped supported version range

# 0.6 - 2023-03-06

* Bumped supported version range

# 0.5 - 2023-02-24

* Removed log messages used during initial development
* Fix removing listeners when no listeners are attached

# 0.4 - 2022-12-21

* Fixed assertion while writing data
* Fixed use of deprecated Base64 
* Support for reading p7b/p7c
* Some refactoring, which might fix some potential crashes

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