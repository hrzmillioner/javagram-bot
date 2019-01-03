package me.jpomykala.javagram.util;

import com.google.common.util.concurrent.Uninterruptibles;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RandomUtils {


  public static void waitRandom(long minSeconds, long maxSeconds) {
    waitRandom((int) minSeconds, (int) maxSeconds);
  }

  public static void waitRandom(int minSeconds, int maxSeconds) {

    if (minSeconds < 0) {
      minSeconds = 0;
    }

    int hour = 60 * 60;
    if (maxSeconds > hour) {
      maxSeconds = hour;
    }

    if (maxSeconds < minSeconds) {
      maxSeconds = minSeconds + 1;
    }

    int randomSeconds = ThreadLocalRandom.current().nextInt(minSeconds, maxSeconds) * 1000;
    int randomMillis = ThreadLocalRandom.current().nextInt(50, 400);
    Uninterruptibles.sleepUninterruptibly(randomSeconds + randomMillis, TimeUnit.MILLISECONDS);
  }

  public static void waitHumanInteraction() {
    int randomMillis = ThreadLocalRandom.current().nextInt(200, 1200);
    Uninterruptibles.sleepUninterruptibly(randomMillis, TimeUnit.MILLISECONDS);
  }

  /**
   * @param probability 0.0 (none) - 1.0 (max)
   * @return true or false
   */
  public static boolean getTrueWithProbability(double probability) {
    probability *= 100;
    int randomProbability = ThreadLocalRandom.current().nextInt(0, 100);
    return probability > randomProbability;
  }

  public static long getRandomBetween(int from, int to) {
    return ThreadLocalRandom.current().nextInt(from, to);
  }
}
