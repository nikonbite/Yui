package net.aravix.yui.messenger.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtil {
    public String extractClassName(String json) {
        int startIndex = json.indexOf("\"@type\":") + 9;
        int endIndex = json.indexOf('"', startIndex);
        String className = json.substring(startIndex, endIndex);
        return className;
    }
}
