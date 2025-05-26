package danhhanma.part_time_job.objects.contact;

public class Contact {
    private long id;
    private String name;
    private String avatar;
    private String status;

    public Contact(long id,String name, String avatar, String status) {
        this.name = name;
        this.id = id;
        if(avatar==null|| avatar.isEmpty()) {
            this.avatar = "/img/user.png";
        } else {
            this.avatar = avatar;
        }
        this.status = status;
    }

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return String.valueOf(id);
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if(avatar == null || avatar.isEmpty()) {
            this.avatar = "/img/user.png";
        } else {
            this.avatar = avatar;
        }
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