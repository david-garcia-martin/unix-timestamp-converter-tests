package com.cloudtalk.tool.model;

import com.cloudtalk.tool.util.ConverterConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConverterRequest {

    @Builder.Default
    private final String cached = null;

    @Builder.Default
    private final String s = ConverterConstants.DEFAULT_DATE_STRING;
}
