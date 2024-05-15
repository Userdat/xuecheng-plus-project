package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.po.TeachplanMedia;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author itcast
 * @since 2024-04-28
 */
public interface TeachplanMediaService extends IService<TeachplanMedia> {

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @author Mr.M
     * @date 2022/9/14 22:20
     */

    TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
