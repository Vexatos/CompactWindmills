/*******************************************************************************
 * Copyright (c) 2013 Aroma1997.
 * All rights reserved. This program and other files related to this program are
 * licensed with a extended GNU General Public License v. 3
 * License informations are at:
 * https://github.com/Aroma1997/CompactWindmills/blob/master/license.txt
 ******************************************************************************/

package aroma1997.compactwindmills;


import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import aroma1997.compactwindmills.helpers.LogHelper;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author Aroma1997
 * 
 */
public class BlockCompactWindmill extends BlockContainer {
	
	public static final int[][] sideAndMetaToTextureNumber = { {0, 0, 0, 0, 0, 0}, {1, 2, 1, 1, 1, 1}, {3, 3, 2, 3, 3, 3}, {3, 3, 3, 2, 3, 3}, {3, 3, 3, 3, 2, 3}, {3, 3, 3, 3, 3, 2}};
	
	@SideOnly(Side.CLIENT)
	private Icon[][] textures;
	
	public BlockCompactWindmill(int id) {
		super(id, Material.iron);
		setUnlocalizedName("compactWindmill");
		setHardness(2.0F);
		setCreativeTab(CompactWindmills.creativeTabCompactWindmills);
		
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return WindType.makeTileEntity(metadata);
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
		int facing = getFacing(iBlockAccess, x, y, z);
		int meta = iBlockAccess.getBlockMetadata(x, y, z);
		int textureIndex = sideAndMetaToTextureNumber[side][facing];
		
		if (meta >= textures.length) {
			return null;
		}
		try {
			return textures[meta][textureIndex];
		}
		catch (Exception e) {
			LogHelper.log(Level.WARNING, "Failed to get texture at: x=" + x + "; y=" + y + "; z="
				+ z + "; facing=" + facing + "; side=" + side + "; meta=" + meta + ";");
		}
		
		return null;
	}
	
	public int getFacing(IBlockAccess iBlockAccess, int x, int y, int z) {
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
		
		if (tileEntity instanceof TileEntityWindmill) {
			return ((TileEntityWindmill) tileEntity).getFacing();
		}
		
		LogHelper.log(Level.WARNING, "Failed to get Facing at: x=" + x + "; y=" + y + "; z=" + z
			+ ";");
		
		return 4;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta) {
		if (meta > WindType.values().length) {
			return null;
		}
		int facing = 4;
		int textureIndex = sideAndMetaToTextureNumber[side][facing];
		try {
			return textures[meta][textureIndex];
		}
		catch (Exception e) {
			LogHelper.log(Level.WARNING, "Failed to get texture at: side=" + side + "; meta="
				+ meta + ";");
		}
		
		return null;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List itemList) {
		for (WindType type : WindType.values()) {
			itemList.add(new ItemStack(this, 1, type.ordinal()));
		}
	}
	
	@Override
	public int idDropped(int meta, Random random, int id) {
		return blockID;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer thePlayer,
		int s, float f1, float f2, float f3) {
		if (thePlayer.isSneaking()) {
			return false;
		}
		
		if (world.isRemote) {
			return true;
		}
		
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity != null && tileEntity instanceof TileEntityWindmill) {
			TileEntityWindmill tileEntityCW = (TileEntityWindmill) tileEntity;
			thePlayer.openGui(CompactWindmills.instance, tileEntityCW.getType()
				.ordinal(), world, x, y, z);
		}
		return true;
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		textures = new Icon[WindType.values().length][4];
		for (WindType type : WindType.values()) {
			for (int side = 0; side < 4; side++) {
				String sideName = side == 0 ? "bottom" : side == 1 ? "top"
					: side == 2 ? "front" : "side";
				textures[type.ordinal()][side] = par1IconRegister
					.registerIcon(Reference.ModID + ":" + type.name() + "_"
						+ sideName);
			}
		}
		
	}
	
	@Override
	public void breakBlock(World world, int par2, int par3, int par4, int par5, int par6)
	{
		super.breakBlock(world, par2, par3, par4, par5, par6);
	}
}
