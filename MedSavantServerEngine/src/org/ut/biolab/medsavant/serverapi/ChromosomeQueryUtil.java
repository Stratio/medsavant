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

package org.ut.biolab.medsavant.serverapi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.healthmarketscience.sqlbuilder.OrderObject.Dir;
import com.healthmarketscience.sqlbuilder.SelectQuery;

import java.rmi.RemoteException;
import org.ut.biolab.medsavant.db.MedSavantDatabase;
import org.ut.biolab.medsavant.db.MedSavantDatabase.ChromosomeTableSchema;
import org.ut.biolab.medsavant.db.TableSchema;
import org.ut.biolab.medsavant.model.Chromosome;
import org.ut.biolab.medsavant.db.connection.ConnectionController;
import org.ut.biolab.medsavant.util.BinaryConditionMS;
import org.ut.biolab.medsavant.util.MedSavantServerUnicastRemoteObject;


/**
 *
 * @author Andrew
 */
public class ChromosomeQueryUtil extends MedSavantServerUnicastRemoteObject implements ChromosomeQueryUtilAdapter {

    private static ChromosomeQueryUtil instance;

    public static synchronized ChromosomeQueryUtil getInstance() throws RemoteException {
        if (instance == null) {
            instance = new ChromosomeQueryUtil();
        }
        return instance;
    }

    public ChromosomeQueryUtil() throws RemoteException {super();}

    @Override
    public List<Chromosome> getContigs(String sid, int refid) throws SQLException{

        TableSchema table = MedSavantDatabase.ChromosomeTableSchema;
        SelectQuery query = new SelectQuery();
        query.addFromTable(table.getTable());
        query.addAllColumns();
        query.addCondition(BinaryConditionMS.equalTo(table.getDBColumn(ChromosomeTableSchema.COLUMNNAME_OF_REFERENCE_ID), refid));
        query.addOrdering(table.getDBColumn(ChromosomeTableSchema.COLUMNNAME_OF_CONTIG_ID), Dir.ASCENDING);

        ResultSet rs = ConnectionController.executeQuery(sid, query.toString());

        List<Chromosome> result = new ArrayList<Chromosome>();
        while(rs.next()){
            result.add(new Chromosome(
                    rs.getString(ChromosomeTableSchema.COLUMNNAME_OF_CONTIG_NAME),
                    null,
                    rs.getLong(ChromosomeTableSchema.COLUMNNAME_OF_CENTROMERE_POS),
                    rs.getLong(ChromosomeTableSchema.COLUMNNAME_OF_CONTIG_LENGTH)));
        }
        return result;
    }

}
