package com.atxiaomian.lease.web.admin.service;

import com.atxiaomian.lease.web.admin.vo.login.CaptchaVo;
import com.atxiaomian.lease.web.admin.vo.login.LoginVo;
import com.atxiaomian.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    CaptchaVo getCaptcha();

    String login(LoginVo loginVo);

    SystemUserInfoVo getLoginUserInfoById(Long userId);
}
