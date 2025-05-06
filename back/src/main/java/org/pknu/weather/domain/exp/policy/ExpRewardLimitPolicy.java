package org.pknu.weather.domain.exp.policy;

public interface ExpRewardLimitPolicy {
    boolean canReward(Long memberId);
}
