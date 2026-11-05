package dev.bbsblur;

import mchorse.bbs_mod.events.BBSAddonMod;
import mchorse.bbs_mod.events.Subscribe;
import mchorse.bbs_mod.events.register.RegisterL10nEvent;
import mchorse.bbs_mod.events.register.RegisterSourcePacksEvent;
import mchorse.bbs_mod.resources.Link;

/**
 * BBS FS addon entrypoint ("bbs-addon"). Its @Subscribe methods are registered
 * on BBS' own event bus. Used here so the clip gets a proper localized name.
 */
public class BlurBbsAddon implements BBSAddonMod
{
    @Subscribe
    public void onRegisterSourcePacks(RegisterSourcePacksEvent event)
    {
        /* Exposes assets/bbsblur/assets/** from this jar to BBS' asset provider */
        event.registerAddon(BlurAddon.MOD_ID, BlurBbsAddon.class);
    }

    @Subscribe
    public void onRegisterL10n(RegisterL10nEvent event)
    {
        event.l10n.registerOne((lang) -> new Link(BlurAddon.MOD_ID, "strings/" + lang + ".json"));
    }
}
