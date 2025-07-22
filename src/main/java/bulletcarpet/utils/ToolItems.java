package bulletcarpet.utils;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ToolItems {
    private ToolItems() {
    }

    public static final Item PICKAXE = register("pickaxes", new Item(new Item.Settings()));
    public static final Item SHOVEL = register("shovels", new Item(new Item.Settings()));
    public static final Item AXE = register("axes", new Item(new Item.Settings()));
    public static final Item HOE = register("hoes", new Item(new Item.Settings()));
    public static final Item ALL_TOOLS = register("all_tools", new Item(new Item.Settings()));

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, new Identifier(ModUtils.NAMESPACE, path), item);
    }

    public static void initialize() {
    }
}
