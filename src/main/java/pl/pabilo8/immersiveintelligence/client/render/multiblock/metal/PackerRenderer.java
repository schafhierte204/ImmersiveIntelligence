package pl.pabilo8.immersiveintelligence.client.render.multiblock.metal;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.client.ClientUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;
import pl.pabilo8.immersiveintelligence.client.model.multiblock.metal.ModelPacker;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.first.TileEntityPacker;

/**
 * Created by Pabilo8 on 21-06-2019.
 */
public class PackerRenderer extends TileEntitySpecialRenderer<TileEntityPacker>
{
	static RenderItem renderItem = ClientUtils.mc().getRenderItem();
	private static ModelPacker model = new ModelPacker();

	private static String texture = ImmersiveIntelligence.MODID+":textures/blocks/multiblock/packer.png";

	@Override
	public void render(TileEntityPacker te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		if(te!=null&&!te.isDummy())
		{
			ClientUtils.bindTexture(texture);
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)x+1, (float)y-2, (float)z+2);
			GlStateManager.rotate(180F, 0F, 1F, 0F);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

			if(te.hasWorld())
			{
				GlStateManager.translate(0f, 1f, 1f);
				GlStateManager.rotate(90F, 0F, 1F, 0F);
			}

			model.getBlockRotation(te.facing, model);
			model.render();


			ClientUtils.bindTexture("textures/atlas/blocks.png");
			GlStateManager.pushMatrix();
			GlStateManager.translate(1f, 1f, -2f);
			ImmersiveEngineering.proxy.drawConveyorInGui("immersiveengineering:conveyor", EnumFacing.NORTH);
			GlStateManager.translate(0, 0, -2);
			ImmersiveEngineering.proxy.drawConveyorInGui("immersiveengineering:conveyor", EnumFacing.NORTH);
			GlStateManager.translate(0, 0, -1);
			ImmersiveEngineering.proxy.drawConveyorInGui("immersiveengineering:conveyor", EnumFacing.NORTH);
			GlStateManager.translate(0, 0, -1);
			ImmersiveEngineering.proxy.drawConveyorInGui("immersiveengineering:conveyor", EnumFacing.NORTH);
			GlStateManager.popMatrix();


			GlStateManager.pushMatrix();
			GlStateManager.translate(0f, 0f, -3f);
			ImmersiveEngineering.proxy.drawConveyorInGui("immersiveengineering:conveyor", EnumFacing.WEST);
			GlStateManager.translate(1, 0, 0);
			ImmersiveEngineering.proxy.drawConveyorInGui("immersiveengineering:conveyor", EnumFacing.WEST);
			GlStateManager.translate(1, 0, 0);
			ImmersiveEngineering.proxy.drawConveyorInGui("immersiveengineering:covered", EnumFacing.WEST);
			GlStateManager.popMatrix();

			GlStateManager.translate(-0.5f, 0.25f, 1f);
			GlStateManager.translate(1f, 0f, -(float)te.processTime/(float)te.processTimeMax);

			renderItem.renderItem(te.inventory.get(0), TransformType.GROUND);


			GlStateManager.popMatrix();

			//ImmersiveIntelligence.logger.info(ImmersiveEngineering.proxy.drawConveyorInGui("immersiveengineering:conveyor", te.facing));
		}
	}
}
