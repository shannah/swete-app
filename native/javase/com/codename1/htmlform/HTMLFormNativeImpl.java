package com.codename1.htmlform;

import com.codename1.ui.CN;
import javafx.application.Platform;

public class HTMLFormNativeImpl implements com.codename1.htmlform.HTMLFormNative{
    private boolean cn1Edt = CN.getProperty("cn1.htmltk.useEdt", "false").equals("true");
    private boolean swingEdt = Boolean.getBoolean("cn1.htmltk.useSwingEdt");
    public boolean isMainThread() {
        if (cn1Edt) {
            return CN.isEdt();
        }
        if (swingEdt) {
            return java.awt.EventQueue.isDispatchThread();
        }
        return Platform.isFxApplicationThread();
    }

    public void notifyDispatchQueue() {
        if (cn1Edt) {
            CN.callSerially(()->HTMLForm.runQueuedEvent());
            return;
        }
        if (swingEdt) {
            java.awt.EventQueue.invokeLater(()->HTMLForm.runQueuedEvent());
            return;
        }
        Platform.runLater(()->{
            HTMLForm.runQueuedEvent();
        });
    }

    public boolean isSupported() {
        return true;
    }

}
