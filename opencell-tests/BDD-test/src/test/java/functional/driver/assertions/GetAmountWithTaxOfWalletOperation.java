package functional.driver.assertions;

import functional.driver.utils.Constants;
import functional.driver.utils.KeyCloakAuthenticationHook;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import org.hamcrest.CoreMatchers;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class GetAmountWithTaxOfWalletOperation implements Question<Object> {

    private final String order;
    private final String entity;
    private final String field;
    private final String expectedValue;

    public GetAmountWithTaxOfWalletOperation(String order, String entity, String field, String expectedValue) {
        this.order = order;
        this.entity = entity;
        this.field = field;
        this.expectedValue = expectedValue;
    }

    public static Question<Object> called(String order, String entity, String field, String expectedValue) {
        return new GetAmountWithTaxOfWalletOperation(order, entity, field, expectedValue);
    }

    @Override
    public Object answeredBy(Actor actor) {
        final String url = "/v2/generic/all/" + entity;
        final String bodyRequest = "{\"limit\":\"1\","
                + "\"offset\":" + this.order + ",\"filters\":{\"code\":\"CH_USG_UNIT\"}}";

        actor.attemptsTo(
                Post.to(url)
                        .with(request -> request.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                        .with(request -> request.header(
                                HttpHeaders.AUTHORIZATION, Constants.OAUTH2 + " " + KeyCloakAuthenticationHook.getToken())
                                .body(bodyRequest)
                        )
        );

        actor.should(
                ResponseConsequence.seeThatResponse(response -> response.statusCode(200)
                        .body("data[0]." + field, CoreMatchers.equalTo(expectedValue)))
        );

        return null;
    }
}
