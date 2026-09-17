package com.atxiaomian.lease.web.app.service;

import com.atxiaomian.lease.web.app.vo.room.RoomItemVo;
import com.atxiaomian.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RoomInfoPaginationTest {

    @Autowired
    private RoomInfoService roomInfoService;

    @Test
    void pageItemReportsTotalAndHonorsPageSize() {
        Page<RoomItemVo> page = new Page<>(1, 1);

        roomInfoService.pageItem(page, new RoomQueryVo());

        assertThat(page.getRecords()).hasSize(1);
        assertThat(page.getTotal()).isGreaterThanOrEqualTo(1);
        assertThat(page.getPages()).isGreaterThanOrEqualTo(1);
    }
}
