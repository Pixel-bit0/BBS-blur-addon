package dev.bbsblur;

import dev.bbsblur.clip.BlurClip;
import mchorse.bbs_mod.BBSMod;
import mchorse.bbs_mod.camera.clips.ClipFactoryData;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import net.fabricmc.api.ModInitializer;

/**
 * Common entrypoint. Registers the Blur camera clip in BBS FS' camera clip factory.
 * Runs after BBS because fabric.mod.json depends on "bbs".
 */
public class BlurAddon implements ModInitializer
{
    public static final String MOD_ID = "bbsblur";
    public static final Link BLUR_CLIP = new Link(MOD_ID, "blur");

    @Override
    public void onInitialize()
    {
        BBSMod.getFactoryCameraClips().register(BLUR_CLIP, BlurClip.class, new ClipFactoryData(Icons.SPHERE, 0x6c5ce7));
    }
}
