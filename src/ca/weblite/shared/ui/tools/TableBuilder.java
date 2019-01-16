/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.shared.ui.tools;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.table.TableLayout;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shannah
 */
public class TableBuilder {
    private List<List<Component>> cmps;
    private int cols;
    private int[] widthPercentages;
    
    public TableBuilder(int... widthPercentages) {
        this.widthPercentages = widthPercentages;
    }
    
    private List<Component> row() {
        if (cmps == null) {
            cmps = new ArrayList<>();
        }
        if (cmps.isEmpty()) {
            cmps.add(new ArrayList<Component>());
        }
        return cmps.get(cmps.size()-1);
    }
    
    public TableBuilder append(Component cmp) {
        row().add(cmp);
        cols = Math.max(cols, row().size());
        return this;
    }
    
    public TableBuilder append(String str) {
        return append(new Label(str));
    }
    
    public TableBuilder newRow() {
        if (cmps == null) {
            cmps = new ArrayList<>();
        }
        cmps.add(new ArrayList<Component>());
        return this;
    }
    
    public Container build() {
        TableLayout tl = new TableLayout(cmps.size(), cols);
        Container cnt = new Container(tl);
        int rowNum = 0;
        for (List<Component> row : cmps) {
            
            for (int i=0; i<cols; i++) {
                Component cmp = i < row.size() ? row.get(i) : null;
                TableLayout.Constraint cnst = tl.createConstraint(rowNum, i);
                cnst.setVerticalAlign(Component.TOP);
                if (widthPercentages.length > i) {
                    cnst.setWidthPercentage(widthPercentages[i]);
                }
                if (cmp == null) {
                    cnt.add(cnst, new Label());
                } else {
                    
                    cnt.add(cnst, cmp);
                }
            }
            rowNum++;
        }
        return cnt;
    }
}