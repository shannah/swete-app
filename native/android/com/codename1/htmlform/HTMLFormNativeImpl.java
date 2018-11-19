package com.codename1.htmlform;

import android.os.Looper;
import com.codename1.impl.android.AndroidImplementation;
import com.codename1.impl.android.AndroidNativeUtil;

public class HTMLFormNativeImpl {
    public boolean isMainThread() {
        return AndroidImplementation.getInstance().isJSDispatchThread();
    }

    public void notifyDispatchQueue() {
        AndroidImplementation.getInstance().runOnJSDispatchThread(new Runnable() {
            public void run() {
                HTMLForm.runQueuedEvent();
            }
        });
    }

    public boolean isSupported() {
        return true;
    }

}
