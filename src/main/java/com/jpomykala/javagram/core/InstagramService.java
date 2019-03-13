package com.jpomykala.javagram.core;

import com.jpomykala.javagram.core.request.InstagramRequest;
import org.jetbrains.annotations.NotNull;

public interface InstagramService
{

    <T> T sendRequest(@NotNull InstagramRequest<T> request);

    String getCsrfCookie();

    void authenticate();
}
