package dev.bbsblur.client;

/**
 * Hand-off between the Blur clip (which runs while BBS evaluates the camera)
 * and the renderer (which runs after the world is drawn). Plain Java on purpose,
 * so it is safe to load on a dedicated server too.
 */
public final class BlurState
{
    public static final int CIRCLE = 0;
    public static final int FULL = 1;
    public static final int ACTOR = 2;

    /** How long a submitted state stays valid without being refreshed by the clip. */
    private static final long STALE_NANOS = 250_000_000L;

    private static volatile Snapshot current;

    public record Snapshot(
        int mode,
        float strength,
        float radius,
        float softness,
        float amount,
        boolean hasActor,
        double actorX,
        double actorY,
        double actorZ,
        double actorHalfWidth,
        double actorHeight,
        long nanos
    ) {}

    private BlurState() {}

    public static void submit(Snapshot snapshot)
    {
        current = snapshot;
    }

    /** @return the current blur state, or null when no Blur clip refreshed it recently. */
    public static Snapshot active()
    {
        Snapshot snapshot = current;

        if (snapshot == null || System.nanoTime() - snapshot.nanos() > STALE_NANOS)
        {
            return null;
        }

        return snapshot;
    }
}
