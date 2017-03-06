package com.marklogic.example.eval;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.example.MarkLogicExamplesMain;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by dbagayau on 21/02/2017.
 */
public class Eval {

    public static String readFile(String path)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }

    public static void processXqy(DatabaseClient client, String filename) throws IOException {
        String query = readFile(filename);
        ServerEvaluationCall eval = client.newServerEval();
        eval.xquery(query).eval();
    }

    public static ServerEvaluationCall processXqyWithParam(DatabaseClient client, String filename) throws IOException {
        String query = readFile(filename);
        ServerEvaluationCall eval = client.newServerEval();
        return eval.xquery(query);
    }

    public static void main (String[] args) {
        MarkLogicExamplesMain main = new MarkLogicExamplesMain();
        try {
            // no param example
            processXqy(main.getDBClient(), "src/main/resources/insert_docs.xqy");

            // with param example
            ServerEvaluationCall sec = processXqyWithParam(main.getDBClient(), "src/main/resources/insert_docs_with_param.xqy");
            sec.addVariable("docUri", "/address/supra_tower.json")
                    .addVariable("contents", new JacksonHandle().with(new ObjectMapper().readTree("{\n" +
                            "  \"location\": {\n" +
                            "    \"building\": \"supra tower\",\n" +
                            "    \"street\": \"st. michael\",\n" +
                            "    \"city\": \"Singapore\",\n" +
                            "    \"zipCode\": \"12121\",\n" +
                            "    \"country\": \"Singapore\"\n" +
                            "  }\n" +
                            "}")))
                    .eval();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
