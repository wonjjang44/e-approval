package org.yang1.eapproval.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResult<T> {

    private final boolean success;
    private final String message;
    private final T data;

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(true, message, data);
    }

    public static <T> ApiResult<T> success(String message) {
        return new ApiResult<>(true, message, null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, null, data);
    }

    public static <T> ApiResult<T> failure(String message, T data) {
        return new ApiResult<>(false, message, data);
    }

    public static <T> ApiResult<T> failure(String message) {
        return new ApiResult<>(false, message, null);
    }

}
