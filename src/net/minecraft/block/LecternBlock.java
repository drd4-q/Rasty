package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LecternBlock extends ContainerBlock
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
    public static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public static final VoxelShape POST_SHAPE = Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D);
    public static final VoxelShape COMMON_SHAPE = VoxelShapes.or(BASE_SHAPE, POST_SHAPE);
    public static final VoxelShape TOP_PLATE_SHAPE = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    public static final VoxelShape COLLISION_SHAPE = VoxelShapes.or(COMMON_SHAPE, TOP_PLATE_SHAPE);
    public static final VoxelShape WEST_SHAPE = VoxelShapes.or(Block.makeCuboidShape(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D), Block.makeCuboidShape(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D), Block.makeCuboidShape(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D), COMMON_SHAPE);
    public static final VoxelShape NORTH_SHAPE = VoxelShapes.or(Block.makeCuboidShape(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D), Block.makeCuboidShape(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D), Block.makeCuboidShape(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D), COMMON_SHAPE);
    public static final VoxelShape EAST_SHAPE = VoxelShapes.or(Block.makeCuboidShape(15.0D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D), Block.makeCuboidShape(10.666667D, 12.0D, 0.0D, 6.333333D, 16.0D, 16.0D), Block.makeCuboidShape(6.333333D, 14.0D, 0.0D, 2.0D, 18.0D, 16.0D), COMMON_SHAPE);
    public static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(Block.makeCuboidShape(0.0D, 10.0D, 15.0D, 16.0D, 14.0D, 10.666667D), Block.makeCuboidShape(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 6.333333D), Block.makeCuboidShape(0.0D, 14.0D, 6.333333D, 16.0D, 18.0D, 2.0D), COMMON_SHAPE);

    protected LecternBlock(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(HAS_BOOK, Boolean.valueOf(false)));
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call expensive.main.viaversion {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return COMMON_SHAPE;
    }

    public boolean isTransparent(BlockState state)
    {
        return true;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getWorld();
        ItemStack itemstack = context.getItem();
        CompoundNBT compoundnbt = itemstack.getTag();
        PlayerEntity playerentity = context.getPlayer();
        boolean flag = false;

        if (!world.isRemote && playerentity != null && compoundnbt != null && playerentity.canUseCommandBlock() && compoundnbt.contains("BlockEntityTag"))
        {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("BlockEntityTag");

            if (compoundnbt1.contains("Book"))
            {
                flag = true;
            }
        }

        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(HAS_BOOK, Boolean.valueOf(flag));
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return COLLISION_SHAPE;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch ((Direction)state.get(FACING))
        {
            case NORTH:
                return NORTH_SHAPE;

            case SOUTH:
                return SOUTH_SHAPE;

            case EAST:
                return EAST_SHAPE;

            case WEST:
                return WEST_SHAPE;

            default:
                return COMMON_SHAPE;
        }
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call expensive.main.viaversion {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call expensive.main.viaversion {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, POWERED, HAS_BOOK);
    }

    @Nullable
    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new LecternTileEntity();
    }

    public static boolean tryPlaceBook(World worldIn, BlockPos pos, BlockState state, ItemStack stack)
    {
        if (!state.get(HAS_BOOK))
        {
            if (!worldIn.isRemote)
            {
                placeBook(worldIn, pos, state, stack);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private static void placeBook(World worldIn, BlockPos pos, BlockState state, ItemStack stack)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof LecternTileEntity)
        {
            LecternTileEntity lecterntileentity = (LecternTileEntity)tileentity;
            lecterntileentity.setBook(stack.split(1));
            setHasBook(worldIn, pos, state, true);
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void setHasBook(World worldIn, BlockPos pos, BlockState state, boolean hasBook)
    {
        worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)).with(HAS_BOOK, Boolean.valueOf(hasBook)), 3);
        notifyNeighbors(worldIn, pos, state);
    }

    public static void pulse(World worldIn, BlockPos pos, BlockState state)
    {
        setPowered(worldIn, pos, state, true);
        worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 2);
        worldIn.playEvent(1043, pos, 0);
    }

    private static void setPowered(World worldIn, BlockPos pos, BlockState state, boolean powered)
    {
        worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(powered)), 3);
        notifyNeighbors(worldIn, pos, state);
    }

    private static void notifyNeighbors(World worldIn, BlockPos pos, BlockState state)
    {
        worldIn.notifyNeighborsOfStateChange(pos.down(), state.getBlock());
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        setPowered(worldIn, pos, state, false);
    }

    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.isIn(newState.getBlock()))
        {
            if (state.get(HAS_BOOK))
            {
                this.dropBook(state, worldIn, pos);
            }

            if (state.get(POWERED))
            {
                worldIn.notifyNeighborsOfStateChange(pos.down(), this);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    private void dropBook(BlockState state, World world, BlockPos pos)
    {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof LecternTileEntity)
        {
            LecternTileEntity lecterntileentity = (LecternTileEntity)tileentity;
            Direction direction = state.get(FACING);
            ItemStack itemstack = lecterntileentity.getBook().copy();
            float f = 0.25F * (float)direction.getXOffset();
            float f1 = 0.25F * (float)direction.getZOffset();
            ItemEntity itementity = new ItemEntity(world, (double)pos.getX() + 0.5D + (double)f, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D + (double)f1, itemstack);
            itementity.setDefaultPickupDelay();
            world.addEntity(itementity);
            lecterntileentity.clear();
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     * @deprecated call expensive.main.viaversion {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
     */
    public boolean canProvidePower(BlockState state)
    {
        return true;
    }

    /**
     * @deprecated call expensive.main.viaversion {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return blockState.get(POWERED) ? 15 : 0;
    }

    /**
     * @deprecated call expensive.main.viaversion {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return side == Direction.UP && blockState.get(POWERED) ? 15 : 0;
    }

    /**
     * @deprecated call expensive.main.viaversion {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
     * is fine.
     */
    public boolean hasComparatorInputOverride(BlockState state)
    {
        return true;
    }

    /**
     * @deprecated call expensive.main.viaversion {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
        if (blockState.get(HAS_BOOK))
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof LecternTileEntity)
            {
                return ((LecternTileEntity)tileentity).getComparatorSignalLevel();
            }
        }

        return 0;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (state.get(HAS_BOOK))
        {
            if (!worldIn.isRemote)
            {
                this.openContainer(worldIn, pos, player);
            }

            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        else
        {
            ItemStack itemstack = player.getHeldItem(handIn);
            return !itemstack.isEmpty() && !itemstack.getItem().isIn(ItemTags.LECTERN_BOOKS) ? ActionResultType.CONSUME : ActionResultType.PASS;
        }
    }

    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos)
    {
        return !state.get(HAS_BOOK) ? null : super.getContainer(state, worldIn, pos);
    }

    private void openContainer(World world, BlockPos pos, PlayerEntity player)
    {
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof LecternTileEntity)
        {
            player.openContainer((LecternTileEntity)tileentity);
            player.addStat(Stats.INTERACT_WITH_LECTERN);
        }
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }
}
