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
package me.jpomykala.javagram.requests.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.jpomykala.javagram.ApiConstants;
import me.jpomykala.javagram.requests.InstagramPostRequest;
import me.jpomykala.javagram.requests.payload.InstagramConfigurePhotoResult;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Like Request
 *
 * @author Bruno Candido Volpato da Cunha
 */
@AllArgsConstructor
@Slf4j
public class InstagramConfigurePhotoRequest extends InstagramPostRequest<InstagramConfigurePhotoResult> {

  BufferedImage image;
  private String uploadId;
  private String caption;

  @Override
  public String getUrl() {
    return "media/configure/?";
  }

  @Override
  @SneakyThrows
  public String getPayload() {

    Map<String, Object> likeMap = new LinkedHashMap<>();
    likeMap.put("_csrftoken", api.getOrFetchCsrf());
    likeMap.put("media_folder", "Instagram");
    likeMap.put("source_type", 4);
    likeMap.put("_uid", api.getUserId());
    likeMap.put("_uuid", api.getUuid());
    likeMap.put("caption", caption);
    likeMap.put("upload_id", uploadId);

    Map<String, Object> deviceMap = new LinkedHashMap<>();
    deviceMap.put("manufacturer", ApiConstants.DEVICE_MANUFACTURER);
    deviceMap.put("model", ApiConstants.DEVICE_MODEL);
    deviceMap.put("android_version", ApiConstants.DEVICE_ANDROID_VERSION);
    deviceMap.put("android_release", ApiConstants.DEVICE_ANDROID_RELEASE);
    likeMap.put("device", deviceMap);

    Map<String, Object> editsMap = new LinkedHashMap<>();
    editsMap.put("crop_original_size", Arrays.asList((double) image.getWidth(), (double) image.getHeight()));
    editsMap.put("crop_center", Arrays.asList((double) 0, (double) 0));
    editsMap.put("crop_zoom", 1.0);
    likeMap.put("edits", editsMap);

    Map<String, Object> extraMap = new LinkedHashMap<>();
    extraMap.put("source_width", image.getWidth());
    extraMap.put("source_height", image.getHeight());
    likeMap.put("extra", extraMap);

    ObjectMapper mapper = api.getObjectMapper();
    String payloadJson = mapper.writeValueAsString(likeMap);

    return payloadJson;
  }

  @Override
  @SneakyThrows
  public InstagramConfigurePhotoResult parseResult(int statusCode, String content) {
    return parseJson(statusCode, content, InstagramConfigurePhotoResult.class);
  }

}
