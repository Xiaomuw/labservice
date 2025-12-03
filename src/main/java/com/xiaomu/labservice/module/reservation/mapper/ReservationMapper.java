package com.xiaomu.labservice.module.reservation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomu.labservice.module.reservation.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约 Mapper
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 检查时间冲突
     */
    @Select("SELECT * FROM reservation WHERE lab_id = #{labId} " +
            "AND status IN ('PENDING', 'APPROVED', 'IN_USE') " +
            "AND ((start_time <= #{startTime} AND end_time > #{startTime}) " +
            "OR (start_time < #{endTime} AND end_time >= #{endTime}) " +
            "OR (start_time >= #{startTime} AND end_time <= #{endTime}))")
    List<Reservation> checkTimeConflict(Long labId, LocalDateTime startTime, LocalDateTime endTime);
}

