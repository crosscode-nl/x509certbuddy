package nl.crosscode.x509certbuddy.ui

import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.*
import java.io.File
import java.io.IOException

class FileDropTargetListener(private val callback: FileDropTargetListenerCallback) : DropTargetListener {
    override fun dragEnter(dtde: DropTargetDragEvent) {
    }

    override fun dragOver(dtde: DropTargetDragEvent) {
    }

    override fun dropActionChanged(dtde: DropTargetDragEvent) {
    }

    override fun dragExit(dte: DropTargetEvent) {
    }

    override fun drop(dtde: DropTargetDropEvent) {
        val filesToReturn: MutableList<File> = ArrayList()
        dtde.acceptDrop(DnDConstants.ACTION_COPY)
        val transferable = dtde.transferable
        for (flavor in transferable.transferDataFlavors) {
            if (flavor.isFlavorJavaFileListType) {
                try {
                    val files = transferable.getTransferData(flavor) as List<*>
                    for (file in files) {
                        filesToReturn.add(file as File)
                    }
                } catch (e: UnsupportedFlavorException) {
                } catch (e: IOException) {
                }
            }
        }
        dtde.dropComplete(true)
        callback.filesDropped(filesToReturn)
    }
}
