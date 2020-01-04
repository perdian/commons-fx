package de.perdian.commons.fx.persistence;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

class PersistenceEnabledDefaultConstructorSupplier<T> implements Supplier<T> {

    private Constructor<T> constructor = null;

    PersistenceEnabledDefaultConstructorSupplier(Class<T> type) {
        try {
            this.setConstructor(type.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No default constructor found for class: " + type.getName());
        }
    }

    @Override
    public T get() {
        try {
            return this.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot instantiate new record", e);
        }
    }

    private Constructor<T> getConstructor() {
        return this.constructor;
    }
    private void setConstructor(Constructor<T> constructor) {
        this.constructor = constructor;
    }

}