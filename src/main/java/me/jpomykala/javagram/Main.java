package me.jpomykala.javagram;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
public class Main {
  private final static String CONFIG_OPTION = "config";
  private final static String DEFAULT_CONFIG_PATH = "config.properties";

  public static void main(String... args) throws Exception {
    String configPath = getConfigurationFilePath(args);
    Properties properties = loadPropertiesFile(configPath);
    JavagramProperties javagramProperties = JavagramProperties
            .load()
            .from(properties)
            .build();
    log.info("Tags: {}", javagramProperties.getTagsToLike());
    log.info("Likes per day: {}", javagramProperties.getLikesPerDay());
    log.info("Chance to follow: {}", javagramProperties.getChanceToFollow());
    log.info("Activity between {} and {}", javagramProperties.getStartTime(), javagramProperties.getEndTime());
    JavagramBot javagramBot = new JavagramBot(javagramProperties);
    javagramBot.invoke();
  }

  private static Properties loadPropertiesFile(String configPath) throws IOException {
    InputStream configFileStream = new FileInputStream(configPath);
    Properties properties = new Properties();
    properties.load(configFileStream);
    return properties;
  }

  @NotNull
  private static String getConfigurationFilePath(String[] args) throws ParseException {
    Options options = new Options();
    Option configurationOption = new Option("c", CONFIG_OPTION, true, "configuration file");
    configurationOption.setRequired(false);
    options.addOption(configurationOption);

    DefaultParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    String configPath = cmd.getOptionValue(CONFIG_OPTION);

    if (isNullOrEmpty(configPath)) {
      log.warn("Using default '{}' configuration file, use custom file using '-config custom.properties' option", DEFAULT_CONFIG_PATH);
      configPath = DEFAULT_CONFIG_PATH;
    }
    return configPath;
  }
}
