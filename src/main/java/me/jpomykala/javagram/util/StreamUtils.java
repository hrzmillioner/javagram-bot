package me.jpomykala.javagram.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {

  public StreamUtils() {
  }

  public static String readContent(InputStream is) {
    String output = "";

    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      StringBuilder out = new StringBuilder();

      String line;
      while ((line = in.readLine()) != null) {
        out.append(line).append("\r\n");
      }

      output = out.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return output;
  }

}
