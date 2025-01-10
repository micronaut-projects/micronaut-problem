package io.micronaut.problem;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "HtmlErrorPageTest")
@MicronautTest
class HtmlErrorPageTest {

    @Test
    void testHtmlErrorPage(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.POST("/book/save", new Book("Building Microservices", "", 5000))
            .accept(MediaType.TEXT_HTML);
        HttpClientResponseException ex = assertThrows(HttpClientResponseException.class, () -> client.exchange(request));
        assertEquals(MediaType.TEXT_HTML_TYPE, ex.getResponse().getContentType().get());
        Optional<String> htmlOptional = ex.getResponse().getBody(String.class);
        assertTrue(htmlOptional.isPresent());
        String html = htmlOptional.get();
        assertTrue(html.contains("<!doctype html>"));
        assertTrue(html.contains("book.author: must not be blank"));
        assertTrue(html.contains("book.pages: must be less than or equal to 4032"));
        assertTrue(ex.getResponse().getContentType().isPresent());
    }

    @Requires(property = "spec.name", value = "HtmlErrorPageTest")
    @Controller("/book")
    static class FooController {

        @Produces(MediaType.TEXT_HTML)
        @Post("/save")
        @Status(HttpStatus.CREATED)
        void save(@Body @Valid Book book) {
            throw new UnsupportedOperationException();
        }
    }

    @Introspected
    record Book(@NotBlank String title, @NotBlank String author, @Max(4032) int pages) {

    }
}
