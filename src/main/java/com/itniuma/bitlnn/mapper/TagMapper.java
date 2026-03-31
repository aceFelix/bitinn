package com.itniuma.bitlnn.mapper;

import com.itniuma.bitlnn.pojo.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author aceFelix
 */
@Mapper
public interface TagMapper {
    @Insert("insert into tag(tag_name, tag_color, create_user, create_time, update_time) " +
            "values(#{tagName}, #{tagColor}, #{createUser}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void add(Tag tag);

    @Select("select * from tag where create_user = #{userId} order by create_time desc")
    List<Tag> list(Integer userId);

    @Select("select * from tag where id = #{id}")
    Tag findById(Integer id);

    @Select("select * from tag where tag_name = #{tagName}")
    Tag findByTagName(String tagName);

    @Update("update tag set tag_name = #{tagName}, tag_color = #{tagColor}, update_time = #{updateTime} " +
            "where id = #{id} and create_user = #{createUser}")
    int update(Tag tag);

    @Delete("delete from tag where id = #{id} and create_user = #{userId}")
    int delete(@Param("id") Integer id, @Param("userId") Integer userId);

    @Insert("<script>" +
            "insert into article_tag(article_id, tag_id, create_time) values " +
            "<foreach collection='tagIds' item='tagId' separator=','>" +
            "(#{articleId}, #{tagId}, #{createTime})" +
            "</foreach>" +
            "</script>")
    void addArticleTags(@Param("articleId") Integer articleId, 
                        @Param("tagIds") List<Integer> tagIds, 
                        @Param("createTime") java.time.LocalDateTime createTime);

    @Delete("delete from article_tag where article_id = #{articleId}")
    void deleteArticleTags(Integer articleId);

    @Select("select t.* from tag t " +
            "inner join article_tag at on t.id = at.tag_id " +
            "where at.article_id = #{articleId}")
    List<Tag> findByArticleId(Integer articleId);
}
