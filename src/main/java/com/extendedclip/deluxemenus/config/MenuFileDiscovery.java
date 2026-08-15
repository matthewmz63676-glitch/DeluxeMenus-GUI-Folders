package com.extendedclip.deluxemenus.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Finds menu files below the DeluxeMenus gui_menus directory.
 *
 * <p>The relative path is also the automatic menu name. Forward slashes are
 * used in that name on every operating system so a menu in
 * {@code gui_menus/admin/tools.yml} is opened as {@code admin/tools}.</p>
 */
public final class MenuFileDiscovery {

    private static final String MENU_EXTENSION = ".yml";

    private MenuFileDiscovery() {
    }

    public static List<DiscoveredMenuFile> find(final Path menuDirectory) throws IOException {
        final Path root = menuDirectory.toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(MenuFileDiscovery::isMenuFile)
                    .map(path -> createDiscoveredFile(root, path))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(DiscoveredMenuFile::menuName, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
        }
    }

    private static boolean isMenuFile(final Path path) {
        return path.getFileName() != null
                && path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(MENU_EXTENSION);
    }

    private static DiscoveredMenuFile createDiscoveredFile(final Path root, final Path file) {
        final Path normalizedFile = file.toAbsolutePath().normalize();
        final Path relativePath = root.relativize(normalizedFile);
        final String relativeFileName = relativePath.toString().replace('\\', '/');
        final String menuName = relativeFileName.substring(0, relativeFileName.length() - MENU_EXTENSION.length());

        if (menuName.isEmpty()) {
            return null;
        }

        return new DiscoveredMenuFile(normalizedFile, relativeFileName, menuName);
    }

    public static final class DiscoveredMenuFile {

        private final Path file;
        private final String relativePath;
        private final String menuName;

        private DiscoveredMenuFile(final Path file, final String relativePath, final String menuName) {
            this.file = file;
            this.relativePath = relativePath;
            this.menuName = menuName;
        }

        public Path file() {
            return file;
        }

        public String relativePath() {
            return relativePath;
        }

        public String menuName() {
            return menuName;
        }
    }
}
