package com.marklogic.example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.example.graph.GraphSPARQLExample;
import com.marklogic.example.json.JsonCRUD;
import com.marklogic.example.json.JsonSearch;
import com.marklogic.example.utils.QueryOptionsUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarkLogicExamplesMain {

	public DatabaseClient getDBClient() {
		return DatabaseClientFactory.newClient(
			"localhost",
			50040,
			"admin",
			"admin",
			DatabaseClientFactory.Authentication.BASIC);
	}

	public static void main(String[] args) {
		SpringApplication.run(MarkLogicExamplesMain.class, args);

		MarkLogicExamplesMain main = new MarkLogicExamplesMain();

		try {
			JsonCRUD jsonCrud = new JsonCRUD();
			jsonCrud.createDoc(main.getDBClient());
			jsonCrud.verify(main.getDBClient());
			jsonCrud.updateDocXpath(main.getDBClient());
			jsonCrud.verify(main.getDBClient());
			jsonCrud.replaceArrayNode(main.getDBClient());
			jsonCrud.verify(main.getDBClient());
			jsonCrud.updateDocJsonpath(main.getDBClient());
			jsonCrud.verify(main.getDBClient());
			//jsonCrud.deleteDocument(main.getDBClient());

			JsonSearch jsonSearch = new JsonSearch();
			jsonCrud.loadDocs(main.getDBClient());
			jsonSearch.searchByExample(main.getDBClient());

			QueryOptionsUtil.configure(main.getDBClient());
			jsonSearch.structuredQuery(main.getDBClient(), "firstName:\"Sarah\"", 10);
			jsonSearch.structuredQuery(main.getDBClient(), "canada", 5);
			jsonSearch.structuredQuery(main.getDBClient(), "sort:\"dob-asc\"", 10);

			//enable this to cleanup
			jsonCrud.deleteCollection(main.getDBClient(), "structuredQuery-samples-marklogic");

			GraphSPARQLExample gse = new GraphSPARQLExample();
			gse.loadTriplesFromFile(main.getDBClient());
			gse.runQuery1(main.getDBClient());
			gse.insertTriples(main.getDBClient());
			gse.runQuery2(main.getDBClient());
			gse.updateTriples(main.getDBClient());
			gse.runQuery2(main.getDBClient());
			// cleanup
			gse.deleteTriples(main.getDBClient());
			gse.deleteGraph(main.getDBClient(), GraphSPARQLExample.GRAPH_URI);
			gse.deleteGraph(main.getDBClient(), GraphSPARQLExample.GRAPH_URI_FOR_CRUD);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
