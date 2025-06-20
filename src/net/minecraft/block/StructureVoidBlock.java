package net.minecraft.block;

import net.minecraft.block.material.PushReaction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class StructureVoidBlock extends Block
{
    private static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

    protected StructureVoidBlock(Properties properties)
    {
        super(properties);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call expensive.main.viaversion {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return 1.0F;
    }

    /**
     * @deprecated call expensive.main.viaversion {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
     */
    public PushReaction getPushReaction(BlockState state)
    {
        return PushReaction.DESTROY;
    }
}
