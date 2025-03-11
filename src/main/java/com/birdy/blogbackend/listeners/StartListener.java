package com.birdy.blogbackend.listeners;

import com.birdy.blogbackend.BlogBackendApplicationKt;
import com.birdy.blogbackend.util.IpRegionUtil;
import com.birdy.blogbackend.util.crypto.AESUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author birdy
 */
@Slf4j
@Component
public class StartListener {
    public static void createDirectory(String... paths) {
        Path path = Paths.get("", paths);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error("Failed to create directory: " + path, e);
        }
    }

    private static void createEmptyFile(String... paths) {
        Path path = Paths.get("", paths);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            log.error("Failed to create an empty file: " + path, e);
        }
    }

    private static void releaseConfigFile(String file) {
        Path path = Paths.get("config", file);
        if (!Files.exists(path)) {
            log.warn("Start releasing the file: " + path);
            try {
                Files.copy(Objects.requireNonNull(BlogBackendApplicationKt.class.getClassLoader().getResourceAsStream(file)), path);
            } catch (IOException e) {
                log.error("Failed to copy file: " + path, e);
            }
            log.info(path + " released.");
        }
    }

    @PostConstruct
    public void init() {
        log.info("StartListener init.\n开始检查项目完整性(这可能需要一些时间)...");
        // 创建一个隐藏的文件.initiated,检查是否已经初始化,如果已经初始化则不再初始化
        Path initiated = Paths.get(".initiated");
        if (Files.exists(initiated)) {
            log.info("项目已经初始化: .initiated");
            return;
        }
        createDirectory("./data");
        // 检查./data/ip2region.xdb是否存在
        String ipDbPath = IpRegionUtil.DB_PATH;
        if (!Files.exists(Paths.get(ipDbPath))) {
            log.error("ip2region.xdb 文件不存在: {}!\n", ipDbPath);
            log.error("开始下载ip2region.xdb文件，请稍等...");
            IpRegionUtil.getInstance().downloadIpDb();
        }
        // 释放config/application.yml
        if (!Files.exists(Paths.get("./config/application.yml"))) {
            log.error("application.yml 文件不存在: ./config/application.yml!\n");
            log.error("开始释放application.yml文件，请稍等...");
            releaseConfigFile("application.yml");
            releaseConfigFile("application.properties");
        }
        createDirectory("data", "alipay");
        createDirectory("data", "wechat");
        createEmptyFile("data", "alipay", "alipayCertPublicKey.crt");
        createEmptyFile("data", "alipay", "alipayRootCert.crt");
        createEmptyFile("data", "alipay", "appCertPublicKey.crt");
        createEmptyFile("data", "alipay", "privateKey.txt");
        createEmptyFile("data", "alipay", "publicKey.txt");
        createEmptyFile("data", "wechat", "apiclient_key.pem");

        AESUtil.init();

        // 创建一个隐藏的文件.initiated,标记项目已经初始化
        try {
            Files.createFile(initiated);
        } catch (IOException e) {
            log.error("Failed to create file: .initiated", e);
        }
    }
}

