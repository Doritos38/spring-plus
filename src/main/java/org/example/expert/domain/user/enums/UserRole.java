package org.example.expert.domain.user.enums;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

public enum UserRole implements GrantedAuthority {
    ROLE_ADMIN("ROLE_ADMIN"), ROLE_USER("ROLE_USER");

    private String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public static UserRole of(String role) {
        UserRole role1 = Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UserRole"));
        System.out.println("sd4f54sd5f4sdf54sd5f456sa            "+role1);
        return role1;
    }
}
