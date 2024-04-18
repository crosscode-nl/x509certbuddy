package nl.crosscode.x509certbuddy.filetypes

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import icons.CertBuddyIcons
import javax.swing.Icon


object PEMFileType : LanguageFileType(Language.ANY) {
    override fun getName(): String = "PEM file"

    override fun getDescription(): String = "PEM files containing X.509 certificates"

    override fun getDefaultExtension(): String = "pem"

    override fun getIcon(): Icon = CertBuddyIcons.CertBuddyToolwindow

}