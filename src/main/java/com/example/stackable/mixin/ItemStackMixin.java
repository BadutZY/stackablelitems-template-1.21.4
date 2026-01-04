package com.example.stackable.mixin;

import com.example.stackable.StackableTotem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    private void makeItemsStackableInStack(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack)(Object)this;

        // Check if this item is in our stackable items map
        if (StackableTotem.getStackableItems().containsKey(stack.getItem())) {
            int newMaxCount = StackableTotem.getStackableItems().get(stack.getItem());
            cir.setReturnValue(newMaxCount);
        }
    }

    @Inject(method = "isStackable", at = @At("HEAD"), cancellable = true)
    private void makeItemsStackableCheck(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object)this;

        // Check if this item is in our stackable items map
        if (StackableTotem.getStackableItems().containsKey(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }

    // Fix untuk fish bucket stacking - ignore NBT/component differences
    @Inject(method = "areItemsAndComponentsEqual", at = @At("HEAD"), cancellable = true)
    private static void allowFishBucketComponentsEqual(ItemStack stack, ItemStack otherStack,
                                                       CallbackInfoReturnable<Boolean> cir) {
        // Jika kedua item adalah fish bucket yang stackable, izinkan combine
        if (stack.getItem() == otherStack.getItem() &&
                isFishBucketStatic(stack) &&
                StackableTotem.getStackableItems().containsKey(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }

    private static boolean isFishBucketStatic(ItemStack stack) {
        var item = stack.getItem();
        return item == Items.PUFFERFISH_BUCKET ||
                item == Items.AXOLOTL_BUCKET ||
                item == Items.COD_BUCKET ||
                item == Items.SALMON_BUCKET ||
                item == Items.TADPOLE_BUCKET ||
                item == Items.TROPICAL_FISH_BUCKET;
    }
}