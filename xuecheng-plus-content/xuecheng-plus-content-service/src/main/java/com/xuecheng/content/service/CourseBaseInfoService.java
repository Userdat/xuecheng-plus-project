package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;

/**
 * @author liuenchen
 * @description TODO
 * @date 2024-04-30 11:42
 */
public interface CourseBaseInfoService {

    CourseBaseInfoDto save(Long companyId,AddCourseDto addCourseDto);

    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);
}
