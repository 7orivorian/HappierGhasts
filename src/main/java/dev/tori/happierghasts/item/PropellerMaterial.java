package dev.tori.happierghasts.item;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Contract;

import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/7orivorian">7orivorian</a>
 * @since 1.0.0
 */
public enum PropellerMaterial implements StringRepresentable {
    COPPER(0, "copper"),
    IRON(1, "iron"),
    DIAMOND(2, "diamond"),
    NETHERITE(3, "netherite");

    private static final IntFunction<PropellerMaterial> INDEX_MAPPER = ByIdMap.continuous(
            PropellerMaterial::getIndex, values(), ByIdMap.OutOfBoundsStrategy.ZERO
    );
    public static final EnumCodec<PropellerMaterial> CODEC = StringRepresentable.fromEnum(PropellerMaterial::values);
    public static final StreamCodec<ByteBuf, PropellerMaterial> PACKET_CODEC = ByteBufCodecs.idMapper(INDEX_MAPPER, PropellerMaterial::getIndex);

    private final int index;
    private final String id;

    PropellerMaterial(int index, String id) {
        this.index = index;
        this.id = id;
    }

    public int getIndex() {
        return this.index;
    }

    public String getId() {
        return this.id;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return this.id;
    }
}