package net.minecraft.tileentity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;
import net.minecraft.world.gen.Heightmap;

public class BeaconTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity
{
    /** List of effects that Beacons can apply */
    public static final Effect[][] EFFECTS_LIST = new Effect[][] {{Effects.SPEED, Effects.HASTE}, {Effects.RESISTANCE, Effects.JUMP_BOOST}, {Effects.STRENGTH}, {Effects.REGENERATION}};
    private static final Set<Effect> VALID_EFFECTS = Arrays.stream(EFFECTS_LIST).flatMap(Arrays::stream).collect(Collectors.toSet());
    private List<BeamSegment> beamSegments = Lists.newArrayList();
    private List<BeamSegment> beamColorSegments = Lists.newArrayList();

    /** Level of this beacon's pyramid. */
    private int levels;
    private int beaconSize = -1;
    @Nullable

    /** Primary potion effect given by this beacon */
    private Effect primaryEffect;
    @Nullable

    /** Secondary potion effect given by this beacon. */
    private Effect secondaryEffect;
    @Nullable

    /**
     * The custom name for this beacon. This was unused until 1.14; see https://bugs.mojang.com/browse/MC-124395
     */
    private ITextComponent customName;
    private LockCode lockCode = LockCode.EMPTY_CODE;
    private final IIntArray beaconData = new IIntArray()
    {
        public int get(int index)
        {
            switch (index)
            {
                case 0:
                    return BeaconTileEntity.this.levels;

                case 1:
                    return Effect.getId(BeaconTileEntity.this.primaryEffect);

                case 2:
                    return Effect.getId(BeaconTileEntity.this.secondaryEffect);

                default:
                    return 0;
            }
        }
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0:
                    BeaconTileEntity.this.levels = value;
                    break;

                case 1:
                    if (!BeaconTileEntity.this.world.isRemote && !BeaconTileEntity.this.beamSegments.isEmpty())
                    {
                        BeaconTileEntity.this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
                    }

                    BeaconTileEntity.this.primaryEffect = BeaconTileEntity.isBeaconEffect(value);
                    break;

                case 2:
                    BeaconTileEntity.this.secondaryEffect = BeaconTileEntity.isBeaconEffect(value);
            }
        }
        public int size()
        {
            return 3;
        }
    };

    public BeaconTileEntity()
    {
        super(TileEntityType.BEACON);
    }

    public void tick()
    {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        BlockPos blockpos;

        if (this.beaconSize < j)
        {
            blockpos = this.pos;
            this.beamColorSegments = Lists.newArrayList();
            this.beaconSize = blockpos.getY() - 1;
        }
        else
        {
            blockpos = new BlockPos(i, this.beaconSize + 1, k);
        }

        BeamSegment beacontileentity$beamsegment = this.beamColorSegments.isEmpty() ? null : this.beamColorSegments.get(this.beamColorSegments.size() - 1);
        int l = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, i, k);

        for (int i1 = 0; i1 < 10 && blockpos.getY() <= l; ++i1)
        {
            BlockState blockstate = this.world.getBlockState(blockpos);
            Block block = blockstate.getBlock();

            if (block instanceof IBeaconBeamColorProvider)
            {
                float[] afloat = ((IBeaconBeamColorProvider)block).getColor().getColorComponentValues();

                if (this.beamColorSegments.size() <= 1)
                {
                    beacontileentity$beamsegment = new BeamSegment(afloat);
                    this.beamColorSegments.add(beacontileentity$beamsegment);
                }
                else if (beacontileentity$beamsegment != null)
                {
                    if (Arrays.equals(afloat, beacontileentity$beamsegment.colors))
                    {
                        beacontileentity$beamsegment.incrementHeight();
                    }
                    else
                    {
                        beacontileentity$beamsegment = new BeamSegment(new float[] {(beacontileentity$beamsegment.colors[0] + afloat[0]) / 2.0F, (beacontileentity$beamsegment.colors[1] + afloat[1]) / 2.0F, (beacontileentity$beamsegment.colors[2] + afloat[2]) / 2.0F});
                        this.beamColorSegments.add(beacontileentity$beamsegment);
                    }
                }
            }
            else
            {
                if (beacontileentity$beamsegment == null || blockstate.getOpacity(this.world, blockpos) >= 15 && block != Blocks.BEDROCK)
                {
                    this.beamColorSegments.clear();
                    this.beaconSize = l;
                    break;
                }

                beacontileentity$beamsegment.incrementHeight();
            }

            blockpos = blockpos.up();
            ++this.beaconSize;
        }

        int j1 = this.levels;

        if (this.world.getGameTime() % 80L == 0L)
        {
            if (!this.beamSegments.isEmpty())
            {
                this.checkBeaconLevel(i, j, k);
            }

            if (this.levels > 0 && !this.beamSegments.isEmpty())
            {
                this.addEffectsToPlayers();
                this.playSound(SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }

        if (this.beaconSize >= l)
        {
            this.beaconSize = -1;
            boolean flag = j1 > 0;
            this.beamSegments = this.beamColorSegments;

            if (!this.world.isRemote)
            {
                boolean flag1 = this.levels > 0;

                if (!flag && flag1)
                {
                    this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);

                    for (ServerPlayerEntity serverplayerentity : this.world.getEntitiesWithinAABB(ServerPlayerEntity.class, (new AxisAlignedBB((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k)).grow(10.0D, 5.0D, 10.0D)))
                    {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayerentity, this);
                    }
                }
                else if (flag && !flag1)
                {
                    this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
                }
            }
        }
    }

    private void checkBeaconLevel(int beaconXIn, int beaconYIn, int beaconZIn)
    {
        this.levels = 0;

        for (int i = 1; i <= 4; this.levels = i++)
        {
            int j = beaconYIn - i;

            if (j < 0)
            {
                break;
            }

            boolean flag = true;

            for (int k = beaconXIn - i; k <= beaconXIn + i && flag; ++k)
            {
                for (int l = beaconZIn - i; l <= beaconZIn + i; ++l)
                {
                    if (!this.world.getBlockState(new BlockPos(k, j, l)).isIn(BlockTags.BEACON_BASE_BLOCKS))
                    {
                        flag = false;
                        break;
                    }
                }
            }

            if (!flag)
            {
                break;
            }
        }
    }

    /**
     * invalidates a tile entity
     */
    public void remove()
    {
        this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
        super.remove();
    }

    private void addEffectsToPlayers()
    {
        if (!this.world.isRemote && this.primaryEffect != null)
        {
            double d0 = (double)(this.levels * 10 + 10);
            int i = 0;

            if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect)
            {
                i = 1;
            }

            int j = (9 + this.levels * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.pos)).grow(d0).expand(0.0D, (double)this.world.getHeight(), 0.0D);
            List<PlayerEntity> list = this.world.getEntitiesWithinAABB(PlayerEntity.class, axisalignedbb);

            for (PlayerEntity playerentity : list)
            {
                playerentity.addPotionEffect(new EffectInstance(this.primaryEffect, j, i, true, true));
            }

            if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect != null)
            {
                for (PlayerEntity playerentity1 : list)
                {
                    playerentity1.addPotionEffect(new EffectInstance(this.secondaryEffect, j, 0, true, true));
                }
            }
        }
    }

    public void playSound(SoundEvent sound)
    {
        this.world.playSound((PlayerEntity)null, this.pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public List<BeamSegment> getBeamSegments()
    {
        return (List<BeamSegment>)(this.levels == 0 ? ImmutableList.of() : this.beamSegments);
    }

    public int getLevels()
    {
        return this.levels;
    }

    @Nullable

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced expensive.main.viaversion World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
     */
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    public double getMaxRenderDistanceSquared()
    {
        return 256.0D;
    }

    @Nullable
    private static Effect isBeaconEffect(int effectId)
    {
        Effect effect = Effect.get(effectId);
        return VALID_EFFECTS.contains(effect) ? effect : null;
    }

    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.primaryEffect = isBeaconEffect(nbt.getInt("Primary"));
        this.secondaryEffect = isBeaconEffect(nbt.getInt("Secondary"));

        if (nbt.contains("CustomName", 8))
        {
            this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
        }

        this.lockCode = LockCode.read(nbt);
    }

    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.putInt("Primary", Effect.getId(this.primaryEffect));
        compound.putInt("Secondary", Effect.getId(this.secondaryEffect));
        compound.putInt("Levels", this.levels);

        if (this.customName != null)
        {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }

        this.lockCode.write(compound);
        return compound;
    }

    /**
     * Sets the custom name for this beacon.
     */
    public void setCustomName(@Nullable ITextComponent aname)
    {
        this.customName = aname;
    }

    @Nullable
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
    {
        return LockableTileEntity.canUnlock(p_createMenu_3_, this.lockCode, this.getDisplayName()) ? new BeaconContainer(p_createMenu_1_, p_createMenu_2_, this.beaconData, IWorldPosCallable.of(this.world, this.getPos())) : null;
    }

    public ITextComponent getDisplayName()
    {
        return (ITextComponent)(this.customName != null ? this.customName : new TranslationTextComponent("container.beacon"));
    }

    public static class BeamSegment
    {
        private final float[] colors;
        private int height;

        public BeamSegment(float[] colorsIn)
        {
            this.colors = colorsIn;
            this.height = 1;
        }

        protected void incrementHeight()
        {
            ++this.height;
        }

        public float[] getColors()
        {
            return this.colors;
        }

        public int getHeight()
        {
            return this.height;
        }
    }
}
