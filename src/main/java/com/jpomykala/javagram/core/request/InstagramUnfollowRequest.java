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
import com.jpomykala.javagram.core.request.payload.StatusResult;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Unfollow Request
 *
 * @author Bruno Candido Volpato da Cunha
 */
@AllArgsConstructor
public class InstagramUnfollowRequest extends InstagramPostRequest<StatusResult>
{

  private long userId;

  @Override
  public String getUrl()
  {
    return "friendships/destroy/" + userId + "/";
  }

  @Override
  @SneakyThrows
  public String getPayload()
  {

    Map<String, Object> output = new LinkedHashMap<>();
    output.put("_uuid", api.getUuid());
    output.put("_uid", api.getUserId());
    output.put("user_id", userId);
    output.put("_csrftoken", api.getCsrfCookie());

    ObjectMapper mapper = api.getObjectMapper();
    return mapper.writeValueAsString(output);
  }

  @Override
  @SneakyThrows
  public StatusResult parseResult(int statusCode, String content)
  {
    return parseJson(statusCode, content, StatusResult.class);
  }

}
