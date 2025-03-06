package org.example.groupmanageservice.enums;

public enum Roles {
    SYSTEM_ADMIN("system_admin"),
    HOSTER("hoster"),
    REGULAR_USER("regular_user");

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}