package com.marklogic.example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
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
			jsonCrud.deleteDocument(main.getDBClient());

			JsonSearch jsonSearch = new JsonSearch();
			jsonCrud.loadDocs(main.getDBClient());
			jsonSearch.searchByExample(main.getDBClient());

			QueryOptionsUtil.configure(main.getDBClient());
			jsonSearch.structuredQuery(main.getDBClient(), "firstName:\"Sarah\"", 10);
			jsonSearch.structuredQuery(main.getDBClient(), "canada", 5);
			jsonSearch.structuredQuery(main.getDBClient(), "sort:\"dob-asc\"", 10);

			//enable this to cleanup
			jsonCrud.deleteCollection(main.getDBClient(), "structuredQuery-samples-marklogic");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
