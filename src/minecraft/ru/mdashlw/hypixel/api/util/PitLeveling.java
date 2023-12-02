package ru.mdashlw.hypixel.api.util;

public final class PitLeveling {

    private static final float[] PRESTIGE_MULTIPLIERS = new float[] { 1.0F, 1.1F, 1.2F, 1.3F, 1.4F, 1.5F, 1.75F, 2.0F, 2.5F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F, 10.0F, 12.0F, 14.0F, 16.0F, 18.0F, 20.0F, 24.0F, 28.0F, 32.0F, 36.0F, 40.0F, 45.0F, 50.0F, 75.0F, 100.0F, 101.0F, 101.0F, 101.0F, 101.0F, 101.0F};
    private static final int[] PRESTIGE_XPS = new int[] { 65950, 138510, 217680, 303430, 395760, 494700, 610140, 742040, 906930, 1104780, 1368580, 1698330, 2094030, 2555680, 3083280, 3676830, 4336330, 5127730, 6051030, 7106230, 8293330, 9612330, 11195130, 13041730, 15152130, 17526330, 20164330, 23132080, 26429580, 31375830, 37970830, 44631780, 51292730, 57953680, 64614630, 71275580};
    private static final int[] LEVEL_XPS = new int[] { 15, 30, 50, 75, 125, 300, 600, 800, 900, 1000, 1200, 1500, 0};

    public static int getLevel(int prestige, int totalXP) {
        int xp = prestige > 0 ? totalXP - PitLeveling.PRESTIGE_XPS[prestige - 1] : totalXP;
        float multiplier = PitLeveling.PRESTIGE_MULTIPLIERS[prestige];
        double level = 0.0D;

        while (xp > 0 && level < 120.0D) {
            double levelXp = (double) ((float) PitLeveling.LEVEL_XPS[(int) Math.floor(level / 10.0D)] * multiplier);

            if ((double) xp >= levelXp * 10.0D) {
                xp = (int) ((double) xp - levelXp * 10.0D);
                level += 10.0D;
            } else {
                double gain = Math.floor((double) xp / levelXp);

                level += gain;
                xp = 0;
            }
        }

        return (int) level;
    }
}
