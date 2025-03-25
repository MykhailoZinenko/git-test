package com.colonygenesis.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A class that represents the result of an operation which can either succeed with a value
 * or fail with an error message.
 *
 * @param <T> The type of the value in case of success
 */
public class Result<T> {
    private final T value;
    private final String errorMessage;
    private final boolean success;

    private Result(T value, String errorMessage, boolean success) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.success = success;
    }

    /**
     * Creates a successful result with the given value.
     *
     * @param value The result value
     * @param <T> The type of the value
     * @return A successful Result
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    /**
     * Creates a successful result with no value (for void operations).
     *
     * @param <T> The type parameter
     * @return A successful Result
     */
    public static <T> Result<T> success() {
        return new Result<>(null, null, true);
    }

    /**
     * Creates a failed result with the given error message.
     *
     * @param errorMessage The error message
     * @param <T> The type parameter
     * @return A failed Result
     */
    public static <T> Result<T> failure(String errorMessage) {
        return new Result<>(null, errorMessage, false);
    }

    /**
     * Checks if this result represents a successful operation.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Checks if this result represents a failed operation.
     *
     * @return true if failed, false otherwise
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * Gets the value if this is a success, or null if it's a failure.
     * Consider using {@link #getValue()} instead for null-safety.
     *
     * @return The value or null
     */
    public T getValueOrNull() {
        return value;
    }

    /**
     * Returns the value wrapped in an Optional.
     *
     * @return An Optional containing the value, or empty if this is a failure
     */
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    /**
     * Gets the error message if this is a failure, or null if it's a success.
     *
     * @return The error message or null
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Executes the given action if this result is a success.
     *
     * @param action The action to execute
     * @return This Result for chaining
     */
    public Result<T> onSuccess(Consumer<T> action) {
        if (success && action != null) {
            action.accept(value);
        }
        return this;
    }

    /**
     * Executes the given action if this result is a failure.
     *
     * @param action The action to execute
     * @return This Result for chaining
     */
    public Result<T> onFailure(Consumer<String> action) {
        if (!success && action != null) {
            action.accept(errorMessage);
        }
        return this;
    }

    /**
     * Maps the value of this result using the given function if it's a success.
     *
     * @param mapper The mapping function
     * @param <R> The type of the mapped value
     * @return A new Result with the mapped value or the same failure
     */
    public <R> Result<R> map(Function<T, R> mapper) {
        if (success) {
            return Result.success(mapper.apply(value));
        } else {
            return Result.failure(errorMessage);
        }
    }

    /**
     * Returns the value if this is a success, or the given default value if it's a failure.
     *
     * @param defaultValue The default value
     * @return The value or the default
     */
    public T getOrElse(T defaultValue) {
        return success ? value : defaultValue;
    }
}