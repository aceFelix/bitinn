package com.itniuma.bigevent.mapper;

import com.itniuma.bigevent.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author aceFelix
 */
@Mapper
public interface CategoryMapper {
    @Insert("insert into category(category_name,category_alias,create_user,create_time,update_time) " +
            "values(#{categoryName},#{categoryAlias},#{createUser},#{createTime},#{updateTime})")
    void add(Category category);

    @Select("select * from category where create_user=#{id}")
    List<Category> list(Integer id);

    @Select("select * from category where id=#{id} and create_user=#{userId}")
    Category detail(@Param("id") Integer id, @Param("userId") Integer userId);

    @Update("update category set category_name=#{categoryName},category_alias=#{categoryAlias}," +
            "update_time=#{updateTime} where id=#{id} and create_user=#{createUser}")
    int update(Category category);

    @Delete("delete from category where id=#{id} and create_user=#{userId}")
    int delete(@Param("id") Integer id, @Param("userId") Integer userId);
}
