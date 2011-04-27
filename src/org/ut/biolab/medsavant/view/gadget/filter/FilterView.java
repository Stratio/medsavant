/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant.view.gadget.filter;

import javax.swing.JComponent;
import org.ut.biolab.medsavant.controller.FilterGenerator;

/**
 *
 * @author mfiume
 */
class FilterView {

    private JComponent _component;
    private String _title;
    private FilterGenerator filterGenerator;

    public FilterView(String title, JComponent content, FilterGenerator fg) {
        setTitle(title);
        setComponent(content);
        setFilterGenerator(fg);
    }

    public String getTitle() {
        return _title;
    }

    private void setTitle(String title) {
        _title = title;
    }

    public JComponent getComponent() {
        return _component;
    }

    private void setComponent(JComponent component) {
        _component = component;
    }

    private void setFilterGenerator(FilterGenerator fg) {
        this.filterGenerator = fg;
    }

     public FilterGenerator getFilterGenerator() {
        return filterGenerator;
    }
}
