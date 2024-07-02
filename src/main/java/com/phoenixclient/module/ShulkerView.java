package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.gui.hud.element.InventoryWindow;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.ArrayList;
import java.util.List;

import static com.phoenixclient.PhoenixClient.MC;

public class ShulkerView extends Module {

    private final SettingGUI<Boolean> chestView = new SettingGUI<>(
            this,
            "Chest Expand",
            "Expands all shulkers around the chest GUI",
            true
    );

    private final SettingGUI<Boolean> tooltipView = new SettingGUI<>(
            this,
            "Tooltips",
            "Draws the shulker inventory as a tooltip",
            true
    );

    private final SettingGUI<String> chestViewOrient = new SettingGUI<>(
            this,
            "Chest Orientation",
            "",
            "Vertical")
            .setModeData("Vertical", "Horizontal").setDependency(chestView, true);

    public ShulkerView() {
        super("ShulkerView", "Renders shulker boxes", Category.RENDER, false, -1);
        addEventActions(render);
        addSettings(chestView, tooltipView, chestViewOrient);
    }

    private final EventAction render = new EventAction(Event.EVENT_RENDER_SCREEN, () -> {
        //TODO: Add item tooltip hovering
        if (MC.screen instanceof ContainerScreen) {

            if (chestView.get()) {
                GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());

                float scale = .5f;
                graphics.pose().scale(scale, scale, 1);
                
                List<ItemStack> containerItems = MC.player.containerMenu.getItems();
                Vector pos = new Vector(2, 2);

                for (ItemStack stack : containerItems) {

                    if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock) {
                        graphics.setColor(1, 1, 1, .5f);
                        InventoryWindow.renderChestInventory(graphics, stack.getHoverName().getString(), pos);
                        InventoryWindow.renderInventoryItems(graphics, getShulkerItemInventory(stack), 0, 26, pos.getAdded(0, 126));

                        switch (chestViewOrient.get()) {
                            case "Vertical" -> {
                                pos.add(new Vector(0, 81));
                                if ((pos.getY() + 80) * scale > MC.getWindow().getGuiScaledHeight())
                                    pos.setY(2).add(new Vector(176, 0));
                            }

                            case "Horizontal" -> {
                                pos.add(new Vector(178, 0));
                                if ((pos.getX() + 176) * scale > MC.getWindow().getGuiScaledWidth())
                                    pos.setX(2).add(new Vector(0, 82));
                            }
                        }
                    }
                }
            }

            if (tooltipView.get()) {

            }
        }
    });

    @Override
    public void onEnabled() {
    }

    @Override
    public void onDisabled() {
    }

    public static List<ItemStack> getShulkerItemInventory(ItemStack stack) {
        HolderLookup.Provider provider = MC.level.registryAccess();
        CompoundTag compound = (CompoundTag) stack.save(provider);

        ListTag listTag = (ListTag) compound.getCompound("components").get("minecraft:container");
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag itemTag = listTag.getCompound(i).getCompound("item");
            list.add(ItemStack.parseOptional(provider, itemTag));
        }

        return list;
    }
}
