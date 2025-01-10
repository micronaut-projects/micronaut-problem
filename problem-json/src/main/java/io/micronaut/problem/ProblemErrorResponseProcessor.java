/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.problem;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import io.micronaut.problem.conf.ProblemConfiguration;
import io.micronaut.web.router.exceptions.UnsatisfiedRouteException;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

/**
 * Error Response processor to respond {@link Problem} responses.
 * <ul>
 *   <li>Adds application/problem+json content type</li>
 *   <li>If the cause is a {@link ThrowableProblem}, it returns as the body.</li>
 *   <li>If the cause is not a {@link ThrowableProblem} , it generates a default Problem based on the {@link ErrorContext} and returns it as the HTTP Body.</li>
 * </ul>
 * @author Sergio del Amo
 * @since 1.0
 * @deprecated Use {@link io.micronaut.problem.ProblemJsonErrorResponseBodyProvider} instead.
 */
@Deprecated(forRemoval = true)
public class ProblemErrorResponseProcessor implements ErrorResponseProcessor<Problem> {

    public static final String APPLICATION_PROBLEM_JSON = "application/problem+json";

    private final ProblemJsonErrorResponseBodyProvider delegate;

    /**
     * Constructor.
     * @param config Problem configuration
     */
    public ProblemErrorResponseProcessor(ProblemConfiguration config) {
        this.delegate = new ProblemJsonErrorResponseBodyProvider(config);
    }

    @Override
    @NonNull
    public MutableHttpResponse<Problem> processResponse(@NonNull ErrorContext errorContext,
                                                        @NonNull MutableHttpResponse<?> baseResponse) {
        if (errorContext.getRequest().getMethod() == HttpMethod.HEAD) {
            return (MutableHttpResponse<Problem>) baseResponse;
        }
        return baseResponse
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(delegate.body(errorContext, baseResponse));
    }

    /**
     * Creates a {@link ThrowableProblem} when the root cause was not an exception of type {@link ThrowableProblem}.
     * @param errorContext Error Context
     * @param httpStatus HTTP Status
     * @return Default problem
     */
    @NonNull
    protected ThrowableProblem defaultProblem(@NonNull ErrorContext errorContext,
                                              @NonNull HttpStatus httpStatus) {
        return delegate.defaultProblem(errorContext, httpStatus);
    }

    /**
     * Whether {@link ThrowableProblem}, created when the root cause is not an exception of type {@link ThrowableProblem}, should
     * contain Error::getMessage in the problem detail.
     *
     * To avoid accidental information leakage defaults to false unless the root cause is of type {@link UnsatisfiedRouteException}
     * which contains helpful information to diagnose the issue (e.g. missing required query value) in the details.
     *
     * @param errorContext Error Context
     * @return To avoid accidental information leakage defaults to false unless the root cause is of type {@link UnsatisfiedRouteException}
     */
    protected boolean includeErrorMessage(@NonNull ErrorContext errorContext) {
        return delegate.includeErrorMessage(errorContext);
    }
}
