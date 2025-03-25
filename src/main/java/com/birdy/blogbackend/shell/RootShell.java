package com.birdy.blogbackend.shell;

import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * @author birdy
 */
@ShellComponent
@ShellCommandGroup("项目根命令")
@Slf4j
public class RootShell {
  // 正常关闭
  public static final int EXIT_CODE_SHUTDOWN = 0;
  // 重启
  public static final int EXIT_CODE_RESTART = 2;

  @ShellMethod("使项目正常关闭")
  public void shutdown() {
    try {
      log.info("用户触发了项目关闭操作，正在关闭项目...");
      System.exit(EXIT_CODE_SHUTDOWN);
    } catch (Exception e) {
      log.error("关闭项目时发生异常: {}", e.getMessage(), e);
      // 异常退出
      System.exit(1);
    }
  }

  @ShellMethod("重启项目")
  public void restart() {
    try {
      log.info("用户触发了项目重启操作，正在重启项目...");
      // 触发重启
      System.exit(EXIT_CODE_RESTART);
    } catch (Exception e) {
      log.error("重启项目时发生异常: {}", e.getMessage(), e);
      // 异常退出
      System.exit(1);
    }
  }
}
