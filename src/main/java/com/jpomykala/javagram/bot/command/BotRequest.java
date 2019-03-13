package com.jpomykala.javagram.bot.command;

import com.jpomykala.javagram.core.DefaultInstagramService;

public interface BotRequest<T>
{

    T execute(DefaultInstagramService instance);

}
