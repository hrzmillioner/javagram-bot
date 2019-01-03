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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.jpomykala.javagram.JavagramApi;
import me.jpomykala.javagram.requests.payload.StatusResult;
import me.jpomykala.javagram.util.StreamUtils;
import org.apache.http.HttpStatus;

import java.io.InputStream;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public abstract class InstagramRequest<T> {

  @Getter
  @Setter
  @JsonIgnore
  protected JavagramApi api;

  public abstract String getUrl();

  public abstract String getPayload();

  public abstract T execute();

  public abstract T parseResult(int resultCode, String content);

  /**
   * @return if request must be logged in
   */
  public boolean requiresLogin() {
    return true;
  }

  /**
   * Parses Json into type, considering the status code
   *
   * @param statusCode HTTP Status Code
   * @param str        Entity content
   * @param clazz      Class
   * @return Result
   */
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public <U> U parseJson(int statusCode, String str, Class<U> clazz) {

    if (clazz.isAssignableFrom(StatusResult.class)) {

      //TODO: implement a better way to handle exceptions
      if (statusCode == HttpStatus.SC_NOT_FOUND) {
        StatusResult result = (StatusResult) clazz.getDeclaredConstructor().newInstance();
        result.setStatus("error");
        result.setMessage("SC_NOT_FOUND");
        return (U) result;
      } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
        StatusResult result = (StatusResult) clazz.getDeclaredConstructor().newInstance();
        result.setStatus("error");
        result.setMessage("SC_FORBIDDEN");
        return (U) result;
      }
    }

    return parseJson(str, clazz);
  }

  /**
   * Parses Json into type
   *
   * @param str   Entity content
   * @param clazz Class
   * @return Result
   */
  @SneakyThrows
  public <U> U parseJson(String str, Class<U> clazz) {

    if (log.isInfoEnabled()) {
      if (log.isDebugEnabled()) {
        log.debug("Reading " + clazz.getSimpleName() + " from " + str);
      }
    }

    ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    return objectMapper.readValue(str, clazz);
  }

  /**
   * Parses Json into type
   *
   * @param is    Entity stream
   * @param clazz Class
   * @return Result
   */
  public T parseJson(InputStream is, Class<T> clazz) {
    return this.parseJson(StreamUtils.readContent(is), clazz);
  }

  /**
   * @return payload should be signed
   */
  public boolean isSigned() {
    return true;
  }

}
