/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import medsavant.db.Util;
import java.util.Vector;
import java.util.ArrayList;
import medsavant.db.table.TableSchema.ColumnType;
import medsavant.db.table.TableSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import java.util.List;
import java.sql.ResultSet;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import java.sql.SQLException;
import medsavant.db.Database;
import medsavant.db.ConnectionController;
import java.sql.Connection;
import medsavant.exception.MedSavantDBException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mfiume
 */
public class TestSimpleQueries {

    public TestSimpleQueries() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}


    @Test
    public void testVariantQueries() throws SQLException, MedSavantDBException {
        Connection c = ConnectionController.connect();

        TableSchema t = Database.getInstance().getVariantTableSchema();
        SelectQuery q1 = new SelectQuery();
        q1.addFromTable(t.getTable());
        q1.addAllColumns();
        String q1String = q1.toString() + " LIMIT 100";

        ResultSet r1 = c.createStatement().executeQuery(q1String);

        List<DbColumn> columns = t.getColumns();
        List<Vector> results = Util.parseResultSet(columns, r1);

        for (Vector row : results) {
            for (Object f : row) {
                System.out.print(f + "\t");
            }
            System.out.println();
        }
    }

    /*
    public static void main(String[] argv) throws SQLException {
        Connection c = ConnectionController.connect();

        int rowsInVariant = getRowsInTable(c,Database.getInstance().getVariantTableSchema().getTable());
        System.out.println("Variants: " + rowsInVariant);

        c.close();
        //int rowsInVariantAnnotations = getRowsInTable(c,DBSettings.TABLE_VARIANT_ANNOTATION);
        //System.out.println("Variant Annotations: " + rowsInVariantAnnotations);

    }
     * 
     */
}