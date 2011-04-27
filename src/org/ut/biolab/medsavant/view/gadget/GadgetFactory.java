/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ut.biolab.medsavant.view.gadget;

import com.jidesoft.dashboard.Gadget;
import fiume.table.SearchableTablePanel;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.ut.biolab.medsavant.controller.ResultController;
import org.ut.biolab.medsavant.images.IconFactory;
import org.ut.biolab.medsavant.model.VariantRecordModel;
import org.ut.biolab.medsavant.util.Util;
import org.ut.biolab.medsavant.view.gadget.chart.ChartPanel;
import org.ut.biolab.medsavant.view.gadget.filter.FilterPanel;

/**
 *
 * @author mfiume
 */
public class GadgetFactory {

    public static Gadget createFilterGadget() {
        GenericGadget g = new GenericGadget("Filters",new GadgetContentGenerator() {

            public JComponent generateGadgetContent() {
                return new FilterPanel();
            }
        });
        g.setLargeIcon(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.FILTER));
        return g;
    }

    public static Gadget createResultsGadget() {
        GenericGadget g = new GenericGadget("Results",new GadgetContentGenerator() {
            public JComponent generateGadgetContent() {
                Vector records = Util.getVariantRecordsVector(ResultController.getVariantRecords());
                JPanel p = new SearchableTablePanel(records, VariantRecordModel.getFieldNames(), VariantRecordModel.getFieldClasses());
                return p;
            }
        });
        g.setLargeIcon(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.RESULTS));
        return g;
    }

    public static Gadget createChartGadget() {
        GenericGadget g = new GenericGadget("Chart",new GadgetContentGenerator() {
            public JComponent generateGadgetContent() {
                return new ChartPanel();
            }
        });
        g.setLargeIcon(IconFactory.getInstance().getIcon(IconFactory.StandardIcon.CHART));
        return g;
    }




}
