/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen;

import com.codename1.htmlform.HTMLContext;
import com.codename1.htmlform.HTMLForm;
import com.codename1.onsen.OnsApplication;
import com.codename1.processing.Result;
import com.codename1.ui.events.ActionListener;
import com.codename1.util.SuccessCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles alert notifications.  See <a href="https://onsen.io/v2/api/js/ons.notification.html">For examples of alert types.</a>
 * @author shannah
 */
public class OnsNotification {
    
    /**
     * Shows an alert
     * @param text Text to display
     * @param callback Called when alert hidden.  Passed index of button user pressed.
     */
    public static void alert(OnsApplication app, String text, SuccessCallback<Integer> callback){
        Alert alert = new Alert(app);
        alert.setMessage(text);
        alert.show(callback);
    }
    
    /**
     * Shows alert.
     * @param alert The alert to show
     * @param callback Called when alert hidden.  Passed index of button user pressed.
     */
    public static void alert(Alert alert, SuccessCallback<Integer> callback) {
        alert.show(callback);
    }
    
    /**
     * Shows confirm dialog.
     * @param text
     * @param callback 
     */
    public static void confirm(OnsApplication app, String text, SuccessCallback<Integer> callback){
        Confirm alert = new Confirm(app);
        alert.setMessage(text);
        alert.show(callback);
    }
    
    public static void confirm(Confirm alert, SuccessCallback<Integer> callback) {
        alert.show(callback);
    }
    
    public static void prompt(OnsApplication app, String text, SuccessCallback<String> callback) {
        Prompt alert = new Prompt(app);
        alert.setMessage(text);
        alert.showPrompt(callback);
    }
    
    public static void prompt(Prompt prompt, SuccessCallback<String> callback) {
        prompt.showPrompt(callback);
    }
    
    /**
     * Shows a toast message.
     * @param text Text to display
     * @param callback Called when toast is hidden.  Passed 0 if user clicked hide button.  -1 otherwise.
     */
    public static void toast(OnsApplication app, String text, SuccessCallback<Integer> callback) {
        Toast toast = new Toast(app);
        toast.setMessage(text);
        toast.setTimeout(5000);
        toast.setButtonLabel("Hide");
        toast.show(callback);
    }
    
    /**
     * 
     * @param toast
     * @param callback Called when toast is hidden.  Passed 0 if user clicked button.  -1 otherwise.
     */
    public static void toast(Toast toast, SuccessCallback<Integer> callback) {
        toast.show(callback);
    }

    
    
    public static enum Animation {
        Slide("fade"),
        None("none");
        
        private String name;
        
        Animation(String name) {
            this.name = name;
        }
    }
    
    public static class Alert {
        protected OnsApplication app;
        private String message="", messageHTML, id, clazz, title, maskColor;
        private List<String> buttonLabels;
        private int primaryButtonIndex=-1;
        private boolean cancelable;
        private Animation animation;
        
        public Alert(OnsApplication app) {
            this.app = app;
        }
        
        public String getOptionsJSON() {
            return Result.fromContent(getOptionsMap()).toString();
            
        }
        
        protected Map getOptionsMap() {
            Map m = new HashMap();
            if (message != null) m.put("message", message);
            if (messageHTML != null) m.put("messageHTML", messageHTML);
            if (id != null) m.put("id", id);
            if (clazz != null) m.put("class", clazz);
            if (title != null) m.put("title", title);
            if (maskColor != null) m.put("maskColor", maskColor);
            if (buttonLabels != null) m.put("buttonLabels", buttonLabels);
            if (primaryButtonIndex >= 0) {
                m.put("primaryButtonIndex", primaryButtonIndex);
            }
            m.put("cancelable", cancelable);
            if (animation != null) m.put("animation", animation.name);
            return m;
        }
        
        protected void showImpl(String methodName, SuccessCallback<Integer> callback) {
            String eventName = "cn1ActionSheethidden";
            ActionListener<HTMLContext.DOMEvent> l = new ActionListener<HTMLContext.DOMEvent>() {
                @Override
                public void actionPerformed(HTMLContext.DOMEvent e) {
                    app.getContext().removeDocumentListener(eventName, this);
                    //System.out.println("Event data "+e.getEventData());
                    int selectedIndex = e.getEventData().getAsInteger("cn1Detail/selectedIndex");
                    if (callback != null) callback.onSucess(selectedIndex);
                }
            };
        
            app.getContext().addDocumentListener(eventName, l);
            
            app.getContext().execute("ons.notification."+methodName+"("+getOptionsJSON()+").then(function(id){"
                    + "var evt=new CustomEvent(${0}, "
                + "{detail:{selectedIndex:id}}); "
                + "document.dispatchEvent(evt);})", new Object[]{eventName});
        }
        
        public void show(SuccessCallback<Integer> callback) {
            showImpl("alert", callback);
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * @return the messageHTML
         */
        public String getMessageHTML() {
            return messageHTML;
        }

        /**
         * @param messageHTML the messageHTML to set
         */
        public void setMessageHTML(String messageHTML) {
            this.messageHTML = messageHTML;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the clazz
         */
        public String getClazz() {
            return clazz;
        }

        /**
         * @param clazz the clazz to set
         */
        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

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
         * @return the primaryButtonIndex
         */
        public int getPrimaryButtonIndex() {
            return primaryButtonIndex;
        }

        /**
         * @param primaryButtonIndex the primaryButtonIndex to set
         */
        public void setPrimaryButtonIndex(int primaryButtonIndex) {
            this.primaryButtonIndex = primaryButtonIndex;
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
        
        public void addButtonLabel(String label) {
            if (buttonLabels == null) {
                buttonLabels = new ArrayList<String>();
            }
            buttonLabels.add(label);
        }
        
        public void removeButtonLabel(String label) {
            if (buttonLabels != null) {
                buttonLabels.remove(label);
                if (buttonLabels.isEmpty()) {
                    buttonLabels = null;
                }
            }
        }
        
        public void addButtonLabels(String... labels) {
            if (buttonLabels == null) {
                buttonLabels = new ArrayList<String>();
            }
            buttonLabels.addAll(Arrays.asList(labels));
        }
        
        public void clearButtonLabels() {
            buttonLabels = null;
        }
        
    }
    public static class Confirm extends Alert {
        
        public Confirm(OnsApplication app) {
            super(app);
        }
        public void show(SuccessCallback<Integer> callback) {
            showImpl("confirm", callback);
        }
    }
    public static class Prompt extends Alert {

        public Prompt(OnsApplication app) {
            super(app);
        }
        
        private String placeholder, defaultValue, inputType;
        private Boolean autofocus, submitOnEnter;

        @Override
        protected Map getOptionsMap() {
            Map m = super.getOptionsMap(); 
            if (placeholder != null) m.put("placeholder", placeholder);
            if (defaultValue != null) m.put("defaultValue", defaultValue);
            if (inputType != null) m.put("inputType", inputType);
            if (autofocus != null) m.put("autofocus", autofocus);
            if (submitOnEnter != null) m.put("submitOnEnter", submitOnEnter);
            return m;
        }

        /**
         * @return the placeholder
         */
        public String getPlaceholder() {
            return placeholder;
        }

        /**
         * @param placeholder the placeholder to set
         */
        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        /**
         * @return the defaultValue
         */
        public String getDefaultValue() {
            return defaultValue;
        }

        /**
         * @param defaultValue the defaultValue to set
         */
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        /**
         * @return the inputType
         */
        public String getInputType() {
            return inputType;
        }

        /**
         * @param inputType the inputType to set
         */
        public void setInputType(String inputType) {
            this.inputType = inputType;
        }

        /**
         * @return the autofocus
         */
        public Boolean getAutofocus() {
            return autofocus;
        }

        /**
         * @param autofocus the autofocus to set
         */
        public void setAutofocus(Boolean autofocus) {
            this.autofocus = autofocus;
        }

        /**
         * @return the submitOnEnter
         */
        public Boolean getSubmitOnEnter() {
            return submitOnEnter;
        }

        /**
         * @param submitOnEnter the submitOnEnter to set
         */
        public void setSubmitOnEnter(Boolean submitOnEnter) {
            this.submitOnEnter = submitOnEnter;
        }

        @Override
        public void show(SuccessCallback<Integer> callback) {
            throw new RuntimeException("Prompt does not support show() method.  Use showPrompt() instead.");
        }

        
        
        public void showPrompt(SuccessCallback<String> callback) {
            String eventName = "cn1ActionSheethidden";
            ActionListener<HTMLContext.DOMEvent> l = new ActionListener<HTMLContext.DOMEvent>() {
                @Override
                public void actionPerformed(HTMLContext.DOMEvent e) {
                    app.getContext().removeDocumentListener(eventName, this);
                    //System.out.println("Event data "+e.getEventData());
                    String userInput = e.getEventData().getAsString("cn1Detail/userInput");
                    if (callback != null) callback.onSucess(userInput);
                }
            };
        
            app.getContext().addDocumentListener(eventName, l);
            
            app.getContext().execute("ons.notification.prompt("+getOptionsJSON()+").then(function(userInput){"
                    + "var evt=new CustomEvent(${0}, "
                + "{detail:{userInput:userInput}}); "
                + "document.dispatchEvent(evt);})", new Object[]{eventName});
        }
        
        
        
        
    }
    
    public static enum ToastAnimation {
        None("none"),
        Fade("fade"),
        Ascend("ascend"),
        Lift("lift"),
        Fall("fall");
        
        private String name;
        
        ToastAnimation(String name) {
            this.name = name;
        }
                
                
    }
    
    public static class Toast {
        private final OnsApplication app;
        private String message, buttonLabel, id, clazz;
        private ToastAnimation animation;
        private Integer timeout;
        private Boolean force;
        
        public Toast(OnsApplication app) {
            this.app = app;
        }
        
        protected Map getOptionsMap() {
            Map m = new HashMap();
            if (message != null) m.put("message", message);
            if (buttonLabel != null) m.put("buttonLabel", buttonLabel);
            if (id != null) m.put("id", id);
            if (clazz != null) m.put("class", clazz);
            if (animation != null) {
                m.put("animation", animation.name);
            }
            if (timeout != null) {
                m.put("timeout", timeout);
            }
            
            if (force != null) {
                m.put("force", force);
            }
            return m;
        }
        
        public String getOptionsJSON() {
            return Result.fromContent(getOptionsMap()).toString();
        }
        
        protected void showImpl(String methodName, SuccessCallback<Integer> callback) {
            //final OnsApplication app = OnsApplication.getInstance();
            String eventName = "cn1ActionSheethidden";
            ActionListener<HTMLContext.DOMEvent> l = new ActionListener<HTMLContext.DOMEvent>() {
                @Override
                public void actionPerformed(HTMLContext.DOMEvent e) {
                    app.getContext().removeDocumentListener(eventName, this);
                    //System.out.println("Event data "+e.getEventData());
                    int value = e.getEventData().getAsInteger("cn1Detail/value");
                    if (callback != null) callback.onSucess(value);
                }
            };
        
            app.getContext().addDocumentListener(eventName, l);
            
            app.getContext().execute("ons.notification."+methodName+"("+getOptionsJSON()+").then(function(id){"
                    + "var evt=new CustomEvent(${0}, "
                + "{detail:{value:id}}); "
                + "document.dispatchEvent(evt);})", new Object[]{eventName});
        }
        
        /**
         * 
         * @param callback Called when toast is hidden.  Passed either 0 if user clicked button to close it, or -1, otherwise.
         */
        public void show(SuccessCallback<Integer> callback) {
            showImpl("toast", callback);
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * @return the buttonLabel
         */
        public String getButtonLabel() {
            return buttonLabel;
        }

        /**
         * @param buttonLabel the buttonLabel to set
         */
        public void setButtonLabel(String buttonLabel) {
            this.buttonLabel = buttonLabel;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the clazz
         */
        public String getClazz() {
            return clazz;
        }

        /**
         * @param clazz the clazz to set
         */
        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        /**
         * @return the animation
         */
        public ToastAnimation getAnimation() {
            return animation;
        }

        /**
         * @param animation the animation to set
         */
        public void setAnimation(ToastAnimation animation) {
            this.animation = animation;
        }

        /**
         * @return the timeout
         */
        public Integer getTimeout() {
            return timeout;
        }

        /**
         * @param timeout the timeout to set
         */
        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        /**
         * @return the force
         */
        public Boolean getForce() {
            return force;
        }

        /**
         * @param force the force to set
         */
        public void setForce(Boolean force) {
            this.force = force;
        }
        
    }
}
