package com.jpomykala.javagram.bot.command;

import com.jpomykala.javagram.bot.exception.MediaAlreadyLiked;
import com.jpomykala.javagram.core.DefaultInstagramService;
import com.jpomykala.javagram.core.request.LikeRequest;
import com.jpomykala.javagram.core.request.payload.FeedItem;
import com.jpomykala.javagram.core.request.payload.InstagramLikeResult;

public final class LikeMedia implements BotRequest<InstagramLikeResult>
{

    private FeedItem feedItem;

    public LikeMedia(FeedItem feedItem)
    {
        this.feedItem = feedItem;
    }

    @Override
    public InstagramLikeResult execute(DefaultInstagramService instance)
    {
        boolean liked = feedItem.isHas_liked();
        if (liked)
        {
            throw new MediaAlreadyLiked();
        }
        long feedResultPk = feedItem.getPk();
        return instance.sendRequest(new LikeRequest(feedResultPk));
    }
}
