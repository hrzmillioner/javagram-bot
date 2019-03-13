package com.jpomykala.javagram.core;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthDetails
{

    private String deviceId;

    @Getter
    private String uuid;

    @Getter
    private String advertisingId;

    private String username;
    private String password;

    @Getter
    private long userId;

    @Getter
    private String rankToken;

}
