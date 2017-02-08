
/*
 * Copyright 2012-2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.example.graph;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.impl.SPARQLQueryDefinitionImpl;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
import com.marklogic.example.json.JsonUtil;
import com.marklogic.example.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GraphSPARQLExample {

    public static String GRAPH_URI = "GraphSPARQLExample";
    public static String GRAPH_URI_FOR_CRUD = "GraphSPARQLExampleCRUD";

    public void runQuery1(DatabaseClient appClient) throws IOException {
        runQuery(appClient, GRAPH_URI, "prefix foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "\n" +
                "select distinct ?name1 ?name2\n" +
                "WHERE\n" +
                "{\n" +
                "  ?p1 foaf:knows ?p2 ;\n" +
                "}\n" +
                "\n");
    }

    public void runQuery2(DatabaseClient appClient) throws IOException {
        runQuery(appClient, GRAPH_URI_FOR_CRUD, "prefix ex: <http://example.org/>\n" +
                "\n" +
                "select ?p ?s\n" +
                "WHERE\n" +
                "{\n" +
                "  ex:jeff_sterling ?p ?s;\n" +
                "}\n" +
                "\n");
    }

    public void runQuery(DatabaseClient appClient, String graphUri, String sparqlQuery) throws IOException {

        SPARQLQueryManager sparqlMgr = appClient.newSPARQLQueryManager();
        sparqlMgr.setPageLength(5);

        StringHandle result = new StringHandle();

        System.out.println("running query");
        SPARQLQueryDefinition query = sparqlMgr.newQueryDefinition(sparqlQuery);
        query.setCollections(graphUri);

        sparqlMgr.executeSelect(query, result);

        System.out.println(JsonUtil.prettify(result.get().toString()));
    }

    public void loadTriplesFromFile(DatabaseClient appClient) throws IOException {

        String filename = "data"
                + File.separator
                + "graph"
                + File.separator
                + "foaf"
                + File.separator
                + "foaf1.nt";

        InputStream tripleStream = Util.openStreamRelative(filename);
        if (tripleStream == null)
            throw new RuntimeException("Could not read triples from " + filename);

        GraphManager graphMgr = appClient.newGraphManager();

        System.out.println("inserting graph");

        graphMgr.write(GRAPH_URI, new InputStreamHandle(tripleStream).withMimetype(RDFMimeTypes.NQUADS));

        System.out.println("inserted graph");
    }

    public void deleteGraph(DatabaseClient appClient, String graphUri) throws IOException {
        GraphManager graphMgr = appClient.newGraphManager();

        System.out.println("deleting graph " + graphUri);

        graphMgr.delete(graphUri);

        System.out.println("deleted graph " + graphUri);
    }

    public void insertTriples(DatabaseClient appClient) throws IOException {
        GraphManager graphMgr = appClient.newGraphManager();

        StringHandle stringHandle = new StringHandle()
                .with("<http://example.org/jeff_sterling> <http://example.org/name> \"Jeff Sterling\" .\n"
                + "<http://example.org/jeff_sterling> <http://example.org/address> \"Telok Blangah, Singapore\" .\n"
                + "<http://example.org/jeff_sterling> <http://example.org/age> \"25\" .\n"
                + "<http://example.org/jeff_sterling> <http://example.org/company> \"Apple\" .\n")
                .withMimetype(RDFMimeTypes.NTRIPLES);
        graphMgr.merge(GRAPH_URI_FOR_CRUD, stringHandle);

        appClient.release();
    }

    public void deleteTriples(DatabaseClient appClient) throws IOException {
        String updateQuery = "PREFIX ex:<http://example.org/>\n" +
                "WITH <" + GRAPH_URI_FOR_CRUD + ">\n" +
                "DELETE \n" +
                "{ \n" +
                "  ex:jeff_sterling ?p ?o .\n" +
                "}\n" +
                "WHERE \n" +
                "{ \n" +
                "  ex:jeff_sterling ?p ?o .\n" +
                "}";
        executeUpdateTriples(appClient, updateQuery);
    }

    public void updateTriples(DatabaseClient client) throws IOException {
        String updateQuery = "PREFIX ex:<http://example.org/>\n" +
                "WITH <" + GRAPH_URI_FOR_CRUD + ">\n" +
                "DELETE\n" +
                "{\n" +
                "  ex:jeff_sterling ex:address ?o .\n" +
                "}\n" +
                "INSERT\n" +
                "{ \n" +
                "  ex:jeff_sterling ex:address \"New York\" .\n" +
                "}\n" +
                "WHERE { \n" +
                "  ex:jeff_sterling ex:address ?o .\n" +
                "}";
        executeUpdateTriples(client, updateQuery);
    }

    public void executeUpdateTriples(DatabaseClient appClient, String updateQuery) throws IOException {
        SPARQLQueryManager sparqlMgr = appClient.newSPARQLQueryManager();
        SPARQLQueryDefinition sqd = new SPARQLQueryDefinitionImpl(updateQuery);
        //sqd.setSparql(updateQuery);
        System.out.println("SPARQL UPDATE : " + sqd.getSparql());
        sparqlMgr.executeUpdate(sqd);
        appClient.release();
    }

}

