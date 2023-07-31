package com.example.incubator.back.entity;

public enum Role {
    ROLE_USER("User"),
    ROLE_BI_MANAGER("Manager"),
    ROLE_ADMIN("Admin");

    private String name;

    public String getName() {
        return name;
    }

    Role(String name) {
        this.name = name;
    }
}
