package com.marklogic.example.utils;

import com.marklogic.client.*;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.StringHandle;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbagayau on 07/02/2017.
 */
public class QueryOptionsUtil {

    public static String OPTIONS_NAME_ALL = "all";
    public static String OPTIONS_NAME_TAGS = "tags";

    private static Map<String, String> optionRegistry = new HashMap<>();

    static {
        optionRegistry.put(OPTIONS_NAME_ALL, "src/main/resources/all.xml");
        optionRegistry.put(OPTIONS_NAME_TAGS, "src/main/resources/tags.xml");
    }

    public static void configureOptions(DatabaseClient client, String optionName) throws IOException {
        // create a manager for writing query options
        QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
        try (InputStream docStream = new FileInputStream(optionRegistry.get(optionName))) {

            if (docStream == null) {
                throw new IOException("Could not read document example");
            }

            StringWriter writer = new StringWriter();
            IOUtils.copy(docStream, writer, "UTF-8");
            // create a handle to send the query options
            StringHandle writeHandle = new StringHandle(writer.toString());

            // write the query options to the database
            optionsMgr.writeOptions(optionName, writeHandle);
        }

        // release the client
        client.release();
    }
}
