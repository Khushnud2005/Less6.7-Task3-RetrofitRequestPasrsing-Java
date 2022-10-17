package uz.example.less67_task3_retrofitrequestpasrsing_java.model;

import com.google.gson.annotations.SerializedName;

public class PosterResp {
    @SerializedName("id")
    int id;
    @SerializedName("userId")
    int user_id;
    @SerializedName("title")
    String title;
    @SerializedName("body")
    String body;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "PosterResp{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
