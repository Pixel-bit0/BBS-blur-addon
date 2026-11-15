package dev.bbsblur;

import dev.bbsblur.clip.BlurClip;
import mchorse.bbs_mod.api.BBSAddonMod;
import mchorse.bbs_mod.api.Subscribe;
import mchorse.bbs_mod.api.client.events.RegisterL10nEvent;
import mchorse.bbs_mod.api.events.RegisterCameraClipsEvent;
import mchorse.bbs_mod.api.events.RegisterSourcePacksEvent;
import mchorse.bbs_mod.camera.clips.ClipFactoryData;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.utils.icons.Icons;

/**
 * BBS FS addon entrypoint ("bbs-addon"). BBS posts its events to the @Subscribe methods below.
 */
public class BlurBbsAddon implements BBSAddonMod
{
    /** Registers the Blur clip in BBS' camera clip factory. */
    @Subscribe
    public void onRegisterCameraClips(RegisterCameraClipsEvent event)
    {
        event.factory.register(BlurAddon.BLUR_CLIP, BlurClip.class, new ClipFactoryData(Icons.SPHERE, 0x6c5ce7));
    }

    /** Exposes assets/bbsblur/assets/** from this jar to BBS' asset provider. */
    @Subscribe
    public void onRegisterSourcePacks(RegisterSourcePacksEvent event)
    {
        event.registerAddon(BlurAddon.MOD_ID, BlurBbsAddon.class);
    }

    /** Gives the clip a proper name in the add menu. */
    @Subscribe
    public void onRegisterL10n(RegisterL10nEvent event)
    {
        event.l10n.registerOne((lang) -> new Link(BlurAddon.MOD_ID, "strings/" + lang + ".json"));
    }
}
