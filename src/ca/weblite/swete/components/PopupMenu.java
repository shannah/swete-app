/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.components;

import com.codename1.components.InteractionDialog;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public class PopupMenu extends InteractionDialog {
    
    /**
     * @return the commandLabel
     */
    public String getCommandLabel() {
        return commandLabel;
    }

    /**
     * @param commandLabel the commandLabel to set
     */
    public void setCommandLabel(String commandLabel) {
        this.commandLabel = commandLabel;
    }

    /**
     * @return the materialIcon
     */
    public char getMaterialIcon() {
        return materialIcon;
    }

    /**
     * @param materialIcon the materialIcon to set
     */
    public void setMaterialIcon(char materialIcon) {
        this.materialIcon = materialIcon;
    }
    private List<Command> commands = new ArrayList<Command>();
    private Command cmd;
    private char materialIcon = FontImage.MATERIAL_MORE_VERT;
    private String commandLabel = "";
    private Container commandsCnt;
    public PopupMenu() {
        super(new BorderLayout());
        commandsCnt = new Container(BoxLayout.y());
        commandsCnt.setScrollableY(true);
        setDisposeWhenPointerOutOfBounds(true);
        add(BorderLayout.CENTER, commandsCnt);
        
    }
    
    public Command getCommand() {
        if (cmd != null) {
            return cmd;
        }
        Command out = Command.createMaterial(getCommandLabel(), getMaterialIcon(),  e->{
            showPopupDialog(e.getComponent());
        });
        cmd = out;
        return out;
    }
    
    public PopupMenu addCommand(Command command) {
        commands.add(command);
        Button b = new Button(command);
        b.addActionListener(e->{
            dispose();
        });
        commandsCnt.add(b);
        return this;
    }
    
    public void removeAllCommands() {
        commandsCnt.removeAll();
    }
    
    
}
