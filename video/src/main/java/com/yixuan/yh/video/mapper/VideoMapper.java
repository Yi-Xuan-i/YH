package com.yixuan.yh.video.mapper;

import com.yixuan.yh.video.entity.Video;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper {
    @Select("SELECT url \n" +
            "FROM video \n" +
            "WHERE id >= (\n" +
            "    SELECT MIN(id) + FLOOR(RAND() * (MAX(id) - MIN(id))) \n" +
            "    FROM video\n" +
            ") \n" +
            "LIMIT 5;")
    List<String> selectRandom();

    @Insert("insert into video (id, url) value (#{id}, #{url})")
    void insert(Video video);

    @Select("select url from video where id = #{id}")
    String selectVideoUrlById(Long id);
}
