module danhhanma.part_time_job {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires jdk.jsobject;
    requires org.json;
    requires Java.WebSocket;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.io;


    opens danhhanma.part_time_job to javafx.fxml;
    exports danhhanma.part_time_job.login;
    opens danhhanma.part_time_job.login to javafx.fxml;
    exports danhhanma.part_time_job.register;
    opens danhhanma.part_time_job.register to javafx.fxml;
    exports danhhanma.part_time_job.dashboard;
    opens danhhanma.part_time_job.dashboard to javafx.fxml;
    exports danhhanma.part_time_job.controllerapp;
    opens danhhanma.part_time_job.controllerapp to javafx.fxml;
    exports danhhanma.part_time_job.application;
    opens danhhanma.part_time_job.application to javafx.fxml;
    opens danhhanma.part_time_job.JobPost;
    exports danhhanma.part_time_job.JobPost;

    // Thêm export và opens cho package chat
    exports danhhanma.part_time_job.chat;
    opens danhhanma.part_time_job.chat to javafx.fxml;
    exports danhhanma.part_time_job.chatbox;
    opens danhhanma.part_time_job.chatbox to javafx.fxml;

    exports danhhanma.part_time_job.dto;
    opens danhhanma.part_time_job.dto to javafx.fxml;
}