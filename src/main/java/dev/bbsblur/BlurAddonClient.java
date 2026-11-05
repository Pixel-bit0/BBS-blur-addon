package dev.bbsblur;

import dev.bbsblur.client.BlurRenderer;
import dev.bbsblur.clip.BlurClip;
import dev.bbsblur.ui.UIBlurClip;
import mchorse.bbs_mod.ui.film.clips.UIClip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class BlurAddonClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        /* Editor panel for the clip */
        UIClip.register(BlurClip.class, (clip, editor) -> new UIBlurClip((BlurClip) clip, editor));

        /* Post-process shader */
        CoreShaderRegistrationCallback.EVENT.register((context) ->
        {
            context.register(new Identifier(BlurAddon.MOD_ID, "blur"), VertexFormats.POSITION, (program) -> BlurRenderer.program = program);
        });
    }
}
