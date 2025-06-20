package net.minecraft.loot;

import expensive.main.baritone.api.utils.BlockOptionalMeta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class LootContext
{
    private final Random random;
    private final float luck;
    private final ServerWorld world;
    private final Function<ResourceLocation, LootTable> lootTableManager;
    private final Set<LootTable> lootTables = Sets.newLinkedHashSet();
    private final Function<ResourceLocation, ILootCondition> field_227499_f_;
    private final Set<ILootCondition> conditions = Sets.newLinkedHashSet();
    private final Map < LootParameter<?>, Object > parameters;
    private final Map<ResourceLocation, IDynamicDropProvider> field_216037_g;

    private LootContext(Random rand, float luckIn, ServerWorld worldIn, Function<ResourceLocation, LootTable> lootTableManagerIn, Function<ResourceLocation, ILootCondition> p_i225885_5_, Map < LootParameter<?>, Object > parametersIn, Map<ResourceLocation, IDynamicDropProvider> conditionsIn)
    {
        this.random = rand;
        this.luck = luckIn;
        this.world = worldIn;
        this.lootTableManager = lootTableManagerIn;
        this.field_227499_f_ = p_i225885_5_;
        this.parameters = ImmutableMap.copyOf(parametersIn);
        this.field_216037_g = ImmutableMap.copyOf(conditionsIn);
    }

    public boolean has(LootParameter<?> parameter)
    {
        return this.parameters.containsKey(parameter);
    }

    public void generateDynamicDrop(ResourceLocation name, Consumer<ItemStack> consumer)
    {
        IDynamicDropProvider lootcontext$idynamicdropprovider = this.field_216037_g.get(name);

        if (lootcontext$idynamicdropprovider != null)
        {
            lootcontext$idynamicdropprovider.add(this, consumer);
        }
    }

    @Nullable
    public <T> T get(LootParameter<T> parameter)
    {
        return (T)this.parameters.get(parameter);
    }

    public boolean addLootTable(LootTable lootTableIn)
    {
        return this.lootTables.add(lootTableIn);
    }

    public void removeLootTable(LootTable lootTableIn)
    {
        this.lootTables.remove(lootTableIn);
    }

    public boolean addCondition(ILootCondition conditionIn)
    {
        return this.conditions.add(conditionIn);
    }

    public void removeCondition(ILootCondition conditionIn)
    {
        this.conditions.remove(conditionIn);
    }

    public LootTable getLootTable(ResourceLocation tableId)
    {
        return this.lootTableManager.apply(tableId);
    }

    public ILootCondition getLootCondition(ResourceLocation conditionId)
    {
        return this.field_227499_f_.apply(conditionId);
    }

    public Random getRandom()
    {
        return this.random;
    }

    public float getLuck()
    {
        return this.luck;
    }

    public ServerWorld getWorld()
    {
        return this.world;
    }

    public static class Builder
    {
        private final ServerWorld world;
        private final Map < LootParameter<?>, Object > lootParameters = Maps.newIdentityHashMap();
        private final Map<ResourceLocation, IDynamicDropProvider> field_216026_c = Maps.newHashMap();
        private Random rand;
        private float luck;

        public Builder(ServerWorld worldIn)
        {
            this.world = worldIn;
        }

        public Builder withRandom(Random randomIn)
        {
            this.rand = randomIn;
            return this;
        }

        public Builder withSeed(long seed)
        {
            if (seed != 0L)
            {
                this.rand = new Random(seed);
            }

            return this;
        }

        public Builder withSeededRandom(long seed, Random p_216020_3_)
        {
            if (seed == 0L)
            {
                this.rand = p_216020_3_;
            }
            else
            {
                this.rand = new Random(seed);
            }

            return this;
        }

        public Builder withLuck(float luckIn)
        {
            this.luck = luckIn;
            return this;
        }

        public <T> Builder withParameter(LootParameter<T> parameter, T value)
        {
            this.lootParameters.put(parameter, value);
            return this;
        }

        public <T> Builder withNullableParameter(LootParameter<T> parameter, @Nullable T value)
        {
            if (value == null)
            {
                this.lootParameters.remove(parameter);
            }
            else
            {
                this.lootParameters.put(parameter, value);
            }

            return this;
        }

        public Builder withDynamicDrop(ResourceLocation p_216017_1_, IDynamicDropProvider p_216017_2_)
        {
            IDynamicDropProvider lootcontext$idynamicdropprovider = this.field_216026_c.put(p_216017_1_, p_216017_2_);

            if (lootcontext$idynamicdropprovider != null)
            {
                throw new IllegalStateException("Duplicated dynamic drop '" + this.field_216026_c + "'");
            }
            else
            {
                return this;
            }
        }

        public ServerWorld getWorld()
        {
            return this.world;
        }

        public <T> T assertPresent(LootParameter<T> parameter)
        {
            T t = (T)this.lootParameters.get(parameter);

            if (t == null)
            {
                throw new IllegalArgumentException("No parameter " + parameter);
            }
            else
            {
                return t;
            }
        }

        @Nullable
        public <T> T get(LootParameter<T> parameter)
        {
            return (T)this.lootParameters.get(parameter);
        }

        public LootContext build(LootParameterSet parameterSet)
        {
            Set < LootParameter<? >> set = Sets.difference(this.lootParameters.keySet(), parameterSet.getAllParameters());

            if (!set.isEmpty())
            {
                throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
            }
            else
            {
                Set < LootParameter<? >> set1 = Sets.difference(parameterSet.getRequiredParameters(), this.lootParameters.keySet());

                if (!set1.isEmpty())
                {
                    throw new IllegalArgumentException("Missing required parameters: " + set1);
                }
                else
                {
                    Random random = this.rand;

                    if (random == null)
                    {
                        random = new Random();
                    }

                    MinecraftServer minecraftserver = getServer(this.world);
                    LootTableManager lootTableManager1 = getLootTableManager(minecraftserver);
                    LootPredicateManager LootPredicateManager = getLootPredicateManager(minecraftserver);
                    return new LootContext(random, this.luck, this.world, lootTableManager1::getLootTableFromLocation, LootPredicateManager::func_227517_a_, this.lootParameters, this.field_216026_c);
                }
            }
        }
    }

    private static LootPredicateManager getLootPredicateManager(MinecraftServer server) {
        if (server == null) {
            return BlockOptionalMeta.getPredicateManager();
        }
        return server.func_229736_aP_();
    }

    private static LootTableManager getLootTableManager(MinecraftServer server) {
        if (server == null) {
            return BlockOptionalMeta.getManager();
        }
        return server.getLootTableManager();
    }

    private static MinecraftServer getServer(ServerWorld world) {
        if (world == null) {
            return null;
        }
        return world.getServer();
    }


    public static enum EntityTarget
    {
        THIS("this", LootParameters.THIS_ENTITY),
        KILLER("killer", LootParameters.KILLER_ENTITY),
        DIRECT_KILLER("direct_killer", LootParameters.DIRECT_KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER);

        private final String targetType;
        private final LootParameter <? extends Entity > parameter;

        private EntityTarget(String targetTypeIn, LootParameter <? extends Entity > parameterIn)
        {
            this.targetType = targetTypeIn;
            this.parameter = parameterIn;
        }

        public LootParameter <? extends Entity > getParameter()
        {
            return this.parameter;
        }

        public static EntityTarget fromString(String type)
        {
            for (EntityTarget lootcontext$entitytarget : values())
            {
                if (lootcontext$entitytarget.targetType.equals(type))
                {
                    return lootcontext$entitytarget;
                }
            }

            throw new IllegalArgumentException("Invalid entity target " + type);
        }

        public static class Serializer extends TypeAdapter<EntityTarget> {
            public void write(JsonWriter p_write_1_, EntityTarget p_write_2_) throws IOException {
                p_write_1_.value(p_write_2_.targetType);
            }

            public EntityTarget read(JsonReader p_read_1_) throws IOException {
                return EntityTarget.fromString(p_read_1_.nextString());
            }
        }
    }

    @FunctionalInterface
    public interface IDynamicDropProvider
    {
        void add(LootContext p_add_1_, Consumer<ItemStack> p_add_2_);
    }
}
