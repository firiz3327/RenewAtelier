package net.firiz.renewatelier.item.json.itemeffect;

import java.util.Objects;

@FunctionalInterface
interface IEffectConsumer<T> extends IItemEffect {

    void accept(T value);

    default IEffectConsumer<T> andThen(IEffectConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }

}
