package com.itniuma.bitlnn.mapper;
        
import com.itniuma.bitlnn.pojo.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author aceFelix
 */
@Mapper
public interface ArticleMapper {
    @Insert("insert into article(title,content,cover_img,state,category_id,create_user,create_time,update_time) " +
            "values(#{title},#{content},#{coverImg},#{state},#{categoryId},#{createUser},#{createTime},#{updateTime})")
    void add(Article article);

    List<Article> list(@Param("userId") Integer userId, @Param("categoryId") Integer categoryId, @Param("state") String state);

    @Select("select * from article where id=#{id} and create_user=#{userId}")
    Article detail(@Param("id") Integer id, @Param("userId") Integer userId);

    @Update("update article set title=#{title},content=#{content},cover_img=#{coverImg},state=#{state},category_id=#{categoryId},update_time=#{updateTime} where id=#{id} and create_user=#{createUser}")
    int update(Article article);

    @Delete("delete from article where id=#{id} and create_user=#{userId}")
    int delete(@Param("id") Integer id, @Param("userId") Integer userId);
}
