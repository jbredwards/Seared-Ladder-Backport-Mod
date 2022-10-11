package git.jbredwards.searedladder.common.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.smeltery.block.BlockEnumSmeltery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.minecraft.block.BlockHorizontal.FACING;

/**
 *
 * @author jbred
 *
 */
public class BlockSearedLadder extends BlockEnumSmeltery<BlockSearedLadder.Type>
{
    @Nonnull public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
    @Nonnull public static final PropertyBool BOTTOM = PropertyBool.create("bottom");

    @Nonnull public static final AxisAlignedBB BOTTOM_BOX = box(0, 0, 0, 16, 2, 16);
    @Nonnull public static final List<AxisAlignedBB>
            NORTH_BOX = ImmutableList.of(
                    box(0,  2, 2, 16, 16, 16),
                    box(14, 2, 0, 16, 16, 2),
                    box(0,  2, 0, 2,  16, 2)
            ),
            SOUTH_BOX = ImmutableList.of(
                    box(0,  2, 0,  16, 16, 14),
                    box(0,  2, 14, 2,  16, 16),
                    box(14, 2, 14, 16, 16, 16)
            ),
            EAST_BOX = ImmutableList.of(
                    box(0,  2, 0,  14, 16, 16),
                    box(14, 2, 14, 16, 16, 16),
                    box(14, 2, 0,  16, 16, 2)
            ),
            WEST_BOX = ImmutableList.of(
                    box(2, 2, 0,  16, 16, 16),
                    box(0, 2, 0,  2,  16, 2),
                    box(0, 2, 14, 2,  16, 16));

    public BlockSearedLadder(@Nonnull Material material) { super(material, TYPE, Type.class); }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, BOTTOM, FACING);
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() << 1 | (state.getValue(BOTTOM) ? 1 : 0);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(BOTTOM, (meta & 1) == 1)
                .withProperty(FACING, EnumFacing.byHorizontalIndex(meta >> 1));
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer) {
        final IBlockState state = getDefaultState().withProperty(FACING, placer.isSneaking()
                ? placer.getHorizontalFacing() : placer.getHorizontalFacing().getOpposite());

        return state.withProperty(BOTTOM, shouldBeBottom(state, worldIn.getBlockState(pos.down())));
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
        return face.getAxis().isVertical() || state.getValue(FACING) == face ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY + I18n.format("tile.tconstruct.seared.tooltip"));
    }

    @Override
    public void neighborChanged(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
        if(pos.down().equals(fromPos)) {
            final boolean isBottom = state.getValue(BOTTOM);
            if(isBottom != shouldBeBottom(state, worldIn.getBlockState(fromPos)))
                worldIn.setBlockState(pos, state.withProperty(BOTTOM, !isBottom));
        }
    }

    @Override
    public int damageDropped(@Nonnull IBlockState state) { return 0; }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state) { return false; }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

    @Override
    public boolean isLadder(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityLivingBase entity) {
        return true;
    }

    @Override
    public void addCollisionBoxToList(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        getCollisionBoxList(state).forEach(aabb -> addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb));
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        final List<RayTraceResult> list = getCollisionBoxList(blockState).stream()
                .map(aabb -> rayTrace(pos, start, end, aabb))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if(list.isEmpty()) return null;
        RayTraceResult furthest = null;
        double dist = 0;

        for(RayTraceResult trace : list) {
            final double newDist = trace.hitVec.squareDistanceTo(end);
            if(newDist > dist) {
                furthest = trace;
                dist = newDist;
            }
        }

        return furthest;
    }

    @Nonnull
    public List<AxisAlignedBB> getCollisionBoxList(@Nonnull IBlockState state) {
        final List<AxisAlignedBB> collisions = new ArrayList<>();
        switch(state.getValue(FACING)) {
            case NORTH:
                collisions.addAll(NORTH_BOX);
                break;
            case SOUTH:
                collisions.addAll(SOUTH_BOX);
                break;
            case EAST:
                collisions.addAll(EAST_BOX);
                break;
            case WEST:
                collisions.addAll(WEST_BOX);
        }

        if(state.getValue(BOTTOM)) collisions.add(BOTTOM_BOX);
        return collisions;
    }

    public boolean shouldBeBottom(@Nonnull IBlockState state, @Nonnull IBlockState down) {
        return !(down.getBlock() instanceof BlockSearedLadder) || down.getValue(FACING) != state.getValue(FACING);
    }

    @Nonnull
    static AxisAlignedBB box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
    }

    //needed for BlockEnumSmeltery
    public enum Type implements IStringSerializable, EnumBlock.IEnumMeta {
        NORMAL;

        @Nonnull
        @Override
        public String getName() { return "normal"; }

        @Override
        public int getMeta() { return 0; }
    }
}
