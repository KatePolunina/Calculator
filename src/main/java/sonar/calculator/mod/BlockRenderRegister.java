package sonar.calculator.mod;

import java.util.ArrayList;
import java.util.List;

import sonar.core.common.block.properties.IMetaRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class BlockRenderRegister {
	public static void register() {
		/*registerBlock(Calculator.reinforcedStoneBlock); registerBlock(Calculator.reinforcedStoneStairs); registerBlock(Calculator.reinforcedStoneBrick); registerBlock(Calculator.reinforcedStoneBrickStairs); registerBlock(Calculator.reinforcedDirtBlock); registerBlock(Calculator.reinforcedDirtStairs); registerBlock(Calculator.reinforcedDirtBrick); registerBlock(Calculator.reinforcedDirtBrickStairs); registerBlock(Calculator.stableStone); registerBlock(Calculator.powerCube); registerBlock(Calculator.advancedPowerCube); registerBlock(Calculator.atomicCalculator); registerBlock(Calculator.dynamicCalculator); registerBlock(Calculator.reinforcedFurnace); registerBlock(Calculator.stoneSeparator); registerBlock(Calculator.algorithmSeparator); registerBlock(Calculator.hungerProcessor); registerBlock(Calculator.healthProcessor); registerBlock(Calculator.basicGreenhouse); registerBlock(Calculator.advancedGreenhouse); registerBlock(Calculator.flawlessGreenhouse); registerBlock(Calculator.CO2Generator); */
		for (Block block : CalculatorBlocks.registeredBlocks) {
			Item item = Item.getItemFromBlock(block);
			if (item.getHasSubtypes()) {				
				List<ItemStack> stacks = new ArrayList();
				item.getSubItems(item, Calculator.Calculator, stacks);
				for (ItemStack stack : stacks) {
					String variant = "variant=meta" + stack.getItemDamage();
					if(block instanceof IMetaRenderer){
						IMetaRenderer meta = (IMetaRenderer) block;
						variant= "variant=" + meta.getVariant(stack.getItemDamage()).getName();
					}
					ModelLoader.setCustomModelResourceLocation(item, stack.getItemDamage(), new ModelResourceLocation(Calculator.modid + ":" + item.getUnlocalizedName().substring(5), variant));
				}
			} else {
				registerBlock(block);
			}
		}
	}

	public static void registerBlock(Block block) {
		if (block != null) {
			Item item = Item.getItemFromBlock(block);
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Calculator.modid + ":" + item.getUnlocalizedName().substring(5), "inventory"));
		}
	}
}
