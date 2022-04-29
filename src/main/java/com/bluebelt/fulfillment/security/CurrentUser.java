package com.bluebelt.fulfillment.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
/**
 * @AuthenticationPrincipalđể truy cập người dùng hiện đã được xác thực trong bộ điều khiển.
 */
public @interface CurrentUser {
}
