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
package me.jpomykala.javagram.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.jpomykala.javagram.requests.payload.InstagramCheckUsernameResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sync Features Request
 *
 * @author Bruno Candido Volpato da Cunha
 */
@AllArgsConstructor
@Slf4j
public class InstagramCheckUsernameRequest extends InstagramPostRequest<InstagramCheckUsernameResult> {

  @NonNull
  private String username;

  @Override
  public String getUrl() {
    return "users/check_username/";
  }

  @Override
  @SneakyThrows
  public String getPayload() {
    ObjectMapper mapper = api.getObjectMapper();

    Map<String, Object> payloadMap = new LinkedHashMap<>();
    payloadMap.put("username", username);
    payloadMap.put("_csrftoken", api.getOrFetchCsrf());

    return mapper.writeValueAsString(payloadMap);
  }

  @Override
  @SneakyThrows
  public InstagramCheckUsernameResult parseResult(int statusCode, String content) {
    return this.parseJson(content, InstagramCheckUsernameResult.class);
  }

  @Override
  public boolean requiresLogin() {
    return false;
  }


}
