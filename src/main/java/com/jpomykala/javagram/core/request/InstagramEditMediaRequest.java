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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpomykala.javagram.core.request.payload.StatusResult;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Request for editing media.
 *
 * @author Evgeny Bondarenko (evgbondarenko@gmail.com)
 */
@RequiredArgsConstructor
public class InstagramEditMediaRequest extends InstagramPostRequest<StatusResult>
{
  private final long mediaId;

  @NonNull
  private final String captionText;
  @Getter
  @Setter
  @JsonProperty("usertags")
  private UserTags userTags;

  @Override
  public String getUrl()
  {
    return "media/" + mediaId + "/edit_media/";
  }

  @Override
  @SneakyThrows
  public String getPayload()
  {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("_uuid", api.getUuid());
    map.put("_uid", api.getUserId());
    map.put("_csrftoken", api.getCsrfCookie());
    map.put("caption_text", captionText);

    ObjectMapper mapper = api.getObjectMapper();
    if (userTags != null)
    {
      map.put("usertags", mapper.writeValueAsString(userTags));
    }

    return mapper.writeValueAsString(map);
  }

  @Override
  public StatusResult parseResult(int resultCode, String content)
  {
    return parseJson(resultCode, content, StatusResult.class);
  }

  @Getter
  @Setter
  public class UserTag
  {
    @NonNull
    @JsonProperty("user_id")
    private String userId;
    private float[] position = { 0, 0 };

    public UserTag(String userId)
    {
      this.userId = userId;
    }

    public UserTag(String userId, float positionX, float positionY)
    {
      this(userId);
      this.position[0] = positionX;
      this.position[1] = positionY;
    }

    public void setPosition(float positionX, float positionY)
    {
      this.position[0] = positionX;
      this.position[1] = positionY;
    }

  }

  @Getter
  @Setter
  public class UserTags
  {
    @NonNull
    @JsonProperty("removed")
    private List<String> userIdsToRemoveTag = new ArrayList<String>();
    @NonNull
    @JsonProperty("in")
    private List<UserTag> tagsToAdd = new ArrayList<UserTag>();
  }
}
