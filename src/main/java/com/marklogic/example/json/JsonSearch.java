package com.marklogic.example.json;

import com.google.gson.*;
import com.marklogic.client.*;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.example.utils.QueryOptionsUtil;
import org.apache.commons.codec.binary.StringUtils;

public class JsonSearch {

    public void searchByExample(DatabaseClient client) {
        QueryManager queryMgr = client.newQueryManager();

        // create a searchByExample definition
        StringHandle handle = new StringHandle(
               // "{ \"$query\": { \"firstName\": \"Bevis\" } }"
                "{ \"$query\": { \"name\": \"john\", \"age\": 11 } }"
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

        String[] collections = {"structuredQuery-samples-marklogic"};

        structuredQuery(client
                , queryParam
                , QueryOptionsUtil.OPTIONS_NAME_ALL
                , collections
                , "/marklogic"
                , pageLength
                , 1);
    }

    public void structuredQuery(DatabaseClient client, String queryParam, long pageLength, long start) {
        String[] collections = {"structuredQuery-samples-marklogic"};

        structuredQuery(client
                , queryParam
                , QueryOptionsUtil.OPTIONS_NAME_ALL
                , collections
                , "/marklogic"
                , pageLength
                , start);
    }

    public void structuredQuery(DatabaseClient client
            , String queryParam
            , String optionName
            , String[] collections
            , String directory
            , long pageLength
            , long start) {

        // create a manager for searching
        QueryManager queryMgr = client.newQueryManager();
        queryMgr.setPageLength(pageLength);

        // create a structuredQuery definition
        StringQueryDefinition querydef = queryMgr.newStringDefinition(optionName);
        querydef.setCriteria(queryParam);

        if (null != collections && collections.length > 0) {
            querydef.setCollections(collections);
        }

        if (null != directory && directory.length() > 0) {
            querydef.setDirectory(directory);
        }

        // create a raw content handle for the structuredQuery results
        StringHandle resultsHandle = new StringHandle().withFormat(Format.JSON);

        // run the structuredQuery
        queryMgr.search(querydef, resultsHandle, start);

        System.out.println("Matched documents with '"+querydef.getCriteria()+"'\n");

        // all we can do is output the result.
        System.out.println("RESULT All    " + JsonUtil.prettify(resultsHandle.get().toString()));

        // release the client
        client.release();
    }

}
