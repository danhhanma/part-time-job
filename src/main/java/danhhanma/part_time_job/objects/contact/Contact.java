package danhhanma.part_time_job.objects.contact;

public class Contact {
    private String name;
    private String avatarPath;
    private String status;

    public Contact(String name, String avatarPath, String status) {
        this.name = name;
        this.avatarPath = avatarPath;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name;
    }
} 