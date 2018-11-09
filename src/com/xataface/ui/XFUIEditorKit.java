/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.ui;

import com.codename1.components.ToastBar;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.util.SuccessCallback;
import com.xataface.query.XFClient;
import com.xataface.query.XFRecord;

/**
 *
 * @author shannah
 */
public class XFUIEditorKit {
    
    public Command getDeleteCommand(XFClient client, XFRecord record, SuccessCallback<Boolean> onComplete) {
        return new Command("Delete") {
            public void actionPerformed(ActionEvent e) {
                if(Dialog.show("Delete Record", "Are you sure you want to delete the record "+record.getTitle()+"?", "Delete", "Cancel")) {
                    ToastBar.Status status = ToastBar.getInstance().createStatus();
                    status.setMessage("Saving...");
                    status.setShowProgressIndicator(true);
                    status.show();
                    client.delete(record, res->{
                        if (res) {
                            
                        } else {
                            ToastBar.showErrorMessage("Failed to delete record");
                        }
                        onComplete.onSucess(res);
                    });
                    
                }
            }
        };
    }
    
    public Command getSaveCommand(XFClient client, XFRecord record, SuccessCallback<XFRecord> onComplete) {
        return new Command("Save") {
            public void actionPerformed(ActionEvent e) {
                ToastBar.Status status = ToastBar.getInstance().createStatus();
                status.setMessage("Saving...");
                status.setShowProgressIndicator(true);
                status.show();
                client.save(record, res -> {
                    status.clear();
                    if (res != null) {
                        //wrapper.getParent().replace(wrapper, createDetailsTable(res, client), CommonTransitions.createFade(300));
                    } else {
                        ToastBar.showErrorMessage("Failed to save record", 5000);
                    }
                    //if (onComplete != null) {
                        onComplete.onSucess(res);
                    //}
                    

                });
            }
        };
    }
}
