package com.atxiaomian.lease.web.admin.service;

import com.atxiaomian.lease.model.entity.*;
import com.atxiaomian.lease.model.enums.ItemType;
import com.atxiaomian.lease.web.admin.vo.graph.GraphVo;
import com.atxiaomian.lease.web.admin.vo.room.RoomDetailVo;
import com.atxiaomian.lease.web.admin.vo.room.RoomItemVo;
import com.atxiaomian.lease.web.admin.vo.room.RoomQueryVo;
import com.atxiaomian.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
* @author liubo
* @description 针对表【room_info(房间信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface RoomInfoService extends IService<RoomInfo> {
    void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo);

    IPage<RoomItemVo> pageRoomItemByQuery(IPage<RoomItemVo> page, RoomQueryVo queryVo);

    RoomDetailVo getRoomDetailById(Long id);

    void removeRoomById(Long id);
}
