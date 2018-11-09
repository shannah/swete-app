/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.query;

import com.codename1.io.ConnectionRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class XFQuery {

    private String table;
    private Map<String, ColumnFilter> columnFilters = new HashMap<String, ColumnFilter>();
    private String search;
    private int skip;
    private int limit = 30;

    private List<String> columns;
    private boolean preview = true;
    private DisplayMode displayMode = DisplayMode.VALUE;
    private boolean selectTitle=true;
    private boolean selectId=true;
    private FindMode findMode = FindMode.ALL;
    private String profile = "basic";
    private List<ColumnSort> sort = new ArrayList<ColumnSort>();

    private List<String> selectedIds;

    public XFQuery(String table) {
        this.table = table;
    }

    private static enum DisplayMode {

        VALUE,
        DISPLAY,
        HTML
    }

    private static enum FindMode {

        ONE,
        ALL
    }

    public static enum SortOrder {

        DESCENDING,
        ASCENDING;
    }

    private static class ColumnSort {

        private String column;
        private SortOrder order;

        private ColumnSort(String column, SortOrder sort) {
            this.column = column;
            this.order = sort;
        }
    }

    public XFQuery sort(SortOrder direction, String... columns) {
        for (String col : columns) {
            sort.add(new ColumnSort(col, direction));
        }
        return this;
    }

    private static enum FilterType {

        EXACT,
        CONTAINS,
        LIKE,
        LESS_THAN,
        GREATER_THAN,
        LESS_THAN_OR_EQUAL,
        GREATER_THAN_OR_EQUAL,
        RANGE
    }

    public XFQuery search(String search) {
        this.search = search;
        return this;
    }

    public XFQuery select(String... columns) {
        this.columns = new ArrayList<String>();
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }

    public XFQuery selectId(boolean selectId) {
        this.selectId = selectId;
        return this;
    }

    public String getTable() {
        return table;
    }

    public XFQuery selectTitle(boolean selectTitle) {
        this.selectTitle = selectTitle;
        return this;
    }

    //public XFQuery setDisplayMode(DisplayMode mode) {
    //    this.displayMode = mode;
    //    return this;
    //}
    public XFQuery selectRawValues() {
        displayMode = DisplayMode.VALUE;
        return this;
    }

    public XFQuery selectDisplayValues() {
        displayMode = DisplayMode.DISPLAY;
        return this;
    }

    public XFQuery selectHtmlValues() {
        displayMode = DisplayMode.HTML;
        return this;
    }

    public XFQuery findOne() {
        findMode = FindMode.ONE;
        return this;
    }

    public XFQuery byId(String... ids) {
        selectedIds = new ArrayList<String>();
        selectedIds.addAll(Arrays.asList(ids));
        return this;
    }

    public XFQuery findAll() {
        findMode = FindMode.ALL;
        return this;
    }

    public XFQuery setProfile(String profile) {
        this.profile = profile;
        return this;
    }

    public XFQuery contains(String column, String val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.CONTAINS));
        return this;
    }

    public XFQuery containsAny(String column, String[] values) {
        ArrayList<ColumnFilter> subFilters = new ArrayList<ColumnFilter>();
        for (String value : values) {
            subFilters.add(new ColumnFilter(column, value, FilterType.CONTAINS));
        }
        columnFilters.put(column, new ColumnFilter(column, subFilters));
        return this;
    }

    public XFQuery matches(String column, String val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.EXACT));
        return this;
    }

    public XFQuery matchesAny(String column, String[] values) {
        ArrayList<ColumnFilter> subFilters = new ArrayList<ColumnFilter>();
        for (String value : values) {
            subFilters.add(new ColumnFilter(column, value, FilterType.EXACT));
        }
        columnFilters.put(column, new ColumnFilter(column, subFilters));
        return this;
    }

    public XFQuery lessThan(String column, String val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.LESS_THAN));
        return this;
    }

    public XFQuery greaterThan(String column, String val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.GREATER_THAN));
        return this;
    }

    public XFQuery lessThanOrEqual(String column, String val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.LESS_THAN_OR_EQUAL));
        return this;
    }

    public XFQuery greaterThanOrEqual(String column, String val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.GREATER_THAN_OR_EQUAL));
        return this;
    }

    public XFQuery in(String column, String lower, String upper) {
        columnFilters.put(column, new ColumnFilter(column, lower, upper, FilterType.RANGE));
        return this;
    }

    public XFQuery like(String column, String val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.LIKE));
        return this;
    }

    public XFQuery matches(String column, int val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.EXACT));
        return this;
    }

    public XFQuery matchesAny(String column, int[] values) {
        ArrayList<ColumnFilter> subFilters = new ArrayList<ColumnFilter>();
        for (int value : values) {
            subFilters.add(new ColumnFilter(column, value, FilterType.EXACT));
        }
        columnFilters.put(column, new ColumnFilter(column, subFilters));
        return this;
    }

    public XFQuery lessThan(String column, int val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.LESS_THAN));
        return this;
    }

    public XFQuery greaterThan(String column, int val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.GREATER_THAN));
        return this;
    }

    public XFQuery lessThanOrEqual(String column, int val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.LESS_THAN_OR_EQUAL));
        return this;
    }

    public XFQuery greaterThanOrEqual(String column, int val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.GREATER_THAN_OR_EQUAL));
        return this;
    }

    public XFQuery in(String column, int lower, int upper) {
        columnFilters.put(column, new ColumnFilter(column, lower, upper, FilterType.RANGE));
        return this;
    }

    public XFQuery contains(String column, Date val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.CONTAINS));
        return this;
    }

    public XFQuery matches(String column, Date val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.EXACT));
        return this;
    }

    public XFQuery lessThan(String column, Date val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.LESS_THAN));
        return this;
    }

    public XFQuery greaterThan(String column, Date val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.GREATER_THAN));
        return this;
    }

    public XFQuery lessThanOrEqual(String column, Date val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.LESS_THAN_OR_EQUAL));
        return this;
    }

    public XFQuery greaterThanOrEqual(String column, Date val) {
        columnFilters.put(column, new ColumnFilter(column, val, FilterType.GREATER_THAN_OR_EQUAL));
        return this;
    }

    public XFQuery in(String column, Date lower, Date upper) {
        columnFilters.put(column, new ColumnFilter(column, lower, upper, FilterType.RANGE));
        return this;
    }

    void setupRequest(ConnectionRequest req) {
        req.addArgument("--profile", profile);
        req.addArgument("-action", "export_json");
        req.addArgument("-table", table);
        req.addArgument("--var", "results");
        req.addArgument("--stats", "1");
        if (selectTitle) {
            req.addArgument("--include-title", "1");
        }
        if (selectId) {
            req.addArgument("--include-id", "1");
        }

        if (columns != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String col : columns) {
                if (!first) {
                    sb.append(" ");
                }
                first = false;
                sb.append(col);
            }
            req.addArgument("--fields", sb.toString());
        }
        
        switch (displayMode) {
            case VALUE:
                req.addArgument("--displayMethod", "val");
                break;
            case DISPLAY:
                req.addArgument("--displayMethod", "display");
                break;
            case HTML:
                req.addArgument("--displayMethod", "html");
                break;
        }
        
        req.addArgument("--filesDisplayMode", "getURL");

        
        if (selectedIds != null && !selectedIds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String id : selectedIds) {
                if (!first) {
                    sb.append("\n");
                }
                sb.append(id);
            }
            req.addArgument("--selected-ids", sb.toString());
            
        } else {

            if (search != null) {
                req.addArgument("-search", search);
            }

            if (columnFilters != null) {
                for (ColumnFilter filter : columnFilters.values()) {
                    filter.add(req);
                }
            }

            switch (findMode) {
                case ALL:
                    req.addArgument("-mode", "list");
                    break;
                case ONE:
                    req.addArgument("-mode", "browse");
                    break;
            }

            

            if (findMode == FindMode.ONE) {
                req.addArgument("-cursor", String.valueOf(skip));
                req.addArgument("-skip", "0");
                req.addArgument("-limit", "0");

            } else {
                req.addArgument("-skip", String.valueOf(skip));
                if (limit > 0) {
                    req.addArgument("-limit", String.valueOf(limit));
                }
            }

            if (sort != null && !sort.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (ColumnSort cs : sort) {
                    if (!first) {
                        sb.append(",");
                    }
                    first = false;
                    sb.append(cs.column).append(" ");
                    switch (cs.order) {
                        case ASCENDING:
                            sb.append("asc");
                            break;
                        case DESCENDING:
                            sb.append("desc");
                            break;
                    }
                }
                req.addArgument("-sort", sb.toString());
            }
        }

    }

    private static class ColumnFilter {

        private FilterType type;
        private List<ColumnFilter> orFilters;
        private String column;
        private Object[] values; // May be up to 2 in length for range filters

        private void add(ConnectionRequest req) {
            switch (type) {
                case RANGE:
                    req.addArgument(column, getSingleValueAsString() + ".." + getSecondValueAsString());
                    break;
                default:
                    req.addArgument(column, getSingleValuePrefix() + getSingleValueAsString());
            }
        }

        private String getSingleValueAsString() {
            if (orFilters != null) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (ColumnFilter f : orFilters) {
                    if (!first) {
                        sb.append(" OR ");
                    }
                    first = false;
                    sb.append(f.getSingleValuePrefix() + f.getSingleValueAsString());
                }
                return sb.toString();
            }
            return getValueAsString(values[0]);
        }

        private String getSecondValueAsString() {
            if (values.length < 2) {
                return null;
            }
            return getValueAsString(values[1]);
        }

        private String getValueAsString(Object val) {
            if (val instanceof Date) {
                Date dt = (Date) val;
                System.out.println(dt);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);
                String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
                if (month.length() == 1) {
                    month = "0" + month;
                }

                String day = String.valueOf(cal.get(Calendar.DATE));
                if (day.length() == 1) {
                    day = "0" + day;
                }

                String hours = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                if (hours.length() == 1) {
                    hours = "0" + hours;
                }

                String minutes = String.valueOf(cal.get(Calendar.MINUTE));
                if (minutes.length() == 1) {
                    minutes = "0" + minutes;
                }

                String seconds = String.valueOf(cal.get(Calendar.SECOND));
                if (seconds.length() == 1) {
                    seconds = "0" + seconds;
                }
                return cal.get(Calendar.YEAR) + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
            }
            return String.valueOf(val);
        }

        private String getSingleValuePrefix() {
            switch (type) {
                case EXACT:
                    return "=";
                case LESS_THAN:
                    return "<";
                case LESS_THAN_OR_EQUAL:
                    return "<=";
                case GREATER_THAN:
                    return ">";
                case GREATER_THAN_OR_EQUAL:
                    return ">=";
                case LIKE:
                    return "~";

            }
            return "";
        }

        private ColumnFilter(String col, Collection<ColumnFilter> orFilters) {
            orFilters = new ArrayList<ColumnFilter>();
            orFilters.addAll(orFilters);
        }

        private ColumnFilter(String col, String val, FilterType type) {
            this.column = col;
            this.values = new Object[]{val};
            this.type = type;
        }

        private ColumnFilter(String col, String lower, String upper, FilterType type) {
            this.column = col;
            this.values = new Object[]{lower, upper};
            this.type = type;
        }

        private ColumnFilter(String col, int val, FilterType type) {
            this.column = col;
            this.values = new Object[]{val};
            this.type = type;
        }

        private ColumnFilter(String col, int lower, int upper, FilterType type) {
            this.column = col;
            this.values = new Object[]{lower, upper};
            this.type = type;
        }

        private ColumnFilter(String col, Date val, FilterType type) {
            this.column = col;
            this.values = new Object[]{val};
            this.type = type;
        }

        private ColumnFilter(String col, Date lower, Date upper, FilterType type) {
            this.column = col;
            this.values = new Object[]{lower, upper};
            this.type = type;
        }
    }

    public XFQuery getNextQuery() {
        return getNextQuery(limit);
    }

    public XFQuery getNextQuery(int count) {
        XFQuery out = new XFQuery(getTable());
        out.columnFilters = columnFilters;
        out.columns = columns;
        out.displayMode = displayMode;
        out.findMode = findMode;
        out.limit = limit;
        out.preview = preview;
        out.profile = profile;
        out.search = search;
        out.selectId = selectId;
        out.selectTitle = selectTitle;
        out.skip = skip;
        out.sort = sort;
        out.table = table;

        out.skip = skip + limit;
        out.limit = count;

        return out;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }
    
    public XFQuery limit(int limit) {
        this.limit = limit;
        return this;
    }
    
    public XFQuery skip(int skip) {
        this.skip = skip;
        return this;
    }

}
