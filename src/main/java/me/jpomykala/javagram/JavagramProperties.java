package me.jpomykala.javagram;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

@Getter
@ToString(exclude = "password")
public final class JavagramProperties {

  private String username;
  private String password;
  private Collection<String> tagsToLike;
  private int likesPerDay;
  private double chanceToFollow;
  private LocalTime startTime;
  private LocalTime endTime;

  private JavagramProperties(JavagramPropertiesBuilder builder) {
    this.username = builder.username;
    this.password = builder.password;
    this.tagsToLike = builder.tagsToLike;
    this.likesPerDay = builder.likesPerDay;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.chanceToFollow = builder.chanceToFollow;
  }

  public static LoadStep load() {
    return new JavagramPropertiesBuilder();
  }

  public static UsernameStep builder() {
    return new JavagramPropertiesBuilder();
  }

  //  Step Builder Interfaces
  protected interface LoadStep {
    ConfigurationStep from(@NotNull Properties properties);
  }

  protected interface UsernameStep {
    PasswordStep username(@NotNull String username);
  }

  protected interface PasswordStep {
    ConfigurationStep password(@NotNull String password);
  }

  protected interface ConfigurationStep {
    ConfigurationStep tagsToLike(@NotNull Collection<String> tags);

    ConfigurationStep likesPerDay(int likesPerDay);

    ConfigurationStep startTime(@NotNull LocalTime startTime);

    ConfigurationStep endTime(@NotNull LocalTime endTime);

    JavagramProperties build();
  }


  // Builder
  public static class JavagramPropertiesBuilder implements LoadStep, UsernameStep, PasswordStep, ConfigurationStep {

    private String username;
    private String password;
    private Collection<String> tagsToLike = List.of();
    private int likesPerDay = 200;
    private double chanceToFollow = 0;
    private LocalTime startTime = LocalTime.of(0, 0);
    private LocalTime endTime = LocalTime.of(23, 59, 59);

    @Override
    public JavagramPropertiesBuilder username(@NotNull String username) {
      this.username = username;
      return this;
    }

    @Override
    public JavagramPropertiesBuilder password(@NotNull String password) {
      this.password = password;
      return this;
    }

    @Override
    public JavagramPropertiesBuilder tagsToLike(@NotNull Collection<String> tagsToLike) {
      this.tagsToLike = tagsToLike;
      return this;
    }

    @Override
    public JavagramPropertiesBuilder likesPerDay(int likesPerDay) {
      this.likesPerDay = likesPerDay;
      return this;
    }

    @Override
    public JavagramPropertiesBuilder startTime(@NotNull LocalTime startTime) {
      this.startTime = startTime;
      return this;
    }

    @Override
    public JavagramPropertiesBuilder endTime(@NotNull LocalTime endTime) {
      this.endTime = endTime;
      return this;
    }

    @Override
    @SneakyThrows
    public JavagramPropertiesBuilder from(@NotNull Properties properties) {
      this.username = properties.getProperty("username");
      this.password = properties.getProperty("password");

      String tags = properties.getProperty("tags", "");
      this.tagsToLike = Splitter.on(",")
              .trimResults()
              .omitEmptyStrings()
              .splitToList(tags);

      String defaultLikesPerDay = String.valueOf(likesPerDay);
      String likesPerDayString = properties.getProperty("likesPerDay", defaultLikesPerDay);
      this.likesPerDay = Integer.parseInt(likesPerDayString);

      String defaultChanceToFollow = String.valueOf(chanceToFollow);
      String chanceToFollow = properties.getProperty("chanceToFollow", defaultChanceToFollow);
      this.chanceToFollow = Double.parseDouble(chanceToFollow);

      String startTimeString = properties.getProperty("startTime", "00:00");
      String endTimeString = properties.getProperty("endTime", "23:59");
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

      this.startTime = LocalTime.parse(startTimeString, formatter);
      this.endTime = LocalTime.parse(endTimeString, formatter);

      return this;
    }

    @Override
    public JavagramProperties build() {
      if (chanceToFollow > 1 || chanceToFollow < 0) {
        throw new IllegalArgumentException("chanceToFollow must be between 0.0 and 1.0");
      }

      if (likesPerDay <= 0 || likesPerDay > 1000) {
        throw new IllegalArgumentException("likesPerDay must be between 1 and 1000");
      }

      return new JavagramProperties(this);
    }
  }
}
