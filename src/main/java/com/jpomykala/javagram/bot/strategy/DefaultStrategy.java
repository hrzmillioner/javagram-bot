package com.jpomykala.javagram.bot.strategy;

import com.google.common.collect.Lists;
import com.jpomykala.javagram.bot.JavagramBot;
import com.jpomykala.javagram.bot.JavagramProperties;
import com.jpomykala.javagram.bot.exception.OutOfWorkException;
import com.jpomykala.javagram.core.request.FollowRequest;
import com.jpomykala.javagram.core.request.LikeRequest;
import com.jpomykala.javagram.core.request.TagFeedRequest;
import com.jpomykala.javagram.core.request.TimelineFeedRequest;
import com.jpomykala.javagram.core.request.payload.FeedItem;
import com.jpomykala.javagram.core.request.payload.FeedResult;
import com.jpomykala.javagram.core.request.payload.User;
import com.jpomykala.javagram.util.RandomUtil;
import com.jpomykala.javagram.util.RandomWaitUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultStrategy implements WorkStrategy
{

    private JavagramProperties properties;

    private JavagramBot instance;

    private Logger log = LoggerFactory.getLogger(DefaultStrategy.class);

    public DefaultStrategy(JavagramProperties properties)
    {
        this.properties = properties;
    }

    @Override
    public void invoke(JavagramBot instance)
    {
        this.instance = instance;
        while (true)
        {
            try
            {
                makeAction();
                RandomWaitUtil.waitRandom(5, 140);
            }
            catch (OutOfWorkException e)
            {
                log.info("Waiting...");
            }
        }
    }

    private void makeAction()
    {
        List<FeedItem> timelineFeed = getTimelineFeed();
        for (FeedItem feedItem : timelineFeed)
        {
            likeMedia(instance, feedItem);
        }

        List<String> list = Lists.newArrayList(properties.getTagsToLike());
        Collections.shuffle(list);
        for (String tag : list)
        {
            log.info("Find media by tag: {}", tag);
            List<FeedItem> notLikedMedias = getNotLikedMediasByTag(tag);
            for (FeedItem feedItem : notLikedMedias)
            {
                waitBeforeNext();
                likeMedia(instance, feedItem);
                maybeFollowUser(feedItem);
            }
        }
    }

    private void likeMedia(JavagramBot instance, FeedItem feedItem)
    {
        boolean shouldLike = RandomUtil.getTrueWithProbability(0.8);
        if (shouldLike)
        {
            boolean liked = feedItem.isHas_liked();
            if (liked)
            {
                throw new IllegalArgumentException("Media already liked");
            }
            long feedResultPk = feedItem.getPk();
            instance.sendRequest(new LikeRequest(feedResultPk));
        }
    }

    private List<FeedItem> getTimelineFeed()
    {
        FeedResult feedResult = instance.sendRequest(new TimelineFeedRequest());
        return feedResult.getItems().stream()
                .filter(feedItem -> !feedItem.isHas_liked())
                .limit(RandomUtil.getRandomBetween(5, 10))
                .collect(Collectors.toList());
    }

    @NotNull
    private List<FeedItem> getNotLikedMediasByTag(@NotNull String tag)
    {
        FeedResult feedResult = instance.sendRequest(new TagFeedRequest(tag));
        return feedResult.getItems().stream()
                .filter(feedItem -> !feedItem.isHas_liked())
                .limit(RandomUtil.getRandomBetween(12, 24))
                .collect(Collectors.toList());
    }

    private void waitBeforeNext()
    {
        LocalTime startTime = properties.getStartTime();
        LocalTime endTime = properties.getEndTime();
        long duration = Duration.between(startTime, endTime).getSeconds();
        long likeEveryXSeconds = duration / properties.getLikesPerDay();
        int offsetToMoreRandom = 20;
        RandomWaitUtil.waitRandom(likeEveryXSeconds - offsetToMoreRandom, likeEveryXSeconds + offsetToMoreRandom);
    }

    private void maybeFollowUser(FeedItem feedItem)
    {
        double chanceToFollow = properties.getChanceToFollow();
        boolean trueWithProbability = RandomUtil.getTrueWithProbability(chanceToFollow);
        if (trueWithProbability)
        {
            RandomWaitUtil.waitRandom(1, 3);
            User user = feedItem.getUser();
            instance.sendRequest(new FollowRequest(user.getPk()));
        }
    }
}
