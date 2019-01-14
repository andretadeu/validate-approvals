package com.andretadeu.validateapprovals;

import java.util.function.Supplier;

final class IllegalArgumentExceptionSupplier implements Supplier<IllegalArgumentException> {

    private final String argument;

    IllegalArgumentExceptionSupplier(final String argument) {
        this.argument = argument;
    }

    @Override
    public IllegalArgumentException get() {
        return new IllegalArgumentException("You must provide a value for '" + argument + "'.");
    }
}
