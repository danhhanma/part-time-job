package danhhanma.part_time_job.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LocalStorage {
    private static final Path TOKEN_FILE = Paths.get(System.getProperty("user.home"), ".myapp_token");

    public static void saveToken(String token) throws IOException {
        Files.writeString(TOKEN_FILE, token, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static String loadToken() throws IOException {
        if (Files.exists(TOKEN_FILE)) {
            return Files.readString(TOKEN_FILE);
        }
        return null;
    }
    public static void saveUserName(String userName) throws IOException {
        Path userNameFile = Paths.get(System.getProperty("user.home"), ".myapp_username");
        Files.writeString(userNameFile, userName, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    public static String loadUserName() throws IOException {
        Path userNameFile = Paths.get(System.getProperty("user.home"), ".myapp_username");
        if (Files.exists(userNameFile)) {
            return Files.readString(userNameFile);
        }
        return null;
    }
    public static void saveUserId(long userId) throws IOException {
        Path userIdFile = Paths.get(System.getProperty("user.home"), ".myapp_userid");
        Files.writeString(userIdFile, String.valueOf(userId), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    public static long loadUserId() throws IOException {
        Path userIdFile = Paths.get(System.getProperty("user.home"), ".myapp_userid");
        if (Files.exists(userIdFile)) {
            String userIdString = Files.readString(userIdFile);
            return Long.parseLong(userIdString);
        }
        return -1;
    }
    public static void clearToken() throws IOException {
        Files.deleteIfExists(TOKEN_FILE);
    }
}
