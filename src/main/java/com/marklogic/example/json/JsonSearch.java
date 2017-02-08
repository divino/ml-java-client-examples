package com.marklogic.example.json;

import com.google.gson.*;
import com.marklogic.client.*;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.example.utils.QueryOptionsUtil;

public class JsonSearch {

    public void searchByExample(DatabaseClient client) {
        QueryManager queryMgr = client.newQueryManager();

        // create a searchByExample definition
        StringHandle handle = new StringHandle(
                "{ \"$query\": { \"firstName\": \"Bevis\" } }"
        ).withFormat(Format.JSON);

        RawQueryByExampleDefinition query = queryMgr.newRawQueryByExampleDefinition(handle);

        // create a handle for the searchByExample results
        StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);

        queryMgr.search(query, resultsHandle);

        JsonObject result = new JsonParser().parse(resultsHandle.get()).getAsJsonObject();

        System.out.println("RESULT All    " + JsonUtil.prettify(result.toString()));
        System.out.println("RESULT Total  " + result.get("total").getAsBigInteger());

        client.release();
    }

    public void structuredQuery(DatabaseClient client, String queryParam, long pageLength) {

        // create a manager for searching
        QueryManager queryMgr = client.newQueryManager();
        queryMgr.setPageLength(pageLength);

        // create a structuredQuery definition
        StringQueryDefinition querydef = queryMgr.newStringDefinition(QueryOptionsUtil.OPTIONS_NAME);
        querydef.setCriteria(queryParam);

        String[] collections = {"structuredQuery-samples-marklogic"};
        querydef.setCollections(collections);

        querydef.setDirectory("/marklogic/");

        // create a raw content handle for the structuredQuery results
        StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);

        // run the structuredQuery
        queryMgr.search(querydef, resultsHandle);

        System.out.println("Matched documents with '"+querydef.getCriteria()+"'\n");

        // all we can do is output the result.
        System.out.println("RESULT All    " + JsonUtil.prettify(resultsHandle.get().toString()));

        // release the client
        client.release();
    }

}
