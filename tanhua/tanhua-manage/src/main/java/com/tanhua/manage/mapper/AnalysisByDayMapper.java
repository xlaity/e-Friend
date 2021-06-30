package com.tanhua.manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.manage.domain.AnalysisByDay;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AnalysisByDayMapper extends BaseMapper<AnalysisByDay> {

    @Select("select sum(num_active) num_active from tb_analysis_by_day" +
            " where record_date BETWEEN #{start} and #{end}")
    public Long findNumActiveByDate(@Param("start") String start, @Param("end") String end);
}