/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.ui;

import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Graphics;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.geom.Rectangle;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundRectBorder;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.ui.util.UITimer;
import com.codename1.util.SuccessCallback;
import com.codename1.xml.Element;
import com.xataface.query.XFClient;
import com.xataface.query.XFRecord;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public class XFUIForm extends Container {
    Element el;
    XFRecord record;
    
    private EventDispatcher actionListeners;
    private EventDispatcher actionListeners() {
        if (actionListeners == null) {
            actionListeners = new EventDispatcher();
        }
        return actionListeners;
    }
    
    public static class ValidationError {
        private final String message;
        private final Component target;
        
        public ValidationError(Component target, String message) {
            this.target = target;
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Component getTarget() {
            return target;
        }
    }
    
    public static class ValidationEvent {
        List<ValidationError> errors;
        private List<ValidationError> errors() {
            if (errors == null) {
                errors = new ArrayList<ValidationError>();
            }
            return errors;
        }
        
        public List<ValidationError> getErrors() {
            if (errors == null) {
                return null;
            }
            return new ArrayList<ValidationError>(errors);
        }
        
        public void addError(ValidationError error) {
            errors().add(error);
        }
        
        public void removeError(ValidationError error) {
            if (errors != null) {
                errors.remove(error);
            }
        }

        /**
         * @return the failed
         */
        public boolean isFailed() {
            return errors != null && !errors.isEmpty();
        }

        
    }
    
    public static interface Validator {
        public boolean validate(ValidationEvent evt);
    }
    
    /**
     * Validators that are added automatically by XFUIWidget when form 
     * is built.  We need to track these separately because we need to 
     * clear and recreate them when rebuilding the form without affecting
     * validators added from the outside.
     */
    private List<Validator> internalValidators;
    private List<Validator> internalValidators() {
        if (internalValidators == null) {
            internalValidators = new ArrayList<Validator>();
        }
        return internalValidators;
    }
    
    void addInternalValidator(Validator v) {
        internalValidators().add(v);
        addValidator(v);
    }
    
    void removeInternalValidator(Validator v) {
        if (internalValidators != null) {
            internalValidators.remove(v);
        }
        removeValidator(v);
    }
    
    
    private List<Validator> validators;
    private List<Validator> validators() {
        if (validators == null) {
            validators = new ArrayList<Validator>();
        }
        return validators;
    }
    
    public void addValidator(Validator v) {
        validators().add(v);
    }
    
    public void removeValidator(Validator v) {
        if (validators != null) {
            validators.remove(v);
        }
    }
    
    public void addActionListener(ActionListener l) {
        actionListeners().addListener(l);
    }
    
    public void removeActionListener(ActionListener l) {
        actionListeners().removeListener(l);
    }
    
    
    private Container fieldPanel;
    private Container overlay;
    
    /**
     * Groups components related to the error for a particular field.
     */
    private class ErrorOverlay {
        SpanLabel errorLabel;
        Button errorIcon;
        
        // The component that was the source of the error
        // This is used for positioning the error label and icon.
        Component target;
        
    }
    
    private List<ErrorOverlay> errorOverlays;
    private List<ErrorOverlay> errorOverlays() {
        if (errorOverlays == null) {
            errorOverlays = new ArrayList<ErrorOverlay>();
        }
        return errorOverlays;
    }
    
    private void addErrorOverlay(ErrorOverlay ov) {
        errorOverlays().add(ov);
        overlay.add(ov.errorLabel);
        overlay.add(ov.errorIcon);
    }
    
    private void removeErrorOverlay(ErrorOverlay ov) {
        if (errorOverlays != null) {
            overlay.removeComponent(ov.errorLabel);
            overlay.removeComponent(ov.errorIcon);
            errorOverlays.remove(ov);
        }
    }
    
    private void clearErrorOverlays() {
        if (errorOverlays != null) {
            List<ErrorOverlay> toRemove = new ArrayList<ErrorOverlay>(errorOverlays);
            for (ErrorOverlay ov : toRemove) {
                removeErrorOverlay(ov);
            }
        }
    }
    
    public XFUIForm(Element el, XFRecord record) {
        super(new LayeredLayout());
        fieldPanel = new Container(BoxLayout.y());
        fieldPanel.setScrollableY(true);
        
        add(fieldPanel);
        
        // For error messages we use an overlay
        overlay = new Container(new OverlayLayout());
        fieldPanel.addScrollListener((x,y,oldX,oldY)->{
            overlay.revalidate();
        });
        add(overlay);
        this.el = el;
        this.record = record;
        rebuild();
    }
    
    /**
     * Layout for the overlay panel.  Mainly used for laying out the error messages.
     */
    private class OverlayLayout extends Layout {

        @Override
        public void layoutContainer(Container parent) {
            if (errorOverlays != null) {
                Rectangle tmp = new Rectangle();
                Rectangle tmp2 = new Rectangle();
                for (ErrorOverlay eo : errorOverlays) {
                    tmp.setX(0);
                    tmp.setY(0);
                    tmp.setWidth(eo.target.getWidth());
                    tmp.setHeight(eo.target.getHeight());
                    
                    // Get the projection of the target component onto the
                    // overlay
                    Rectangle targetRect = UIUtilities.convertRect(tmp, tmp2, eo.target, parent);
                    
                    // We want the error label to be right below it
                    eo.errorLabel.setX(targetRect.getX() + eo.errorLabel.getStyle().getMarginLeftNoRTL());
                    eo.errorLabel.setY(targetRect.getY() 
                            + targetRect.getHeight() 
                            + eo.target.getStyle().getMarginBottom() 
                            + eo.errorLabel.getStyle().getMarginTop());
                    eo.errorLabel.setWidth(Math.min(
                            eo.errorLabel.getPreferredW(),
                            overlay.getWidth() 
                                - eo.errorLabel.getX() 
                                - overlay.getStyle().getPaddingRight(false) 
                                - eo.errorLabel.getStyle().getMarginRightNoRTL()
                            )
                    );
                    
                    eo.errorLabel.setHeight(eo.errorLabel.getPreferredH());
                    
                    // Now layout the icon
                    // If there is room, we layout on the right side of the target
                    // Otherwise we'll overlay inside the right side of the target
                    
                    eo.errorIcon.setX(targetRect.getX() 
                            + targetRect.getWidth() 
                            + eo.target.getStyle().getMarginRightNoRTL() 
                            + eo.errorIcon.getStyle().getMarginLeftNoRTL()
                    );
                    
                    eo.errorIcon.setWidth(eo.errorIcon.getPreferredW());
                    if (eo.errorIcon.getX() + eo.errorIcon.getWidth() > parent.getWidth()) {
                        /// We need to slide it in a bit
                        eo.errorIcon.setX(parent.getWidth() 
                                - eo.errorIcon.getWidth() 
                                - eo.errorIcon.getStyle().getMarginRightNoRTL() 
                                - parent.getStyle().getPaddingRightNoRTL());
                    }
                    
                    eo.errorIcon.setY(targetRect.getY());
                    eo.errorIcon.setHeight(targetRect.getHeight());
                }
            }
        }

        @Override
        public Dimension getPreferredSize(Container parent) {
            Dimension tmp = fieldPanel.getPreferredSize();
            return new Dimension(tmp.getWidth(), tmp.getHeight());
        }
        
    }
    
    /**
     * The overlay container - used for displaying error messages.
     */
    private class Overlay extends Container {
        Overlay() {
            super(new OverlayLayout());
        }

        @Override
        protected void paintBackground(Graphics g) {
            super.paintBackground(g); //To change body of generated methods, choose Tools | Templates.
        }
        
        
    }
    
    /**
     * Removes all fields and rebuilds form.
     */
    private void rebuild() {
        fieldPanel.removeAll();
        overlay.removeAll();
        if (validators != null && internalValidators != null) {
            validators.removeAll(internalValidators);
            internalValidators.clear();
        }
        List<Element> sections = el.getDescendantsByTagName("section");
        for(Element section : sections) {
            Component sectCnt = buildSection(section);
            fieldPanel.add(sectCnt);
        }
        Button submit = new Button("Save");
        submit.addActionListener(e->{
            submit();
            
        });
        fieldPanel.add(submit);
        
    }
    
    protected void validationFailed(ValidationEvent vevt, boolean scrollToError) {
        if (vevt.isFailed()) {
            ErrorOverlay first = null;
            for (ValidationError verr : vevt.getErrors()) {
                ErrorOverlay eo = new ErrorOverlay();
                if (first == null) {
                    first = eo;
                }
                eo.errorIcon = new Button();
                $(eo.errorIcon).selectAllStyles()
                        .setBgTransparency(0)
                        .setFgColor(0xff0000)
                        .setBorder(Border.createEmpty());
                FontImage.setMaterialIcon(eo.errorIcon, FontImage.MATERIAL_ERROR);
                eo.errorLabel = new SpanLabel(verr.getMessage());
                $(eo.errorLabel.getTextComponent()).selectAllStyles().setFgColor(0xffffff)
                        .setFontSizeMillimeters(2f);
                $(eo.errorLabel).selectAllStyles()
                        .setBgColor(0xff0000)
                        .setBgTransparency(0xaa)
                        .setBorder(RoundRectBorder.create().cornerRadius(1f).shadowBlur(1f));
                eo.target = verr.getTarget();
                addErrorOverlay(eo);
            }
            if (scrollToError) {
                overlay.setHidden(true);
                overlay.getParent().revalidate();
                fieldPanel.scrollComponentToVisible(first.target.getParent());
                UITimer.timer(500, false, ()->{
                    overlay.layoutContainer();
                    overlay.animateLayoutFade(500, 0);
                });
            } else {
                overlay.animateLayout(300);
            }
        }
    }
    
    void validate(boolean scrollToError) {
        clearErrorOverlays();
        
        if (validators != null) {
            ValidationEvent vevt = new ValidationEvent();
            for (Validator v : validators) {
                v.validate(vevt);
                
            }
            if (vevt.isFailed()) {
                validationFailed(vevt, scrollToError);
                return;
            }
        }
    }
    
    boolean hasValidationErrors() {
        return errorOverlays != null && !errorOverlays.isEmpty();
    }
    
    private void submit() {
        validate(true);
        if (!hasValidationErrors()) {
            if (actionListeners != null) {
                actionListeners.fireActionEvent(new ActionEvent(this));
            }
        }
        
    }
    
    private Component buildSection(Element el) {
        Container sectionCnt = new Container(BoxLayout.y());
        List<Element> fields = el.getChildrenByTagName("field");
        XFUIWidget.Builder builder = new XFUIWidget.Builder();
        if (fields != null) {
            for(Element field : fields) {
                String fieldName = field.getAttribute("name");
                if (fieldName == null || "".equals(fieldName)) {
                    continue;
                }
                sectionCnt.add(builder.build(this, field, record));
            }
        }
        return sectionCnt;
    }
    
    public XFRecord getRecord() {
        return record;
    }
}
