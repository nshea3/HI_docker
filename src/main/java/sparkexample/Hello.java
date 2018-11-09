package sparkexample;

import ca.uhn.fhir.parser.IParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.nio.ch.IOUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.model.primitive.IdDt;

import static spark.Spark.get;


public class Hello {

    public static void main(String[] args) {

        //get("/", (req, res) -> "Hello Worlddd");
        get("/", (req, res) -> {
            FhirContext ctx = FhirContext.forDstu3();
            String serverBase = "http://hapi.fhir.org/baseDstu3"; //try HDAP test servers
            IGenericClient client = ctx.newRestfulGenericClient(serverBase);

            // Placeholder
            String id = "23129";

            // Patient object if needed
            Patient patient = client.read().resource(Patient.class).withId(id).execute();

            // Get all Medication Statements that are associated with the patient ID
            Bundle results = client
                    .search()
                    .forResource(MedicationStatement.class)
                    .where(MedicationStatement.PATIENT.hasId(id))
                    .returnBundle(Bundle.class)
                    .prettyPrint()
                    .encodedJson()
                    .execute();

            // Get the bundle in list form
            List<BundleEntryComponent> resultsList = new ArrayList<BundleEntryComponent>();
            // Handle pagination (limited to 20 entries a page). If there was more than one page, get those medication statements too
            while (results != null) {
                resultsList.addAll(results.getEntry());
                // Load next page if there is one
                if(results.getLink(Bundle.LINK_NEXT) != null) {
                    results = client.loadPage().next(results).execute();
                // Else we are on the last page, so stop
                } else {
                    results = null;
                }
            }
            //System.out.println(resultsList.size());

            // Cast all results to Medication Statement objects and put in a new list
            List<MedicationStatement> medications = new ArrayList<MedicationStatement>();
            for(int i = 0; i < resultsList.size(); i++){
                MedicationStatement medStatement = (MedicationStatement) resultsList.get(i).getResource();
                medications.add(medStatement);
            }

            // Return all the Medication Statements as Json strings, to display in webpage
            IParser jsonParser = ctx.newJsonParser();
            jsonParser.setPrettyPrint(true);
            List<String> encodedJsons = new ArrayList<String>();
            for(int i = 0; i < medications.size(); i++){
                String encodedJson = jsonParser.encodeResourceToString(medications.get(i));
                encodedJsons.add(encodedJson);
            }
            System.out.println(encodedJsons);
            return encodedJsons;

        });
        //System.out.println("Hello World");

    }

}












/* OLD DSTU2 VERSION

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

        // Original code
        //get("/", (req, res) -> {
        //    FhirContext ctx = FhirContext.forDstu2();
        //    String serverBase = "http://fhirtest.uhn.ca/baseDstu2";
        //    IGenericClient client = ctx.newRestfulGenericClient(serverBase);
        //    Bundle results = client
        //            .search()
        //            .forResource(Patient.class)
        //            .where(Patient.FAMILY.matches().value("Smith"))
        //            .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
        //            .execute();
        //    return results.getEntry().size();
        //});



        // New code
        //get("/", (req, res) -> "Hello Worlddd");
        get("/", (req, res) -> {
            FhirContext ctx = FhirContext.forDstu2();
            String serverBase = "http://fhirtest.uhn.ca/baseDstu2";
            IGenericClient client = ctx.newRestfulGenericClient(serverBase);

            // Get Patient with the given ID
            //Patient patient = client.read().resource(Patient.class).withId(id).execute();

            // Get the Medication Statement attatched to that Patient

            Bundle results = client
                    .search()
                    .forResource(MedicationStatement.class)
                    //.where(Patient.FAMILY.matches().value("Smith"))
                    .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                    .prettyPrint()
                    .encodedJson()
                    .execute();

            MedicationStatement ms = (MedicationStatement) results.getEntry().get(0).getResource();
            String encoded = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(ms);
            return encoded;
        });
        System.out.println("Hello World");

    }

}
*/