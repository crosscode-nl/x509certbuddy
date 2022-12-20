package nl.crosscode.x509certbuddy.utils;

import com.intellij.openapi.editor.Editor;

public class EditorUtilsFactory {

    private static final EditorUtils editorUtils = new EditorUtils();
    public static EditorUtils getInstance() {
        return editorUtils;
    }
}
