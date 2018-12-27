package com.rumaruka.tb.network.proxy;

import DummyCore.Client.GuiCommon;
import DummyCore.Utils.ASMManager;





import com.rumaruka.tb.client.render.HerobrinesScytheMH;
import com.rumaruka.tb.client.render.RenderCampfire;
import com.rumaruka.tb.client.render.RenderOverchanter;

import com.rumaruka.tb.common.tiles.TileCampfire;
import com.rumaruka.tb.common.tiles.TileOverchanter;
import com.rumaruka.tb.core.TBCore;
import com.rumaruka.tb.init.TBBlocks;
import com.rumaruka.tb.init.TBItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.fx.beams.FXArc;
import java.lang.reflect.Field;
import java.util.Arrays;

public class TBClient extends TBServer {


    @Override
    public void preInit(FMLPreInitializationEvent e) {





        ModelLoader.setCustomStateMapper(TBBlocks.pyrofluid,new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());
    }



    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        OBJLoader.INSTANCE.addDomain(TBCore.modid);
    }


    @Override
    public void Renders() {
        TBBlocks.Render();
        TBItems.Renders();
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    public void registerRenderInformation()
    {
       ClientRegistry.bindTileEntitySpecialRenderer(TileOverchanter.class, new RenderOverchanter());
       ClientRegistry.bindTileEntitySpecialRenderer(TileCampfire.class, new RenderCampfire());
       MinecraftForge.EVENT_BUS.register(new HerobrinesScytheMH());




      /*  RenderAccessLibrary.registerItemRenderingHandler(TBItems.ukulele, new UkuleleRenderer());
        RenderAccessLibrary.registerItemRenderingHandler(TBItems.revolver, new RenderRevolver());

        RenderAccessLibrary.registerItemRenderingHandler(TBItems.nodeFoci, new NodeFociRenderer());
        RenderAccessLibrary.registerItemRenderingHandler(TBItems.spawnerCompass, new SpawnerCompassRenderer());


       */

    }
    @Override
    public void lightning(World world, double sx, double sy, double sz, double ex, double ey, double ez, int dur, float curve, int speed, int type)
    {
        FXArc efa = new FXArc(world, sx, sy, sz, ex, ey, ez, 0.1F, 0.0F, 0.1F, 0.1F);
        try
        {
            Field f = FXDispatcher.class.getDeclaredField(ASMManager.chooseByEnvironment("particleMaxAge", "field_70547_e"));
            f.setAccessible(true);
            f.setInt(efa, dur);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(efa);
    }


    @Override
    public boolean fancyGraphicsEnable() {
        return Minecraft.getMinecraft().gameSettings.fancyGraphics;
    }
}
