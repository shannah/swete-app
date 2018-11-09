/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xataface.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates a set of records retrieved from the database.
 * @author shannah
 */
public class XFRowSet implements Iterable<XFRecord> {
    private String table;
    private int limit;
    private int found;
    private int skip;
    private List<XFRecord> rows = new ArrayList<XFRecord>();
    private XFQuery query;
    
    /**
     * Add a record to this set.
     * @param record 
     */
    void add(XFRecord record) {
        rows.add(record);
    }

    /**
     * Iterator to iterate through the records of this set.
     * @return 
     */
    @Override
    public Iterator<XFRecord> iterator() {
        return rows.iterator();
    }

    /**
     * The table from which this rowset came.
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * The table from which this row set came.
     * @param table the table to set
     */
    void setTable(String table) {
        this.table = table;
    }

    /**
     * The max number of records to fetch in this result set.
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * The max number of records to fetch to this row set.
     * @param limit the limit to set
     */
    void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * The number of records in the found set.  The row set may not contain this many records
     * since the RowSet will only conatin a max {@code limit} number of records.
     * @return the found
     */
    public int getFound() {
        return found;
    }

    /**
     * @param found the found to set
     */
    void setFound(int found) {
        this.found = found;
    }

    /**
     * Gets the skip position of this rowset within the found set.  0 == no skip.
     * @return the skip
     */
    public int getSkip() {
        return skip;
    }

    /**
     * Skip position within the found set.  0 == no skip (i.e. first record of this rowset is the first record of the found set).
     * @param skip the skip to set
     */
    void setSkip(int skip) {
        this.skip = skip;
    }
    
    /**
     * Returns the query that was used to produce this set.
     * @return 
     */
    public XFQuery getQuery() {
        return query;
    }
    
    /**
     * Sets the query that was used to get this set.
     * @param query 
     */
    void setQuery(XFQuery query) {
        this.query = query;
    }
    
    /**
     * Position of first record.  First record is 1.
     * getFirst() == getSkip() + 1
     * @return 
     */
    public int getFirst() {
        return getSkip() + 1;
    }
    
    /**
     * Position of last record.  First record is 1.  
     * min(getFound(), getSkip() + getLimit()) == getLast()
     * @return 
     */
    public int getLast() {
        return Math.min(getSkip() + getLimit(), getFound());
    }
    
}
