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
package me.jpomykala.javagram;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.jpomykala.javagram.requests.*;
import me.jpomykala.javagram.requests.internal.*;
import me.jpomykala.javagram.requests.payload.InstagramLoginPayload;
import me.jpomykala.javagram.requests.payload.InstagramLoginResult;
import me.jpomykala.javagram.util.InstagramGenericUtil;
import me.jpomykala.javagram.util.InstagramHashUtil;
import me.jpomykala.javagram.util.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Slf4j
public final class JavagramApi {

  @Getter
  @Setter
  protected HttpResponse lastResponse;
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

  @Builder
  public JavagramApi(String deviceId, String uuid, String advertisingId, String username, String password, long userId, String rankToken, CookieStore cookieStore, CloseableHttpClient client) {
    this.deviceId = deviceId;
    this.uuid = uuid;
    this.advertisingId = advertisingId;
    this.username = username;
    this.password = password;
    this.userId = userId;
    this.rankToken = rankToken;
    this.cookieStore = cookieStore;
    this.client = client;
    this.objectMapper = new ObjectMapper();
  }

  public void login() {

    configureClient();
    log.info("Login as {}", username);

    this.sendRequest(new InstagramReadMsisdnHeaderRequest());
    this.sendRequest(new InstagramSyncFeaturesRequest(true));
    this.sendRequest(new InstagramZeroRatingTokenRequest());
    this.sendRequest(new InstagramLogAttributionRequest());


    InstagramLoginPayload loginPayload = InstagramLoginPayload.builder().username(username)
            .password(password)
            .guid(uuid)
            .device_id(deviceId)
            .phone_id(InstagramGenericUtil.generateUuid())
            .login_attempt_account(0)
            ._csrftoken(getOrFetchCsrf())
            .build();
    InstagramLoginRequest loginRequest = new InstagramLoginRequest(loginPayload);
    InstagramLoginResult loginResult = this.sendRequest(loginRequest);
    emulateApplicationLoginFlow(loginResult);

    if (loginResult.getTwo_factor_info() != null) {
      String identifier = loginResult.getTwo_factor_info().getTwo_factor_identifier();
      log.warn("2FA identifier {}", identifier);
    } else if (loginResult.getChallenge() != null) {
      log.warn("Challenge required: {}", loginResult.getChallenge());
    }

    log.info("Login successful");

  }

  @NotNull
  @SneakyThrows
  public <T> T sendRequest(@NotNull InstagramRequest<T> request) {

    log.debug("Request: {}", request.getClass().getName());

    boolean needLogin = !this.isLoggedIn && request.requiresLogin();
    if (needLogin) {
      throw new IllegalStateException("Need to login first!");
    }

    RandomUtils.waitHumanInteraction();

    request.setApi(this);
    T response = request.execute();

    log.debug("Response {}", response.getClass().getName());

    return response;
  }

  private void configureClient() {
    if (StringUtils.isEmpty(this.username)) {
      throw new IllegalArgumentException("Username is mandatory.");
    }

    if (StringUtils.isEmpty(this.password)) {
      throw new IllegalArgumentException("Password is mandatory.");
    }

    this.deviceId = InstagramHashUtil.generateDeviceId(this.username, this.password);

    if (StringUtils.isEmpty(this.uuid)) {
      this.uuid = InstagramGenericUtil.generateUuid();
    }

    if (StringUtils.isEmpty(this.advertisingId)) {
      this.advertisingId = InstagramGenericUtil.generateUuid();
    }

    if (this.cookieStore == null) {
      this.cookieStore = new BasicCookieStore();
    }

    if (this.client == null) {
      HttpClientBuilder builder = HttpClientBuilder.create();
      builder.setDefaultCookieStore(this.cookieStore);
      this.client = builder.build();
    }
  }

  public String getOrFetchCsrf() {
    Optional<Cookie> csrfCookie = getCsrfCookie();
    return csrfCookie
            .or(this::requestCsrfCookie)
            .map(Cookie::getValue)
            .orElseThrow(() -> new IllegalArgumentException("Cannot get CSRF cookie"));
  }

  private Optional<Cookie> getCsrfCookie() {
    return cookieStore.getCookies().stream().filter(cookie -> cookie.getName().equalsIgnoreCase("csrftoken")).findFirst();
  }

  private Optional<? extends Cookie> requestCsrfCookie() {
    sendRequest(new InstagramFetchHeadersRequest());
    return getCsrfCookie();
  }

  private void emulateApplicationLoginFlow(@NotNull InstagramLoginResult loginResult) {
    boolean loginSuccessful = loginResult.getStatus().equalsIgnoreCase("ok");
    if (!loginSuccessful) {
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
