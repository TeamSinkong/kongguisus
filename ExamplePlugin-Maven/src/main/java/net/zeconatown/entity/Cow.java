package net.zeconatown.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AllMatchEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.*;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.CustomEntityDefinition;

import cn.nukkit.entity.passive.EntityCow;
import cn.nukkit.entity.passive.EntityWalkingAnimal;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Objects;
import java.util.Set;

public class Cow extends EntityWalkingAnimal implements CustomEntity {

    public Cow(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private IBehaviorGroup behaviorGroup;

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null) {
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(
                            //用于刷新InLove状态的核心行为
                            new Behavior(
                                    new InLoveExecutor(400),
                                    new AllMatchEvaluator(
                                            new PassByTimeEvaluator<>(PlayerBreedingMemory.class, 0, 400),
                                            new PassByTimeEvaluator<>(InLoveMemory.class, 6000, Integer.MAX_VALUE, true)
                                    ),
                                    1, 1
                            )
                    ),
                    Set.of(
                            new Behavior(new RandomRoamExecutor(0.25f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator<>(AttackMemory.class, 0, 100), 4, 1),
                            new Behavior(new EntityBreedingExecutor<>(EntityCow.class, 16, 100, 0.5f), entity -> Objects.requireNonNull(entity.getMemoryStorage()).get(InLoveMemory.class).isInLove(), 3, 1),
                            new Behavior(new MoveToTargetExecutor(NearestFeedingPlayerMemory.class, 0.25f, true), new MemoryCheckNotEmptyEvaluator(NearestFeedingPlayerMemory.class), 2, 1),
                            new Behavior(new LookAtTargetExecutor(NearestPlayerMemory.class, 100), new ProbabilityEvaluator(4, 10), 1, 1, 100),
                            new Behavior(new RandomRoamExecutor(0.1f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                    ),
                    Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
            );
        }
        return behaviorGroup;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    public String getOriginalName() {
        return "ZetCow";
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.LEATHER), Item.get(((this.isOnFire()) ? Item.COOKED_BEEF : Item.RAW_BEEF))};
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
        this.setHealth(10);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (super.onInteract(player, item, clickedPos)) {
            return true;
        }

        if (item.getId() == Item.BUCKET && item.getDamage() == 0) {
            item.count--;
            player.getInventory().addItem(Item.get(Item.BUCKET, 1));
            return true;
        }

        return false;
    }

    @Override
    public CustomEntityDefinition getDefinition() {
        return getDefinitions();
    }

    static CustomEntityDefinition getDefinitions() {
        return CustomEntityDefinition.builder().identifier("d240:blue1").summonable(true).spawnEgg(true).build();
    }
}