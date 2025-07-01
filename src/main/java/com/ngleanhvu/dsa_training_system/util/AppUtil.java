package com.ngleanhvu.dsa_training_system.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AppUtil {
    public static BigDecimal convertKbToMbRaw(int kilobytes) {
        return new BigDecimal(kilobytes).divide(BigDecimal.valueOf(1024), 3, RoundingMode.HALF_UP);
    }
}
