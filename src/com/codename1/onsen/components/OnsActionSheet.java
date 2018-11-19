/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen.components;

import com.codename1.htmlform.HTMLContext.DOMEvent;
import com.codename1.onsen.OnsApplication;
import static com.codename1.htmlform.HTMLUtil.quote;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.SuccessCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author shannah
 */
public class OnsActionSheet  {
    private final OnsApplication app;
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the maskColor
     */
    public String getMaskColor() {
        return maskColor;
    }

    /**
     * @param maskColor the maskColor to set
     */
    public void setMaskColor(String maskColor) {
        this.maskColor = maskColor;
    }

    /**
     * @return the cancelable
     */
    public boolean isCancelable() {
        return cancelable;
    }

    /**
     * @param cancelable the cancelable to set
     */
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @return the animation
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * @param animation the animation to set
     */
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }
    
    public static enum Animation {
        Slide("slide"),
        None("none");
        
        private String name;
        
        Animation(String name) {
            this.name = name;
        }
    }
    
    public static class AnimationOptions {
        double duration;
        double delay;
        AnimationTiming timing;
        
    }
    
    public static enum AnimationTiming {
        EaseIn("ease-in");
        
        private String name;
        
        AnimationTiming(String name) {
            this.name = name;
        }
    }
    
    private String title, maskColor;
    private boolean cancelable, disabled;
    private Animation animation;
    private List<OnActionSheetButton> buttons = new ArrayList<>();
    
    
    
    public static class OnActionSheetButton {
        private String icon;
        private String label;
        private Modifier modifier;

        /**
         * @return the modifier
         */
        public Modifier getModifier() {
            return modifier;
        }

        /**
         * @param modifier the modifier to set
         */
        public void setModifier(Modifier modifier) {
            this.modifier = modifier;
        }
        
        public static enum Modifier {
            Destructive("destructive"),
            Material("material");
            
            private String name;
            Modifier(String name) {
                this.name = name;
            }
        }

        public OnActionSheetButton(String label) {
            this.label = label;
            
        }
        
        public OnActionSheetButton(String label, String icon) {
            this.icon = icon;
            this.label = label;
        }
        
        /**
         * @return the icon
         */
        public String getIcon() {
            return icon;
        }

        /**
         * @param icon the icon to set
         */
        public void setIcon(String icon) {
            this.icon = icon;
        }

        /**
         * @return the label
         */
        public String getLabel() {
            return label;
        }

        /**
         * @param label the label to set
         */
        public void setLabel(String label) {
            this.label = label;
        }
        
        String asJSString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            if (label != null) {
                sb.append("label:").append(quote(label)).append(",");
            }
            if (icon != null) {
                sb.append("icon:").append(quote(icon)).append(",");
            }
            if (modifier != null) {
                sb.append("modifier:").append(quote(modifier.name)).append(",");
            }
            if (sb.length() > 1) {
                sb.setLength(sb.length()-1);
            }
            sb.append("}");
            return sb.toString();
        }
    }
    
    
    public OnsActionSheet(OnsApplication app) {
        this.app = app;
    }
    
    public void addButton(OnActionSheetButton button) {
        buttons.add(button);
    }
    
    public void removeButton(OnActionSheetButton button) {
        buttons.remove(button);
    }
    
    public void addButtons(OnActionSheetButton... buttons) {
        this.buttons.addAll(Arrays.asList(buttons));
    }
    
    public void addButtons(String... buttons) {
        for (String btn : buttons) {
            addButton(btn);
        }
    }
    
    public void removeButtons(OnActionSheetButton... buttons) {
        this.buttons.removeAll(Arrays.asList(buttons));
    }
    
    public void clearButtons() {
        buttons.clear();
    }
    
    public List<OnActionSheetButton> getButtons() {
        return new ArrayList<OnActionSheetButton>(buttons);
    }
    
    private String getButtonsJSString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first=true;
        for (OnActionSheetButton button : buttons) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(button.asJSString());
        }
        sb.append("]");
        return sb.toString();
    }
    
    public void addButton(String label) {
        buttons.add(new OnActionSheetButton(label));
    }
    
    public void show(SuccessCallback<Integer> callback) {
        String eventName = "cn1ActionSheethidden";
        ActionListener<DOMEvent> l = new ActionListener<DOMEvent>() {
            @Override
            public void actionPerformed(DOMEvent e) {
                app.getContext().removeDocumentListener(eventName, this);
                //System.out.println("Event data "+e.getEventData());
                int selectedIndex = e.getEventData().getAsInteger("cn1Detail/selectedIndex");
                if (callback != null) callback.onSucess(selectedIndex);
            }
        };
        
        app.getContext().addDocumentListener(eventName, l);
    
        app.getContext().execute("ons.openActionSheet("
                + "{title:${0}, "
                + "cancelable:${1}, "
                + "buttons:"+getButtonsJSString()+
                "}).then(function(id){"
                    + "var evt=new CustomEvent(${2}, "
                + "{detail:{selectedIndex:id}}); "
                + "document.dispatchEvent(evt);})", new Object[]{title, cancelable, eventName});
    }
    
}
