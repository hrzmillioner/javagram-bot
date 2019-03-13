package com.jpomykala.javagram.bot.command;

import com.jpomykala.javagram.core.DefaultInstagramService;
import com.jpomykala.javagram.core.request.FollowRequest;
import com.jpomykala.javagram.core.request.payload.User;

public final class FollowUser implements BotRequest
{

    private FollowRequest followRequest;

    public FollowUser(User user)
    {
        long userId = user.getPk();
        this.followRequest = new FollowRequest(userId);
    }

    @Override
    public Object execute(DefaultInstagramService instance)
    {
        return instance.sendRequest(followRequest);
    }
}
