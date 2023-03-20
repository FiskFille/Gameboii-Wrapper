package com.fiskmods.gameboii.wrapper;

import com.fiskmods.gameboii.Engine;

public class Timer
{
    /** The number of timer ticks per second of real time */
    float ticksPerSecond;
    /** The time reported by the high-resolution clock at the last call of updateTimer(), in seconds */
    private double lastHRTime;
    /** How many full ticks have turned over since the last call to updateTimer(), capped at 10. */
    public int elapsedTicks;
    /**
     * How much time has elapsed since the last tick, in ticks, for use by display rendering routines (range: 0.0 - 1.0). This field is frozen if the display is paused to eliminate jitter.
     */
    public float renderPartialTicks;
    /**
     * A multiplier to make the timer (and therefore the game) go faster or slower. 0.5 makes the game run at half- speed.
     */
    public float timerSpeed = 1.0F;
    /** How much time has elapsed since the last tick, in ticks (range: 0.0 - 1.0). */
    public float elapsedPartialTicks;
    /** The time reported by the system clock at the last sync, in milliseconds */
    private long lastSyncSysClock;
    /** The time reported by the high-resolution clock at the last sync, in milliseconds */
    private long lastSyncHRClock;
    private long field_74285_i;
    /** A ratio used to sync the high-resolution clock to the system clock, updated once per second */
    private double timeSyncAdjustment = 1.0D;

    public Timer(float tps)
    {
        ticksPerSecond = tps;
        lastSyncSysClock = Engine.getSystemTime();
        lastSyncHRClock = System.nanoTime() / 1000000L;
    }

    public void updateTimer()
    {
        long i = Engine.getSystemTime();
        long j = i - lastSyncSysClock;
        long k = System.nanoTime() / 1000000L;
        double d0 = k / 1000.0D;

        if (j <= 1000L && j >= 0L)
        {
            field_74285_i += j;

            if (field_74285_i > 1000L)
            {
                long l = k - lastSyncHRClock;
                double d1 = (double) field_74285_i / (double) l;
                timeSyncAdjustment += (d1 - timeSyncAdjustment) * 0.20000000298023224D;
                lastSyncHRClock = k;
                field_74285_i = 0L;
            }

            if (field_74285_i < 0L)
            {
                lastSyncHRClock = k;
            }
        }
        else
        {
            lastHRTime = d0;
        }

        lastSyncSysClock = i;
        double d2 = (d0 - lastHRTime) * timeSyncAdjustment;
        lastHRTime = d0;

        if (d2 < 0.0D)
        {
            d2 = 0.0D;
        }

        if (d2 > 1.0D)
        {
            d2 = 1.0D;
        }

        elapsedPartialTicks = (float) (elapsedPartialTicks + d2 * timerSpeed * ticksPerSecond);
        elapsedTicks = (int) elapsedPartialTicks;
        elapsedPartialTicks -= elapsedTicks;

        if (elapsedTicks > 10)
        {
            elapsedTicks = 10;
        }

        renderPartialTicks = elapsedPartialTicks;
    }
}