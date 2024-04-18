package nl.crosscode.x509certbuddy.ui

import java.io.File

fun interface FileDropTargetListenerCallback {
    fun filesDropped(files: List<File>)
}
