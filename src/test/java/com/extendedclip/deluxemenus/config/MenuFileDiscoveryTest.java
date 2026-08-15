package com.extendedclip.deluxemenus.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MenuFileDiscoveryTest {

    @Test
    void discoversYamlMenusRecursivelyUsingRelativeNames(@TempDir final Path menuDirectory) throws IOException {
        Files.createDirectories(menuDirectory.resolve("admin/tools"));
        Files.writeString(menuDirectory.resolve("home.yml"), "menu_title: Home");
        Files.writeString(menuDirectory.resolve("admin/tools/staff.yml"), "menu_title: Staff");
        Files.writeString(menuDirectory.resolve("admin/tools/README.txt"), "ignored");

        final List<MenuFileDiscovery.DiscoveredMenuFile> menus = MenuFileDiscovery.find(menuDirectory);

        assertEquals(List.of("admin/tools/staff", "home"), menus.stream()
                .map(MenuFileDiscovery.DiscoveredMenuFile::menuName)
                .collect(Collectors.toList()));
        assertEquals("admin/tools/staff.yml", menus.get(0).relativePath());
    }

    @Test
    void acceptsUppercaseYamlExtension(@TempDir final Path menuDirectory) throws IOException {
        Files.writeString(menuDirectory.resolve("test.YML"), "menu_title: Test");

        final List<MenuFileDiscovery.DiscoveredMenuFile> menus = MenuFileDiscovery.find(menuDirectory);

        assertEquals(List.of("test"), menus.stream()
                .map(MenuFileDiscovery.DiscoveredMenuFile::menuName)
                .collect(Collectors.toList()));
    }
}
