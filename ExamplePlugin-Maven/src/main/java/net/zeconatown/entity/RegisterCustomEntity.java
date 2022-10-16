package net.zeconatown.entity;

import cn.nukkit.entity.Entity;
import net.zeconatown.Main;

public class RegisterCustomEntity {
    public RegisterCustomEntity(Main api) {
        Entity.registerCustomEntity(Cow.getDefinitions(), Cow.class);
        api.getLogger().info("Register Success");
    }
}
