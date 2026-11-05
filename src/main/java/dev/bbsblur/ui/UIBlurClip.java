package dev.bbsblur.ui;

import dev.bbsblur.clip.BlurClip;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.film.IUIClipsDelegate;
import mchorse.bbs_mod.ui.film.clips.UIClip;
import mchorse.bbs_mod.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs_mod.ui.framework.elements.input.UITrackpad;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.utils.Direction;

/**
 * Editor panel for {@link BlurClip}. Fields are assigned in registerUI(), which the
 * UIClip constructor calls, so they must not have initializers here.
 */
public class UIBlurClip extends UIClip
{
    public UICirculate mode;
    public UITrackpad strength;
    public UITrackpad radius;
    public UITrackpad softness;
    public UITrackpad actor;
    public UITrackpad padding;

    public UIBlurClip(BlurClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    private BlurClip blur()
    {
        return (BlurClip) this.clip;
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.mode = new UICirculate((button) -> this.blur().mode.set(button.getValue()));
        this.mode.addLabel(IKey.raw("Circle (sharp middle)"));
        this.mode.addLabel(IKey.raw("Full blur"));
        this.mode.addLabel(IKey.raw("Actor focus"));

        this.strength = new UITrackpad((value) -> this.blur().strength.set(value.floatValue()));
        this.strength.limit(0, 64);
        this.strength.tooltip(IKey.raw("Blur strength (pixels at 1080p)"), Direction.BOTTOM);

        this.radius = new UITrackpad((value) -> this.blur().radius.set(value.floatValue()));
        this.radius.limit(0, 2);
        this.radius.tooltip(IKey.raw("Circle: sharp radius (screen heights)"), Direction.BOTTOM);

        this.softness = new UITrackpad((value) -> this.blur().softness.set(value.floatValue()));
        this.softness.limit(0, 2);
        this.softness.tooltip(IKey.raw("Edge softness"), Direction.BOTTOM);

        this.actor = new UITrackpad((value) -> this.blur().actor.set(value.intValue()));
        this.actor.limit(0, 1000).integer();
        this.actor.tooltip(IKey.raw("Actor focus: replay index"), Direction.BOTTOM);

        this.padding = new UITrackpad((value) -> this.blur().padding.set(value.floatValue()));
        this.padding.limit(0, 4);
        this.padding.tooltip(IKey.raw("Actor focus: extra sharp margin (blocks)"), Direction.BOTTOM);
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(this.section(IKey.raw("Blur mode"), this.mode));
        this.panels.add(this.section(IKey.raw("Strength / radius / softness"), UI.row(5, 0, 20, this.strength, this.radius, this.softness)));
        this.panels.add(this.section(IKey.raw("Actor focus (replay index / margin)"), UI.row(5, 0, 20, this.actor, this.padding)));
    }

    @Override
    public void fillData()
    {
        super.fillData();

        BlurClip clip = this.blur();

        this.mode.setValue(clip.mode.get());
        this.strength.setValue(clip.strength.get());
        this.radius.setValue(clip.radius.get());
        this.softness.setValue(clip.softness.get());
        this.actor.setValue(clip.actor.get());
        this.padding.setValue(clip.padding.get());
    }
}
