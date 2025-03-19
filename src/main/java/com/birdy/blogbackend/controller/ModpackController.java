package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.config.ConfigProperties;
import com.birdy.blogbackend.domain.ResultUtil;
import com.birdy.blogbackend.domain.dto.ModpackVO;
import com.birdy.blogbackend.domain.entity.Modpack;
import com.birdy.blogbackend.domain.entity.Photo;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.vo.request.ModpackQueryRequest;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.domain.vo.response.TencentCaptchaResponse;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.ModpackService;
import com.birdy.blogbackend.service.PhotoService;
import com.birdy.blogbackend.service.UserService;
import com.birdy.blogbackend.util.gson.GsonProvider;
import com.birdy.blogbackend.util.tencent.TencentCaptchaUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.birdy.blogbackend.constant.CommonConstant.CAPTCHA_HEADER;
import static com.birdy.blogbackend.constant.UserConstant.LOGIN_TOKEN;

/**
 * @author birdy
 */
@RestController
@RequestMapping("/modpack")
@Slf4j
public class ModpackController {
    public static TencentCaptchaUtil TENCENT_CAPTCHA_UTIL = null;

    @Autowired
    private ConfigProperties configProperties;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModpackService modpackService;

    public void checkCaptcha(HttpServletRequest request) {
        if (!configProperties.captcha.isEnable()) {
            return;
        }
        if (TENCENT_CAPTCHA_UTIL == null) {
            // init
            TENCENT_CAPTCHA_UTIL = new TencentCaptchaUtil(configProperties.tencent.getSecretId(), configProperties.tencent.getSecretKey(), configProperties.captcha.getAppSecretKey());
        }
        // 获取Header里的captcha
        String captcha = request.getHeader(CAPTCHA_HEADER);
        // 将captcha转换为CaptchaResult
        TencentCaptchaResponse captchaResult = GsonProvider.normal().fromJson(captcha, TencentCaptchaResponse.class);
        if (captchaResult == null) {
            throw new BusinessException(ReturnCode.VALIDATION_FAILED, "请进行人机验证", request);
        }
        // 验证captcha
        try {
            TENCENT_CAPTCHA_UTIL.isCaptchaValid(captchaResult, Long.parseLong(configProperties.captcha.getCaptchaAppId()), request);
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, e.getMessage(), request);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<BaseResponse<Long>> uploadModpack(
            @RequestParam("modpackFile") MultipartFile modpackFile,
            @RequestParam("logoFile") MultipartFile logoFile,
            @RequestParam("launchArguments") String launchArguments,
            @RequestParam("brief") String brief,
            @RequestParam("client") String client,
            @RequestParam("version") String version,
            HttpServletRequest request) {

        checkCaptcha(request);

        // Assume authentication is needed similarly to BlogController
        String token = request.getHeader(LOGIN_TOKEN);
        User user = userService.getUserByToken(token, request);

        // Create modpack entity
        Modpack modpack = new Modpack();
        modpack.setLaunchArguments(launchArguments);
        modpack.setBrief(brief);
        modpack.setClient(client);
        modpack.setVersion(version);
        modpack.setUid(user.getUid());
        modpack.setStatus(0);

        try {
            // Handle logo file
            String logoExt = Objects.requireNonNull(logoFile.getOriginalFilename())
                    .substring(logoFile.getOriginalFilename().lastIndexOf("."));
            byte[] logoData = logoFile.getBytes();
            String logoMd5 = DigestUtils.md5DigestAsHex(logoData);

            String ext = Objects.requireNonNull(logoFile.getOriginalFilename()).substring(logoFile.getOriginalFilename().lastIndexOf("."));

            // Save logo using PhotoService
            String fileName = logoMd5 + ext;
            Photo photo = photoService.savePhotoByMd5(logoMd5, logoExt, logoData.length, request);
            Path path = Paths.get("photos", fileName);
            Files.createDirectories(path.getParent());
            byte[] bytes = logoFile.getBytes();
            Files.write(path, bytes);
            modpack.setLogoId(photo.getPid());

            // Handle modpack file
            String modpackExt = Objects.requireNonNull(modpackFile.getOriginalFilename())
                    .substring(modpackFile.getOriginalFilename().lastIndexOf("."));
            byte[] modpackData = modpackFile.getBytes();
            String modpackMd5 = DigestUtils.md5DigestAsHex(modpackData);
            String modpackFileName = modpackMd5 + modpackExt;

            // Save modpack file to disk
            Path modpackPath = Paths.get("modpacks", modpackFileName);
            Files.createDirectories(modpackPath.getParent());
            Files.write(modpackPath, modpackData);

            modpack.setFilePath(modpackFileName);
            modpack.setFileSize((long) modpackData.length);
            modpack.setMd5(modpackMd5);

            // Save to database
            modpackService.save(modpack);

            return ResultUtil.ok(modpack.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, "Failed to save modpack", request);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<Page<ModpackVO>>> getModpackList(ModpackQueryRequest modpackQueryRequest, HttpServletRequest request) {
        String token = request.getHeader(LOGIN_TOKEN);
        User user = userService.getUserByToken(token, request);

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid", user.getUid());

        Page<Modpack> page = modpackService.getMapper()
                .paginate(Page.of(modpackQueryRequest.getCurrent(), modpackQueryRequest.getPageSize()), queryWrapper);
        Page<ModpackVO> modpackVOPage = page.map(i -> {
            ModpackVO vo = new ModpackVO();
            BeanUtils.copyProperties(i, vo);
            Photo photo = photoService.getById(i.getLogoId());
            vo.setLogoMd5(photo.getMd5());
            return vo;
        });
        return ResultUtil.ok(modpackVOPage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> deleteModpack(@PathVariable("id") Long id, HttpServletRequest request) {
        String token = request.getHeader(LOGIN_TOKEN);
        User user = userService.getUserByToken(token, request);
        Modpack modpack = modpackService.getById(id);
        if (modpack == null) {
            throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "Modpack not found", request);
        }
        modpackService.removeById(id);
        return ResultUtil.ok(id);
    }
}
