package com.marklogic.example.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.*;
import com.marklogic.client.extra.gson.GSONHandle;
import com.marklogic.client.impl.DocumentUriTemplateImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.QueryManager;
import org.apache.http.client.utils.DateUtils;

import java.io.*;
import java.util.Date;

public class JsonCRUD {

    String docUri = "/marklogic/example/maria.json";
    String jsonFile = "data/json/persons.json";

    public void deleteDocument(DatabaseClient client) {
        // create a manager for JSON documents
        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        // delete the document
        try {
            docMgr.delete(docUri);
            verify(client);
        } catch (ResourceNotFoundException e) {
            System.out.println("File " + this.docUri + " is confirmed deleted.");
            //e.printStackTrace();
        } finally {
            client.release();
        }

    }

    public void createDoc(DatabaseClient client) {
        System.out.println("Create Document");

        // create a manager for JSON documents
        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        // construct a GSON JSON structure
        JsonObject dataObj = new JsonObject();
        dataObj.addProperty("name", "jose");
        dataObj.addProperty("key", "not important");
        dataObj.addProperty("country", "japan");
        dataObj.addProperty("age", 100);
        dataObj.addProperty("enrolled", true);
        JsonObject metaDataObj = new JsonObject();
        metaDataObj.addProperty("createdOn", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
        metaDataObj.addProperty("createdBy", "jose");

        JsonArray dependentsArray = new JsonArray();
        JsonObject dependent1Obj = new JsonObject();
        dependent1Obj.addProperty("name", "josie");
        dependent1Obj.addProperty("age", 10);
        JsonObject dependent2Obj = new JsonObject();
        dependent2Obj.addProperty("name", "john");
        dependent2Obj.addProperty("age", 11);
        dependentsArray.add(dependent1Obj);
        dependentsArray.add(dependent2Obj);

        JsonArray tagsArray = new JsonArray();
        tagsArray.add(new JsonPrimitive("mother"));
        tagsArray.add(new JsonPrimitive("person"));

        JsonObject tripleObj = new JsonObject();
        tripleObj.addProperty("subject", "http://example.org/#josie");
        tripleObj.addProperty("predicate", "http://xmlns.com/foaf/0.1/firstname/");
        JsonObject tripleObjValue = new JsonObject();
        tripleObjValue.addProperty("datatype", "http://www.w3.org/2001/XMLSchema#string");
        tripleObjValue.addProperty("value", "josie");
        tripleObj.add("object", tripleObjValue);

        JsonObject writeRoot = new JsonObject();
        writeRoot.add("data", dataObj);
        writeRoot.add("metaData", metaDataObj);
        writeRoot.add("tags",  tagsArray);
        writeRoot.add("dependents", dependentsArray);
        writeRoot.add("triple", tripleObj);

        // create a handle for the JSON structure
        GSONHandle writeHandle = new GSONHandle(writeRoot);

        // write the document to the database
        docMgr.write(docUri, writeHandle);

        // release the client
        client.release();

    }

    public void verify (DatabaseClient client, boolean release) {
        verify(client);
        if (release) {
            client.release();
        }
    }

    public void verify (DatabaseClient client) {
        JSONDocumentManager docMgr = client.newJSONDocumentManager();
        String data = docMgr.read(this.docUri, new StringHandle()).get();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(data);
        String prettyJsonString = gson.toJson(je);

        System.out.println("DOC " + prettyJsonString);
    }

    public void loadDocs(DatabaseClient client) throws IOException {
        System.out.println("Load documents from json file " + this.jsonFile);

        DocumentMetadataHandle meta = new DocumentMetadataHandle();
        meta.getCollections().add("structuredQuery-samples-marklogic");

        try (InputStream docStream = new FileInputStream(this.jsonFile)) {
            if (docStream == null) {
                throw new IOException("Could not read document example");
            }

            // parse the example file with GSON
            JsonElement writeDocument = new JsonParser().parse(
                    new InputStreamReader(docStream, "UTF-8"));

            JsonArray jsonArray = writeDocument.getAsJsonArray();

            JSONDocumentManager docMgr = client.newJSONDocumentManager();

            jsonArray.forEach(element -> {
                // write the document to the database
                DocumentUriTemplate uriTemplate = new DocumentUriTemplateImpl("json");
                uriTemplate.setDirectory("/marklogic/examples/");
                // create a handle for the JSON structure
                GSONHandle writeHandle = new GSONHandle(element);
                DocumentDescriptor id = docMgr.createAs(uriTemplate, meta, writeHandle);
                System.out.println("uri " + id.getUri());
              }
            );
        }

        // release the client
        client.release();

    }

    public void updateDocXpath(DatabaseClient client) {

        // create a manager for JSON documents
        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        DocumentPatchBuilder patchBuilder = docMgr.newPatchBuilder();

        patchBuilder.pathLanguage(DocumentPatchBuilder.PathLanguage.XPATH);
        ObjectMapper mapper = new ObjectMapper();

        //replace node
        patchBuilder.replaceFragment("/data/key", mapper.createObjectNode().put("test", "test").put("xxx", "xxx"));

        //replace value
        patchBuilder.replaceValue("createdBy", "new created by");

        System.out.println ("patch " + patchBuilder.build().toString());

        docMgr.patch(docUri, patchBuilder.build());

        // release the client
        client.release();
    }

    public void replaceArrayNode(DatabaseClient client) {

        // create a manager for JSON documents
        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        DocumentPatchBuilder patchBuilder = docMgr.newPatchBuilder();

        patchBuilder.pathLanguage(DocumentPatchBuilder.PathLanguage.XPATH);
        ObjectMapper mapper = new ObjectMapper();

        //replace array node
        patchBuilder.replaceFragment("/array-node('tags')",
                mapper.createArrayNode().add("tag 1").add("tag 2"));

        System.out.println ("patch " + patchBuilder.build().toString());

        docMgr.patch(docUri, patchBuilder.build());

        // release the client
        client.release();
    }

    public void updateDocJsonpath(DatabaseClient client) {

        // create a manager for JSON documents
        JSONDocumentManager docMgr = client.newJSONDocumentManager();

        DocumentPatchBuilder patchBuilder = docMgr.newPatchBuilder();

        patchBuilder.pathLanguage(DocumentPatchBuilder.PathLanguage.JSONPATH);
        ObjectMapper mapper = new ObjectMapper();

        //replace node
        patchBuilder.replaceFragment("$.dependents[?(@.name='josie')]"
                , mapper.createObjectNode().put("name", "josef").put("age", 10));

        System.out.println ("patch " + patchBuilder.build().toString());

        docMgr.patch(docUri, patchBuilder.build());

        // release the client
        client.release();
    }

    public void deleteCollection(DatabaseClient client, String collection) {
        QueryManager qman = client.newQueryManager();
        DeleteQueryDefinition delDef = qman.newDeleteDefinition();
        delDef.setCollections(collection);
        qman.delete(delDef);
    }

}
