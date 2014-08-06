package deltageek.eu4.util;

import java.util.function.Consumer;

public class DeltaUtilities {
    public static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedConsumer<T> {
        void accept(T t) throws Exception;
    }}
