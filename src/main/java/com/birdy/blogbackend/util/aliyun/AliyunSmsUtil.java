package com.birdy.blogbackend.util.aliyun;


import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.birdy.blogbackend.config.ConfigProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author birdy
 */
@Data
@Component
@Slf4j
public class AliyunSmsUtil {
    @Autowired
    private ConfigProperties configProperties;

    //短信API产品名称（短信产品名固定，无需修改）
    private final String product = "Dysmsapi";
    //短信API产品域名（接口地址固定，无需修改）
    private final String domain = "dysmsapi.aliyuncs.com";

    /**
     * 发送验证码
     *
     * @param param 验证码
     * @param phone 手机号
     * @return
     */
    public boolean send(Map<String, String> param, String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }

        //default 地域节点，默认就好  后面是 阿里云的 id和秘钥（这里记得去阿里云复制自己的id和秘钥哦）
        DefaultProfile profile = DefaultProfile.getProfile("default", configProperties.getAliyun().getSecretId(),
                configProperties.getAliyun().getSecretKey());
        IAcsClient client = new DefaultAcsClient(profile);

        //这里不能修改
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");

        request.setAction("SendSms");
        //手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //申请阿里云 签名名称（暂时用阿里云测试的，自己还不能注册签名）
        request.putQueryParameter("SignName", configProperties.getSms().getSignName());
        //申请阿里云 模板code（用的也是阿里云测试的）
        request.putQueryParameter("TemplateCode", configProperties.getSms().getTemplateCode());
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));

        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
