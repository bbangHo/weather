package org.pknu.weather.security.util;

import java.util.Map;

public interface UserInfoStrategy {
    Map<String, Object> getUserInfo(String token);
}
