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
package com.jpomykala.javagram.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpomykala.javagram.core.request.payload.InstagramLoginResult;
import com.jpomykala.javagram.core.request.payload.InstagramLoginTwoFactorPayload;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Login TwoFactorRequest
 *
 * @author Ozan Karaali
 */
@AllArgsConstructor
@Slf4j
public class InstagramLoginTwoFactorRequest extends InstagramPostRequest<InstagramLoginResult>
{

  private InstagramLoginTwoFactorPayload payload;

  @Override
  public String getUrl()
  {
    return "accounts/two_factor_login/";
  }

  @Override
  @SneakyThrows
  public String getPayload()
  {
    ObjectMapper mapper = api.getObjectMapper();
    String payloadJson = mapper.writeValueAsString(payload);

    return payloadJson;
  }

  @Override
  @SneakyThrows
  public InstagramLoginResult parseResult(int statusCode, String content)
  {
    return parseJson(statusCode, content, InstagramLoginResult.class);
  }

  @Override
  public boolean requiresLogin()
  {
    return false;
  }

}
