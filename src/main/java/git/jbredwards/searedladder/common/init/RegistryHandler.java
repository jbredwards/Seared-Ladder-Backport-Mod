package git.jbredwards.searedladder.common.init;

import git.jbredwards.searedladder.Constants;
import git.jbredwards.searedladder.common.block.BlockSearedLadder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 *
 * @author jbred
 *
 */
@Mod.EventBusSubscriber(modid = Constants.MODID)
public final class RegistryHandler
{
    @SubscribeEvent
    static void registerBlock(@Nonnull RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModBlocks.SEARED_LADDER.setRegistryName(Constants.MODID, "seared_ladder")
                .setTranslationKey("searedladder.seared_ladder"));
    }

    @SubscribeEvent
    static void registerItem(@Nonnull RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModItems.SEARED_LADDER.setRegistryName(Constants.MODID, "seared_ladder")
                .setTranslationKey("searedladder.seared_ladder"));

        OreDictionary.registerOre("blockSeared", ModItems.SEARED_LADDER);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    static void registerModels(@Nonnull ModelRegistryEvent event) {
        ModelLoader.setCustomStateMapper(ModBlocks.SEARED_LADDER,
                new StateMap.Builder().ignore(BlockSearedLadder.TYPE).build());

        ModelLoader.setCustomModelResourceLocation(ModItems.SEARED_LADDER, 0, new ModelResourceLocation(
                Objects.requireNonNull(ModItems.SEARED_LADDER.getRegistryName()), "inventory"));
    }
}
