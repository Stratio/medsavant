/**
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.ut.biolab.medsavant;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.NoRouteToHostException;
import java.rmi.ConnectIOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import javax.net.ssl.SSLHandshakeException;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ut.biolab.medsavant.shared.db.TableSchema;
import org.ut.biolab.medsavant.shared.model.SessionExpiredException;
import org.ut.biolab.medsavant.shared.serverapi.AnnotationManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.CohortManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.CustomTablesAdapter;
import org.ut.biolab.medsavant.shared.serverapi.DBUtilsAdapter;
import org.ut.biolab.medsavant.shared.serverapi.GeneSetManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.LogManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.MedSavantServerRegistry;
import org.ut.biolab.medsavant.shared.serverapi.NetworkManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.NotificationManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.OntologyManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.PatientManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.ProjectManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.ReferenceManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.RegionSetManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.SessionManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.SettingsManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.SetupAdapter;
import org.ut.biolab.medsavant.shared.serverapi.UserManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.VariantManagerAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.UnaryCondition;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;

public class MedSavantServlet extends HttpServlet implements MedSavantServerRegistry
{
    private static final long serialVersionUID = -77006512859078222L;

    private static final Gson gson; // does not maintain state, can be static.

    private static final String JSON_PARAM_NAME = "json";

    private static final Log LOG = LogFactory.getLog(MedSavantServlet.class);

    @SuppressWarnings("unused")
    private CohortManagerAdapter cohortManager;

    @SuppressWarnings("unused")
    private PatientManagerAdapter patientManager;

    @SuppressWarnings("unused")
    private CustomTablesAdapter customTablesManager;

    @SuppressWarnings("unused")
    private AnnotationManagerAdapter annotationManagerAdapter;

    @SuppressWarnings("unused")
    private GeneSetManagerAdapter geneSetManager;

    @SuppressWarnings("unused")
    private LogManagerAdapter logManager;

    private NetworkManagerAdapter networkManager;

    @SuppressWarnings("unused")
    private OntologyManagerAdapter ontologyManager;

    @SuppressWarnings("unused")
    private ProjectManagerAdapter projectManager;

    @SuppressWarnings("unused")
    private UserManagerAdapter userManager;

    private SessionManagerAdapter sessionManager;

    @SuppressWarnings("unused")
    private SettingsManagerAdapter settingsManager;

    @SuppressWarnings("unused")
    private RegionSetManagerAdapter regionSetManager;

    @SuppressWarnings("unused")
    private ReferenceManagerAdapter referenceManager;

    @SuppressWarnings("unused")
    private DBUtilsAdapter dbUtils;

    @SuppressWarnings("unused")
    private SetupAdapter setupManager;

    private VariantManagerAdapter variantManager;

    @SuppressWarnings("unused")
    private NotificationManagerAdapter notificationManager;

    private static boolean initialized = false;

    private String medSavantServerHost;

    private int medSavantServerPort;

    private String username;

    private String password;

    private String db;

    // Debug variable, for the test method. Don't use for other purposes.
    // (doesn't work for multiple users)
    private static Object lastReturnVal;

    private static String sessionId = null;

    private static final int RENEW_RETRY_TIME = 10000;

    private static int UPLOAD_BUFFER_SIZE = 4096;

    private class SimplifiedCondition
    {

        private int projectId;

        private int refId;

        private String type;

        private String method;

        private String[] args;

        private Condition getCondition() throws JsonParseException
        {
            try {
                if (this.args.length < 1) {
                    throw new JsonParseException("No arguments given for SimplifiedCondition with type " + this.type
                        + " and method " + this.method);
                }

                // this should really be cached...
                TableSchema tableSchema =
                    MedSavantServlet.this.variantManager.getCustomTableSchema(getSessionId(), this.projectId,
                        this.refId);

                DbColumn col = tableSchema.getDBColumn(this.args[0]);
                if (this.type.equals("BinaryCondition")) {
                    if (this.method.equals("lessThan")) {
                        return BinaryCondition.lessThan(col, this.args[1], Boolean.parseBoolean(this.args[2]));
                    } else if (this.method.equals("greaterThan")) {
                        return BinaryCondition.greaterThan(col, this.args[1], Boolean.parseBoolean(this.args[2]));
                    } else if (this.method.equals("equalTo")) {
                        return BinaryCondition.equalTo(col, this.args[1]);
                    } else if (this.method.equals("notEqualTo")) {
                        return BinaryCondition.notEqualTo(col, this.args[1]);
                    } else if (this.method.equals("like")) {
                        return BinaryCondition.like(col, this.args[1]);
                    } else if (this.method.equals("notLike")) {
                        return BinaryCondition.notLike(col, this.args[1]);
                    }
                    throw new JsonParseException("Unrecognized method " + this.method + " for simplified condition "
                        + this.type);
                } else if (this.type.equals("UnaryCondition")) {
                    if (this.method.equals("isNull")) {
                        return UnaryCondition.isNull(col);
                    } else if (this.method.equals("isNotNull")) {
                        return UnaryCondition.isNotNull(col);
                    } else if (this.method.equals("exists")) {
                        return UnaryCondition.exists(col);
                    } else if (this.method.equals("unique")) {
                        return UnaryCondition.unique(col);
                    }
                    throw new JsonParseException("Unrecognized method " + this.method + " for simplified condition "
                        + this.type);
                }
                throw new JsonParseException("Unrecognized simplified condition type " + this.type);
            } catch (ArrayIndexOutOfBoundsException ai) {
                throw new JsonParseException("Invalid arguments specified for SimplifiedCondition of type" + this.type
                    + ", method " + this.method + ", and args=" + this.args);
            } catch (SQLException ex) {
                throw new JsonParseException("Couldn't fetch variant table schema: " + ex);
            } catch (RemoteException re) {
                throw new JsonParseException("Couldn't fetch variant table schema: " + re);
            } catch (SessionExpiredException se) {
                throw new JsonParseException("Couldn't fetch variant table schema: " + se);
            }
        }
    }

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Handle the condition type.
        gsonBuilder.registerTypeAdapter(Condition.class, new JsonDeserializer<Condition>()
        {
            @Override
            public Condition deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException
            {
                return gson.fromJson(je, SimplifiedCondition.class).getCondition();
            }
        });
        gson = gsonBuilder.create();
    }

    private synchronized boolean renewSession()
    {
        try {
            sessionId = null;
            sessionId = this.sessionManager.registerNewSession(this.username, this.password, this.db);
            LOG.info("Renewed new session with id " + sessionId);
        } catch (Exception e) {
            // can't recover from this.
            LOG.error("Exception while registering session, retrying in " + RENEW_RETRY_TIME + " ms: " + e);
            sessionId = null;
            return false;
        }

        return true;
    }

    private synchronized String getSessionId()
    {
        try {
            if (sessionId == null) {
                while (!renewSession()) {
                    Thread.sleep(RENEW_RETRY_TIME);
                }
            }
            return sessionId;
        } catch (InterruptedException iex) {
            LOG.error("CRITICAL: Thread interrupted while trying to get session id");
        }
        return null;
    }

    public String json_invoke(String adapter, String method, String jsonStr) throws IllegalArgumentException
    {

        adapter = adapter + "Adapter";

        Field selectedAdapter = null;
        for (Field f : MedSavantServlet.class.getDeclaredFields()) {
            if (f.getType().getSimpleName().equalsIgnoreCase(adapter)) {
                selectedAdapter = f;
            }
        }

        if (selectedAdapter == null) {
            throw new IllegalArgumentException("The adapter " + adapter + " does not exist");
        }

        JsonParser parser = new JsonParser();
        JsonArray gArray = parser.parse(jsonStr).getAsJsonArray();
        JsonElement jse = parser.parse(jsonStr);
        JsonArray jsonArray;

        if (jse.isJsonArray()) {
            jsonArray = jse.getAsJsonArray();
        } else {
            throw new IllegalArgumentException("The json method arguments are not an array");
        }

        Method selectedMethod = null;

        for (Method m : selectedAdapter.getType().getMethods()) {
            if (m.getName().equalsIgnoreCase(method) && m.getGenericParameterTypes().length == (jsonArray.size() + 1)) {
                selectedMethod = m;
            }
        }

        if (selectedMethod == null) {
            throw new IllegalArgumentException("The method " + method + " in adapter " + adapter + " with "
                + jsonArray.size() + " arguments does not exist");
        }

        int i = 0;
        Object[] methodArgs = new Object[selectedMethod.getParameterTypes().length];

        try {
            for (Type t : selectedMethod.getGenericParameterTypes()) {
                LOG.debug("Field " + i + " is " + t.toString() + " for method " + selectedMethod.toString());
                methodArgs[i] = (i > 0) ? gson.fromJson(gArray.get(i - 1), t) : getSessionId();
                ++i;
            }
        } catch (JsonParseException je) {
            LOG.error(je);
        }

        while (true) {
            try {
                Object selectedAdapterInstance = selectedAdapter.get(this);
                if (selectedAdapterInstance == null) {
                    throw new NullPointerException("Requested adapter " + selectedAdapter.getName()
                        + " was not initialized.");
                }
                // Method invocation
                Object returnVal = selectedMethod.invoke(selectedAdapterInstance, methodArgs);
                lastReturnVal = returnVal;
                if (returnVal == null) {
                    return null;
                } else {
                    return gson.toJson(returnVal, selectedMethod.getReturnType());
                }
            } catch (IllegalAccessException iae) {
                throw new IllegalArgumentException("Couldn't execute method with given arguments: " + iae.getMessage());
            } catch (InvocationTargetException ite) {
                if (ite.getCause() instanceof SessionExpiredException) {
                    // session expired. renew and try again.
                    renewSession();
                } else {
                    throw new IllegalArgumentException("Couldn't execute method with given arguments, "
                        + ite.getCause());
                }
            }
        }
    }

    @Override
    public void init() throws ServletException
    {
        LOG.info("MedSavant JSON Client/Server booted.");
        try {
            loadConfiguration();
            initializeRegistry(this.medSavantServerHost, Integer.toString(this.medSavantServerPort));
        } catch (Exception ex) {
            LOG.error(ex);
            ex.printStackTrace();
        }
    }

    public void initializeRegistry(String serverAddress, String serverPort) throws RemoteException, NotBoundException,
        NoRouteToHostException, ConnectIOException
    {

        if (initialized) {
            return;
        }

        int port = (new Integer(serverPort)).intValue();

        Registry registry;

        LOG.debug("Connecting to MedSavantServerEngine @ " + serverAddress + ":" + serverPort + "...");

        try {
            registry = LocateRegistry.getRegistry(serverAddress, port, new SslRMIClientSocketFactory());
            LOG.debug("Retrieving adapters...");
            setAdaptersFromRegistry(registry);
            LOG.info("Connected with SSL/TLS Encryption");
            initialized = true;
        } catch (ConnectIOException ex) {
            if (ex.getCause() instanceof SSLHandshakeException) {
                registry = LocateRegistry.getRegistry(serverAddress, port);
                LOG.debug("Retrieving adapters...");
                setAdaptersFromRegistry(registry);
                LOG.info("Connected without SSL/TLS encryption");
            }
        }
        LOG.debug("Done");

    }

    private void setAdaptersFromRegistry(Registry registry) throws RemoteException, NotBoundException,
        NoRouteToHostException, ConnectIOException
    {
        this.annotationManagerAdapter = (AnnotationManagerAdapter) registry.lookup(ANNOTATION_MANAGER);
        this.cohortManager = (CohortManagerAdapter) (registry.lookup(COHORT_MANAGER));
        this.logManager = (LogManagerAdapter) registry.lookup(LOG_MANAGER);
        this.networkManager = (NetworkManagerAdapter) registry.lookup(NETWORK_MANAGER);
        this.ontologyManager = (OntologyManagerAdapter) registry.lookup(ONTOLOGY_MANAGER);
        this.patientManager = (PatientManagerAdapter) registry.lookup(PATIENT_MANAGER);
        this.projectManager = (ProjectManagerAdapter) registry.lookup(PROJECT_MANAGER);
        this.geneSetManager = (GeneSetManagerAdapter) registry.lookup(GENE_SET_MANAGER);
        this.referenceManager = (ReferenceManagerAdapter) registry.lookup(REFERENCE_MANAGER);
        this.regionSetManager = (RegionSetManagerAdapter) registry.lookup(REGION_SET_MANAGER);
        this.sessionManager = (SessionManagerAdapter) registry.lookup(SESSION_MANAGER);
        this.settingsManager = (SettingsManagerAdapter) registry.lookup(SETTINGS_MANAGER);
        this.userManager = (UserManagerAdapter) registry.lookup(USER_MANAGER);
        this.variantManager = (VariantManagerAdapter) registry.lookup(VARIANT_MANAGER);
        this.dbUtils = (DBUtilsAdapter) registry.lookup(DB_UTIL_MANAGER);
        this.setupManager = (SetupAdapter) registry.lookup(SETUP_MANAGER);
        this.customTablesManager = (CustomTablesAdapter) registry.lookup(CUSTOM_TABLES_MANAGER);
        this.notificationManager = (NotificationManagerAdapter) registry.lookup(NOTIFICATION_MANAGER);
    }

    private void setExceptionHandler()
    {
        Thread.setDefaultUncaughtExceptionHandler(
            new Thread.UncaughtExceptionHandler()
            {
                @Override
                public void uncaughtException(Thread t, Throwable e)
                {
                    LOG.info("Global exception handler caught: " + t.getName() + ": " + e);

                    if (e instanceof InvocationTargetException) {
                        e = ((InvocationTargetException) e).getCause();
                    }

                    if (e instanceof SessionExpiredException) {
                        SessionExpiredException see = (SessionExpiredException) e;
                        LOG.error("Session expired exception: " + see.toString());
                        return;
                    }
                    e.printStackTrace();
                }
            });
    }

    private int copyStreamToServer(InputStream inputStream, String filename, long filesize) throws IOException,
        InterruptedException
    {

        int streamID = -1;

        try {
            streamID = this.networkManager.openWriterOnServer(getSessionId(), filename, filesize);
            int numBytes;
            byte[] buf = new byte[UPLOAD_BUFFER_SIZE];

            while ((numBytes = inputStream.read(buf)) != -1) {
                // System.out.println("Read " + numBytes +" bytes");
                this.networkManager.writeToServer(getSessionId(), streamID, ArrayUtils.subarray(buf, 0, numBytes));
            }
        } finally {
            if (streamID >= 0) {
                this.networkManager.closeWriterOnServer(getSessionId(), streamID);
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return streamID;
    }

    private static Semaphore uploadSem = new Semaphore(1, true);

    private static class Upload
    {

        String fieldName;

        int streamId;

        public Upload(String fieldName, int streamId)
        {
            this.fieldName = fieldName;
            this.streamId = streamId;
        }
    }

    private Upload[] handleUploads(FileItemIterator iter) throws FileUploadException, IOException,
        InterruptedException
    {
        List<Upload> uploads = new ArrayList<Upload>();
        try {
            if (!uploadSem.tryAcquire()) {
                throw new FileUploadException("Can't upload file: other uploads are in progress");
            }
            // uploadSem.acquire();
            FileItemStream streamToUpload = null;
            long filesize = -1;

            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                // System.out.println("Got file " + name);
                if (item.isFormField()) {
                    if (name.startsWith("size_")) {
                        filesize = Long.parseLong(Streams.asString(stream));

                    }
                } else if (name.startsWith("file_")) {
                    if (streamToUpload != null) {
                        throw new IllegalArgumentException(
                            "More than one file detected -- only one file can be uploaded at a time");
                    } else {
                        streamToUpload = item;
                    }
                } else {
                    throw new IllegalArgumentException("Unrecognized file detected with field name " + name);
                }
                if (streamToUpload == null) {
                    throw new IllegalArgumentException("Can't begin upload - no files were detetected");
                }
                if (filesize == -1) {
                    // System.out.println("No filesize given for file " + name);
                }

                // Do the upload
                int streamId = copyStreamToServer(streamToUpload.openStream(), streamToUpload.getName(), filesize);
                if (streamId >= 0) {
                    uploads.add(new Upload(name, streamId));
                }
            }

        } finally {
            uploadSem.release();
        }
        return uploads.toArray(new Upload[uploads.size()]);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // format: ..../adapter/method

        String uri = req.getRequestURI();
        String[] x = uri.split("/");
        int methodIndex;
        int adapterIndex;

        if (uri.endsWith("/")) {
            methodIndex = x.length - 2;
            adapterIndex = x.length - 3;
        } else {
            methodIndex = x.length - 1;
            adapterIndex = x.length - 2;
        }

        resp.setContentType("text/x-json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        try {
            if (adapterIndex >= 0 && x[adapterIndex].equals("UploadManager") && x[methodIndex].equals("upload")) {
                if (!ServletFileUpload.isMultipartContent(req)) {
                    throw new IllegalArgumentException(gson.toJson("File upload failed: content is not multipart",
                        String.class));
                }
                FileItemIterator iter = (new ServletFileUpload()).getItemIterator(req);
                System.out.println("Handling upload");
                Upload[] uploads = handleUploads(iter); // note this BLOCKS until upload is finished.

                resp.getWriter().print(gson.toJson(uploads, uploads.getClass()));
                resp.getWriter().close();

            } else if (methodIndex < 0 || adapterIndex < 0) {
                throw new IllegalArgumentException(gson.toJson("Malformed URL", String.class));
            } else {

                // Print parameter map to stdout
                /*
                 * for (Object o : req.getParameterMap().entrySet()) { Map.Entry e = (Map.Entry) o;
                 * System.out.println("Key="+e.getKey()); for(String a : (String[])e.getValue()){
                 * System.out.println("\tVal="+a); } }
                 */
                String json_args = req.getParameter(JSON_PARAM_NAME);
                if (json_args == null) {
                    json_args = "[]";
                }

                String ret = json_invoke(x[adapterIndex], x[methodIndex], json_args);

                resp.getWriter().print(ret);
                resp.getWriter().close();
            }
        } catch (FileUploadException fue) {
            LOG.error(fue);
        } catch (IllegalArgumentException iae) {
            LOG.error(iae);
        } catch (InterruptedException iex) { // file upload cancelled.
            LOG.error(iex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String uri = req.getRequestURI();
        String[] x = uri.split("/");
        int adapterIndex = x.length - 1;

        resp.setContentType("text/x-json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        if (x[adapterIndex].equalsIgnoreCase("testing")) {
            resp.setContentType("text/html;charset=UTF-8");
            resp.setHeader("Cache-Control", "no-cache");
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("/testing.html");
            IOUtils.copy(input, resp.getOutputStream());
            resp.getOutputStream().close();
        } else {
            resp.getWriter().print("Invalid");
            resp.getWriter().close();
        }
    }

    private void loadConfiguration() throws ServletException
    {
        String host = null;
        String uname = null;
        String pass = null;
        String dbase = null;
        int p = -1;
        try {
            String configFileLocation = getServletContext().getInitParameter("MedSavantConfigFile");
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileLocation);
            Properties props = new Properties();
            props.load(in);
            in.close();

            host = props.getProperty("host", "");
            uname = props.getProperty("username", "");
            pass = props.getProperty("password", "");
            dbase = props.getProperty("db", "");

            String portStr = props.getProperty("port", "-1");
            if (portStr == null) {
                LOG.error("No port specified in configuration, cannot continue");

            }
            p = Integer.parseInt(portStr);
            if (p <= 0) {
                throw new ServletException("Illegal port specified in configuration: " + portStr + ", cannot continue.");
            }

            if (uname.length() < 1) {
                throw new ServletException("No username specified in configuration file, cannot continue.");
            }
            if (pass.length() < 1) {
                throw new ServletException("No password specified in configuration file, cannot continue.");
            }
            if (dbase.length() < 1) {
                throw new ServletException("No database specified in configuration file, cannot continue.");
            }
            if (host.length() < 1) {
                throw new ServletException("No host specified in configuration file, cannot continue.");
            }
        } catch (IOException iex) {
            throw new ServletException("IO Exception reading config file, cannot continue: " + iex);
        }
        this.medSavantServerHost = host;
        this.medSavantServerPort = p;
        this.username = uname;
        this.password = pass;
        this.db = dbase;

        LOG.info("Configured with:");
        LOG.info("Host = " + host);
        LOG.info("Port = " + p);
        LOG.info("Username = " + uname);
        LOG.info("Database = " + this.db);
    }

    private void test()
    {
        // A few simple tests.

        String js = json_invoke("ProjectManager", "getProjectNames", "[\"\"]");
        System.out.println("JS: " + js + "\n");
        String firstProject = ((String[]) lastReturnVal)[0];

        js = json_invoke("ProjectManager", "getProjectID", "[\"" + firstProject + "\"]");
        System.out.println("JS: " + js + "\n");

        int projId = ((Integer) lastReturnVal);

        js = json_invoke("CohortManager", "getCohorts", "[\"" + projId + "\"]");
        System.out.println("JS: " + js + "\n");

        int cohortId = 1;
        js = json_invoke("CohortManager", "getIndividualsInCohort", "[\"" + projId + "\", \"" + cohortId + "\"]");
        System.out.println("JS: " + js + "\n");

        js = json_invoke("PatientManager", "getPatientFields", "[\"" + projId + "\"]");
        System.out.println("JS: " + js + "\n");
    }
}
