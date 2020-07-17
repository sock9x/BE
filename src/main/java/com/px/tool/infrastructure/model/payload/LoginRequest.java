package com.px.tool.infrastructure.model.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest extends AbstractObject {
    private String userName;
    private String password;

}
