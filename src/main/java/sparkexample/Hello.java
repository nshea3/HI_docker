package sparkexample;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.nio.ch.IOUtil;
import java.nio.charset.StandardCharsets;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.dstu2.valueset.*;
import ca.uhn.fhir.model.primitive.IdDt;

import static spark.Spark.get;


public class Hello {

    public static void main(String[] args) {
        get("/", (req, res) -> {
            FhirContext ctx = FhirContext.forDstu2();
            String serverBase = "http://fhirtest.uhn.ca/baseDstu2";
            IGenericClient client = ctx.newRestfulGenericClient(serverBase);
            Bundle results = client
                    .search()
                    .forResource(Patient.class)
                    .where(Patient.FAMILY.matches().value("duck"))
                    .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                    .execute();
            return results.getEntry().size();
        });
    }

}
