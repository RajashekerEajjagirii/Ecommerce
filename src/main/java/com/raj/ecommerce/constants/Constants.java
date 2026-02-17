package com.raj.ecommerce.constants;

import com.raj.ecommerce.domain.Role;

import java.util.HashSet;
import java.util.Set;

public class Constants {

    public Constants() {
    }

    public static final String SECURITY_SCHEME_NAME="BearerAuth";

    public static final String[] SWAGGER_WHITELIST={
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public static final String[] PUBLIC_ENDPOINTS={
         "/api/auth/**"
    };

    public static final String[] USER_ACCESS_ENDPOINTS={
            "/api/users/**"
    };

    public static final String[] ADMIN_ACCESS_ENDPOINTS={
            "/api/products/**"
    };

    public static final String ROLE_TYPE="ROLE_ADMIN";
    public static final Long ROLE_ID=2L;
    public static final String ORDER_STATUS_SHIPPED="SHIPPED";

}
