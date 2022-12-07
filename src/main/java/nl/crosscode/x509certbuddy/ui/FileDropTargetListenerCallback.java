package nl.crosscode.x509certbuddy.ui;

import java.io.File;
import java.util.List;

public interface FileDropTargetListenerCallback {
    void filesDropped(List<File> files);
}
