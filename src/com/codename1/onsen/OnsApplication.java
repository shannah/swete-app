/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.onsen;

import com.codename1.htmlform.HTMLContext;
import com.codename1.onsen.components.OnsPage;
import com.codename1.onsen.components.ElementFactory;
import com.codename1.onsen.components.AbstractElement;
import com.codename1.onsen.components.Template;
import com.codename1.onsen.components.Element;
import com.codename1.htmlform.HTMLForm;
import com.codename1.htmlform.HTMLWindow;
import com.codename1.processing.Result;
import com.codename1.system.NativeLookup;
import com.codename1.ui.CN;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class OnsApplication {
    
    
    
    //private static OnsApplication instance;
    private final HTMLContext context;
    private int nextComponentIndex;
    private Map<String,OnsPage> pages = new HashMap<>();
    private Map<String,Template> templates = new HashMap<>();
    private OnsPage currentPage;
    private ElementFactory factory = new ElementFactory.OnDefaultComponentFactory(this);
    private static boolean autoUpdateHarness=true;
    
    public static void setAutoUpdateHarness(boolean autoUpdate) {
        autoUpdateHarness = autoUpdate;
    }
    
    public static boolean isAutoUpdateHarness() {
        return autoUpdateHarness;
    }
    
    public static OnsApplication launch(OnsRunnable onReady) {
        try {
            return launchImpl(onReady);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    
    public static OnsApplication launch(OnsUIBuilder builder) {
        return launch(app->{
            builder.setApplication(app);
            app.getContext().run(builder);
        });
    }
    
    public ElementFactory getFactory() {
        return factory;
    }
    
    private static OnsApplication launchImpl(OnsRunnable onReady) throws IOException {
        Updater updater = (Updater)NativeLookup.create(Updater.class);
        if (autoUpdateHarness && updater.isSupported()) {
            updater.updateWebHarness();
        }
        HTMLForm htmlForm = new HTMLForm("/cn1-htmltk-harness/index.html");
        OnsApplication app = new OnsApplication(htmlForm.getContext());
        htmlForm.getContext().runLater(()->{
            app.init();
        });
        htmlForm.show();
        htmlForm.getContext().runLater(new OnsRunner(onReady, app));
        return app;
    }
    
     public <T extends AbstractElement> T load(Class<T> type, String path) throws IOException {
        TagParser parser = new TagParser(this);
        return (T)parser.parseHTML(CN.getResourceAsStream(path));
        
    }
    
    public static OnsApplication launchWindow(OnsRunnable onReady) {
        return launchWindow(null, onReady);
    }
    
    public static OnsApplication launchWindow(OnsUIBuilder builder) {
        return launchWindow(app->{
            builder.setApplication(app);
            app.getContext().run(builder);
        });
    }
    
    public static OnsApplication launchWindow(Object internal, OnsUIBuilder builder) {
        return launchWindow(internal, app->{
            builder.setApplication(app);
            app.getContext().run(builder);
        });
    }
    
    public static OnsApplication launchWindow(Object internal, OnsRunnable onReady) {
        try {
            return launchWindowImpl(internal, onReady);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    
    
    private static OnsApplication launchWindowImpl(Object internal, OnsRunnable onReady) throws IOException {
        Updater updater = (Updater)NativeLookup.create(Updater.class);
        if (autoUpdateHarness && updater.isSupported()) {
            updater.updateWebHarness();
        }
        HTMLWindow htmlForm = new HTMLWindow(internal, "/cn1-htmltk-harness/index.html");
        OnsApplication app = new OnsApplication(htmlForm.getContext());
        htmlForm.getContext().runLater(()->{
            app.init();
        });
        htmlForm.setVisible(true);
        htmlForm.getContext().runLater(new OnsRunner(onReady, app));
        return app;
    }
    
    private OnsApplication(HTMLContext context) {
        this.context = context;
        context.addLookup(this);
        //if (instance == null) instance = this;
    }
    
   // public static void setInstance(OnsApplication app) {
    //    instance = app;
    //}
    
    public static OnsApplication getInstance(HTMLContext context) {
       return context.lookup(OnsApplication.class);
    }
    
    public void setInstance(HTMLContext context) {
        context.replaceLookup(OnsApplication.class, this);
    }
    
    
    
    public int incrementComponentIndex() {
        return nextComponentIndex++;
    }
    
    public void install(AbstractElement cmp) {
        if (cmp instanceof OnsPage) {
            registerPage((OnsPage)cmp);
        }
        cmp.install();
    }

    
    public void onReady() {
        
    }
    
    public HTMLContext getContext() {
        return context;
    }
    
    public void init() {
        this.context.execute("document.addEventListener('init', function(evt){evt.pageId = evt.target.id});");
        this.context.addDocumentListener("init", e->{
            //System.out.println("In init event");
            Result eventData = e.getEventData();
            String pageId = eventData.getAsString("pageId");
            OnsPage page = pages.get(pageId);
            if (page == null) {
                System.out.println("Could not find page "+pageId);
                return;
            }
            page.setInitialized(true);
            System.out.println("Received init for page "+page.getElement().getId());
            if (page.getContainingTemplate() != null) {
                for (AbstractElement script : page.getElementsByTagName("script")) {
                    String scriptText = script.createInnerHTML();
                    scriptText = "window.cn1ScriptPage = "+page.getElement().getSelectionJSString()+";"+scriptText+";if (cn1ScriptPage && cn1ScriptPage.onInit) cn1ScriptPage.onInit.call(cn1ScriptPage); window.cn1ScriptPage = null;";
                    this.context.execute(scriptText);
                }
            }
            
        });

    }
    
    
    public void registerTemplate(Template template) {
        templates.put(template.getElement().getId(), template);
    }
    
    public void registerPage(OnsPage page) {
        pages.put(page.getElement().getId(), page);
    }
    
    //public static OnsApplication getInstance() {
    //    return instance;
    //}
    
    
    public void setComponentFactory(ElementFactory factory) {
        this.factory = factory;
    }
    
    
    
    /**
     * Creates an element either with an HTML snippet, or with a tag name.
     * @param tagName The tag name of the element to create.  If this begins with {@literal &lt;}, then it will treat it as an HTML snippet and will parse it. 
     * @return 
     */
    public AbstractElement createComponent(String tagName) {
        if (tagName.startsWith("<")) {
            //This is a full snippet... we need to parse it
            TagParser parser = new TagParser(this);
            try {
                return parser.parse(tagName);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            
        }
        
        return factory.create(tagName);
    }
    
    public Element createElement(String tagName) {
        AbstractElement cmp = createComponent(tagName);
        if (cmp instanceof Element) { 
            return (Element)cmp;
        }
        throw new RuntimeException("Factory created non-element for tag "+tagName);
    }
    
    public <T extends AbstractElement> T createComponent(Class<T> cls, String tagName) {
        return (T)createComponent(tagName);
    } 
    
    /**
     * Alias for {@link #createComponent(java.lang.String) }
     * @param tagName
     * @return 
     */
    public AbstractElement ons(String tagName) {
        return createComponent(tagName);
    }
    
    public <T extends AbstractElement> T ons(Class<T> cls, String tagName) {
        return (T)createComponent(tagName);
    } 
    

}
