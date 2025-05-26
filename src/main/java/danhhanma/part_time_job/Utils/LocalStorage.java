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
    public static void clearAll() throws IOException {
        clearToken();
        clearUserName();
        clearUserId();
        clearEmployerId();
        clearApplicantId();
    }
    public static void saveAvatarPath(String avatarPath) throws IOException {
        Path avatarFile = Paths.get(System.getProperty("user.home"), ".myapp_avatar");
        Files.writeString(avatarFile, avatarPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    public static String loadAvatarPath() throws IOException {
        Path avatarFile = Paths.get(System.getProperty("user.home"), ".myapp_avatar");
        if (Files.exists(avatarFile)) {
            return Files.readString(avatarFile);
        }
        return  "/img/user.png";
    }
    public static void clearUserName() throws IOException {
        Path userNameFile = Paths.get(System.getProperty("user.home"), ".myapp_username");
        Files.deleteIfExists(userNameFile);
    }
    public static void clearUserId() throws IOException {
        Path userIdFile = Paths.get(System.getProperty("user.home"), ".myapp_userid");
        Files.deleteIfExists(userIdFile);
    }
    public static void clearEmployerId() throws IOException {
        Path employerIdFile = Paths.get(System.getProperty("user.home"), ".myapp_employerid");
        Files.deleteIfExists(employerIdFile);
    }
    public static void saveEmployerId(String employerId) throws IOException {
        Path employerIdFile = Paths.get(System.getProperty("user.home"), ".myapp_employerid");
        Files.writeString(employerIdFile, String.valueOf(employerId), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    public static String loadEmployerId() throws IOException {
        Path employerIdFile = Paths.get(System.getProperty("user.home"), ".myapp_employerid");
        if (Files.exists(employerIdFile)) {
            String employerIdString = Files.readString(employerIdFile);
            return employerIdString;
        }
        return null;
    }
    public static void saveApplicantId(String applicantId) throws IOException {
        Path applicantIdFile = Paths.get(System.getProperty("user.home"), ".myapp_applicantid");
        Files.writeString(applicantIdFile, String.valueOf(applicantId), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    public static String loadApplicantId() throws IOException {
        Path applicantIdFile = Paths.get(System.getProperty("user.home"), ".myapp_applicantid");
        if (Files.exists(applicantIdFile)) {
            String applicantIdString = Files.readString(applicantIdFile);
            return applicantIdString;
        }
        return null;
    }
    public static void clearApplicantId() throws IOException {
        Path applicantIdFile = Paths.get(System.getProperty("user.home"), ".myapp_applicantid");
        Files.deleteIfExists(applicantIdFile);
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
