package com.itniuma.bitinn.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.itniuma.bitinn.annotation.State;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private Integer id;

    @Size(max = 100)
    private String title;

    private String content;

    private String coverImg;

    private String excerpt;

    @State
    private String state;

    private Integer categoryId;

    private Integer createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private List<Integer> tagIds;
    private List<Tag> tags;

    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;

    private String authorName;
    private String authorAvatar;

    private String categoryName;

    private Double hotScore;

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
