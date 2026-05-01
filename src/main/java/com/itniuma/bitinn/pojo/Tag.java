package com.itniuma.bitinn.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签实体类
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    @NotNull(groups = Update.class)
    private Integer id;

    @NotEmpty
    @Pattern(regexp = "^\\S{1,20}$")
    private String tagName;

    private String tagColor;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public interface Add extends Default {
    }

    public interface Update extends Default {
    }
}
