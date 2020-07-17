package com.px.tool.infrastructure.model;

import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse extends AbstractObject {
    private String code;
    private String message;
}
