package com.killshot.config.util;

public class IntWrapper {
    public int value;

    public IntWrapper() {
        value = 0;
    }

    public IntWrapper(final int value) {
        this.value = value;
    }

    public IntWrapper(final IntWrapper intWrapper) {
        this.value = intWrapper.value;
    }

    public void increment() {
        value++;
    }

    public int postfixIncrement() {
        return value++;
    }

    public int prefixIncrement() {
        value++;
        return value;
    }

    public void zeroOut() {
        value = 0;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof IntWrapper)) {
            return false;
        }

        return this.value == ((IntWrapper) object).value;
    }
}