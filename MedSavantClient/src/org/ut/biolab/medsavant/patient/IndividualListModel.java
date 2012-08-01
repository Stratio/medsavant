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

package org.ut.biolab.medsavant.patient;

import java.rmi.RemoteException;
import java.sql.SQLException;

import org.ut.biolab.medsavant.MedSavantClient;
import org.ut.biolab.medsavant.format.PatientFormat;
import org.ut.biolab.medsavant.login.LoginController;
import org.ut.biolab.medsavant.project.ProjectController;
import org.ut.biolab.medsavant.view.list.DetailedListModel;

/**
 *
 * @author mfiume
 */
public class IndividualListModel implements DetailedListModel {
    private static final String[] COLUMN_NAMES = new String[] { PatientFormat.ALIAS_OF_PATIENT_ID,
                                                                PatientFormat.ALIAS_OF_FAMILY_ID,
                                                                PatientFormat.ALIAS_OF_HOSPITAL_ID,
                                                                PatientFormat.ALIAS_OF_IDBIOMOM,
                                                                PatientFormat.ALIAS_OF_IDBIODAD,
                                                                PatientFormat.ALIAS_OF_GENDER,
                                                                PatientFormat.ALIAS_OF_DNA_IDS };
    private static final Class[] COLUMN_CLASSES = new Class[] { Integer.class, String.class, String.class, String.class, String.class, Integer.class, String.class };
    private static final int[] HIDDEN_COLUMNS = new int[] { 0, 1, 3, 4, 5, 6 };

    @Override
    public Object[][] getList(int limit) throws RemoteException, SQLException {
        return MedSavantClient.PatientManager.getBasicPatientInfo(LoginController.sessionId, ProjectController.getInstance().getCurrentProjectID(), limit).toArray(new Object[0][0]);
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public Class[] getColumnClasses() {
        return COLUMN_CLASSES;
    }

    @Override
    public int[] getHiddenColumns() {
        return HIDDEN_COLUMNS;
    }
}
