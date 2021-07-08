package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.TestSoulFile;
import com.tanhua.domain.mongo.TestSoulUser;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * 作者：czd
 */
public interface TestSoulApi {
    void save(TestSoulFile testSoulFile);

    TestSoulFile findTestSoulFile(ObjectId testSoulId);

    List<TestSoulUser> findTestSoulByUserId(Long userId);

    String findTestSoulIdByName(String name);

    void updateTestSoulFileUser(String reportId, String name,Long userId);
}
