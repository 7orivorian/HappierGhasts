package dev.tori.happierghasts.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.Contract;

import java.util.function.IntFunction;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public enum PropellerMaterial implements StringIdentifiable {
    COPPER(0, "copper"),
    IRON(1, "iron"),
    DIAMOND(2, "diamond"),
    NETHERITE(3, "netherite");

    private static final IntFunction<PropellerMaterial> INDEX_MAPPER = ValueLists.createIndexToValueFunction(
            PropellerMaterial::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
    );
    public static final StringIdentifiable.EnumCodec<PropellerMaterial> CODEC = StringIdentifiable.createCodec(PropellerMaterial::values);
    public static final PacketCodec<ByteBuf, PropellerMaterial> PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, PropellerMaterial::getIndex);

    private final int index;
    private final String id;

    @Contract(pure = true)
    PropellerMaterial(int index, String id) {
        this.index = index;
        this.id = id;
    }

    @Contract(pure = true)
    public int getIndex() {
        return this.index;
    }

    @Contract(pure = true)
    public String getId() {
        return this.id;
    }

    @Contract(pure = true)
    @Override
    public String asString() {
        return this.id;
    }
}