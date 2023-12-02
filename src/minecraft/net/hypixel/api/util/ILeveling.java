package net.hypixel.api.util;

public interface ILeveling {

    double BASE = 10000.0D;
    double GROWTH = 2500.0D;
    double HALF_GROWTH = 1250.0D;
    double REVERSE_PQ_PREFIX = -3.5D;
    double REVERSE_CONST = 12.25D;
    double GROWTH_DIVIDES_2 = 8.0E-4D;

    static double getLevel(double exp) {
        return exp < 0.0D ? 1.0D : Math.floor(-2.5D + Math.sqrt(12.25D + 8.0E-4D * exp));
    }

    static double getExactLevel(double exp) {
        return getLevel(exp) + getPercentageToNextLevel(exp);
    }

    static double getExpFromLevelToNext(double level) {
        return level < 1.0D ? 10000.0D : 2500.0D * (level - 1.0D) + 10000.0D;
    }

    static double getTotalExpToLevel(double level) {
        double lv = Math.floor(level);
        double x0 = getTotalExpToFullLevel(lv);

        return level == lv ? x0 : (getTotalExpToFullLevel(lv + 1.0D) - x0) * (level % 1.0D) + x0;
    }

    static double getTotalExpToFullLevel(double level) {
        return (1250.0D * (level - 2.0D) + 10000.0D) * (level - 1.0D);
    }

    static double getPercentageToNextLevel(double exp) {
        double lv = getLevel(exp);
        double x0 = getTotalExpToLevel(lv);

        return (exp - x0) / (getTotalExpToLevel(lv + 1.0D) - x0);
    }
}
