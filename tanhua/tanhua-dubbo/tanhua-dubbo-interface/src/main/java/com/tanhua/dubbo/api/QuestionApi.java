package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Question;

/**
 * 陌生人问题服务接口
 */
public interface QuestionApi {

    /**
     * 查询用户的陌生人设置
     */
    Question findByUserId(Long id);

    /**
     * 新增陌生人问题
     * @param question
     */
    void save(Question question);

    /**
     * 修改陌生人问题
     * @param question
     */
    void update(Question question);
}
