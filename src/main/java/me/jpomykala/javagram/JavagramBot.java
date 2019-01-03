package me.jpomykala.javagram;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.extern.slf4j.Slf4j;
import me.jpomykala.javagram.requests.FollowRequest;
import me.jpomykala.javagram.requests.LikeRequest;
import me.jpomykala.javagram.requests.TagFeedRequest;
import me.jpomykala.javagram.requests.TimelineFeedRequest;
import me.jpomykala.javagram.requests.payload.FeedItem;
import me.jpomykala.javagram.requests.payload.FeedResult;
import me.jpomykala.javagram.requests.payload.User;
import me.jpomykala.javagram.util.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public final class JavagramBot {

  private JavagramProperties properties;
  private JavagramApi instagram;

  public JavagramBot(JavagramProperties properties) {
    this.properties = properties;
  }

  public void invoke() {
    instagram = JavagramApi.builder()
            .username(properties.getUsername())
            .password(properties.getPassword())
            .build();
    instagram.login();

    while (true) {

      if (isOutOfWorkRange()) {
        log.info("Out of work period, waiting...");
        Uninterruptibles.sleepUninterruptibly(20, TimeUnit.MINUTES);
        continue;
      }

      List<FeedItem> timelineFeed = getTimelineFeed();
      for (FeedItem feedItem : timelineFeed) {
        RandomUtils.waitHumanInteraction();
        boolean shouldLike = RandomUtils.getTrueWithProbability(0.6);
        if (shouldLike) {
          likeItem(feedItem);
        }
      }


      List<String> list = Lists.newArrayList(properties.getTagsToLike());
      Collections.shuffle(list);
      for (String tag : list) {
        log.info("Find media by tag: {}", tag);
        List<FeedItem> notLikedMedias = getNotLikedMediasByTag(tag);
        for (FeedItem feedItem : notLikedMedias) {
          waitBeforeNext();
          boolean shouldLike = RandomUtils.getTrueWithProbability(0.8);
          if (shouldLike) {
            likeItem(feedItem);
          }
          maybeFollowUser(feedItem);
        }
      }
      RandomUtils.waitRandom(5, 140);
    }
  }

  private boolean isOutOfWorkRange() {
    LocalTime now = LocalTime.now();
    LocalTime startTime = properties.getStartTime();
    LocalTime endTime = properties.getEndTime();
    return now.isBefore(startTime) || now.isAfter(endTime);
  }

  @NotNull
  private List<FeedItem> getNotLikedMediasByTag(@NotNull String tag) {
    FeedResult feedResult = instagram.sendRequest(new TagFeedRequest(tag));
    return feedResult.getItems().stream()
            .filter(feedItem -> !feedItem.isHas_liked())
            .limit(RandomUtils.getRandomBetween(12, 24))
            .collect(Collectors.toList());
  }

  @NotNull
  private List<FeedItem> getTimelineFeed() {
    FeedResult feedResult = instagram.sendRequest(new TimelineFeedRequest());
    return feedResult.getItems().stream()
            .filter(feedItem -> !feedItem.isHas_liked())
            .limit(RandomUtils.getRandomBetween(5, 10))
            .collect(Collectors.toList());
  }

  private void waitBeforeNext() {
    LocalTime startTime = properties.getStartTime();
    LocalTime endTime = properties.getEndTime();
    long duration = Duration.between(startTime, endTime).getSeconds();
    long likeEveryXSeconds = duration / properties.getLikesPerDay();
    int offsetToMoreRandom = 20;
    RandomUtils.waitRandom(likeEveryXSeconds - offsetToMoreRandom, likeEveryXSeconds + offsetToMoreRandom);
  }

  private void maybeFollowUser(FeedItem feedItem) {
    double chanceToFollow = properties.getChanceToFollow();
    boolean trueWithProbability = RandomUtils.getTrueWithProbability(chanceToFollow);
    if (trueWithProbability) {
      RandomUtils.waitRandom(1, 3);
      followUser(feedItem);
    }
  }

  private void followUser(FeedItem feedResult) {
    User user = feedResult.getUser();
    long userId = user.getPk();
    boolean accountIsPrivate = user.is_private();
    if (accountIsPrivate) {
      return;
    }
    log.info("Follow: #{} ({})", userId, user.getUsername());
    instagram.sendRequest(new FollowRequest(userId));

  }

  private void likeItem(FeedItem feedResult) {
    boolean notLiked = !feedResult.isHas_liked();
    if (notLiked) {
      long feedResultPk = feedResult.getPk();
      log.info("Like: #{}", feedResult.getPk());
      instagram.sendRequest(new LikeRequest(feedResultPk));
    } else {
      log.info("#{} already liked", feedResult.getPk());
    }
  }

}
