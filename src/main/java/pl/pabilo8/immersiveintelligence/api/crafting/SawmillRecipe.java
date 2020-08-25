package pl.pabilo8.immersiveintelligence.api.crafting;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.util.ListUtils;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import pl.pabilo8.immersiveintelligence.api.utils.ISawblade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author Pabilo8
 * @since 14-04-2020
 */
public class SawmillRecipe extends MultiblockRecipe
{
	public static float torqueModifier = 1.0F;
	public static float timeModifier = 1.0F;
	//The tier of the saw required, 1 for cutting wood (bronze), 2 iron, 3 steel, 4 tungsten
	public final IngredientStack itemInput;
	public final ItemStack itemOutput, itemSecondaryOutput;

	public static HashMap<String, ISawblade> toolMap = new HashMap<>();
	public static ArrayList<SawmillRecipe> recipeList = new ArrayList();
	int totalProcessTime;

	public int getTorque()
	{
		return torque;
	}

	public int getHardness()
	{
		return hardness;
	}

	int torque;
	int hardness;
	int dustColor;

	public SawmillRecipe(ItemStack itemOutput, Object itemInput, ItemStack itemSecondaryOutput, int torque, int time, int hardness, int dustColor)
	{
		this.itemOutput = itemOutput;
		this.itemSecondaryOutput = itemSecondaryOutput;
		this.itemInput = ApiUtils.createIngredientStack(itemInput);
		this.torque = (int)Math.floor(torque*torqueModifier);
		this.totalProcessTime = (int)Math.floor(time*timeModifier);
		this.hardness = hardness;

		this.inputList = Lists.newArrayList(this.itemInput);
		this.outputList = ListUtils.fromItem(this.itemOutput);
		this.dustColor = dustColor;
	}

	public static SawmillRecipe addRecipe(ItemStack itemOutput, IngredientStack itemInput, ItemStack itemSecondaryOutput, int torque, int time, int hardness)
	{
		return addRecipe(itemOutput, itemInput, itemSecondaryOutput, torque, time, hardness, 0xffffff);
	}

	public static SawmillRecipe addRecipe(ItemStack itemOutput, IngredientStack itemInput, ItemStack itemSecondaryOutput, int torque, int time, int hardness, int dustColor)
	{
		SawmillRecipe r = new SawmillRecipe(itemOutput, itemInput, itemSecondaryOutput, torque, time, hardness, dustColor);
		recipeList.add(r);
		return r;
	}

	public static List<SawmillRecipe> removeRecipesForOutput(ItemStack stack)
	{
		List<SawmillRecipe> list = new ArrayList();
		Iterator<SawmillRecipe> it = recipeList.iterator();
		while(it.hasNext())
		{
			SawmillRecipe ir = it.next();
			if(OreDictionary.itemMatches(ir.itemOutput, stack, true))
			{
				list.add(ir);
				it.remove();
			}
		}
		return list;
	}

	public static SawmillRecipe findRecipe(ItemStack item_input)
	{
		for(SawmillRecipe recipe : recipeList)
		{
			if(recipe.itemInput.matchesItemStackIgnoringSize(item_input))
			{
				return recipe;
			}
		}
		return null;
	}

	public static void registerSawblade(String name, ISawblade blade)
	{
		toolMap.putIfAbsent(name, blade);
	}

	public static boolean isValidRecipeInput(ItemStack stack)
	{
		for(SawmillRecipe recipe : recipeList)
			if(recipe.itemInput.matchesItemStack(stack))
				return true;
		return false;
	}

	@Override
	public int getMultipleProcessTicks()
	{
		return 0;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("item_input", itemInput.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	public static SawmillRecipe loadFromNBT(NBTTagCompound nbt)
	{
		IngredientStack item_input = IngredientStack.readFromNBT(nbt.getCompoundTag("item_input"));

		return findRecipe(item_input.stack);
	}

	public int getTotalProcessTime()
	{
		return this.totalProcessTime;
	}

	public int getTotalProcessTorque()
	{
		return this.torque;
	}

	@Override
	public void setupJEI()
	{
		super.setupJEI();
		this.jeiTotalItemOutputList.add(this.itemSecondaryOutput);
	}
}