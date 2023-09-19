package com.cloudtalk.tool.converter;

import com.cloudtalk.tool.model.ConverterRequest;
import com.cloudtalk.tool.util.ConverterConstants;
import com.cloudtalk.tool.util.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class ConverterHelpers {
    public final ObjectMapper objectMapper;

    public ConverterHelpers() {
        objectMapper = new ObjectMapper();
    }

    public Response doGetRequest(String url, ConverterRequest converterRequest) {
        AtomicReference<Response> responseAtomicReference = new AtomicReference<>(HttpClient.doGetRequest(url, converterRequest));
        try {
            Awaitility.await()
                    .atMost(ConverterConstants.DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .pollInterval(ConverterConstants.DEFAULT_POLL_INTERVAL_IN_SECONDS, TimeUnit.SECONDS)
                    .until(() -> {
                        Response response = HttpClient.doGetRequest(url, converterRequest);
                        responseAtomicReference.set(response);
                        int responseCode = response.code();
                        return responseCode != ConverterConstants.SERVICE_UNAVAILABLE_ERROR_CODE;
                    });
            return responseAtomicReference.get();
        } catch (ConditionTimeoutException e) {
            log.error(String.format("Exception thrown:%s", e.getMessage()));
        }
        return responseAtomicReference.get();
    }

    public Response doGetRequestWithoutParameters(String url) {
        AtomicReference<Response> responseAtomicReference = new AtomicReference<>(HttpClient.doGetRequestWithoutParameters(url));
        try {
            Awaitility.await()
                    .atMost(ConverterConstants.DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .pollInterval(ConverterConstants.DEFAULT_POLL_INTERVAL_IN_SECONDS, TimeUnit.SECONDS)
                    .until(() -> {
                        Response response = HttpClient.doGetRequestWithoutParameters(url);
                        responseAtomicReference.set(response);
                        int responseCode = response.code();
                        return responseCode != ConverterConstants.SERVICE_UNAVAILABLE_ERROR_CODE;
                    });
            return responseAtomicReference.get();
        } catch (ConditionTimeoutException e) {
            log.error(String.format("Exception thrown:%s", e.getMessage()));
        }
        return responseAtomicReference.get();
    }

    public Response doRequest(String url, String method, String body) {
        AtomicReference<Response> responseAtomicReference = new AtomicReference<>(HttpClient.doRequest(url, method, body));
        try {
            Awaitility.await()
                    .atMost(ConverterConstants.DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                    .pollInterval(ConverterConstants.DEFAULT_POLL_INTERVAL_IN_SECONDS, TimeUnit.SECONDS)
                    .until(() -> {
                        Response response = HttpClient.doRequest(url, method, body);
                        responseAtomicReference.set(response);
                        int responseCode = response.code();
                        return responseCode != ConverterConstants.SERVICE_UNAVAILABLE_ERROR_CODE;
                    });
            return responseAtomicReference.get();
        } catch (ConditionTimeoutException e) {
            log.error(String.format("Exception thrown:%s", e.getMessage()));
        }
        return responseAtomicReference.get();
    }

    @SneakyThrows
    public String getResponseInStringFormat(Response response) {
        return Objects.requireNonNull(response.body()).string();
    }

    public static Stream<String> generateInvalidStringInputs() {
        return Stream.of("test", "18/12/2023", "20_10_2023", "10%10*2023", "10:20:2023", "");
    }

    @SneakyThrows
    public static Stream<Arguments> generateInvalidHttpMethods() {
        ConverterRequest converterRequest = ConverterRequest.builder().build();
        String requestBody = new ObjectMapper().writeValueAsString(converterRequest);
        return Stream.of(arguments("POST", requestBody),
                arguments("PUT", requestBody),
                arguments("PATCH", requestBody),
                arguments("DELETE", ""));
    }
}