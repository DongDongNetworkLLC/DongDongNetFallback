package com.dongdongnetwork.punishment.utils;

@FunctionalInterface
public interface Permissionable {
    boolean hasPermission(String permission);
}
