package lab.security;

import lab.User.domain.Role;

public record UserPrincipal(Long id, String username, Role role) {

    public boolean isAdmin() {
        return role == Role.ROLE_ADMIN;
    }
}
