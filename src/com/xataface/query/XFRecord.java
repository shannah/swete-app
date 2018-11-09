/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.query;


import com.codename1.io.Util;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.ParseException;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.processing.Result;
import com.xataface.utils.json.core.JSONArray;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Encapsulates a single row from the the databse.
 * @author shannah
 */
public class XFRecord {
    private final XFClient client;
    private String table;
    private String id;
    private Result data;
    private Map<String,Object> updates;
    private Map<String,FileData> fileUpdates;
    private static DateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Set<String> columnNames = new HashSet<String>();
    
    private static class FileData {
        String path;
        String mimetype;
        
        FileData(String path, String mime) {
            this.path = path;
            this.mimetype = mime;
        }
    }
    
    XFRecord(XFClient client, String table, Result data, Collection<String> columnNames) {
        this.table = table;
        this.data = data;
        this.columnNames.addAll(columnNames);
        this.client = client;
    }
    
    public DateFormat getServerDateFormat() {
        return serverDateFormat;
    }
    
    public DateFormat getOutputDateFormat() {
        return outputDateFormat;
    }
    
    public XFRecord(XFClient client, String table, String id) {
        this.table = table;
        this.id = id;
        this.data = Result.fromContent(updates());
        this.client = client;
    }
    
    
    private Map<String, Object> updates() {
        if (updates == null) {
            updates = new HashMap<String,Object>();
        }
        return updates;
    }
    
    private Map<String,FileData> fileUpdates() {
        if (fileUpdates == null) {
            fileUpdates = new HashMap<String,FileData>();
        }
        return fileUpdates;
    }
    
    public boolean changed() {
        return (updates != null && !updates.isEmpty());
    }
    
    public boolean changedFiles() {
        return fileUpdates != null && !fileUpdates.isEmpty();
    }
    
    public boolean changed(String column) {
        return updates != null && updates.containsKey(column);
    }
    
    public boolean changedFile(String column) {
        return fileUpdates != null && fileUpdates.containsKey(column);
    }
    
    public Collection<String> getChangedColumns() {
        if (updates == null) {
            return Collections.EMPTY_SET;
        } else {
            return Collections.unmodifiableCollection(updates().keySet());
        }
    }
    
    public Collection<String> getChangedFiles() {
        if (fileUpdates == null) {
            return Collections.EMPTY_SET;
        } else {
            return Collections.unmodifiableCollection(fileUpdates.keySet());
        }
    }
    
    public String getFile(String column) {
        if (fileUpdates != null) {
            FileData d = fileUpdates.get(column);
            if (d == null) {
                return null;
            }
            return d.path;
        }
        return null;
    }
    
    public void setFile(String column, String path, String mime) {
        if (mime == null) {
            mime = XFClient.guessMimetype(path);
        }
        FileData d = new FileData(path, mime);
        fileUpdates().put(column, d);
    }
            
    
    public String getFileMimeType(String column) {
        if (fileUpdates != null) {
            FileData d = fileUpdates.get(column);
            if (d == null) {
                return null;
            }
            
            return d.mimetype;
        }
        return null;
    }
    
    public int getInt(String column) {
        if (changed(column)) {
            return (int)updates.get(column);
        }
        return data.getAsInteger(column);
    }
    
    public boolean isset(String column) {
        if (changed(column)) {
            return true;
        }
        return data.get(column) != null;
    }
    
    public long getLong(String column) {
        if (changed(column)) {
            return (long)updates.get(column);
        }
        Object v = data.get(column);
        if (isDateStruct(v)) {
            Date d = parseDate((Map)v);
            return d.getTime();
        }
        return data.getAsLong(column);
    }
    
    public String getString(String column) {
        if (changed(column)) {
            return (String)updates.get(column);
        }
        Object v = data.get(column);
        if (isDateStruct(v)) {
            Date d = parseDate((Map)v);
            return outputDateFormat.format(d);
        }
        return data.getAsString(column);
    }
    
    public List<String> getList(String column) {
        if (changed(column)) {
            Object o = updates.get(column);
            List out = new ArrayList();
            if (o instanceof Collection) {
                for (Object oi : (Collection)o) {
                    out.add(String.valueOf(oi));
                }
                
            } else if (o != null) {
                out.add(String.valueOf(o));  
            }
            
            return out;
        }
        List<String> tmp = data.getAsArray(column);
        List<String> l = new ArrayList<String>();
        if (tmp != null) {
            for (Object o : tmp) {
                l.add(String.valueOf(o));
            }
        }
        return l;
        
    }
    
    
    public void add(String column, Object value) {
        List l = getList(column);
        l.add(String.valueOf(value));
        updates.put(column, l);
    }
    
    public void remove(String column, Object value) {
        List l = getList(column);
        l.remove(String.valueOf(value));
        updates.put(column, l);
    }
    
    private boolean isDateStruct(Object o) {
        if (o instanceof Map) {
            Map m = (Map)o;
            return m.containsKey("year");
        }
        return false;
    }
    
    public boolean getBoolean(String column) {
        if (changed(column)) {
            return (boolean)updates.get(column);
        }
        return data.getAsBoolean(column);
    }
    
    public double getDouble(String column) {
        if (changed(column)) {
            return (double)updates.get(column);
        }
        Object v = data.get(column);
        if (isDateStruct(v)) {
            Date d = parseDate((Map)v);
            return (double)d.getTime();
        }
        return data.getAsDouble(column);
    }
    
    public Date getDate(String column) {
        if (updates != null && updates.containsKey(column)) {
            Object out = updates.get(column);
            if (out instanceof Date) {
                return (Date)out;
            }
        } else {
            Object v = data.get(column);
            if (isDateStruct(v)) {
                Date d = parseDate((Map)v);
                return d;
            }
        }
        
        try {
            String str = getString(column);
            if (str != null) {
                return serverDateFormat.parse(str);
            } else {
                return null;
            }
        } catch (ParseException ex) {
            try {
                return new Date(Long.parseLong(getString(column)));
            } catch (NumberFormatException ex2) {
                return null;
            }
        }
    }
    
    private static Long asNumber(String value) {
        try {
            long val = Long.parseLong(value);
            return val;
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Parses map structure of date as returned form server.
     * date expected to be in server timezone.
     * @param m
     * @return 
     */
    private Date parseDate(Map m) {
        Result res = Result.fromContent(m);
        Calendar cal = Calendar.getInstance(client.getServerTimeZone());
        cal.set(Calendar.YEAR, res.getAsInteger("year"));
        cal.set(Calendar.MONTH, res.getAsInteger("month"));
        cal.set(Calendar.DAY_OF_MONTH, res.getAsInteger("day"));
        cal.set(Calendar.HOUR_OF_DAY, res.getAsInteger("hours"));
        cal.set(Calendar.MINUTE, res.getAsInteger("minutes"));
        cal.set(Calendar.SECOND, res.getAsInteger("seconds"));
        return cal.getTime();
    }
    
    private Date parseDate(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        Long num = asNumber(value);
        if (num != null) {
            return new Date(num);
        } else if (value.indexOf(" ") > 0) {
            // it's a date time
            String[] parts = Util.split(value, " ");
            String dt = parts[0];
            String time = parts[1];
            
            if (dt.indexOf("-") < 0) {
                return null;
            }
            parts = Util.split(dt, "-");
            Calendar cal = Calendar.getInstance(client.getServerTimeZone());
            cal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(parts[1])-1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
            
            
            if (time.indexOf(":") < 0) {
                return null;
            }
            
            parts = Util.split(time, ":");
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(removeLeadingZero(parts[0])));
            cal.set(Calendar.MINUTE, Integer.parseInt(removeLeadingZero(parts[1])));
            cal.set(Calendar.SECOND, Integer.parseInt(removeLeadingZero(parts[2])));
            
            return cal.getTime();
        } else {
            if (value.indexOf("-") < 0) {
                return null;
            }
            String[] parts = Util.split(value, "-");
            Calendar cal = Calendar.getInstance(client.getServerTimeZone());
            cal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(removeLeadingZero(parts[1]))-1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(removeLeadingZero(parts[2])));
            return cal.getTime();
        }
        
        
    }
    
    private String removeLeadingZero(String intVal) {
        if (intVal.length() > 1 && intVal.charAt(0) == '0') {
            return intVal.substring(1);
        } else {
            return intVal;
        }
    }
    
    private String addLeadingZero(String intVal) {
        if (intVal.length() < 2) {
            return "0" + intVal;
        }
        return intVal;
    }
    
    private String formatDateForServer(Date dt) {
        Calendar cal = Calendar.getInstance(client.getServerTimeZone());
        cal.setTime(dt);
        return cal.get(Calendar.YEAR) + "-" + addLeadingZero(String.valueOf(cal.get(Calendar.MONTH)+1)) + "-" 
                + addLeadingZero(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)))
                + " "
                + addLeadingZero(String.valueOf(cal.get(Calendar.HOUR_OF_DAY))) + ":"
                + addLeadingZero(String.valueOf(cal.get(Calendar.MINUTE))) + ":"
                + addLeadingZero(String.valueOf(cal.get(Calendar.SECOND)));
        
    }
    
    public String getTitle() {
        return data.getAsString("__title__");
    }
    
    public String getId() {
        if (id != null) {
            return id;
        }
        return data.getAsString("__id__");
    }
    
    public void set(String column, int value) {
        updates().put(column, value);
    }
    
    public void set(String column, long value) {
        updates().put(column, value);
    }
    
    public void set(String column, double value) {
        updates().put(column, value);
    }
    
    public void set(String column, String value) {
        updates().put(column, value);
    }
    
    public void set(String column, boolean value) {
        updates().put(column, value);
    }
    
    public void set(String column, Date value) {
        updates().put(column, value);
    }
    
    public void set(String column, String dateValue, DateFormat dateFormat) throws ParseException {
        if (dateValue == null || dateValue.trim().length() == 0) {
            updates().put(column, null);
            return;
        }
        updates().put(column, dateFormat.parse(dateValue));
    }
    
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTable() {
        return table;
    }
    
    
    private String listToJSON(List<String> l) {
        JSONArray a = new JSONArray();
        int index = 0;
        try {
            for (String s : l) {
                a.put(index++, s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return a.toString();
        
    }
    
    String getSerializedValue(String column) {
        if (changed(column)) {
            Object val = updates().get(column);
            if (val instanceof Date) {
                return formatDateForServer((Date)val);
            } else if (val instanceof List) {
                return listToJSON((List<String>)val);
            } else {
                return String.valueOf(val);
            }
        } else {
            return data.getAsString(column);
        }
        
               
    }
    
    public Collection<String> getColumnNames() {
        Set<String> out = new HashSet<String>();
        if (updates != null) {
            out.addAll(updates.keySet());
        }
        out.addAll(columnNames);
        out.remove("__id__");
        out.remove("__title__");
        return out;
    }
    
    public Collection<String> getFileColumnNames() {
        if (fileUpdates == null && fileUpdates.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        return Collections.unmodifiableCollection(fileUpdates.keySet());
    }
    
    Map<String,String> parseId() {
        if (getId() == null) {
            return null;
        }
        String[] parts = Util.split(getId(), "?");
        String table = parts[0];
        String fields = parts[1];
        
        parts = Util.split(fields, "&");
        Map<String,String> out = new HashMap<String,String>();
        for (String part : parts) {
            String[] kv = Util.split(part, "=");
            out.put(kv[0], kv[1]);
        }
        return out;
        
    }
}
