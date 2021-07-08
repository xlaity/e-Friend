package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.TestSoulReport;

import java.util.List;

public interface TestSoulReportApi {
    String save(TestSoulReport testSoulReport);

    TestSoulReport findById(String reportId);

    List<Long> findByLevel(Integer level, Integer size, Long userId, String testSoulFileId);
}
