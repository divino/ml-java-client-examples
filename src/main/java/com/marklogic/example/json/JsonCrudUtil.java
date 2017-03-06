package com.marklogic.example.json;

import com.google.gson.*;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.extra.gson.GSONHandle;
import com.marklogic.client.impl.DocumentUriTemplateImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonCrudUtil {

    public static void deleteDocument(DatabaseClient client, String docUri) {
        // create a manager for JSON documents
        JSONDocumentManager docMgr = client.newJSONDocumentManager();
        // delete the document
        docMgr.delete(docUri);
        System.out.println("File " + docUri + " is confirmed deleted.");
    }

    public static void insertDoc(DatabaseClient client, String docUri, JsonObject jsonObject) {
        // create a manager for JSON documents
        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        insertDoc(docMgr, docUri, jsonObject);
    }

    public static void insertDoc(JSONDocumentManager docMgr, String docUri, JsonObject jsonObject) {
        // create a handle for the JSON structure
        GSONHandle writeHandle = new GSONHandle(jsonObject);

        // write the document to the database
        docMgr.write(docUri, writeHandle);
    }

    public static String getJsonDocString(DatabaseClient client, String docUri) {
        JSONDocumentManager docMgr = client.newJSONDocumentManager();
        String data = docMgr.read(docUri, new StringHandle()).get();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(data);
        return gson.toJson(je);
    }

    //load JSON array and split into multiple docs
    public static void loadDocs(DatabaseClient client, String jsonFile, String collection) throws IOException {

        System.out.println("Load documents from json file " + jsonFile);

        DocumentMetadataHandle meta = new DocumentMetadataHandle();
        meta.getCollections().add(collection);

        try (InputStream docStream = new FileInputStream(jsonFile)) {
            if (docStream == null) {
                throw new IOException("Could not read document example");
            }

            // parse the example file with GSON
            JsonElement writeDocument = new JsonParser().parse(
                    new InputStreamReader(docStream, "UTF-8"));

            JsonArray jsonArray = writeDocument.getAsJsonArray();

            JSONDocumentManager docMgr = client.newJSONDocumentManager();

            DocumentUriTemplate uriTemplate = new DocumentUriTemplateImpl("json");
            uriTemplate.setDirectory("/marklogic/examples/");
            jsonArray.forEach(element -> {
                // create a handle for the JSON structure
                GSONHandle writeHandle = new GSONHandle(element);
                // write the document to the database
                DocumentDescriptor id = docMgr.createAs(uriTemplate, meta, writeHandle);
                System.out.println("uri " + id.getUri());
              }
            );
        }
    }

    public static void removeNodeJsonpath(DatabaseClient client, String uri, String jsonPath) {
        // create a manager for JSON documents
        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        DocumentPatchBuilder patchBuilder = docMgr.newPatchBuilder();

        patchBuilder.pathLanguage(DocumentPatchBuilder.PathLanguage.JSONPATH);

        //replace node
        patchBuilder.delete(jsonPath);

        System.out.println ("remove '" + jsonPath
                + "' from '" + uri
                + "' query == " + patchBuilder.build().toString());

        docMgr.patch(uri, patchBuilder.build());
    }

    public static void deleteCollection(DatabaseClient client, String collection) {
        QueryManager qman = client.newQueryManager();
        DeleteQueryDefinition delDef = qman.newDeleteDefinition();
        delDef.setCollections(collection);
        qman.delete(delDef);
    }

}
