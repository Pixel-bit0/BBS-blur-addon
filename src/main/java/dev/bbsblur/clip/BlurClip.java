package dev.bbsblur.clip;

import dev.bbsblur.client.BlurState;
import mchorse.bbs_mod.camera.clips.CameraClip;
import mchorse.bbs_mod.camera.clips.CameraClipContext;
import mchorse.bbs_mod.camera.data.Position;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.settings.values.numeric.ValueFloat;
import mchorse.bbs_mod.settings.values.numeric.ValueInt;
import mchorse.bbs_mod.utils.AABB;
import mchorse.bbs_mod.utils.clips.Clip;
import mchorse.bbs_mod.utils.clips.ClipContext;

/**
 * Camera clip that blurs the screen.
 *
 * Modes: 0 = circle (sharp middle, blurry edges), 1 = full, 2 = actor focus
 * (actor stays sharp, everything else is blurred).
 */
public class BlurClip extends CameraClip
{
    public final ValueInt mode = new ValueInt("mode", BlurState.CIRCLE);
    public final ValueFloat strength = new ValueFloat("strength", 10F);
    public final ValueFloat radius = new ValueFloat("radius", 0.25F);
    public final ValueFloat softness = new ValueFloat("softness", 0.35F);
    public final ValueInt actor = new ValueInt("actor", 0);
    public final ValueFloat padding = new ValueFloat("padding", 0.15F);

    public BlurClip()
    {
        super();

        this.add(this.mode);
        this.add(this.strength);
        this.add(this.radius);
        this.add(this.softness);
        this.add(this.actor);
        this.add(this.padding);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        float fade = this.envelope.factorEnabled(this.duration.get(), context.relativeTick + context.transition);
        int mode = this.mode.get();

        boolean hasActor = false;
        double ax = 0;
        double ay = 0;
        double az = 0;
        double halfWidth = 0.3;
        double height = 1.8;

        if (mode == BlurState.ACTOR && context instanceof CameraClipContext cameraContext)
        {
            Object found = cameraContext.entities.get(this.actor.get());

            if (found instanceof IEntity entity)
            {
                double t = context.transition;

                ax = entity.getPrevX() + (entity.getX() - entity.getPrevX()) * t;
                ay = entity.getPrevY() + (entity.getY() - entity.getPrevY()) * t;
                az = entity.getPrevZ() + (entity.getZ() - entity.getPrevZ()) * t;
                hasActor = true;

                AABB box = entity.getPickingHitbox();

                if (box != null && box.w > 0.01 && box.h > 0.01)
                {
                    halfWidth = Math.max(box.w, box.d) * 0.5;
                    height = box.h;
                }
            }
        }

        float pad = Math.max(0F, this.padding.get());

        BlurState.submit(new BlurState.Snapshot(
            mode,
            Math.max(0F, this.strength.get()),
            Math.max(0F, this.radius.get()),
            Math.max(0F, this.softness.get()),
            Math.max(0F, Math.min(1F, fade)),
            hasActor,
            ax, ay, az,
            halfWidth + pad,
            height + pad * 2,
            System.nanoTime()
        ));
    }

    @Override
    protected Clip create()
    {
        return new BlurClip();
    }
}
