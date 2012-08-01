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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

import org.ut.biolab.medsavant.db.TableSchema;
import org.ut.biolab.medsavant.model.Range;


/**
 *
 * @author Andrew
 */
public interface DBUtilsAdapter extends Remote {
    
    public TableSchema importTableSchema(String sessID, String tableName) throws SQLException, RemoteException;
    
    public List<String> getDistinctValuesForColumn(String sessID, String tableName, String columnName, boolean useCache) throws SQLException, RemoteException;
    public Range getExtremeValuesForColumn(String sid, String tablename, String columnname) throws SQLException, RemoteException;
    public Condition getRangeCondition(DbColumn col, Range r) throws RemoteException;
    public int getNumRecordsInTable(String sessID, String name) throws SQLException, RemoteException;
}