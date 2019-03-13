package com.jpomykala.javagram.bot;

import com.jpomykala.javagram.bot.exception.OutOfWorkException;
import com.jpomykala.javagram.bot.strategy.WorkStrategy;
import com.jpomykala.javagram.core.DefaultInstagramService;
import com.jpomykala.javagram.core.request.InstagramRequest;
import com.jpomykala.javagram.util.RandomWaitUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

/**
 * User friendly adapter for InstagramService, takes care about login and out of work time.
 */
@Slf4j
public final class JavagramBot
{

    private JavagramProperties properties;
    private WorkStrategy strategy;
    private DefaultInstagramService instance;

    public JavagramBot(JavagramProperties properties)
    {
        this.properties = properties;
    }

    public void setWorkStrategy(WorkStrategy strategy)
    {
        this.strategy = strategy;
    }

    public void start()
    {
        String username = properties.getUsername();
        String password = properties.getPassword();
        instance = DefaultInstagramService.createInstance(username, password);
        instance.authenticate();
        strategy.invoke(this);
    }

    public <T> T sendRequest(@NotNull InstagramRequest<T> request)
    {
        if (isOutOfWorkRange())
        {
            throw new OutOfWorkException();
        }
        RandomWaitUtil.waitHumanInteraction();
        return instance.sendRequest(request);
    }

    private boolean isOutOfWorkRange()
    {
        LocalTime now = LocalTime.now();
        LocalTime startTime = properties.getStartTime();
        LocalTime endTime = properties.getEndTime();
        return now.isBefore(startTime) || now.isAfter(endTime);
    }

}
