package com.dlong.creeper.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
    public static Properties readProperties(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(path));
        return properties;
    }
}
