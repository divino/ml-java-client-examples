package com.marklogic.example.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.*;
import org.apache.http.client.utils.DateUtils;

import java.io.*;
import java.util.Date;

public class JsonCRUD {

    String docUri = "/marklogic/example/maria.json";
    String jsonFile = "data/json/persons.json";

    public void deleteDocument(DatabaseClient client) {
        try {
            JsonCrudUtil.deleteDocument(client, this.docUri);
            verify(client);
        } catch (ResourceNotFoundException e) {
            System.out.println("deleted " + this.docUri);
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

        JsonArray triplesArray = new JsonArray();
        JsonObject tripleObj1 = new JsonObject();
        tripleObj1.addProperty("subject", "http://example.org/#josie");
        tripleObj1.addProperty("predicate", "http://xmlns.com/foaf/0.1/firstname/");
        JsonObject tripleObj1Value = new JsonObject();
        tripleObj1Value.addProperty("datatype", "http://www.w3.org/2001/XMLSchema#string");
        tripleObj1Value.addProperty("value", "josie");
        tripleObj1.add("object", tripleObj1Value);
        JsonObject tripleObj2 = new JsonObject();
        tripleObj2.addProperty("subject", "http://example.org/#josie");
        tripleObj2.addProperty("predicate", "http://example.org/parentUri/");
        JsonObject tripleObj2Value = new JsonObject();
        tripleObj2Value.addProperty("datatype", "http://www.w3.org/2001/XMLSchema#string");
        tripleObj2Value.addProperty("value", this.docUri);
        tripleObj2.add("object", tripleObj2Value);
        triplesArray.add(tripleObj1);
        triplesArray.add(tripleObj2);

        JsonObject writeRoot = new JsonObject();
        writeRoot.add("data", dataObj);
        writeRoot.add("metaData", metaDataObj);
        writeRoot.add("tags",  tagsArray);
        writeRoot.add("dependents", dependentsArray);
        writeRoot.add("triple", triplesArray);

        JsonCrudUtil.insertDoc(client, this.docUri, writeRoot);

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
        System.out.println("DOC " + JsonCrudUtil.getJsonDocString(client, this.docUri));
    }

    public void loadDocs(DatabaseClient client) throws IOException {
        JsonCrudUtil.loadDocs(client, this.jsonFile, "stringQuery-samples-marklogic");
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

    public void removeArrayNodeJsonpath(DatabaseClient client) {
        JsonCrudUtil.removeNodeJsonpath(client, docUri, "$.dependents[?(@.name='josef')]");
        client.release();
    }

}
