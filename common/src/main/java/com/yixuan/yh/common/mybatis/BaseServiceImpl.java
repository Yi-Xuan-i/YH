package com.yixuan.yh.common.mybatis;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
// 这里以 Hutool 拷贝为例，也可以用 MapStruct 或 Spring 的 BeanUtils

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    /**
     * 根据 VO 类的字段进行查询，并返回 VO 列表
     *
     * @param voClass   VO 类（字段名需与实体一致，或自行扩展映射）
     * @param condition 附加查询条件（可为 null）
     * @param <V>       VO 类型
     * @return VO 对象列表
     */
    public <V> List<V> selectVoList(Class<V> voClass, Consumer<LambdaQueryWrapper<T>> condition) {
        LambdaQueryWrapper<T> wrapper = selectByVo(voClass, getEntityClass());

        if (condition != null) {
           condition.accept(wrapper);
        }

        List<T> entityList = list(wrapper);

        return entityList.stream()
                .map(entity -> {
                    try {
                        V vo = voClass.getDeclaredConstructor().newInstance();
                        org.springframework.beans.BeanUtils.copyProperties(entity, vo);
                        return vo;
                    } catch (Exception e) {
                        throw new RuntimeException("VO 实例化或属性复制失败", e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static <E> LambdaQueryWrapper<E> selectByVo(Class<?> voClass, Class<E> entityClass) {
        Set<String> voFieldNames = Arrays.stream(voClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        return new LambdaQueryWrapper<E>().select(entityClass,
                fieldInfo -> voFieldNames.contains(fieldInfo.getProperty()));
    }
}