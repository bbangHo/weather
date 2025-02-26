package org.pknu.weather.security.util.signup;

import java.util.Map;
import org.pknu.weather.security.dto.SocialUserInfo;

public interface UserInfoStrategy {
    SocialUserInfo getUserInfo(String token);
}
