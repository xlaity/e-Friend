package com.tanhua.server.test;

import com.google.common.collect.Lists;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.entity.Option;
import com.tanhua.domain.entity.Question;
import com.tanhua.domain.mongo.TestSoulFile;
import com.tanhua.dubbo.api.TestSoulApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作者：czd
 * 目的：添加问卷数据
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSoulFileTest {

    @Reference
    private TestSoulApi testSoulApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private OssTemplate ossTemplate;

    //问题question在redis中的key
    private String questionKey = "tanhua_question_id";

    //选项option在redis中的key
    private String optionKey = "tanhua_option_id";


    //添加问卷  --  初级灵魂题
    @Test
    public void  saveTestSoulFile() throws FileNotFoundException {
        TestSoulFile testSoulFile = new TestSoulFile();
        testSoulFile.setName("初级灵魂题");
        //获取图片并上传到阿里云oss
        /*String fileName = "D:\\2.jpg";
        File file = new File(fileName);
        String url = ossTemplate.upload(fileName, new FileInputStream(file));*/
        String url = "https://lk-bukect.oss-cn-guangzhou.aliyuncs.com/images/2021/07/03/cefdebc2-f396-4b09-83a4-286c87beac56.jpg";
        testSoulFile.setCover(url);
        testSoulFile.setLevel("初级");
        testSoulFile.setStar(2);
        //添加问题
        //1、问卷列表
        List<Question> questions = new ArrayList<>();
        //2、设置quesitonId在redis中的初始值
        redisTemplate.opsForValue().set(questionKey,"0");
        //3、设置optionId在redis中的初始值
        redisTemplate.opsForValue().set(optionKey,"0");
        ArrayList<String> opTexts = new ArrayList<>();

        //第1个问题
        opTexts.add("早晨");
        opTexts.add("下午及傍晚");
        opTexts.add("夜里");
        Question q1 = this.getOneQuestion("你何时感觉最好？", opTexts);
        questions.add(q1);

        //第2个问题
        opTexts.clear();
        opTexts.add("大步地快走");
        opTexts.add("小步地块走");
        opTexts.add("不快，仰着头面对着世界");
        opTexts.add("不快，低着头");
        opTexts.add("很慢");
        Question q2 = this.getOneQuestion("你走路时是", opTexts);
        questions.add(q2);

        //第3个问题
        opTexts.clear();
        opTexts.add("手臂交叠站着");
        opTexts.add("双手紧握着");
        opTexts.add("一只手或两只手放在臀部");
        opTexts.add("碰着或推着与你说话的人");
        opTexts.add("玩着你的耳朵，摸着你的下巴或用手整理头发");
        Question q3 = this.getOneQuestion("和人说话时", opTexts);
        questions.add(q3);

        //第4个问题
        opTexts.clear();
        opTexts.add("两膝盖并拢");
        opTexts.add("两腿交叉");
        opTexts.add("两腿伸直");
        opTexts.add("一腿蜷在身下");
        Question q4 = this.getOneQuestion("坐着休息时", opTexts);
        questions.add(q4);

        //第5个问题
        opTexts.clear();
        opTexts.add("一个欣赏的大笑");
        opTexts.add("笑着，但不大声");
        opTexts.add("轻声地笑");
        opTexts.add("羞怯的微笑");
        Question q5 = this.getOneQuestion("碰到你感到发笑的事时，你的反应是", opTexts);
        questions.add(q5);

        //第6个问题
        opTexts.clear();
        opTexts.add("很大声地入场以引起注意");
        opTexts.add("安静地入场，找你认识的人");
        opTexts.add("非常安静地入场，尽量保持不被注意");
        Question q6 = this.getOneQuestion("当你去一个派对或社交场合时", opTexts);
        questions.add(q6);

        //第7个问题
        opTexts.clear();
        opTexts.add("欢迎他");
        opTexts.add("感到非常愤怒");
        opTexts.add("在上述两极端之间");
        Question q7 = this.getOneQuestion("当你非常专心工作时，有人打断你，你会", opTexts);
        questions.add(q7);

        //第8个问题
        opTexts.clear();
        opTexts.add("红或橘色");
        opTexts.add("黑色");
        opTexts.add("黄色或浅蓝色");
        opTexts.add("绿色");
        opTexts.add("深蓝色或紫色");
        opTexts.add("白色");
        opTexts.add("棕色或灰色");
        Question q8 = this.getOneQuestion("下列颜色中，你最喜欢哪一种颜色？", opTexts);
        questions.add(q8);

        //第9个问题
        opTexts.clear();
        opTexts.add("仰躺，伸直");
        opTexts.add("俯躺，伸直");
        opTexts.add("侧躺，微蜷");
        opTexts.add("头睡在一手臂上");
        opTexts.add("被子盖过头");
        Question q9 = this.getOneQuestion("临入睡的前几分钟，你在床上的姿势是", opTexts);
        questions.add(q9);

        //第10个问题
        opTexts.clear();
        opTexts.add("落下");
        opTexts.add("打架或挣扎");
        opTexts.add("找东西或人");
        opTexts.add("飞或漂浮");
        opTexts.add("你平常不做梦");
        opTexts.add("你的梦都是愉快的");
        Question q10 = this.getOneQuestion("你经常梦到自己在", opTexts);
        questions.add(q10);

        testSoulFile.setQuestions(questions);
        testSoulFile.setCreated(System.currentTimeMillis());
        testSoulApi.save(testSoulFile);
    }



    //添加问卷  --  中级灵魂题
    @Test
    public void  saveTestSoulFile2() throws FileNotFoundException {
        TestSoulFile testSoulFile = new TestSoulFile();
        testSoulFile.setName("中级灵魂题");
        //获取图片并上传到阿里云oss
        /*String fileName = "D:\\2.jpg";
        File file = new File(fileName);
        String url = ossTemplate.upload(fileName, new FileInputStream(file));*/
        //固定图片链接，不做更改
        String url = "https://lk-bukect.oss-cn-guangzhou.aliyuncs.com/images/2021/07/03/cefdebc2-f396-4b09-83a4-286c87beac56.jpg";
        testSoulFile.setCover(url);
        testSoulFile.setLevel("中级");
        testSoulFile.setStar(3);
        //添加问题
        //1、问卷列表
        List<Question> questions = new ArrayList<>();
        ArrayList<String> opTexts = new ArrayList<>();

        //第1个问题
        opTexts.add("早晨");
        opTexts.add("下午及傍晚");
        opTexts.add("夜里");
        Question q1 = this.getOneQuestion("你何时感觉最好？", opTexts);
        questions.add(q1);

        //第2个问题
        opTexts.clear();
        opTexts.add("大步地快走");
        opTexts.add("小步地块走");
        opTexts.add("不快，仰着头面对着世界");
        opTexts.add("不快，低着头");
        opTexts.add("很慢");
        Question q2 = this.getOneQuestion("你走路时是", opTexts);
        questions.add(q2);

        testSoulFile.setQuestions(questions);
        testSoulFile.setCreated(System.currentTimeMillis());
        testSoulApi.save(testSoulFile);
    }


    //添加问卷  --  高级灵魂题
    @Test
    public void  saveTestSoulFile3() throws FileNotFoundException {
        TestSoulFile testSoulFile = new TestSoulFile();
        testSoulFile.setName("高级灵魂题");
        //获取图片并上传到阿里云oss
        /*String fileName = "D:\\2.jpg";
        File file = new File(fileName);
        String url = ossTemplate.upload(fileName, new FileInputStream(file));*/
        String url = "https://lk-bukect.oss-cn-guangzhou.aliyuncs.com/images/2021/07/03/cefdebc2-f396-4b09-83a4-286c87beac56.jpg";
        testSoulFile.setCover(url);
        testSoulFile.setLevel("高级");
        testSoulFile.setStar(5);
        //添加问题
        //1、问卷列表
        List<Question> questions = new ArrayList<>();
        ArrayList<String> opTexts = new ArrayList<>();

        //第1个问题
        opTexts.add("早晨");
        opTexts.add("下午及傍晚");
        opTexts.add("夜里");
        Question q1 = this.getOneQuestion("你何时感觉最好？", opTexts);
        questions.add(q1);

        testSoulFile.setQuestions(questions);
        testSoulFile.setCreated(System.currentTimeMillis());
        testSoulApi.save(testSoulFile);
    }



    //封装question并返回
    public Question getOneQuestion(String questionText,List<String> opTexts){
        Question q = new Question();
        Long questionId = redisTemplate.opsForValue().increment(questionKey);
        q.setId(questionId.toString());
        q.setQuestion(questionText);
        ArrayList<Option> options = new ArrayList<>();
        for (String op : opTexts) {
            Option option = new Option();
            Long optionId = redisTemplate.opsForValue().increment(optionKey);
            option.setId(optionId.toString());
            option.setOption(op);
            options.add(option);
        }
        q.setOptions(options);
        return q;
    }


}
