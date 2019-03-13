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

import com.jpomykala.javagram.bot.ApiConstants;
import com.jpomykala.javagram.util.InstagramHashUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author brunovolpato
 */
@Slf4j
public abstract class InstagramPostRequest<T> extends InstagramRequest<T>
{

    @Override
    @SneakyThrows
    public T execute()
    {
        HttpPost post = new HttpPost(ApiConstants.API_URL + getUrl());
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");
        post.addHeader("User-Agent", ApiConstants.USER_AGENT);

        log.debug("User-Agent: " + ApiConstants.USER_AGENT);
        String payload = getPayload();
        log.debug("Base Payload: " + payload);

        if (isSigned())
        {
            payload = InstagramHashUtil.generateSignature(payload);
        }
        log.debug("Final Payload: " + payload);
        post.setEntity(new StringEntity(payload));

        HttpResponse response = api.getClient().execute(post);

        int resultCode = response.getStatusLine().getStatusCode();
        String content = EntityUtils.toString(response.getEntity());

        post.releaseConnection();

        return parseResult(resultCode, content);
    }

}
