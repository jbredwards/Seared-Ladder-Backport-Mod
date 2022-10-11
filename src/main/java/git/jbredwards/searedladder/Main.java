package git.jbredwards.searedladder;

import com.google.common.collect.ImmutableSet;
import git.jbredwards.searedladder.common.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nonnull;

import static git.jbredwards.searedladder.Constants.*;

/**
 *
 * @author jbred
 *
 */
@Mod(modid = MODID, name = NAME, version = VERSION, dependencies = DEPENDENCIES)
public final class Main
{
    @Mod.EventHandler
    static void construct(@Nonnull FMLConstructionEvent event) {
        ForgeModContainer.fullBoundingBoxLadders = true;
    }

    @Mod.EventHandler
    static void init(@Nonnull FMLInitializationEvent event) {
        TinkerSmeltery.validSmelteryBlocks = ImmutableSet.<Block>builder()
                .addAll(TinkerSmeltery.validSmelteryBlocks)
                .add(ModBlocks.SEARED_LADDER)
                .build();
    }
}
