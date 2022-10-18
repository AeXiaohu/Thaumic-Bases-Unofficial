package com.rumaruka.thaumicbases.common.item;

import com.rumaruka.thaumicbases.api.ITobacco;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;

import javax.annotation.Nullable;

public class ItemSmokingPipe extends Item {
    public boolean isSilverwood;


    public ItemSmokingPipe(boolean silverwood) {

        super();
        isSilverwood = silverwood;

        this.setFull3D();
        this.setMaxStackSize(1);

        this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null) {
                    return 0.0F;
                } else {
                    return !(entityIn.getActiveItemStack().getItem() instanceof ItemSmokingPipe) ? 0.0F : (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
                }
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
    }


    public ItemStack findTobacco(EntityPlayer smoker) {
        if (this.isTobacco(smoker.getHeldItem(EnumHand.OFF_HAND))) {
            return smoker.getHeldItem(EnumHand.OFF_HAND);
        } else if (this.isTobacco(smoker.getHeldItem(EnumHand.MAIN_HAND))) {
            return smoker.getHeldItem(EnumHand.MAIN_HAND);
        } else {
            for (int i = 0; i < smoker.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = smoker.inventory.getStackInSlot(i);

                if (this.isTobacco(itemstack)) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }


    protected boolean isTobacco(ItemStack stack) {
        return stack.getItem() instanceof TBTobacco;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        boolean flag = !this.findTobacco(playerIn).isEmpty();

        if (!flag) {
            return flag ? new ActionResult<>(EnumActionResult.PASS, itemstack) : new ActionResult<>(EnumActionResult.FAIL, itemstack);
        } else {
            playerIn.setActiveHand(handIn);
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }

    /**
     * AeXiaohu modified
     * 修复烟斗对准方块才能使用，只需烟斗按住右键持续 20 tick 再松开即可使用
     */
    public void onPlayerStoppedUsing(ItemStack itemstack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if(getMaxItemUseDuration(itemstack) - timeLeft < 20) return;
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer playerIn = (EntityPlayer)entityLiving;
            ItemStack tobacco = findTobacco(playerIn);
            if (tobacco.isEmpty())
                return;
            ITobacco t = (ITobacco) tobacco.getItem();
            t.performTobaccoEffect(playerIn, tobacco, isSilverwood);
            for (int i = 0; i < playerIn.inventory.getSizeInventory(); ++i) {
                ItemStack stk = playerIn.inventory.getStackInSlot(i);
                if (stk != ItemStack.EMPTY && !new ItemStack(stk.getItem()).equals(ItemStack.EMPTY) && stk.getItem() instanceof ITobacco) {
                    getMaxItemUseDuration(stk);
                    playerIn.inventory.decrStackSize(i, 1);
                    break;
                }
            }
            Vec3d look = Vec3d.fromPitchYawVector(new Vec2f(playerIn.rotationPitch, playerIn.rotationYaw));
            for (int i = 0; i < 100; ++i) {
                double x = playerIn.posX + look.x / 5;
                double y = playerIn.posY + playerIn.getEyeHeight() + look.y / 5;
                double z = playerIn.posZ + look.z / 5;
                playerIn.world.spawnParticle(isSilverwood ? EnumParticleTypes.EXPLOSION_NORMAL : EnumParticleTypes.SMOKE_NORMAL, x, y, z, look.x / 10, look.y / 10, look.z / 10);
            }
        }
    } // End of modification

    /**
     * AeXiaohu modified
     * 烟斗按住右键完成使用即可生效
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer playerIn = (EntityPlayer)entityLiving;
            ItemStack tobacco = findTobacco(playerIn);
            if (tobacco.isEmpty())
                return stack;
            ITobacco t = (ITobacco) tobacco.getItem();
            t.performTobaccoEffect(playerIn, tobacco, isSilverwood);
            for (int i = 0; i < playerIn.inventory.getSizeInventory(); ++i) {
                ItemStack stk = playerIn.inventory.getStackInSlot(i);
                if (stk != ItemStack.EMPTY && !new ItemStack(stk.getItem()).equals(ItemStack.EMPTY) && stk.getItem() instanceof ITobacco) {
                    getMaxItemUseDuration(stk);
                    playerIn.inventory.decrStackSize(i, 1);
                    break;
                }
            }
            Vec3d look = Vec3d.fromPitchYawVector(new Vec2f(playerIn.rotationPitch, playerIn.rotationYaw));
            for (int i = 0; i < 100; ++i) {
                double x = playerIn.posX + look.x / 5;
                double y = playerIn.posY + playerIn.getEyeHeight() + look.y / 5;
                double z = playerIn.posZ + look.z / 5;
                playerIn.world.spawnParticle(isSilverwood ? EnumParticleTypes.EXPLOSION_NORMAL : EnumParticleTypes.SMOKE_NORMAL, x, y, z, look.x / 10, look.y / 10, look.z / 10);
            }
        }
        return stack;
    } // End of modification

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 64;
    }


    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {

        Vec3d look = Vec3d.fromPitchYawVector(new Vec2f(player.rotationPitch, player.rotationYaw));

        double x = player.posX + look.x / 5;
        double y = player.posY + player.getEyeHeight() + look.y / 5;
        double z = player.posZ + look.z / 5;
        if (count < 32)
            player.world.spawnParticle(isSilverwood ? EnumParticleTypes.EXPLOSION_NORMAL : EnumParticleTypes.SMOKE_NORMAL, x, y, z, look.x / 10, look.y / 10, look.z / 10);
    }


    /**
     * AeXiaohu modified
     * 修复烟斗对准方块才能使用-移除对准方块的方法
     */
//    @Override
//    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
//        ItemStack tobacco = findTobacco(player);
//        if (tobacco.isEmpty())
//            return EnumActionResult.FAIL;
//        ITobacco t = (ITobacco) tobacco.getItem();
//        t.performTobaccoEffect(player, tobacco, isSilverwood);
//        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
//            ItemStack stk = player.inventory.getStackInSlot(i);
//            if (stk != ItemStack.EMPTY && !new ItemStack(stk.getItem()).equals(ItemStack.EMPTY) && stk.getItem() instanceof ITobacco) {
//                getMaxItemUseDuration(stk);
//                player.inventory.decrStackSize(i, 1);
//                break;
//            }
//        }

//        Vec3d look = Vec3d.fromPitchYawVector(new Vec2f(player.rotationPitch, player.rotationYaw));
//        for (int i = 0; i < 100; ++i) {
//            double x = player.posX + look.x / 5;
//            double y = player.posY + player.getEyeHeight() + look.y / 5;
//            double z = player.posZ + look.z / 5;
//            player.world.spawnParticle(isSilverwood ? EnumParticleTypes.EXPLOSION_NORMAL : EnumParticleTypes.SMOKE_NORMAL, x, y, z, look.x / 10, look.y / 10, look.z / 10);
//        }

//        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
//    }


}


