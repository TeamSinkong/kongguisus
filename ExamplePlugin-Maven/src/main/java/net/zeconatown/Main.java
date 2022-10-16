package net.zeconatown;

import cn.nukkit.plugin.PluginBase;
import net.zeconatown.entity.RegisterCustomEntity;
public class Main extends PluginBase {

    @Override
    public void onEnable() {
        getLogger().info("Enabled");
        new RegisterCustomEntity(this);

    }


}
