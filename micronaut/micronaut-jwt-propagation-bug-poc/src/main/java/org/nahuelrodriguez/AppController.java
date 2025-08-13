package io.github.nahuel92;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller
// Endpoints themselves are not required to be protected, JWT just needs to be propagated to a downstream service only.
@Secured(SecurityRule.IS_ANONYMOUS)
public class AppController {
    private final ExternalServiceApi externalServiceApi;

    AppController(final ExternalServiceApi externalServiceApi) {
        this.externalServiceApi = externalServiceApi;
    }

    @Get("/automatic")
    String automaticJwtPropagation() {
        // JWT is not propagated (this is the bug).
        return externalServiceApi.jwtShouldBeAutomaticallyPropagatedForThisCall();
    }

    @Get("/manual")
    String manualJwtPropagation(@Header(HttpHeaders.AUTHORIZATION) final String jwt) {
        return externalServiceApi.jwtIsManuallyPropagatedForThisCall(jwt);
    }
}
