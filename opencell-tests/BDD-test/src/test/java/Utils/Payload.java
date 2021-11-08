package Utils;

import functional.driver.utils.Constants;
import io.restassured.response.ExtractableResponse;
import org.apache.http.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Class contains all useful methods to construct, compare, update payload
 * @author Thang Nguyen
 * @version 1.0
 */
public class Payload {

    static JSONParser parser = new JSONParser();

    /**
     * Compare two payloads: payload that we want to update and result retrieved from
     * database after executing the UPDATE request
     * @author Thang Nguyen
     * @version 1.0
     * @since   2020-12-06
     * @param payload : payload that we want to update on the entity
     * @param result : result retrieved from database
     * @return boolean value : true if the entity has been successfully updated, false otherwise
     * @throws ParseException
     */
    public static void comparePayloadToResult( String payload, String result, String entity ) throws ParseException {
        JSONObject jsonPayload = (JSONObject) parser.parse( payload );
        JSONObject jsonResponse = (JSONObject) parser.parse(
                ( (JSONObject) parser.parse( result ) ).get("data").toString() );

        System.out.println( Constants.ANSI_BLUE + "========================================" );
        System.out.println( Constants.ANSI_RESET + "For the entity : " + Constants.ANSI_RED + entity );
        for ( Iterator iterator = jsonPayload.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();

            if ( ! jsonPayload.get(key).toString()
                    .equals( jsonResponse.get(key).toString() ) ) {
                System.out.println( Constants.ANSI_RESET + "Field is not updated : "
                        + Constants.ANSI_RED + key );
            }
        }
        System.out.println( Constants.ANSI_BLUE + "========================================" );
    }

    /**
     * Generate a new payload from the response of the request POST used to get a particular
     * entity (based on id). This new payload is used for updating the entity
     * @author  Thang Nguyen
     * @version 1.0
     * @since   2020-12-07
     * @param payload : the initial payload
     * @param env : the environment on that all requests are executed
     * @return the method returns the new payload whose fields have been updated
     * @throws ParseException
     */
    public static String generatePayload( String payload ) throws ParseException {
        JSONObject jsonResponse = (JSONObject) parser.parse(
                ((JSONObject) parser.parse(payload)).get("data").toString());

        // Remove fields that we cannot update
        jsonResponse.remove("id");
        jsonResponse.remove("auditable");

        // May be we need to create special treatment process for these kinds of field
        // (accountType, invoiceRoundingMode, etc.)
        jsonResponse.remove("accountType");
        jsonResponse.remove("invoiceRoundingMode");
        jsonResponse.remove("roundingMode");
        jsonResponse.remove("paymentMethods");

        for (Iterator iterator = jsonResponse.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            Object element = jsonResponse.get(key);

            if (element instanceof JSONObject) {
                Set aSetObj = ((JSONObject) element).keySet();
                String url = Constants.PREFIX_POST_ALL_API_V2 + key;
                ExtractableResponse aResponse =
                        RestApiUtils.post(url, Constants.EMPTY_PAYLOAD_TO_VERIFY_EXISTENCE).extract();

                if (aResponse.statusCode() == HttpStatus.SC_OK) {
                    String getAllPostResponse = aResponse.asString();
//                    System.out.println("url : " + url);
//                    System.out.println("getAllPostResponse : " + getAllPostResponse);
                    JSONArray jsonAllPostResponse = (JSONArray) parser.parse(
                            ((JSONObject) parser.parse(getAllPostResponse)).get("data").toString());
                    Random ran = new Random();

                    for (Object anObj : aSetObj) {
                        Object arrResp[] = new Object[jsonAllPostResponse.size()];
                        for (int i = 0; i < jsonAllPostResponse.size(); i++) {
                            JSONObject jsonObj = (JSONObject) jsonAllPostResponse.get(i);
                            arrResp[i] = jsonObj.get(anObj);
                        }
                        ((JSONObject) element).put(anObj, arrResp[ran.nextInt(arrResp.length)]);
                    }
                    jsonResponse.put(key, element);
                }
            } else if (element instanceof Boolean) {
                if (element.equals(Boolean.FALSE))
                    jsonResponse.put(key, Boolean.TRUE);
                else
                    jsonResponse.put(key, Boolean.FALSE);
            } else if (element instanceof String) {
                jsonResponse.put(key, "updated_" + element.toString());
            }
        }
        return jsonResponse.toString();
    }

    /**
     * Update the payload when users want to modify several fields
     * @author  Thang Nguyen
     * @version 1.0
     * @since   2020-12-07
     * @param payload : the initial payload before being updated
     * @param updatedFields : a map that contains all fields which will be updated
     * @return the payload after being updated
     * @throws ParseException
     */
    public static String updatePayload( String payload, Map<String, String> updatedFields ) throws ParseException {
        JSONObject jsonPayload = (JSONObject) parser.parse( payload );

        for ( Map.Entry<String, String> entry : updatedFields.entrySet() ) {
            jsonPayload.put( entry.getKey(), entry.getValue() );
        }

        return jsonPayload.toString();
    }

}
