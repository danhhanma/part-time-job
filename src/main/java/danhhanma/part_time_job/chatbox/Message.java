package danhhanma.part_time_job.chatbox;

import java.time.LocalDateTime;

public class Message {
    public enum Type {
        TEXT, IMAGE, VIDEO, FILE, FOLDER, EMOJI, LINK
    }

    private String content;
    private boolean isSent;
    private LocalDateTime timestamp;
    private Type type;
    private String fileName;

    public Message(String content, boolean isSent) {
        this(content, isSent, Type.TEXT, null);
    }
    public void setType(String type) {
        switch (type.toLowerCase()) {
            case "text":
                this.type = Type.TEXT;
                break;
            case "image":
                this.type = Type.IMAGE;
                break;
            case "video":
                this.type = Type.VIDEO;
                break;
            case "file":
                this.type = Type.FILE;
                break;
            case "folder":
                this.type = Type.FOLDER;
                break;
            case "emoji":
                this.type = Type.EMOJI;
                break;
            case "link":
                this.type = Type.LINK;
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    public Message(String content, boolean isSent, Type type, String fileName) {
        this.content = content;
        this.isSent = isSent;
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.fileName = fileName;
    }

    public Message(String content, boolean isSent, LocalDateTime timestamp, Type type, String fileName) {
        this.content = content;
        this.isSent = isSent;
        this.timestamp = timestamp;
        this.type = type;
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }
}