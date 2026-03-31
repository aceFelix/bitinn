package com.itniuma.bitlnn.pojo;

import com.itniuma.bitlnn.annotation.State;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 *
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @NotNull(groups = Update.class)
    private Integer id;

    @NotEmpty
    @Pattern(regexp = "^\\S{1,15}$")
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    @URL
    private String coverImg;

    @State
    private String state;

    @NotNull
    private Integer categoryId;

    private Integer createUser;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Integer> tagIds;

    private List<Tag> tags;

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
