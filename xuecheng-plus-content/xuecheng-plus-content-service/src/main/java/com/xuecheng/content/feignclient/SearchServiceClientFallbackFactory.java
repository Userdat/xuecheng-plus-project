package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liuenchen
 * @description TODO
 * @date 2024-05-15 17:31
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean addCourseIndex(CourseIndex courseIndex) {
                log.error("添加课程索引发生错误，索引信息{},熔断异常{}",courseIndex,throwable.toString(),throwable);
                return false;
            }
        };
    }
}
