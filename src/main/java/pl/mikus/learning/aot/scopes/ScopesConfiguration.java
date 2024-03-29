package pl.mikus.learning.aot.scopes;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Configuration
class ScopesConfiguration {
}

@Controller
@ResponseBody
class ScopeHttpController {

    private final RequestContext requestContext;

    ScopeHttpController(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @GetMapping("/scopes/context")
    String uuid() {
        return requestContext.getUuid();
    }
}

@Component
@RequestScope
class RequestContext {
    private final String uuid = UUID.randomUUID().toString();

    String getUuid() {
        return uuid;
    }
}
