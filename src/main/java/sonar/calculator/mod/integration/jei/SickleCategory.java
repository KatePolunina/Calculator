package sonar.calculator.mod.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.ResourceLocation;
import sonar.calculator.mod.CalculatorConstants;
import sonar.core.integration.jei.IJEIHandlerV3;
import sonar.core.integration.jei.JEICategoryV3;
import sonar.core.integration.jei.JEIHelper.RecipeMapper;
import sonar.core.recipes.RecipeObjectType;

import javax.annotation.Nonnull;

public class SickleCategory extends JEICategoryV3 {

	private final IDrawable background;

	public SickleCategory(IGuiHelper guiHelper, IJEIHandlerV3 handler) {
		super(handler);
		ResourceLocation location = new ResourceLocation("calculator", "textures/gui/" + handler.getTextureName() + ".png");
		background = guiHelper.createDrawable(location, 52, 23, 64, 36);
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
		RecipeMapper mapper = new RecipeMapper();
		mapper.map(RecipeObjectType.INPUT, 0, 0, 0, 10);
		mapper.map(RecipeObjectType.OUTPUT, 0, 1, 46, 0);
		mapper.map(RecipeObjectType.OUTPUT, 1, 2, 46, 18);
		mapper.mapTo(recipeLayout.getItemStacks(), ingredients);
	}
}