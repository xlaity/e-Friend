package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.TestSoulReport;
import com.tanhua.dubbo.api.TestSoulReportApi;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestSoulReportApiImpl implements TestSoulReportApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(TestSoulReport testSoulReport) {

        Query query = new Query(Criteria.where("testSoulFileId").is(testSoulReport.getTestSoulFileId())
                .and("userId").is(testSoulReport.getUserId()));
        TestSoulReport report = mongoTemplate.findOne(query, TestSoulReport.class);
        if (report != null) {
            //存在，就更新分数值，等级
            Update update = new Update();
            update.set("score", testSoulReport.getScore());
            update.set("level", testSoulReport.getLevel());
            update.set("cover", testSoulReport.getCover());
            update.set("conclusion", testSoulReport.getConclusion());
            update.set("dimensions", testSoulReport.getDimensions());
            update.set("updated", System.currentTimeMillis());
            mongoTemplate.updateFirst(query, update, TestSoulReport.class);
            return report.getId().toString();
        } else {
            //不存在，就添加数据
            testSoulReport.setCreated(System.currentTimeMillis());
            testSoulReport.setUpdated(System.currentTimeMillis());
            mongoTemplate.save(testSoulReport);
            return testSoulReport.getId().toString();
        }

    }

    @Override
    public TestSoulReport findById(String reportId) {
        return mongoTemplate.findById(reportId, TestSoulReport.class);
    }

    @Override
    public List<Long> findByLevel(Integer level, Integer size, Long userId, String testSoulFileId) {
        //查询mongodb中的数据
        Query query = new Query(Criteria.where("level").is(level)
                .and("testSoulFileId").is(testSoulFileId)
                .and("userId").ne(userId));
        query.with(Sort.by(Sort.Direction.DESC, "updated"));
        query.limit(size).skip(0);//取前5条数据
        List<TestSoulReport> testSoulReports = mongoTemplate.find(query, TestSoulReport.class);

        //返回userId的集合
        ArrayList<Long> userIdList = new ArrayList<>();
        if (testSoulReports != null && testSoulReports.size() > 0) {
            for (TestSoulReport testSoulReport : testSoulReports) {
                userIdList.add(testSoulReport.getUserId());
            }
        }
        return userIdList;
    }
}
