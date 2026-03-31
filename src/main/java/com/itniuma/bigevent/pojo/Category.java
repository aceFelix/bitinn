package com.itniuma.bigevent.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 文章分类实体类
 * @author aceFelix
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    //主键ID
    @NotNull(groups = Update.class)
    private Integer id;
    // ^\S{1,15}$: 匹配1到15个非空字符
    @NotEmpty
    @Pattern(regexp = "^\\S{1,15}$"/*,groups = {Add.class, Update.class}*/)
    //分类名称
    private String categoryName;
    //分类别名
    @NotEmpty/*(groups = {Add.class, Update.class})*/
    private String categoryAlias;
    //创建人ID
    private Integer createUser;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    //更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 校验项 添加时，使用Add分组，更新时，使用Update分组
    // 如果某个校验项没有指定分组，则默认为Default分组
    // 分组之间可以继承，可以继承多个分组 A extens B：A中拥有B的所有校验项，B中有的校验项，A中也有，A的校验项优先级高
    public interface Add extends Default{}
    public interface Update extends Default{}
}
