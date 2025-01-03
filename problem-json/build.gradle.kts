plugins {
    id("io.micronaut.build.internal.problem-json-module")
}

dependencies {
    annotationProcessor(mnSerde.micronaut.serde.processor)
    annotationProcessor(mnValidation.micronaut.validation.processor)

    api(libs.managed.zalando.problem)
    api(mnSerde.micronaut.serde.api)
    api(mn.jackson.annotations)
    implementation(mnValidation.micronaut.validation)
    implementation(mn.micronaut.http.server)

    // for spock Groovy
    testImplementation(mnValidation.micronaut.validation.processor)
    // for Junit Java
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)

    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(mnTest.micronaut.test.junit5)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testRuntimeOnly(libs.junit.engine)
}
