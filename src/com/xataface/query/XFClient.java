/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.query;

import com.codename1.components.ToastBar;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.io.MultipartRequest;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.processing.Result;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.ui.util.UITimer;
import com.codename1.util.Callback;
import com.codename1.util.StringUtil;
import com.codename1.util.SuccessCallback;
import com.codename1.xml.Element;
import com.codename1.xml.XMLParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author shannah
 */
public class XFClient {

    private String username;
    private String password;
    private String url;
    private TimeZone serverTimeZone;

    EventDispatcher errorHandler;

    public XFClient(String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    public void loadNewForm(String table, SuccessCallback<Element> callback) {
        ConnectionRequest req = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                if (code == 401) {
                    return;
                }
                super.handleErrorResponseCode(code, message);
            }
            
        };
        req.setReadResponseForErrors(true);
        req.setUrl(url);
        req.setHttpMethod("GET");
        req.setPost(false);
        req.addArgument("-table", table);
        req.addArgument("-action", "new");
        req.addArgument("-format", "xml");
        req.addArgument("--date-format", "server");
        
        req.addArgument("--no-prompt", "1");
        
        req.addResponseListener(e -> {

            if (e.getResponseCode() == 401) {
                // We need to log in
                handleUnauthorized(false, res -> {
                    if (res) {
                        loadNewForm(table, callback);
                    } else {
                        if (errorHandler != null) {
                            errorHandler.fireActionEvent(new ActionEvent("Unauthorized"));
                        }
                        callback.onSucess(null);
                    }
                });
                return;

            }

            XMLParser xp = new XMLParser();
            try {
                Element el = xp.parse(new InputStreamReader(new ByteArrayInputStream(req.getResponseData()), "UTF-8"));
                callback.onSucess(el);
                return;
            } catch (IOException ex) {
                if (callback instanceof Callback) {
                    ((Callback)callback).onError(XFClient.this, ex, 0, ex.getMessage());
                    return;
                } else {
                    if (errorHandler != null) {
                        errorHandler.fireActionEvent(new ActionEvent("Failed to parse XML: " + ex.getMessage()));
                    } else {
                        Log.e(ex);
                    }
                    callback.onSucess(null);
                }
            }
            
            
        });

        NetworkManager.getInstance().addToQueue(req);
    }
    
    public void loadEditForm(XFRecord record, SuccessCallback<Element> callback) {
        ConnectionRequest req = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                if (code == 401) {
                    return;
                }
                super.handleErrorResponseCode(code, message);
            }
            
        };
        req.setReadResponseForErrors(true);
        req.setUrl(url);
        req.setHttpMethod("GET");
        req.setPost(false);
        req.addArgument("-table", record.getTable());
        req.addArgument("-action", "edit");
        req.addArgument("-format", "xml");
        req.addArgument("--record-id", record.getId());
        req.addArgument("--no-prompt", "1");
        req.addArgument("--date-format", "server");
        
        req.addResponseListener(e -> {

            if (e.getResponseCode() == 401) {
                // We need to log in
                handleUnauthorized(false, res -> {
                    if (res) {
                        loadEditForm(record, callback);
                    } else {
                        if (errorHandler != null) {
                            errorHandler.fireActionEvent(new ActionEvent("Unauthorized"));
                        }
                        callback.onSucess(null);
                    }
                });
                return;

            }

            XMLParser xp = new XMLParser();
            try {
                Element el = xp.parse(new InputStreamReader(new ByteArrayInputStream(req.getResponseData()), "UTF-8"));
                callback.onSucess(el);
                return;
            } catch (IOException ex) {
                if (callback instanceof Callback) {
                    ((Callback)callback).onError(XFClient.this, ex, 0, ex.getMessage());
                    return;
                } else {
                    if (errorHandler != null) {
                        errorHandler.fireActionEvent(new ActionEvent("Failed to parse XML: " + ex.getMessage()));
                    } else {
                        Log.e(ex);
                    }
                    callback.onSucess(null);
                }
            }
            
            
        });

        NetworkManager.getInstance().addToQueue(req);
    }
    
    public Map postSyncJSON(XFCustomAction action) throws IOException  {
        ConnectionRequest req = postSync(action);
        if (req.getResponseCode() >= 200 && req.getResponseCode() < 300) {
            JSONParser parser = new JSONParser();
            return parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(req.getResponseData())));
        } else {
            throw new IOException("Failed to get JSON.  Response code "+req.getResponseCode());
        }
        
        
    }
    
    public ConnectionRequest postSync(XFCustomAction action) {
        Object[] out = new Object[1];
        boolean[] complete = new boolean[1];
        Display.getInstance().invokeAndBlock(() -> {
            post(action, r -> {
                out[0] = r;
                complete[0] = true;
                synchronized (complete) {
                    complete.notifyAll();
                }
            });

            while (!complete[0]) {
                synchronized (complete) {
                    try {
                        complete.wait();
                    } catch (Exception ex) {
                    }
                }
            }
        });
        return (ConnectionRequest) out[0];
    }
    
    public void post(XFCustomAction action, SuccessCallback<ConnectionRequest> callback) {
        ConnectionRequest req = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                if (code == 401) {
                    return;
                }
                super.handleErrorResponseCode(code, message);
            }
            
        };
        req.setReadResponseForErrors(true);
        req.setUrl(url);
        req.setHttpMethod("POST");
        req.setPost(true);
        req.setFailSilently(true);
        action.setupRequest(req);
        req.addArgument("--no-prompt", "1");
        
        
        req.addResponseListener(e -> {

            if (e.getResponseCode() == 401) {
                // We need to log in
                handleUnauthorized(false, res -> {
                    if (res) {
                        post(action, callback);
                    } else {
                        if (errorHandler != null) {
                            errorHandler.fireActionEvent(new ActionEvent("Unauthorized"));
                        }
                        callback.onSucess(null);
                    }
                });
                return;

            }
            callback.onSucess(req);

        });
        NetworkManager.getInstance().addToQueue(req);
        

        
    }
    
    /**
     * Finds a result set.
     * @param query
     * @param callback 
     */
    public void find(XFQuery query, SuccessCallback<XFRowSet> callback) {

        ConnectionRequest req = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                if (code == 401) {
                    return;
                }
                super.handleErrorResponseCode(code, message);
            }
            
        };
        req.setReadResponseForErrors(true);
        req.setUrl(url);
        req.setHttpMethod("GET");
        req.setPost(false);
        query.setupRequest(req);
        req.addArgument("--no-prompt", "1");
        System.out.println("About to send request");
        
        
        req.addResponseListener(e -> {

            if (e.getResponseCode() == 401) {
                // We need to log in
                handleUnauthorized(false, res -> {
                    if (res) {
                        find(query, callback);
                    } else {
                        if (errorHandler != null) {
                            errorHandler.fireActionEvent(new ActionEvent("Unauthorized"));
                        }
                        callback.onSucess(null);
                    }
                });
                return;

            }

            
            Display.getInstance().scheduleBackgroundTask(() -> {

                JSONParser jp = new JSONParser();
                try {
                    Map<String, Object> result = jp.parseJSON(new InputStreamReader(new ByteArrayInputStream(req.getResponseData()), "UTF-8"));
                    System.out.println(result);
                    List<Map> results = (List<Map>) result.get("results");
                    XFRowSet rowset = new XFRowSet();
                    rowset.setTable(query.getTable());
                    Result stats = Result.fromContent((Map) result.get("metaData"));
                    rowset.setSkip(stats.getAsInteger("skip"));
                    rowset.setLimit(stats.getAsInteger("limit"));
                    rowset.setFound(stats.getAsInteger("found"));
                    rowset.setQuery(query);
                    for (Map row : results) {
                        XFRecord rec = new XFRecord(this, query.getTable(), Result.fromContent(row), row.keySet());
                        if (row.containsKey("__id__")) {
                            rec.setId((String)row.get("__id__"));
                        }
                        if (row.containsKey("__title__")) {
                            rec.setTitle((String)row.get("__title__"));
                        }
                        rowset.add(rec);
                    }

                    Display.getInstance().callSerially(() -> {
                        callback.onSucess(rowset);
                    });

                } catch (IOException ex) {
                    if (errorHandler != null) {
                        errorHandler.fireActionEvent(new ActionEvent("Failed to parse JSON: " + ex.getMessage()));
                    }
                    callback.onSucess(null);
                }

            });
        });

        NetworkManager.getInstance().addToQueue(req);

    }

    /**
     * Alias of {@link #findSync}
     * @param query
     * @return 
     */
    public XFRowSet findAndWait(XFQuery query) {
        return findSync(query);
    }
    
    public XFRowSet findSync(XFQuery query) {
        Object[] out = new Object[1];
        boolean[] complete = new boolean[1];
        Display.getInstance().invokeAndBlock(() -> {
            find(query, r -> {
                out[0] = r;
                complete[0] = true;
                synchronized (complete) {
                    complete.notifyAll();
                }
            });

            while (!complete[0]) {
                synchronized (complete) {
                    try {
                        complete.wait();
                    } catch (Exception ex) {
                    }
                }
            }
        });
        return (XFRowSet) out[0];
    }

    static String guessMimetype(String filePath) {
        int pointPos = filePath.lastIndexOf(".");
        if (pointPos >= 0) {
            String ext = filePath.substring(pointPos + 1).toLowerCase();
            if ("jpg".equals(ext) || "jpeg".equals(ext)) {
                return "image/jpeg";
            }
            if ("gif".equals(ext)) {
                return "image/gif";
            }
            if ("png".equals(ext)) {
                return "image/png";
            }
            if ("tif".equals(ext)) {
                return "image/tif";
            }
            if ("pdf".equals(ext)) {
                return "application/pdf";
            }
            if ("txt".equals(ext)) {
                return "text/plain";
            }

        }
        return null;
    }

    private static String filename(String path) {
        path = StringUtil.replaceAll(path, "\\", "/");
        if (path.indexOf("/") != -1) {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        return path;
    }

    private void addArgument(ConnectionRequest req, boolean multipart, String key, String val) {
        if (multipart) {
            req.addArgumentNoEncoding(key, Util.encodeUrl(val));
        } else {
            req.addArgument(key, val);
        }
    }
    
    
    public XFRecord saveAndWait(XFRecord record) throws IOException  {
        final Object[] out = new Object[1];
        final Throwable[] outError = new Throwable[1];
        boolean[] complete = new boolean[1];
        Callback<XFRecord> callback = new Callback<XFRecord>() {
            @Override
            public void onSucess(XFRecord r) {
                out[0] = r;
                complete[0] = true;
                synchronized (complete) {
                    complete.notifyAll();
                }
            }

            @Override
            public void onError(Object sender, Throwable err, int errorCode, String errorMessage) {
                outError[0] = err;
                complete[0] = true;
                synchronized(complete) {
                    complete.notifyAll();
                }
            }
            
        };
        Display.getInstance().invokeAndBlock(() -> {
            save(record, callback);

            while (!complete[0]) {
                synchronized (complete) {
                    try {
                        complete.wait();
                    } catch (Exception ex) {
                    }
                }
            }
        });
        if (outError[0] != null) {
            throw new IOException("Failed to save record "+record, outError[0]);
        }
        return (XFRecord) out[0];
    }
    
    public void save(XFRecord record, SuccessCallback<XFRecord> callback) {

        if (!record.changed()) {
            callback.onSucess(record);
            return;
        }

        ConnectionRequest req;
        boolean multipart = false;
        if (record.changedFiles()) {
            req = new MultipartRequest();
            multipart = true;
        } else {
            req = new ConnectionRequest();
        }

        req.setReadResponseForErrors(true);
        req.setUrl(url);
        req.setPost(true);
        req.setHttpMethod("POST");
        StringBuilder fieldsFld = new StringBuilder();
        
        boolean first = true;
        for (String col : record.getChangedColumns()) {
            
            if (!first) {
                fieldsFld.append(" ");
            }
            first = false;
            fieldsFld.append(col);
            addArgument(req, multipart, col, record.getSerializedValue(col));
        }
        req.addArgument("--no-query", "1");

        if (record.changedFiles()) {
            if (!first) {
                fieldsFld.append(" ");
            }
            first = false;
            req.addArgument("MAX_FILE_SIZE", "134217728");
            MultipartRequest mreq = (MultipartRequest) req;
            for (String col : record.getChangedFiles()) {

                try {
                    mreq.addData(col, record.getFile(col), record.getFileMimeType(col));
                    mreq.setFilename(col, filename(record.getFile(col)));
                } catch (Exception ex) {
                    if (errorHandler != null) {
                        errorHandler.fireActionEvent(new ActionEvent(ex));
                    }
                    if (callback instanceof Callback) {
                        ((Callback)callback).onError(this, ex, 0, "Failed to save record "+record);
                    } else {
                        callback.onSucess(null);
                    }
                }
            }

        }
        addArgument(req, multipart, "-fields", fieldsFld.toString());
        req.addArgument("-table", record.getTable());
        req.addArgument("--session:save", "Save");
        req.addArgument("--escape-json", "n");
        if (record.getId() == null) {
            req.addArgument("-new", "1");
            req.addArgument("-action", "new");
            req.addArgument("_qf__new_" + record.getTable() + "_record_form", "");
        } else {
            req.addArgument("-action", "edit");
            req.addArgument("_qf__existing_" + record.getTable() + "_record_form", "");
            req.addArgument("--recordid", record.getId());
            //req.addArgument("__keys__[id]", record.getString("id"));
            Map<String,String> keys = record.parseId();
            for (String col : keys.keySet()) {
                addArgument(req, multipart, "__keys__["+col+"]", keys.get(col));
            }

        }
        //req.addArgument("--no-query", "1");

        req.addArgument("-response", "json");
        req.addArgument("--no-prompt", "1");
        req.addArgument("--date-format", "server");

        req.addResponseListener(nevt -> {
            if (nevt.getResponseCode() == 401) {
                handleUnauthorized(false, res -> {
                    if (res) {
                        save(record, callback);
                    } else {
                        if (errorHandler != null) {
                            errorHandler.fireActionEvent(new ActionEvent("Unauthorized"));
                        }
                        callback.onSucess(null);
                    }
                });
                return;
            }

            if (nevt.getResponseCode() >= 200 && nevt.getResponseCode() < 300) {
                Display.getInstance().scheduleBackgroundTask(() -> {
                    JSONParser jp = new JSONParser();
                    try {
                        System.out.println(new String(req.getResponseData(), "UTF-8"));
                        Map<String, Object> result = jp.parseJSON(new InputStreamReader(new ByteArrayInputStream(req.getResponseData()), "UTF-8"));
                        Map results = (Map) result.get("record_data");
                        XFRecord rec = new XFRecord(this, record.getTable(), Result.fromContent(results), results.keySet());
                        
                        Display.getInstance().callSerially(() -> {
                            callback.onSucess(rec);
                        });
                    } catch (Exception ex) {
                        if (errorHandler != null) {
                            errorHandler.fireActionEvent(new ActionEvent(ex));
                        }
                        Display.getInstance().callSerially(() -> {
                            callback.onSucess(null);
                        });
                    }
                });
            } else {
                if (errorHandler != null) {
                    errorHandler.fireActionEvent(nevt);
                }
                callback.onSucess(null);
            }
        });
        req.addArgument("--dummy", "1");
        NetworkManager.getInstance().addToQueue(req);
    }

    public void delete(XFRecord record, SuccessCallback<Boolean> callback) {
        
        ConnectionRequest req = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                if (code == 401) {
                    return;
                }
                super.handleErrorResponseCode(code, message);
            }
            
        };
        req.setUrl(url);
        req.setPost(true);
        req.setHttpMethod("POST");
        req.setReadResponseForErrors(true);
        req.addArgument("-action", "delete");
        req.addArgument("--recordid", record.getId());
        req.addArgument("-table", record.getTable());
        req.addArgument("-delete-one", "1");
        req.addArgument("--no-prompt", "1");
        Map<String,String> keys = record.parseId();
        for (String key : keys.keySet()) {
            req.addArgument(key, keys.get(key));
        }
        
        req.addArgument("-response", "json");
        req.addResponseListener(nevt->{
            if (nevt.getResponseCode() == 401) {
                handleUnauthorized(false, res -> {
                    if (res) {
                        delete(record, callback);
                    } else {
                        if (errorHandler != null) {
                            errorHandler.fireActionEvent(new ActionEvent("Unauthorized"));
                        }
                        callback.onSucess(false);
                    }
                });
                return;
            }
            
            if (nevt.getResponseCode() >= 200 && nevt.getResponseCode() < 300) {
                JSONParser p = new JSONParser();
                try {
                    Map<String,Object> map = p.parseJSON(new InputStreamReader(new ByteArrayInputStream(req.getResponseData()), "UTF-8"));
                    Result res = Result.fromContent(map);
                    if (res.getAsInteger("code") == 200) {
                        callback.onSucess(true);
                    } else {
                        if (errorHandler != null) {
                            errorHandler.fireActionEvent(new ActionEvent(res.getAsString("message")));
                        }
                        callback.onSucess(true);
                    }
                } catch (Exception ex) {
                    if (errorHandler != null) {
                        errorHandler.fireActionEvent(new ActionEvent(ex.getMessage()));
                    }
                    callback.onSucess(false);
                }
                
                callback.onSucess(true);
            } else {
                if (errorHandler != null) {
                    errorHandler.fireActionEvent(new ActionEvent("Unauthorized"));
                }
                callback.onSucess(false);
            }
        });
        req.addArgument("--dummy","1");
        NetworkManager.getInstance().addToQueue(req);
    }

    public TimeZone getServerTimeZone() {
        if (serverTimeZone == null) {
            serverTimeZone = TimeZone.getDefault();
        }
        return serverTimeZone;
    }

    public void setServerTimeZone(TimeZone tz) {
        serverTimeZone = tz;
    }

    public void logout(SuccessCallback<Boolean> callback) {
        ConnectionRequest c = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                if (code == 401) {
                    return;
                }
                super.handleErrorResponseCode(code, message); 
            }
            
        };
        c.setUrl(url);
        c.setPost(true);
        c.setReadResponseForErrors(true);
        
        c.addArgument("-action", "logout");
        c.addArgument("--no-prompt", "1");
        c.addResponseListener(e -> {
            System.out.println("Response code: " + c.getResponseCode());
            try {
                System.out.println("Content: " + new String(c.getResponseData(), "Utf-8"));
            } catch (Exception ex) {
            }
            if (e.getResponseCode() == 401) {
                callback.onSucess(false);
            } else {
                callback.onSucess(true);
            }
        });
        NetworkManager.getInstance().addToQueue(c);
        
    }
    private void login(SuccessCallback<Boolean> callback) {
        ConnectionRequest c = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                if (code == 401) {
                    return;
                }
                super.handleErrorResponseCode(code, message); 
            }
            
        };
        c.setUrl(url);
        c.setPost(true);
        c.setReadResponseForErrors(true);
        
        c.addArgument("-action", "login");
        c.addArgument("-redirect", "/index.php");
        c.addArgument("UserName", username);
        c.addArgument("Password", password);
        c.addArgument("--no-prompt", "1");
        c.addResponseListener(e -> {
            System.out.println("Response code: " + c.getResponseCode());
            try {
                //System.out.println("Content: " + new String(c.getResponseData(), "Utf-8"));
            } catch (Exception ex) {
            }
            if (e.getResponseCode() == 401) {
                callback.onSucess(false);
            } else {
                callback.onSucess(true);
            }
        });
        NetworkManager.getInstance().addToQueue(c);

    }

    public void login(boolean showPrompt, SuccessCallback<Boolean> callback) {
        handleUnauthorized(showPrompt, callback);
    }

    protected void handleUnauthorized(boolean showPrompt, SuccessCallback<Boolean> onComplete) {
        if (showPrompt) {
            Container loginCnt = new Container();
            TextField usernameField = new TextField();
            usernameField.setText(username);
            

            TextField passwordField = new TextField();
            passwordField.setText(password);
            passwordField.setConstraint(TextArea.PASSWORD);

            usernameField.setNextFocusDown(passwordField);
            
            loginCnt.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            loginCnt.addComponent(new Label("Username"));
            loginCnt.addComponent(usernameField);
            loginCnt.addComponent(new Label("Password"));
            loginCnt.addComponent(passwordField);

            //boolean[] loggedIn = new boolean[1];
            UITimer.timer(300, false, ()->{
                usernameField.startEditingAsync();
            });
            
            Command cmdLogin = new Command("Login") {
                public void actionPerformed(ActionEvent e) {
                    username = usernameField.getText();
                    password = passwordField.getText();
                    login(res -> {
                        if (res) {
                            System.out.println("1");
                            onComplete.onSucess(res);
                        } else {
                            ToastBar.showErrorMessage("Login failed. Try again", 3000);
                            handleUnauthorized(true, onComplete);
                        }
                        //loggedIn[0] = res;
                    });

                    //Preferences.set("username", usernameField.getText());
                    //Preferences.set("password", passwordField.getText());
                }
            };
            
            //usernameField.addActionListener(cmdLogin);
            //passwordField.addActionListener(cmdLogin);
            
            
            Dialog.show("Login", loginCnt, new Command[]{
                cmdLogin,
                new Command("Cancel") {
                    public void actionPerformed(ActionEvent e) {
                        onComplete.onSucess(false);
                    }
                }

            });
            
            //nComplete.onSucess(loggedIn[0]);

        } else {
            login(res -> {
                if (!res) {
                    handleUnauthorized(true, onComplete);
                } else {
                    onComplete.onSucess(res);
                }
            });
        }
    }

    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
}
