/*
 *    Copyright 2011-2012 University of Toronto
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.ut.biolab.medsavant.view.genetics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.TableModelWrapperUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ut.biolab.medsavant.MedSavantClient;
import org.ut.biolab.medsavant.controller.ResultController;
import org.ut.biolab.medsavant.db.DefaultVariantTableSchema;
import org.ut.biolab.medsavant.db.Settings;
import org.ut.biolab.medsavant.format.CustomField;
import org.ut.biolab.medsavant.format.AnnotationFormat;
import org.ut.biolab.medsavant.format.VariantFormat;
import org.ut.biolab.medsavant.controller.LoginController;
import org.ut.biolab.medsavant.model.VariantComment;
import org.ut.biolab.medsavant.model.event.VariantSelectionChangedListener;
import org.ut.biolab.medsavant.project.ProjectController;
import org.ut.biolab.medsavant.controller.ReferenceController;
import org.ut.biolab.medsavant.util.*;
import org.ut.biolab.medsavant.vcf.VariantRecord;
import org.ut.biolab.medsavant.view.component.SearchableTablePanel;
import org.ut.biolab.medsavant.view.genetics.filter.FilterUtils;
import org.ut.biolab.medsavant.view.util.DialogUtils;
import org.ut.biolab.medsavant.view.util.WaitPanel;

/**
 *
 * @author mfiume
 */
public class TablePanel extends JLayeredPane {
    private static final Log LOG = LogFactory.getLog(TablePanel.class);
    private SearchableTablePanel tablePanel;
    private WaitPanel waitPanel;
    private boolean init = false;
    private boolean updateRequired = true;
    private final Object updateLock = new Object();
    private String pageName;
    private GridBagConstraints c;
    private Map<Integer, List<VariantComment>> starMap = new HashMap<Integer, List<VariantComment>>();
    private static List<VariantSelectionChangedListener> listeners = new ArrayList<VariantSelectionChangedListener>();


    public TablePanel(String page) {

        this.pageName = page;
        this.setLayout(new GridBagLayout());

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;

        waitPanel = new WaitPanel("Generating List View");

        add(waitPanel, c, JLayeredPane.MODAL_LAYER);

        showWaitCard();

        MedSavantWorker worker = new MedSavantWorker(pageName) {

            @Override
            protected Object doInBackground() throws Exception {

                List<String> fieldNames = new ArrayList<String>();
                final List<Class> fieldClasses = new ArrayList<Class>();
                List<Integer> hiddenColumns = new ArrayList<Integer>();

                AnnotationFormat[] afs = ProjectController.getInstance().getCurrentAnnotationFormats();
                for (AnnotationFormat af : afs) {
                    for (CustomField field : af.getCustomFields()) {
                        fieldNames.add(field.getAlias());
                        switch (field.getColumnType()) {
                            case INTEGER:
                            case BOOLEAN:
                                fieldClasses.add(Integer.class);
                                break;
                            case FLOAT:
                            case DECIMAL:
                                fieldClasses.add(Double.class);
                                break;
                            case VARCHAR:
                            default:
                                fieldClasses.add(String.class);
                                break;
                        }

                        //only show some vcf fields
                        if (!(af.getProgram().equals(VariantFormat.ANNOTATION_FORMAT_DEFAULT)
                                && !(field.getColumnName().equals(DefaultVariantTableSchema.COLUMNNAME_OF_UPLOAD_ID)
                                || field.getColumnName().equals(DefaultVariantTableSchema.COLUMNNAME_OF_FILE_ID)
                                || field.getColumnName().equals(DefaultVariantTableSchema.COLUMNNAME_OF_VARIANT_ID)
                                || field.getColumnName().equals(DefaultVariantTableSchema.COLUMNNAME_OF_FILTER)
                                || field.getColumnName().equals(DefaultVariantTableSchema.COLUMNNAME_OF_QUAL)
                                || field.getColumnName().equals(DefaultVariantTableSchema.COLUMNNAME_OF_GT)
                                || field.getColumnName().equals(DefaultVariantTableSchema.COLUMNNAME_OF_DBSNP_ID)
                                || field.getColumnName().equals(DefaultVariantTableSchema.COLUMNNAME_OF_CUSTOM_INFO)))) {
                            //|| af.getProgram().equals(VariantFormat.ANNOTATION_FORMAT_CUSTOM_VCF))) {
                            hiddenColumns.add(fieldNames.size() - 1);
                        }
                    }
                }
                if (isCancelled()) {
                    throw new InterruptedException();
                }

                final DataRetriever<Object[]> retriever = new DataRetriever<Object[]>() {

                    @Override
                    public List<Object[]> retrieve(int start, int limit) {
                        showWaitCard();
                        try {
                            List<Object[]> result = ResultController.getInstance().getFilteredVariantRecords(start, limit, null);
                            //checkStarring(result);
                            showShowCard();
                            return result;
                        } catch (Exception ex) {
                            LOG.error("Error retrieving data.", ex);
                            showShowCard();
                            return null;
                        }
                    }

                    @Override
                    public int getTotalNum() {
                        showWaitCard();
                        int result = 0;
                        try {
                            result = ResultController.getInstance().getFilteredVariantCount();
                        } catch (Exception ex) {
                            LOG.error("Error getting total number.", ex);
                        }
                        showShowCard();
                        return result;
                    }

                    @Override
                    public void retrievalComplete() {
                        synchronized (updateLock) {
                            updateRequired = false;
                        }
                    }
                };

                final SearchableTablePanel stp = new SearchableTablePanel(pageName, fieldNames.toArray(new String[0]), fieldClasses.toArray(new Class[0]), MiscUtils.toIntArray(hiddenColumns), 1000, retriever) {

                    @Override
                    public String getToolTip(int actualRow) {
                        if (starMap.get(actualRow) != null && !starMap.get(actualRow).isEmpty()) {
                            String s = "<HTML>";
                            List<VariantComment> starred = starMap.get(actualRow);
                            for (int i = 0; i < starred.size(); i++) {
                                VariantComment current = starred.get(i);
                                s += "\"" + ClientMiscUtils.addBreaksToString(current.getDescription(), 100) + "\"<BR>";
                                s += "- " + current.getUser() + ", " + current.getTimestamp().toString();
                                if (i != starred.size() - 1) {
                                    s += "<BR>----------<BR>";
                                }
                            }
                            s += "</HTML>";
                            return s;
                        }
                        return null;
                    }
                };

                stp.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        int rowToFetch = stp.getTable().getSelectedRows()[0];

                        int uploadID = (Integer) stp.getTable().getModel().getValueAt(rowToFetch, DefaultVariantTableSchema.INDEX_OF_UPLOAD_ID);
                        int fileID = (Integer) stp.getTable().getModel().getValueAt(rowToFetch, DefaultVariantTableSchema.INDEX_OF_FILE_ID);
                        int variantID = (Integer) stp.getTable().getModel().getValueAt(rowToFetch, DefaultVariantTableSchema.INDEX_OF_VARIANT_ID);

                        DbColumn uIDcol = ProjectController.getInstance().getCurrentVariantTableSchema().getDBColumn(DefaultVariantTableSchema.COLUMNNAME_OF_UPLOAD_ID);
                        DbColumn fIDcol = ProjectController.getInstance().getCurrentVariantTableSchema().getDBColumn(DefaultVariantTableSchema.COLUMNNAME_OF_FILE_ID);
                        DbColumn vIDcol = ProjectController.getInstance().getCurrentVariantTableSchema().getDBColumn(DefaultVariantTableSchema.COLUMNNAME_OF_VARIANT_ID);

                        Condition[][] conditions = new Condition[1][3];
                        conditions[0][0] = BinaryConditionMS.equalTo(uIDcol, uploadID);
                        conditions[0][1] = BinaryConditionMS.equalTo(fIDcol, fileID);
                        conditions[0][2] = BinaryConditionMS.equalTo(vIDcol, variantID);


                        List<Object[]> rows;
                        try {
                            rows = MedSavantClient.VariantManager.getVariants(
                                    LoginController.sessionId,
                                    ProjectController.getInstance().getCurrentProjectID(),
                                    ReferenceController.getInstance().getCurrentReferenceID(),
                                    conditions,
                                    0, 1);

                        } catch (Exception ex) {
                            DialogUtils.displayError("Error", "There was a problem retriving variant results");
                            return;
                        }

                        Object[] row = rows.get(0);

                        VariantRecord r = new VariantRecord(
                                (Integer)   row[DefaultVariantTableSchema.INDEX_OF_UPLOAD_ID],
                                (Integer)   row[DefaultVariantTableSchema.INDEX_OF_FILE_ID],
                                (Integer)   row[DefaultVariantTableSchema.INDEX_OF_VARIANT_ID],
                                (Integer)   ReferenceController.getInstance().getCurrentReferenceID(),
                                (Integer)   0, // pipeline ID
                                (String)    row[DefaultVariantTableSchema.INDEX_OF_DNA_ID],
                                (String)    row[DefaultVariantTableSchema.INDEX_OF_CHROM],
                                (Integer)   row[DefaultVariantTableSchema.INDEX_OF_POSITION], // TODO: this should be a long
                                (String)    row[DefaultVariantTableSchema.INDEX_OF_DBSNP_ID],
                                (String)    row[DefaultVariantTableSchema.INDEX_OF_REF],
                                (String)    row[DefaultVariantTableSchema.INDEX_OF_ALT],
                                (Float)     row[DefaultVariantTableSchema.INDEX_OF_QUAL],
                                (String)    row[DefaultVariantTableSchema.INDEX_OF_FILTER],
                                (String)    row[DefaultVariantTableSchema.INDEX_OF_CUSTOM_INFO],
                                new Object[]{});

                        String type = (String)row[DefaultVariantTableSchema.INDEX_OF_VARIANT_TYPE];
                        String zygosity = (String)row[DefaultVariantTableSchema.INDEX_OF_ZYGOSITY];
                        String genotype = ((String)row[DefaultVariantTableSchema.INDEX_OF_GT]);

                        r.setType(VariantRecord.VariantType.valueOf(type));
                        r.setZygosity(VariantRecord.Zygosity.valueOf(zygosity));
                        r.setGenotype(genotype);

                        for (VariantSelectionChangedListener l : listeners) {
                            l.variantSelectionChanged(r);
                        }
                    }
                });

                stp.setExportButtonVisible(false);

                stp.getTable().addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseReleased(MouseEvent e) {

                        //check for right click
                        if (!SwingUtilities.isRightMouseButton(e)) {
                            return;
                        }

                        SortableTable table = tablePanel.getTable();
                        int numSelected = table.getSelectedRows().length;
                        if (numSelected == 1) {
                            int r = table.rowAtPoint(e.getPoint());
                            if (r < 0 || r >= table.getRowCount()) {
                                return;
                            }
                            JPopupMenu popup = createPopupSingle(table, r);
                            popup.show(e.getComponent(), e.getX(), e.getY());
                        } else if (numSelected > 1) {
                            JPopupMenu popup = createPopupMultiple(table);
                            popup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });

                return stp;
            }

            @Override
            protected void showProgress(double fraction) {
                //do nothing
            }

            @Override
            protected void showSuccess(Object result) {
                tablePanel = (SearchableTablePanel) result;
                remove(tablePanel);
                add(tablePanel, c, JLayeredPane.DEFAULT_LAYER);
                showShowCard();
                updateIfRequired();
                init = true;
            }
        };
        worker.execute();

    }

    private void showWaitCard() {
        waitPanel.setVisible(true);
        this.setLayer(waitPanel, JLayeredPane.MODAL_LAYER);
        waitPanel.repaint();
    }

    private void showShowCard() {
        waitPanel.setVisible(false);
    }

    public boolean isInit() {
        return init;
    }

    void setUpdateRequired(boolean b) {
        updateRequired = b;
    }

    public static void addVariantSelectionChangedListener(VariantSelectionChangedListener l) {
        listeners.add(l);
    }

    public void updateIfRequired() {
        if (tablePanel == null) {
            return;
        }
        synchronized (updateLock) {
            if (updateRequired) {
                tablePanel.forceRefreshData();
            }
        }
    }

    private JPopupMenu createPopupSingle(SortableTable table, int r) {

        table.setRowSelectionInterval(r, r);
        int row = TableModelWrapperUtils.getActualRowAt(table.getModel(), r);

        final String chrom = (String) table.getModel().getValueAt(r, DefaultVariantTableSchema.INDEX_OF_CHROM);
        final int position = (Integer) table.getModel().getValueAt(r, DefaultVariantTableSchema.INDEX_OF_POSITION);
        final String alt = (String) table.getModel().getValueAt(r, DefaultVariantTableSchema.INDEX_OF_ALT);


        JPopupMenu menu = new JPopupMenu();

        //Filter by position
        JMenuItem filter1Item = new JMenuItem("Filter by Position");
        filter1Item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ThreadController.getInstance().cancelWorkers(pageName);

                Condition[] conditions = new Condition[2];
                conditions[0] = BinaryConditionMS.equalTo(ProjectController.getInstance().getCurrentVariantTableSchema().getDBColumn(DefaultVariantTableSchema.COLUMNNAME_OF_CHROM), chrom);
                conditions[1] = BinaryConditionMS.equalTo(ProjectController.getInstance().getCurrentVariantTableSchema().getDBColumn(DefaultVariantTableSchema.COLUMNNAME_OF_POSITION), position);
                FilterUtils.createAndApplyGenericFixedFilter(
                        "Table - Filter by Position",
                        "Chromosome: " + chrom + ", Position: " + position,
                        ComboCondition.and(conditions));
            }
        });
        menu.add(filter1Item);

        //Filter by position and alt
        JMenuItem filter2Item = new JMenuItem("Filter by Position and Alt");
        filter2Item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ThreadController.getInstance().cancelWorkers(pageName);

                Condition[] conditions = new Condition[3];
                conditions[0] = BinaryConditionMS.equalTo(ProjectController.getInstance().getCurrentVariantTableSchema().getDBColumn(DefaultVariantTableSchema.COLUMNNAME_OF_CHROM), chrom);
                conditions[1] = BinaryConditionMS.equalTo(ProjectController.getInstance().getCurrentVariantTableSchema().getDBColumn(DefaultVariantTableSchema.COLUMNNAME_OF_POSITION), position);
                conditions[2] = BinaryConditionMS.equalTo(ProjectController.getInstance().getCurrentVariantTableSchema().getDBColumn(DefaultVariantTableSchema.COLUMNNAME_OF_ALT), alt);
                FilterUtils.createAndApplyGenericFixedFilter(
                        "Table - Filter by Position",
                        "Chromosome: " + chrom + ", Position: " + position + ", Alt: " + alt,
                        ComboCondition.and(conditions));
            }
        });
        menu.add(filter2Item);

        return menu;
    }

    private JPopupMenu createPopupMultiple(SortableTable table) {
        JPopupMenu menu = new JPopupMenu();
        return menu;
    }

/*
    private void checkStarring(List<Object[]> variants) {

        List<Integer> selected = new ArrayList<Integer>();
        starMap.clear();

        try {
            Set<StarredVariant> starred = MedSavantClient.VariantManager.getStarredVariants(LoginController.sessionId, ProjectController.getInstance().getCurrentProjectID(), ReferenceController.getInstance().getCurrentReferenceID());

            for (int i = 0; i < variants.size(); i++) {
                Object[] row = variants.get(i);
                StarredVariant current = new StarredVariant(
                        (Integer) row[DefaultVariantTableSchema.INDEX_OF_UPLOAD_ID],
                        (Integer) row[DefaultVariantTableSchema.INDEX_OF_FILE_ID],
                        (Integer) row[DefaultVariantTableSchema.INDEX_OF_VARIANT_ID],
                        null,
                        null,
                        null);
                if (starred.contains(current)) {

                    selected.add(i);
                    if (starMap.get(i) == null) {
                        starMap.put(i, new ArrayList<StarredVariant>());
                    }

                    Object[] arr = starred.toArray();
                    for (Object a : arr) {
                        StarredVariant sv = (StarredVariant) a;
                        if (sv.getUploadId() == current.getUploadId() && sv.getFileId() == current.getFileId() && sv.getVariantId() == current.getVariantId()) {
                            starMap.get(i).add(sv);
                        }
                    }

                }
            }

        } catch (Exception ex) {
            LOG.error("Error checking stars.", ex);
        }

        tablePanel.setSelectedRows(selected);
    }
*/
    private boolean isStarredByUser(int row) {
        if (!starMap.containsKey(row)) {
            return false;
        }
        List<VariantComment> starred = starMap.get(row);
        for (VariantComment current : starred) {
            if (current.getUser().equals(LoginController.getInstance().getUserName())) {
                return true;
            }
        }
        return false;
    }

    private void removeStarForUser(int row) {
        List<VariantComment> list = starMap.get(row);
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            VariantComment current = list.get(i);
            if (current.getUser().equals(LoginController.getInstance().getUserName())) {
                index = i;
            }
        }
        if (index != -1) {
            list.remove(index);
        }
    }
}
