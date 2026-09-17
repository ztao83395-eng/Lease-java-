package com.atxiaomian.lease.web.app.service.impl;

import com.atxiaomian.lease.common.constant.RedisConstant;
import com.atxiaomian.lease.common.exception.LeaseException;
import com.atxiaomian.lease.common.result.ResultCodeEnum;
import com.atxiaomian.lease.common.utils.JwtUtil;
import com.atxiaomian.lease.common.utils.VerifyCodeUtil;
import com.atxiaomian.lease.model.entity.UserInfo;
import com.atxiaomian.lease.model.enums.BaseStatus;
import com.atxiaomian.lease.web.app.mapper.UserInfoMapper;
import com.atxiaomian.lease.web.app.service.LoginService;
import com.atxiaomian.lease.web.app.service.SmsService;
import com.atxiaomian.lease.web.app.service.UserInfoService;
import com.atxiaomian.lease.web.app.vo.user.LoginVo;
import com.atxiaomian.lease.web.app.vo.user.UserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void getSMSCode(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }

        String code = VerifyCodeUtil.getVerifyCode(6);
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl >= 0
                    && RedisConstant.APP_LOGIN_CODE_TTL_SEC - ttl < RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC) {
                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
            }
        }

        smsService.sendCode(phone, code);
        redisTemplate.opsForValue().set(key, code, RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);
    }

    @Override
    public String login(LoginVo loginVo) {
        if (loginVo == null || !StringUtils.hasText(loginVo.getPhone())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY);
        }
        if (!StringUtils.hasText(loginVo.getCode())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }
        String phone = loginVo.getPhone().trim();
        String submittedCode = loginVo.getCode().trim();
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;
        String code = redisTemplate.opsForValue().get(key);

        if (!StringUtils.hasText(code)) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED);
        }
        if (!code.equals(submittedCode)) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR);
        }

        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, phone);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setNickname("用户-" + phone.substring(Math.max(0, phone.length() - 4)));
            userInfoMapper.insert(userInfo);
        } else if (userInfo.getStatus() == BaseStatus.DISABLE) {
            throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }

        // A verification code is single-use. Do this only after all checks pass.
        redisTemplate.delete(key);
        return JwtUtil.createToken(userInfo.getId(), userInfo.getPhone());
    }

    @Override
    public UserInfoVo getUserInfoById(Long userId) {
        UserInfo userInfo = userInfoService.getById(userId);
        return new UserInfoVo(userInfo.getNickname(), userInfo.getAvatarUrl());
    }
}
