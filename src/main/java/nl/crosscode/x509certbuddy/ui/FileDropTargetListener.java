package nl.crosscode.x509certbuddy.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileDropTargetListener implements DropTargetListener {

    private final FileDropTargetListenerCallback callback;

    public FileDropTargetListener(FileDropTargetListenerCallback callback) {
        this.callback = callback;
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        List<File> filesToReturn = new ArrayList<File>();
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        Transferable transferable = dtde.getTransferable();
        for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
            if (flavor.isFlavorJavaFileListType()) {
                try {
                    List files = (List) transferable.getTransferData(flavor);
                    for (Object file : files) {
                        filesToReturn.add((File)file);
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                }
            }
        }
        dtde.dropComplete(true);
        callback.filesDropped(filesToReturn);
    }
}
