package com.jpomykala.javagram;

import com.jpomykala.javagram.bot.JavagramBot;
import com.jpomykala.javagram.bot.JavagramProperties;
import com.jpomykala.javagram.bot.strategy.DefaultStrategy;
import com.jpomykala.javagram.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
public class Main
{
  private final static String CONFIG_OPTION = "config";
  private final static String DEFAULT_CONFIG_PATH = "config.properties";

  public static void main(String... args) throws Exception
  {
    String configPath = getPropertiesFilePath(args);
    Properties properties = PropertyUtil.getProperties(configPath);
    JavagramProperties javagramProperties = JavagramProperties
            .load()
            .from(properties)
            .build();
    javagramProperties.print();

    JavagramBot javagramBot = new JavagramBot(javagramProperties);
    javagramBot.setWorkStrategy(new DefaultStrategy(javagramProperties));
    javagramBot.start();
  }

  @NotNull
  private static String getPropertiesFilePath(String[] args) throws ParseException
  {
    Options options = new Options();
    Option configurationOption = new Option("c", CONFIG_OPTION, true, "configuration file");
    configurationOption.setRequired(false);
    options.addOption(configurationOption);

    DefaultParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    String configPath = cmd.getOptionValue(CONFIG_OPTION);

    if (isNullOrEmpty(configPath))
    {
      log.warn("Using default '{}' configuration file, use custom file using '-config custom.properties' option", DEFAULT_CONFIG_PATH);
      configPath = DEFAULT_CONFIG_PATH;
    }
    return configPath;
  }
}
