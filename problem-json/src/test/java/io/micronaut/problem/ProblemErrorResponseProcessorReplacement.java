package io.micronaut.problem;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.problem.conf.ProblemConfiguration;
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException;
import jakarta.inject.Singleton;

@Requires(property = "spec.name", value = "DataLeakageOverrideSpec")
//tag::clazz[]
@Replaces(ProblemJsonErrorResponseBodyProvider.class)
@Singleton
public class ProblemErrorResponseProcessorReplacement
        extends ProblemJsonErrorResponseBodyProvider {
    ProblemErrorResponseProcessorReplacement(ProblemConfiguration config) {
        super(config);
    }

    @Override
    protected boolean includeErrorMessage(@NonNull ErrorContext errorContext) {
        return errorContext.getRootCause()
                .map(t -> t instanceof FooException || t instanceof UnsatisfiedRouteException)
                .orElse(false);
    }
}
//end::clazz[]
