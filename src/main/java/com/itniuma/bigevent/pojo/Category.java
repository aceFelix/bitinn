package com.itniuma.bigevent.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;


public class Category {
    @NotNull(groups = Update.class)
    private Integer id;//主键ID
    @NotEmpty
    @Pattern(regexp = "^\\S{1,15}$"/*,groups = {Add.class, Update.class}*/) // ^\S{1,15}$: 匹配1到15个非空字符
    private String categoryName;//分类名称
    @NotEmpty/*(groups = {Add.class, Update.class})*/
    private String categoryAlias;//分类别名
    private Integer createUser;//创建人ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;//更新时间

    // 校验项 添加时，使用Add分组，更新时，使用Update分组
    // 如果某个校验项没有指定分组，则默认为Default分组
    // 分组之间可以继承，可以继承多个分组 A extens B：A中拥有B的所有校验项，B中有的校验项，A中也有，A的校验项优先级高
    public interface Add extends Default{}
    public interface Update extends Default{}


    public Category() {
    }

    public Category(Integer id, String categoryName, String categoryAlias, Integer createUser, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryAlias = categoryAlias;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * 获取
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取
     * @return categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 设置
     * @param categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 获取
     * @return categoryAlias
     */
    public String getCategoryAlias() {
        return categoryAlias;
    }

    /**
     * 设置
     * @param categoryAlias
     */
    public void setCategoryAlias(String categoryAlias) {
        this.categoryAlias = categoryAlias;
    }

    /**
     * 获取
     * @return createUser
     */
    public Integer getCreateUser() {
        return createUser;
    }

    /**
     * 设置
     * @param createUser
     */
    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    /**
     * 获取
     * @return createTime
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置
     * @param createTime
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取
     * @return updateTime
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置
     * @param updateTime
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Category{id = " + id + ", categoryName = " + categoryName + ", categoryAlias = " + categoryAlias + ", createUser = " + createUser + ", createTime = " + createTime + ", updateTime = " + updateTime + "}";
    }
}
