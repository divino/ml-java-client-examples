package com.marklogic.example;

import com.marklogic.client.*;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.StringHandle;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by dbagayau on 07/02/2017.
 */
public class QueryOptionsUtil {

    public static String OPTIONS_NAME = "all";

    private static String optionFilename = "src/main/resources/all.xml";

    public static void configure(DatabaseClient client) throws IOException {

        // create a manager for writing query options
        QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();
        try (InputStream docStream = new FileInputStream(optionFilename)) {

            if (docStream == null) {
                throw new IOException("Could not read document example");
            }

            StringWriter writer = new StringWriter();
            IOUtils.copy(docStream, writer, "UTF-8");
            // create a handle to send the query options
            StringHandle writeHandle = new StringHandle(writer.toString());

            // write the query options to the database
            optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);
        }

        // release the client
        client.release();
    }
}
