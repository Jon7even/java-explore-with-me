package ru.practicum.ewm.rating.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CalculateRating {
    public int getRate(int like, int disLike) {
        int result = 0;

        if (like == 0) {
            if (disLike == 0) {
                return result;
            } else {
                return -disLike;
            }
        } else {
            result = like - disLike;
        }
        return result;
    }
}
