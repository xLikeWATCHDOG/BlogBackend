package com.birdy.blogbackend.util;

import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.exception.BusinessException;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 提前从 xdb 文件中加载出来 VectorIndex 数据，然后全局缓存
 * 每次创建 Searcher 对象的时候使用全局的 VectorIndex 缓存可以减少一次固定的 IO 操作
 * 从而加速查询，减少 IO 压力。
 *
 * @author birdy
 */
@Getter
@Slf4j
public class IpRegionUtil {
    private static final IpRegionUtil IP_REGION_UTIL = new IpRegionUtil();
    private static final String DB_PATH = "./data/ip2region.xdb";
    // 1、从 dbPath 中预先加载 VectorIndex 缓存，并且把这个得到的数据作为全局变量，后续反复使用。
    private byte[] vIndex;

    public IpRegionUtil() {
        try {
            vIndex = Searcher.loadVectorIndexFromFile(DB_PATH);
        } catch (Exception e) {
            System.out.printf("failed to load vector index from `%s`: %s\n", DB_PATH, e);
            return;
        }
    }

    public static IpRegionUtil getInstance() {
        return IP_REGION_UTIL;
    }

    public Region search(String ip) {
        // 2、使用全局的 vIndex 创建带 VectorIndex 缓存的查询对象。
        Searcher searcher;
        try {
            searcher = Searcher.newWithVectorIndex(DB_PATH, vIndex);
        } catch (Exception e) {
            log.error("无法创建向量索引缓存搜索器{}:", DB_PATH, e);
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, "无法创建向量索引缓存搜索器" + DB_PATH, null);
        }

        Region r = new Region(ip);
        // 3、查询
        try {
            long sTime = System.nanoTime();
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            // 国家|区域|省份|城市|ISP
            String[] regionInfo = region.split("\\|");
            r.setCountry(Objects.equals(regionInfo[0], "0") ? null : regionInfo[0]);
            r.setRegion(Objects.equals(regionInfo[1], "0") ? null : regionInfo[1]);
            r.setProvince(Objects.equals(regionInfo[2], "0") ? null : regionInfo[2]);
            r.setCity(Objects.equals(regionInfo[3], "0") ? null : regionInfo[3]);
            r.setIsp(Objects.equals(regionInfo[4], "0") ? null : regionInfo[4]);
            r.setIoCount(searcher.getIOCount());
            r.setCost(cost);
            log.info("region: {}, ioCount: {}, took: {} μs", region, searcher.getIOCount(), cost);
        } catch (Exception e) {
            log.error("搜索失败({}):", ip, e);
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, "搜索失败:" + ip, null);
        }
        // 4、关闭资源
        try {
            searcher.close();
        } catch (Exception e) {
            log.error("关闭搜索器失败:", e);
            throw new BusinessException(ReturnCode.SYSTEM_ERROR, "关闭搜索器失败", null);
        }
        return r;
    }

    @Data
    public static class Region {
        private final String ip;
        /**
         * 国家
         */
        private String country;
        /**
         * 区域
         */
        private String region;
        /**
         * 省份
         */
        private String province;
        /**
         * 城市
         */
        private String city;
        /**
         * 服务商
         */
        private String isp;
        private int ioCount;
        private long cost;
    }
}
