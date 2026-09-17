package com.atxiaomian.lease.web.admin.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleTasksTest {
    @Autowired
    private ScheduleTasks scheduleTasks;

    public void test(){
        scheduleTasks.checkLeaseStatus();
    }

}