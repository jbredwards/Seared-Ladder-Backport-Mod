package git.jbredwards.searedladder.common.init;

import git.jbredwards.searedladder.common.block.BlockSearedLadder;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class ModBlocks
{
    @Nonnull
    public static final BlockSearedLadder SEARED_LADDER = new BlockSearedLadder(Material.ROCK);
}
