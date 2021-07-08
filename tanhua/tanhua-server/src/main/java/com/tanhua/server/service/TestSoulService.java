package com.tanhua.server.service;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.entity.Answer;
import com.tanhua.domain.entity.Dimension;
import com.tanhua.domain.entity.SimilarYou;
import com.tanhua.domain.mongo.TestSoulReport;
import com.tanhua.domain.mongo.TestSoulUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.ReportVo;
import com.tanhua.domain.vo.TestSoulListVo;
import com.tanhua.dubbo.api.TestSoulReportApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.bson.types.ObjectId;

import com.tanhua.domain.mongo.TestSoulFile;
import com.tanhua.dubbo.api.TestSoulApi;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * 作者：czd
 */
@Service
public class TestSoulService {

    @Reference
    private TestSoulApi testSoulApi;

    @Reference
    private TestSoulReportApi testSoulReportApi;

    @Reference
    private UserInfoApi userInfoApi;

    /**
     * 测灵魂-问卷列表
     *
     * @return TestSoulListVo
     */
    public ResponseEntity<Object> testSoulList() {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //根据用户id查询testSoul_file_user1中的数据并返回
        List<TestSoulUser> testSoulUsers = testSoulApi.findTestSoulByUserId(userId);

        //封装数据
        ArrayList<TestSoulListVo> vos = new ArrayList<>();
        for (TestSoulUser testSoulUser : testSoulUsers) {
            TestSoulListVo vo = new TestSoulListVo();
            //根据testSoulId找到对应的问卷信息
            TestSoulFile testSoulFile = testSoulApi.findTestSoulFile(new ObjectId(testSoulUser.getTestSoulId()));
            vo.setId(testSoulFile.getId().toString());
            BeanUtils.copyProperties(testSoulFile, vo);
            vo.setIsLock(testSoulUser.getIsLock());
            if (testSoulUser.getReportId().equals("0")) {
                //还没开始测试
                vo.setReportId(null);
            } else {
                //已经测试过了，有数据
                vo.setReportId(testSoulUser.getReportId());
            }
            vos.add(vo);
        }
        return ResponseEntity.ok(vos);
    }

    /**
     * 测灵魂-提交问卷
     *
     * @param answers 答案
     * @return reportId
     */
    public ResponseEntity<Object> addAnswers(List<Answer> answers) {
        //1、获取当前用户id
        Long userId = UserHolder.getUserId();
        //2、optionId的集合
        ArrayList<String> optionIdList = new ArrayList<>();
        if (answers != null && answers.size() > 0) {
            for (Answer answer : answers) {
                optionIdList.add(answer.getOptionId());
            }
        }

        //需要返回的数据，报告表的id
        String id = null;
        if (answers != null && answers.size() > 0) {
            Long questionId = null;
            for (Answer answer : answers) {
                //根据其中一个问题编号就可以判断出该问卷的类型(已知条件)
                questionId = Long.valueOf(answer.getQuestionId());
                break;
            }
            //初级问卷有10道题   1--10
            //中级问卷有2道题   11-12
            //高级问卷有1道题    13
            if (questionId <= 10L) {
                //初级问卷
                id = this.getReportId("初级灵魂题", userId, optionIdList);

            } else if (questionId <= 12L) {
                //中级问卷
                id = this.getReportId("中级灵魂题", userId, optionIdList);
            } else {
                //高级问卷
                id = this.getReportId("高级灵魂题", userId, optionIdList);
            }
        }
        return ResponseEntity.ok(id);
    }

    //保存报告并获取报告id
    public String getReportId(String name, Long userId, List<String> optionIdList) {
        //1、获取问卷id
        String testSoulFileId = testSoulApi.findTestSoulIdByName(name);
        //2、往报告表中添加数据testSoul_report并获取reportId
        TestSoulReport testSoulReport = new TestSoulReport();
        testSoulReport.setId(new ObjectId());
        testSoulReport.setTestSoulFileId(testSoulFileId);
        testSoulReport.setUserId(userId);

        //计算分数
        Integer score = this.getScore(optionIdList);
        testSoulReport.setScore(score);
        //计算等级
        Integer level = this.getLevel(score);
        testSoulReport.setLevel(level);
        //填写结论
        String conclusion = this.getConclusion(level);
        testSoulReport.setConclusion(conclusion);
        //统一封面
        String url = "https://lk-bukect.oss-cn-guangzhou.aliyuncs.com/images/2021/07/03/cefdebc2-f396-4b09-83a4-286c87beac56.jpg";
        testSoulReport.setCover(url);
        //维度值
        List<Dimension> dimensions = this.getDimensions(level);
        testSoulReport.setDimensions(dimensions);

        String reportId = testSoulReportApi.save(testSoulReport);

        //3、更新个人用户问卷表testSoul_file_user1 的isLock 和reportId
        testSoulApi.updateTestSoulFileUser(reportId, name, UserHolder.getUserId());
        //4、返回报告id
        return reportId;
    }


    //计算分数
    //初级问卷
    //+1 分 ： 8  17  34  39  45
    //+2 分 ： 1  7  10  16  24  26  33  38   41
    //+3分  ： 20  32  42
    //+4分  ： 2  5  9  14  19  23  27  31  37  40
    //+5分  ： 11  21  30  43（4）
    //+6分  ： 3  4  13  15  18  22  25  28  36  44
    //+7分  ： 6  12  29  35（4）
    //中级问卷
    //+6分 ：46 47 48
    //+7分 ：49 50 51 52 53
    //高级问卷
    //+7分 ：54 55 56
    public Integer getScore(List<String> optionIdList) {
        Integer score = 0;
        if (optionIdList != null && optionIdList.size() > 0) {
            for (String optionId : optionIdList) {
                switch (Integer.valueOf(optionId)) {
                    case 8:
                    case 17:
                    case 34:
                    case 39:
                    case 45:
                        score += 1;
                        break;
                    case 1:
                    case 7:
                    case 10:
                    case 16:
                    case 24:
                    case 26:
                    case 33:
                    case 38:
                        score += 2;
                        break;
                    case 20:
                    case 32:
                    case 42:
                        score += 3;
                        break;
                    case 2:
                    case 5:
                    case 9:
                    case 14:
                    case 19:
                    case 23:
                    case 27:
                    case 31:
                    case 37:
                    case 40:
                        score += 4;
                        break;
                    case 11:
                    case 21:
                    case 30:
                    case 43:
                        score += 5;
                        break;
                    case 3:
                    case 4:
                    case 13:
                    case 15:
                    case 18:
                    case 22:
                    case 25:
                    case 28:
                    case 36:
                    case 44:
                    case 46:
                    case 47:
                    case 48:
                        score += 6;
                        break;
                    case 6:
                    case 12:
                    case 29:
                    case 35:
                    default:
                        score += 7;
                        break;
                }
            }
        }
        return score;
    }

    //计算等级
    //等级 ：低于21分     1
    //      21-40分     2
    //      41分-55分:   3
    //      56以上：     4
    public Integer getLevel(Integer score) {
        if (score < 21) {
            return 1;
        } else if (score <= 40) {
            return 2;
        } else if (score <= 55) {
            return 3;
        } else {
            return 4;
        }
    }

    //填写结论
    //等级 ：低于21分     1
    //      21-40分     2
    //      41分-55分:   3
    //      56以上：     4
    private String getConclusion(Integer level) {
        if (level == 1) {
            return "猫头鹰：他们的共同特质为重计划、条理、细节精准。在行为上，表现出喜欢理性思考与分析、较重视制度、结构、规范。他们注重执行游戏规则、循规蹈矩、巨细靡遗、重视品质、敬业负责。";
        } else if (level == 2) {
            return "白兔型：平易近人、敦厚可靠、避免冲突与不具批判性。在行为上，表现出不慌不忙、冷静自持的态度。他们注重稳定与中长程规划，现实生活中，常会反思自省并以和谐为中心，即使面对困境，亦能泰然自若，从容应付。";
        } else if (level == 3) {
            return "狐狸型 ：人际关系能力极强，擅长以口语表达感受而引起共鸣，很会激励并带动气氛。他们喜欢跟别人互动，重视群体的归属感，基本上是比较「人际导向」。由于他们富同理心并乐于分享，具有很好的亲和力，在服务业、销售业、传播业及公共关系等领域中，狐狸型的领导者都有很杰出的表现。";
        } else {
            return "狮子型：性格为充满自信、竞争心强、主动且企图心强烈，是个有决断力的领导者。一般而言，狮子型的人胸怀大志，勇于冒险，看问题能够直指核心，并对目标全力以赴。他们在领导风格及决策上，强调权威与果断，擅长危机处理，此种性格最适合开创性与改革性的工作。";
        }
    }

    //计算维度值
    //等级 ：低于21分     1     猫头鹰  外向： 60 判断： 80 抽象： 60 理性： 90
    //      21-40分     2    白兔   外向： 70 判断： 80 抽象：70  理性：80
    //      41分-55分:   3    狐狸   外向：90  判断：70  抽象：80  理性：60
    //      56以上：     4    狮子   外向： 80 判断：90  抽象： 70 理性：80
    private List<Dimension> getDimensions(Integer level) {
        ArrayList<Dimension> dimensions = new ArrayList<>();
        if (level == 1) {
            Dimension d1 = new Dimension("外向", "60%");
            Dimension d2 = new Dimension("判断", "80%");
            Dimension d3 = new Dimension("抽象", "60%");
            Dimension d4 = new Dimension("理性", "90%");
            dimensions.add(d1);
            dimensions.add(d2);
            dimensions.add(d3);
            dimensions.add(d4);
            return dimensions;
        } else if (level == 2) {
            dimensions.clear();
            Dimension d1 = new Dimension("外向", "70%");
            Dimension d2 = new Dimension("判断", "80%");
            Dimension d3 = new Dimension("抽象", "70%");
            Dimension d4 = new Dimension("理性", "80%");
            dimensions.add(d1);
            dimensions.add(d2);
            dimensions.add(d3);
            dimensions.add(d4);
            return dimensions;
        } else if (level == 3) {
            dimensions.clear();
            Dimension d1 = new Dimension("外向", "90%");
            Dimension d2 = new Dimension("判断", "70%");
            Dimension d3 = new Dimension("抽象", "80%");
            Dimension d4 = new Dimension("理性", "60%");
            dimensions.add(d1);
            dimensions.add(d2);
            dimensions.add(d3);
            dimensions.add(d4);
            return dimensions;
        } else {
            dimensions.clear();
            Dimension d1 = new Dimension("外向", "80%");
            Dimension d2 = new Dimension("判断", "90%");
            Dimension d3 = new Dimension("抽象", "70%");
            Dimension d4 = new Dimension("理性", "80%");
            dimensions.add(d1);
            dimensions.add(d2);
            dimensions.add(d3);
            dimensions.add(d4);
            return dimensions;
        }
    }

    /**
     * 测灵魂-查看结果
     *
     * @param reportId 报告id
     * @return ReportVo
     */
    public ResponseEntity<Object> findReport(String reportId) {
        //1、根据reportId找到对应的报告
        TestSoulReport testSoulReport = testSoulReportApi.findById(reportId);
        //2、封装到vo类中
        ReportVo vo = new ReportVo();
        BeanUtils.copyProperties(testSoulReport, vo);
        //3、根据对应等级查询最新的5个用户（排除自身）userId
        List<Long> userIdList = testSoulReportApi.findByLevel(testSoulReport.getLevel(), 5, UserHolder.getUserId(),testSoulReport.getTestSoulFileId());
        //4、更加userIdList找到对应的用户
        if (userIdList != null && userIdList.size() > 0) {
            List<SimilarYou> similarYous = userInfoApi.findByIdList(userIdList);
            vo.setSimilarYou(similarYous);
        }

        return ResponseEntity.ok(vo);
    }
}
