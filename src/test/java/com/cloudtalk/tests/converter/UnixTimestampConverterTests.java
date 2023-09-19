package com.cloudtalk.tests.converter;

import com.cloudtalk.tool.converter.ConverterHelpers;
import com.cloudtalk.tool.model.ConverterRequest;
import com.cloudtalk.tool.util.ConverterConstants;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.SoftAssertions.assertSoftly;


@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UnixTimestampConverterTests {
    private ConverterHelpers converterHelpers;

    @BeforeAll
    void initialSetUp() {
        log.info("Initiating test setup ...");
        converterHelpers = new ConverterHelpers();
    }

    @Test
    @DisplayName("When correct request is made with a Date String, then it's converted to Unix Timestamp")
    void When_CorrectRequestIsSentToConvertDateStringToTimestamp_ThenResponseIsSuccess() {
        ConverterRequest converterRequest = ConverterRequest.builder().build();
        Response response = converterHelpers.doGetRequest(ConverterConstants.BASE_URL, converterRequest);
        String responseString = converterHelpers.getResponseInStringFormat(response);
        assertSoftly(softly -> {
            softly.assertThat(response.isSuccessful()).isTrue();
            softly.assertThat(responseString).isEqualTo(ConverterConstants.DEFAULT_UNIX_TIMESTAMP);
        });

    }

    @Test
    @DisplayName("When correct request is made with a Unix Timestamp, then it's converted to Date String")
    void When_CorrectRequestIsSentToConvertTimeStampToDateString_ThenResponseIsSuccess() {
        ConverterRequest converterRequest = ConverterRequest.builder().s(ConverterConstants.DEFAULT_UNIX_TIMESTAMP).build();
        Response response = converterHelpers.doGetRequest(ConverterConstants.BASE_URL, converterRequest);
        String responseString = converterHelpers.getResponseInStringFormat(response);
        String expectedResponse = String.format("\"%s\"", ConverterConstants.DEFAULT_DATE_STRING);
        assertSoftly(softly -> {
            softly.assertThat(response.isSuccessful()).isTrue();
            softly.assertThat(responseString).isEqualTo(expectedResponse);
        });
    }

    @Test
    @DisplayName("When no values are added to the request's parameters, then Bad Request is returned.")
    void When_NoParametersValuesAreSent_ThenResponseIsFailure() {
        ConverterRequest converterRequest = ConverterRequest.builder().s(null).cached(null).build();
        Response response = converterHelpers.doGetRequest(ConverterConstants.BASE_URL, converterRequest);
        String responseString = converterHelpers.getResponseInStringFormat(response);
        assertSoftly(softly -> {
            softly.assertThat(response.code()).isEqualTo(ConverterConstants.BAD_REQUEST_ERROR_CODE);
            softly.assertThat(responseString).isEqualTo(ConverterConstants.BAD_REQUEST_ERROR_MESSAGE);
        });
    }

    @Test
    @DisplayName("When no parameters are added to the request, then Bad Request is returned.")
    void When_NoParametersAreSent_ThenResponseIsFailure() {
        Response response = converterHelpers.doGetRequestWithoutParameters(ConverterConstants.BASE_URL);
        String responseString = converterHelpers.getResponseInStringFormat(response);
        assertSoftly(softly -> {
            softly.assertThat(response.code()).isEqualTo(ConverterConstants.NOT_FOUND_ERROR_CODE);
            softly.assertThat(responseString).isEqualTo(ConverterConstants.NOT_FOUND_ERROR_MESSAGE);
        });
    }

    @DisplayName("When invalid string is sent, then Bad Request is returned.")
    @ParameterizedTest
    @MethodSource("com.cloudtalk.tool.converter.ConverterHelpers#generateInvalidStringInputs")
    void When_InvalidStringFormatIsSent_ThenResponseIsFailure(String invalidString) {
        ConverterRequest converterRequest = ConverterRequest.builder().s(invalidString).build();
        Response response = converterHelpers.doGetRequest(ConverterConstants.BASE_URL, converterRequest);
        String responseString = converterHelpers.getResponseInStringFormat(response);
        assertSoftly(softly -> {
            // This should be improved, it should return a failure in these cases. It's returning a 200 and "false" as response.
            // Response error code should be 400 and response error message should be Bad Request
            softly.assertThat(response.code()).isEqualTo(ConverterConstants.BAD_REQUEST_ERROR_CODE);
            softly.assertThat(responseString).isEqualTo(ConverterConstants.BAD_REQUEST_ERROR_MESSAGE);
        });
    }

    @DisplayName("When invalid http methods are used, then Method Not Allowed is returned.")
    @ParameterizedTest
    @MethodSource("com.cloudtalk.tool.converter.ConverterHelpers#generateInvalidHttpMethods")
    void When_InvalidHttpMethods_Then_MethodNotAllowed(String invalidHttpMethod, String requestBody) {
        Response response = converterHelpers.doRequest(ConverterConstants.BASE_URL, invalidHttpMethod, requestBody);
        String responseString = converterHelpers.getResponseInStringFormat(response);
        assertSoftly(softly -> {
            // This should be improved, it should return a failure in these cases
            softly.assertThat(response.code()).isEqualTo(ConverterConstants.METHOD_NOT_ALLOWED_ERROR_CODE);
            softly.assertThat(responseString).isEqualTo(ConverterConstants.METHOD_NOT_ALLOWED_ERROR_MESSAGE);
        });
    }

}
