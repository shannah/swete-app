/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.ui.components;

import com.codename1.components.InteractionDialog;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Button;
import com.codename1.ui.FontImage;
import com.codename1.ui.layouts.BorderLayout;

/**
 *
 * @author shannah
 */
public class HelpButton extends Button {

    /**
     * @return the helpText
     */
    public String getHelpText() {
        return helpText;
    }

    /**
     * @param helpText the helpText to set
     */
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    private String helpText;
    private InteractionDialog dlg;
    public HelpButton(String helpText) {
        super("");
        this.helpText = helpText;
        setMaterialIcon(FontImage.MATERIAL_HELP);
        addActionListener(e->{
            if (dlg != null && dlg.isShowing()) {
                dlg.dispose();
                dlg = null;
                return;
            }
            dlg = new InteractionDialog();
            dlg.setLayout(new BorderLayout());
            dlg.add(BorderLayout.CENTER, new SpanLabel(getHelpText()));
            dlg.setDisposeWhenPointerOutOfBounds(true);
            dlg.showPopupDialog(this);
        });
    }
}
