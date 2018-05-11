package sonar.calculator.mod.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.CalculatorConstants;
import sonar.core.integration.jei.IJEIHandler;
import sonar.core.integration.jei.JEICategoryV2;
import sonar.core.integration.jei.JEIHelper.RecipeMapper;
import sonar.core.recipes.RecipeObjectType;

import javax.annotation.Nonnull;

public class SingleProcessCategory extends JEICategoryV2 {

	private final IDrawable background;
	protected final IDrawableAnimated arrow;

	public SingleProcessCategory(IGuiHelper guiHelper, IJEIHandler handler) {
		super(handler);
		ResourceLocation location = new ResourceLocation("calculator", "textures/gui/" + handler.getTextureName() + ".png");
		background = guiHelper.createDrawable(location, 48, 19, 80, 27);
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 177, 10, 22, 15);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 100, IDrawableAnimated.StartDirection.LEFT, false);
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
	public void drawExtras(Minecraft minecraft) {
		arrow.draw(minecraft, 29, 5);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		RecipeMapper mapper = new RecipeMapper();
		mapper.map(RecipeObjectType.INPUT, 0, 0, 4, 4);
		mapper.map(RecipeObjectType.OUTPUT, 0, 2, 58, 4);
		mapper.mapTo(recipeLayout.getItemStacks(), ingredients);
	}
}