package com.jpomykala.javagram.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil
{

    /**
     * @param probability 0.0 (none) - 1.0 (max)
     * @return true or false
     */
    public static boolean getTrueWithProbability(double probability)
    {
        probability *= 100;
        int randomProbability = ThreadLocalRandom.current().nextInt(0, 100);
        return probability > randomProbability;
    }

    public static long getRandomBetween(int from, int to)
    {
        return ThreadLocalRandom.current().nextInt(from, to);
    }

}
