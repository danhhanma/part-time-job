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

    public Message(String content, boolean isSent, Type type, String fileName) {
        this.content = content;
        this.isSent = isSent;
        this.timestamp = LocalDateTime.now();
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