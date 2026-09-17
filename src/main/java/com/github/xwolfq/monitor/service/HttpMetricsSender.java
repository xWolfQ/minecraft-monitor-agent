package com.github.xwolfq.monitor.service;

import com.github.xwolfq.monitor.config.PluginConfig;
import com.github.xwolfq.monitor.model.MetricsPayload;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Logger;

/**
 * Wysyła zmierzone metryki do backendu Minecraft Monitor.
 *
 * <p>Payload jest serializowany przez Gson do JSON i przesyłany metodą HTTP POST
 * na adres {@code {backend-url}{metrics-path}} z nagłówkami
 * {@code X-API-Key} oraz {@code X-Server-UUID}.</p>
 *
 * <p>Metoda {@link #send(PluginConfig, MetricsPayload)} blokuje wątek do czasu
 * odpowiedzi lub timeoutu — należy ją wywoływać poza głównym wątkiem serwera.</p>
 */
public class HttpMetricsSender {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;
    private final Gson gson;
    private final Logger logger;

    public HttpMetricsSender(Logger logger) {
        this(HttpClient.newBuilder().connectTimeout(TIMEOUT).build(), new Gson(), logger);
    }

    /**
     * Konstruktor wewnętrzny — ułatwia podstawianie atrap w testach.
     */
    HttpMetricsSender(HttpClient httpClient, Gson gson, Logger logger) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.logger = logger;
    }

    /**
     * Wynik próby wysłania metryk.
     *
     * @param success     {@code true} gdy backend odpowiedział kodem 2xx
     * @param description opis wyniku (status HTTP) lub przyczyny błędu
     */
    public record Result(boolean success, String description) {
    }

    /**
     * Wysyła payload metryk na skonfigurowany endpoint backendu.
     *
     * @param cfg     aktualna konfiguracja (URL, klucz API, ścieżka endpointu)
     * @param payload kompletny payload metryk
     * @return wynik wysyłki wraz z opisem
     */
    public Result send(PluginConfig cfg, MetricsPayload payload) {
        if (cfg.backendUrl() == null || cfg.backendUrl().isBlank()) {
            return new Result(false, "backend-url nie jest skonfigurowany");
        }

        try {
            var response = httpClient.send(buildRequest(cfg, payload), HttpResponse.BodyHandlers.ofString());
            var description = "HTTP " + response.statusCode();
            var success = response.statusCode() >= 200 && response.statusCode() < 300;

            if (!success) {
                logger.warning("Backend zwrócił błąd przy wysyłce metryk: " + description);
            }
            return new Result(success, description);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            logger.warning("Nie udało się wysłać metryk: " + e.getMessage());
            return new Result(false, e.getMessage());
        }
    }

    private HttpRequest buildRequest(PluginConfig cfg, MetricsPayload payload) {
        String json = gson.toJson(payload);
        return HttpRequest.newBuilder(URI.create(endpointUri(cfg)))
                .header("Content-Type", "application/json")
                .header("X-API-Key", cfg.apiKey())
                .header("X-Server-UUID", cfg.serverUuid())
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .timeout(TIMEOUT)
                .build();
    }

    /**
     * Buduje pełny adres endpointu z base URL i względnej ścieżki, np. {@code http://host:8080/metrics}.
     */
    private String endpointUri(PluginConfig cfg) {
        var base = cfg.backendUrl();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        var path = cfg.metricsPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return base + path;
    }
}