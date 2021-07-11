package com.tanhua.manage.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.manage.mapper.LogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.manage.domain.AnalysisByDay;
import com.tanhua.manage.mapper.AnalysisByDayMapper;
import com.tanhua.manage.utils.ComputeUtil;
import com.tanhua.manage.vo.AnalysisSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AnalysisByDayService extends ServiceImpl<AnalysisByDayMapper, AnalysisByDay> {

    @Autowired
    private LogMapper logMapper;

    /**
     * 接口名称：概要统计信息
     */
    public ResponseEntity<Object> summary() {
        // 1.查询累计用户数
        // select sum(num_registered) num_registered from tb_analysis_by_day
        AnalysisByDay analysisByDay = query().select("sum(num_registered) num_registered").one();
        Integer numRegistered = analysisByDay.getNumRegistered();

        // 2.查询今日新增、今日活跃、今日登录数
        // select * from tb_analysis_by_day where record_date = '2021-03-06'
        Date date = new Date();
        String now = new SimpleDateFormat("yyyy-MM-dd").format(date);
        AnalysisByDay recodeToday = query().eq("record_date", now).one();
        // 今日新增
        Integer numRegisteredToday = 0;
        // 今日活跃
        Integer numActiveToday = 0;
        // 今日登录
        Integer numLoginToday = 0;
        if(recodeToday!=null){
            // 今日新增
            numRegisteredToday = recodeToday.getNumRegistered();
            // 今日活跃
            numActiveToday = recodeToday.getNumActive();
            // 今日登录
            numLoginToday = recodeToday.getNumLogin();
        }

        // 3.查询昨日新增、昨日活跃、昨日登录数
        // select * from tb_analysis_by_day where record_date = '2021-03-05'
        AnalysisByDay recodeYes = query().eq("record_date", ComputeUtil.offsetDay(date, -1)).one();
        // 昨日新增
        Integer numRegisteredYes = 0;
        // 昨日活跃
        Integer numActiveYes = 0;
        // 昨日登录
        Integer numLoginYes = 0;
        if (recodeYes != null) {
            numRegisteredYes = recodeYes.getNumRegistered();
            numActiveYes = recodeYes.getNumActive();
            numLoginYes = recodeYes.getNumLogin();
        }

        // 4.查询过去七天活跃、30天活跃数
        Long day7 = this.getBaseMapper().findNumActiveByDate(ComputeUtil.offsetDay(date, -7), now);
        Long day30 = this.getBaseMapper().findNumActiveByDate(ComputeUtil.offsetDay(date, -30), now);

        // 5.封装返回结果
        AnalysisSummaryVo vo = new AnalysisSummaryVo();

        // 设置累计用户
        vo.setCumulativeUsers(numRegistered.longValue());

        // 设置今日新增
        vo.setNewUsersToday(numRegisteredToday.longValue());
        // 设置今日登录
        vo.setLoginTimesToday(numLoginToday.longValue());
        // 设置今日活跃
        vo.setActiveUsersToday(numActiveToday.longValue());

        // 设置过去7天活跃
        vo.setActivePassWeek(day7);
        // 设置过去30天活跃
        vo.setActivePassMonth(day30);

        // 设置新增用户涨跌率
        vo.setNewUsersTodayRate(ComputeUtil.computeRate(numRegisteredToday, numRegisteredYes));
        // 设置登录次数涨跌率
        vo.setLoginTimesTodayRate(ComputeUtil.computeRate(numLoginToday, numLoginYes));
        // 设置活跃用户涨跌率
        vo.setActiveUsersTodayRate(ComputeUtil.computeRate(numActiveToday, numActiveYes));

        return ResponseEntity.ok(vo);
    }

    /**
     * 统计log表中数据，设置到统计表中
     */
    public void anasysis() {
        // 1.查询是否存在当天的统计数据
        Date date = new Date();
        String now = new SimpleDateFormat("yyyy-MM-dd").format(date);
        QueryWrapper<AnalysisByDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_date", now);
        AnalysisByDay analysisByDay = this.getBaseMapper().selectOne(queryWrapper);

        // 2.判断当前统计数据
        if (analysisByDay == null) {
            analysisByDay = new AnalysisByDay();
            // 当前时间对象, 这样Date就不包含时分秒，数据库要求没时分秒
            Date nowDate = DateUtil.parse(now);
            analysisByDay.setRecordDate(nowDate);
            analysisByDay.setCreated(new Date());
            analysisByDay.setUpdated(new Date());
            this.getBaseMapper().insert(analysisByDay);
        }

        // 3.查询log表，得到统计数据，设置到统计日表
        // 查询今日新增用户数
        Long numRegister = logMapper.queryNumsByType(now, "0102");
        // 查询今日登录数
        Long numLogin = logMapper.queryNumsByType(now, "0101");
        // 查询今日活跃用户数
        Long numActive = logMapper.queryNumsByDate(now);
        // 查询次日留存数
        Long nunRetention = logMapper.queryRetention1d(now, ComputeUtil.offsetDay(date, -1));

        // 4.更新统计日表
        analysisByDay.setNumRegistered(numRegister.intValue());
        analysisByDay.setNumActive(numActive.intValue());
        analysisByDay.setNumLogin(numLogin.intValue());
        analysisByDay.setNumRetention1d(nunRetention.intValue());

        this.getBaseMapper().updateById(analysisByDay);

    }


    public ResponseEntity<Object> usersData(Long sd, Long ed, Integer type) {
        //今年开始时间
        Date nowS = new Date(sd);
        //今年结束时间
        Date nowE = new Date(ed);

        //去年开始时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowS);
        calendar.add(Calendar.YEAR, -1);
        Date lastS = calendar.getTime();
        //去年结束时间
        calendar.setTime(nowE);
        calendar.add(Calendar.YEAR, -1);
        Date lastE = calendar.getTime();


        //创建一个List集合来接收
        List<AnalysisSummaryVo> analysisByDayVosNow = new ArrayList<>();
        List<AnalysisSummaryVo> analysisByDayVosLast = new ArrayList<>();

        //计算开始和结束时间之间有多少天
        long days = (ed - sd) / (1000 * 60 * 60 * 24);

        //法一，多次查询数据库，效率较低
        //设置今年开始时间
        calendar.setTime(nowS);
        for (long i = 0; i <= days; i++) {
            //创建vo对象
            AnalysisSummaryVo analysisByDayVo = new AnalysisSummaryVo();
            //获取日期时间
            Date date = calendar.getTime();
            String dateS = new SimpleDateFormat("yyyy-MM-dd").format(date);
            //封装回vo类中
            analysisByDayVo.setTitle(dateS);
            //根据数据库字段查询日期
            AnalysisByDay analysisByDay = query().eq("record_date", dateS).one();
            //判断analysisByDay不等于空
            if (analysisByDay != null) {
                //判断type为101，设置他的新增人数
                if (type == 101) {
                    analysisByDayVo.setAmount(analysisByDay.getNumRegistered());
                    //判断type为102，设置他的活跃人数
                } else if (type == 102) {
                    analysisByDayVo.setAmount(analysisByDay.getNumActive());
                    //判断type为103，设置他的次日留存人数
                } else if (type == 103) {
                    analysisByDayVo.setAmount(analysisByDay.getNumRetention1d());
                }
            }
            //添加vo到一个新的List集合中
            analysisByDayVosNow.add(analysisByDayVo);
            //每次循环完，让天数自动加一
            calendar.add(Calendar.DATE, 1);
        }


        //法二
        //设置去年的开始时间
        calendar.setTime(lastS);
        ArrayList<String> list = new ArrayList<>();
        for (long i = 0; i <= days; i++) {
            Date time = calendar.getTime();
            list.add(new SimpleDateFormat("yyyy-MM-dd").format(time));
            calendar.add(Calendar.DATE, 1);
        }

        List<AnalysisByDay> analysisByDayList = query().in("record_date", list).list();
        for (AnalysisByDay analysisByDay : analysisByDayList) {
            AnalysisSummaryVo analysisByDayVo = new AnalysisSummaryVo();
            if(analysisByDay!=null){
                //判断type为101，设置他的新增人数
                if (type == 101) {
                    analysisByDayVo.setAmount(analysisByDay.getNumRegistered());
                    //判断type为102，设置他的活跃人数
                } else if (type == 102) {
                    analysisByDayVo.setAmount(analysisByDay.getNumActive());
                    //判断type为103，设置他的次日留存人数
                } else if (type == 103) {
                    analysisByDayVo.setAmount(analysisByDay.getNumRetention1d());
                }
            }

            analysisByDayVo.setTitle(analysisByDay.getRecordDate().toString());
            analysisByDayVosLast.add(analysisByDayVo);
        }

        //把两个集合封装成map返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("thisYear", analysisByDayVosNow);
        map.put("lastYear", analysisByDayVosLast);

        return ResponseEntity.ok(map);
    }
}

