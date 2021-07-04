package com.tanhua.dubbo.api.impl;

import com.tanhua.domain.mongo.TestSoulFile;
import com.tanhua.domain.mongo.TestSoulUser;
import com.tanhua.dubbo.api.TestSoulApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * 作者：czd
 */
@Service
public class TestSoulApiImpl implements TestSoulApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(TestSoulFile testSoulFile) {
        mongoTemplate.save(testSoulFile);
    }

    @Override
    public TestSoulFile findTestSoulFile(ObjectId testSoulId) {
        return mongoTemplate.findById(testSoulId, TestSoulFile.class);
    }

    @Override
    public List<TestSoulUser> findTestSoulByUserId(Long userId) {
        //1、查询是否存在存在数据
        if (!mongoTemplate.exists(new Query(), "testSoul_file_user" + userId)) {
            //2、不存在，添加数据,总共有3张问卷，需要添加3个数据
            List<TestSoulFile> testSoulFiles = mongoTemplate.find(new Query(), TestSoulFile.class);
            if (testSoulFiles != null) {
                for (TestSoulFile testSoulFile : testSoulFiles) {
                    TestSoulUser tsu = new TestSoulUser();
                    //初级问卷
                    if (testSoulFile.getName().equals("初级灵魂题")) {
                        tsu.setTestSoulId(testSoulFile.getId().toString());
                        tsu.setIsLock(0);
                        tsu.setReportId("0");
                        tsu.setCreated(System.currentTimeMillis());
                    }
                    //中级问卷
                    if (testSoulFile.getName().equals("中级灵魂题")) {
                        tsu.setTestSoulId(testSoulFile.getId().toString());
                        tsu.setIsLock(1);
                        tsu.setReportId("0");
                        tsu.setCreated(System.currentTimeMillis());
                    }
                    //高级问卷
                    if (testSoulFile.getName().equals("高级灵魂题")) {
                        tsu.setTestSoulId(testSoulFile.getId().toString());
                        tsu.setIsLock(1);
                        tsu.setReportId("0");
                        tsu.setCreated(System.currentTimeMillis());
                    }
                    mongoTemplate.save(tsu, "testSoul_file_user" + userId);
                }
            }
        }
        //3、存在就查询表中数据并返回
        return mongoTemplate.find(new Query(), TestSoulUser.class, "testSoul_file_user" + userId);
    }

    @Override
    public String findTestSoulIdByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        TestSoulFile testSoulFile = mongoTemplate.findOne(query, TestSoulFile.class);
        return testSoulFile.getId().toString();
    }

    @Override
    public void updateTestSoulFileUser(String reportId, String name, Long userId) {
        //根据问卷name找到对应的testSoulId
        String testSoulIdLevel1 = this.findTestSoulIdByName("初级灵魂题");
        String testSoulIdLevel2 = this.findTestSoulIdByName("中级灵魂题");
        String testSoulIdLevel3 = this.findTestSoulIdByName("高级灵魂题");
        if (name.equals("初级灵魂题")){
            //初级
            //修改单个用户初级问卷答卷情况
            Query query = new Query(Criteria.where("testSoulId").is(testSoulIdLevel1));
            Update update = new Update();
            update.set("reportId",reportId);
            mongoTemplate.updateFirst(query,update,"testSoul_file_user" + userId);
            //解锁中级问卷
            Query query2 = new Query(Criteria.where("testSoulId").is(testSoulIdLevel2));
            Update update2 = new Update();
            update2.set("isLock",0);
            mongoTemplate.updateFirst(query2,update2,"testSoul_file_user" + userId);
        }else if (name.equals("中级灵魂题")){
            //中级
            //修改单个用户中级问卷答卷情况
            Query query = new Query(Criteria.where("testSoulId").is(testSoulIdLevel2));
            Update update = new Update();
            update.set("reportId",reportId);
            mongoTemplate.updateFirst(query,update,"testSoul_file_user" + userId);
            //解锁高级问卷
            Query query2 = new Query(Criteria.where("testSoulId").is(testSoulIdLevel3));
            Update update2 = new Update();
            update2.set("isLock",0);
            mongoTemplate.updateFirst(query2,update2,"testSoul_file_user" + userId);
        }else {
            //高级
            //修改单个用户高级问卷答卷情况
            Query query = new Query(Criteria.where("testSoulId").is(testSoulIdLevel3));
            Update update = new Update();
            update.set("reportId",reportId);
            mongoTemplate.updateFirst(query,update,"testSoul_file_user" + userId);
        }

    }
}