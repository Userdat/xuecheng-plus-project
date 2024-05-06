package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuenchen
 * @description TODO
 * @date 2024-04-29 10:15
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    List<CourseCategoryTreeDto>  childrenTreeNodes;
}
