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

import com.jpomykala.javagram.core.request.payload.InstagramMediaTypeEnum;
import com.jpomykala.javagram.core.request.payload.StatusResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Request for deleting media.
 *
 * @author Bruno Candido Volpato da Cunha
 */
@RequiredArgsConstructor
public class InstagramDeleteMediaRequest extends InstagramPostRequest<StatusResult>
{
    private final String mediaId;

    @NonNull
    private final InstagramMediaTypeEnum mediaType;

    @Override
    public String getUrl()
    {
        return "media/" + mediaId + "/delete/";
    }

    @Override
    @SneakyThrows
    public String getPayload()
    {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("_uuid", api.getUuid());
        map.put("_uid", api.getUserId());
        map.put("_csrftoken", api.getCsrfCookie());
        map.put("media_type", mediaType.name());
        map.put("media_id", mediaId);

        return api.getObjectMapper().writeValueAsString(map);
    }

    @Override
    public StatusResult parseResult(int resultCode, String content)
    {
        return parseJson(resultCode, content, StatusResult.class);
    }

}
