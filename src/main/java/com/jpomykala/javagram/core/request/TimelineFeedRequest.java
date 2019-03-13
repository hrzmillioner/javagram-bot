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

import com.jpomykala.javagram.core.request.payload.FeedResult;

/**
 * Timeline Feed Request
 *
 * @author Bruno Candido Volpato da Cunha
 */
public class TimelineFeedRequest extends InstagramGetRequest<FeedResult>
{

  private String maxId;

  public TimelineFeedRequest()
  {
  }

  public TimelineFeedRequest(String maxId)
  {
    this.maxId = maxId;
  }

  @Override
  public String getUrl()
  {
    String url = "feed/timeline/";
    if (maxId != null && !maxId.isEmpty())
    {
      url += "&max_id=" + maxId;
    }

    return url;
  }

  @Override
  public String getPayload()
  {
    return null;
  }

  @Override
  public FeedResult parseResult(int statusCode, String content)
  {
    try
    {
      return this.parseJson(statusCode, content, FeedResult.class);
    }
    catch (Throwable var4)
    {
      throw var4;
    }
  }

}
