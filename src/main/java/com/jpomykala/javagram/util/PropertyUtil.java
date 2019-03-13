package com.jpomykala.javagram.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil
{

    public static Properties getProperties(String configPath) throws IOException
    {
        InputStream configFileStream = new FileInputStream(configPath);
        Properties properties = new Properties();
        properties.load(configFileStream);
        return properties;
    }

}
