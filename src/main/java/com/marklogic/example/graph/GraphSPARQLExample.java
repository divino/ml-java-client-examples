
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

    private static String GRAPH_URI = "com.marklogic.client.example.extension.GraphSPARQLExample";

    public void runQuery(DatabaseClient appClient) throws IOException {

        SPARQLQueryManager sparqlMgr = appClient.newSPARQLQueryManager();

        StringHandle result = new StringHandle();

        System.out.println("running query");
        SPARQLQueryDefinition query = sparqlMgr.newQueryDefinition("prefix foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "\n" +
                "select distinct ?name1 ?name2\n" +
                "WHERE\n" +
                "{\n" +
                "  ?p1 foaf:knows ?p2 ;\n" +
                "      foaf:name ?name1 .\n" +
                "  ?p2 foaf:name ?name2\n" +
                "}\n" +
                "\n");
        query.setCollections(GRAPH_URI);

        sparqlMgr.executeSelect(query, result);

        System.out.println(JsonUtil.prettify(result.get().toString()));
    }

    public void insertGraph(DatabaseClient appClient) throws IOException {
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

    public void deleteGraph(DatabaseClient appClient) throws IOException {
        GraphManager graphMgr = appClient.newGraphManager();

        System.out.println("deleting graph");

        graphMgr.delete(GRAPH_URI);

        System.out.println("deleted graph");
    }
}

