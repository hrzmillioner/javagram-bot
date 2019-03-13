/**
 * Copyright (C) 2016 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jpomykala.javagram.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpomykala.javagram.core.request.*;
import com.jpomykala.javagram.core.request.internal.*;
import com.jpomykala.javagram.core.request.payload.InstagramLoginPayload;
import com.jpomykala.javagram.core.request.payload.InstagramLoginResult;
import com.jpomykala.javagram.util.InstagramGenericUtil;
import com.jpomykala.javagram.util.InstagramHashUtil;
import com.jpomykala.javagram.util.RandomWaitUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Slf4j
public final class DefaultInstagramService implements InstagramService
{

    private String deviceId;

    @Getter
    private String uuid;

    @Getter
    private String advertisingId;

    private String username;
    private String password;

    @Getter
    private ObjectMapper objectMapper;

    @Getter
    private long userId;

    @Getter
    private String rankToken;
    private CookieStore cookieStore;

    @Getter
    private CloseableHttpClient client;

    private boolean isLoggedIn;

    private DefaultInstagramService(@NotNull String username, @NotNull String password)
    {
        this.username = username;
        this.password = password;
        this.deviceId = InstagramHashUtil.generateDeviceId(this.username, this.password);
        this.objectMapper = new ObjectMapper();
        this.uuid = InstagramGenericUtil.generateUuid();
        this.advertisingId = InstagramGenericUtil.generateUuid();
        this.cookieStore = new BasicCookieStore();
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultCookieStore(this.cookieStore);
        this.client = builder.build();
    }

    public static DefaultInstagramService createInstance(@NotNull String username, @NotNull String password)
    {
        return new DefaultInstagramService(username, password);
    }

    @NotNull
    @Override
    public <T> T sendRequest(@NotNull InstagramRequest<T> request)
    {

        log.debug("Request: {}", request.getClass().getName());

        boolean needLogin = !isLoggedIn && request.requiresLogin();
        if (needLogin)
        {
            throw new IllegalStateException("Need to login first!");
        }

        RandomWaitUtil.waitHumanInteraction();

        request.setApi(this);
        T response = request.execute();

        log.debug("Response {}", response.getClass().getName());

        return response;
    }

    @Override
    public String getCsrfCookie()
    {
        Optional<Cookie> csrfCookie = findCsrfCookieInCookieStore();
        return csrfCookie
                .or(this::requestCsrfCookie)
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get CSRF cookie"));
    }

    private Optional<Cookie> findCsrfCookieInCookieStore()
    {
        return cookieStore.getCookies().stream()
                .filter(cookie -> cookie.getName().equalsIgnoreCase("csrftoken"))
                .findFirst();
    }

    private Optional<? extends Cookie> requestCsrfCookie()
    {
        sendRequest(new InstagramFetchHeadersRequest());
        return findCsrfCookieInCookieStore();
    }

    @Override
    public void authenticate()
    {
        log.info("Login as {}", username);

        this.sendRequest(new InstagramReadMsisdnHeaderRequest());
        this.sendRequest(new InstagramSyncFeaturesRequest(true));
        this.sendRequest(new InstagramZeroRatingTokenRequest());
        this.sendRequest(new InstagramLogAttributionRequest());

        InstagramLoginRequest loginRequest = getInstagramLoginRequest();
        InstagramLoginResult loginResult = this.sendRequest(loginRequest);
        emulateApplicationLoginFlow(loginResult);

        if (loginResult.getTwo_factor_info() != null)
        {
            String identifier = loginResult.getTwo_factor_info().getTwo_factor_identifier();
            log.warn("2FA identifier {}", identifier);
        }

        if (loginResult.getChallenge() != null)
        {
            log.warn("Challenge required: {}", loginResult.getChallenge());
        }
    }

    @NotNull
    private InstagramLoginRequest getInstagramLoginRequest()
    {
        InstagramLoginPayload loginPayload = InstagramLoginPayload.builder()
                .username(username)
                .password(password)
                .guid(uuid)
                .device_id(deviceId)
                .phone_id(InstagramGenericUtil.generateUuid())
                .login_attempt_account(0)
                ._csrftoken(getCsrfCookie())
                .build();
        return new InstagramLoginRequest(loginPayload);
    }

    private void emulateApplicationLoginFlow(@NotNull InstagramLoginResult loginResult)
    {
        boolean loginSuccessful = loginResult.getStatus().equalsIgnoreCase("ok");
        if (!loginSuccessful)
        {
            return;
        }

        this.userId = loginResult.getLogged_in_user().getPk();
        this.rankToken = this.userId + "_" + this.uuid;
        this.isLoggedIn = true;

        this.sendRequest(new InstagramSyncFeaturesRequest(false));
        this.sendRequest(new InstagramAutoCompleteUserListRequest());
        this.sendRequest(new TimelineFeedRequest());
        this.sendRequest(new InstagramGetInboxRequest());
        this.sendRequest(new InstagramGetRecentActivityRequest());
    }

}
