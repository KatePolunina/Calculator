package sonar.calculator.mod.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import sonar.calculator.mod.CalculatorConstants;
import sonar.core.integration.jei.IJEIHandlerV3;
import sonar.core.integration.jei.JEICategoryV3;

import javax.annotation.Nonnull;

public class AnalysingCategory extends JEICategoryV3 {

	private final IDrawable background;

	public AnalysingCategory(IGuiHelper guiHelper, IJEIHandlerV3 handler) {
		super(handler);
		ResourceLocation location = new ResourceLocation("calculator", "textures/gui/" + handler.getTextureName() + ".png");
		background = guiHelper.createDrawable(location, 75, 29, 26, 36);
	}

    @Nonnull
    @Override
    public String getModName() {
		return CalculatorConstants.NAME;
    }

	@Nonnull
    @Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0, false, 4, 4);
		stacks.set(0, ingredients.getOutputs(ItemStack.class).get(0));
	}
}