package com.birdy.blogbackend.controller;

import com.birdy.blogbackend.annotation.AuthCheck;
import com.birdy.blogbackend.domain.ResultUtil;
import com.birdy.blogbackend.domain.entity.Report;
import com.birdy.blogbackend.domain.entity.User;
import com.birdy.blogbackend.domain.enums.ReturnCode;
import com.birdy.blogbackend.domain.vo.request.report.ReportChangeStatusRequest;
import com.birdy.blogbackend.domain.vo.request.report.ReportQueryRequest;
import com.birdy.blogbackend.domain.vo.request.report.ReportRequest;
import com.birdy.blogbackend.domain.vo.response.BaseResponse;
import com.birdy.blogbackend.exception.BusinessException;
import com.birdy.blogbackend.service.MailService;
import com.birdy.blogbackend.service.PermissionService;
import com.birdy.blogbackend.service.ReportService;
import com.birdy.blogbackend.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.birdy.blogbackend.constant.UserConstant.LOGIN_TOKEN;

/**
 * @author birdy
 */
@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {
  @Autowired
  private ReportService reportService;
  @Autowired
  private UserService userService;
  @Autowired
  private MailService mailService;
  @Autowired
  private PermissionService permissionService;

  @PostMapping("")
  public ResponseEntity<BaseResponse<Long>> report(@RequestBody ReportRequest reportRequest, HttpServletRequest request) {
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    // 通过token获取用户
    Report report = new Report();
    User user = userService.getUserByToken(token, request);
    long uid = user.getUid();
    report.setReporter(uid);
    report.setItemId(reportRequest.getItemId());
    report.setReason(reportRequest.getReason() + " " + reportRequest.getDetail());
    report.setType(reportRequest.getType());
    this.reportService.save(report);
    mailService.sendThanksMail(user.getEmail(), report.getId().toString(), request);
    userService.sendReportMailToAdmin(report.getId().toString(), report.getReason(), request);
    return ResultUtil.ok(report.getId());
  }

  @RequestMapping("/page")
  public ResponseEntity<BaseResponse<Page<Report>>> getReportPage(@RequestBody ReportQueryRequest reportQueryRequest, HttpServletRequest request) {
    // 从请求头获取token
    String token = request.getHeader(LOGIN_TOKEN);
    if (token == null) {
      throw new BusinessException(ReturnCode.VALIDATION_FAILED, "Token不存在", request);
    }
    // 通过token获取用户
    Report report = new Report();
    User user = userService.getUserByToken(token, request);
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.orderBy("id", false);
    if (permissionService.checkPermission(user.getUid(), "group.admin")) {
      // 如果 uid 为空,则查询所有
      if (reportQueryRequest.getUid() == null) {
        Page<Report> reportPage = reportService.getMapper().paginate(reportQueryRequest.getCurrent(), reportQueryRequest.getPageSize(), queryWrapper);
        return ResultUtil.ok(reportPage);
      } else {
        // 如果 uid 不为空,则查询该用户的举报
        queryWrapper.eq("reporter", reportQueryRequest.getUid());
        Page<Report> reportPage = reportService.getMapper().paginate(reportQueryRequest.getCurrent(), reportQueryRequest.getPageSize(), queryWrapper);
        return ResultUtil.ok(reportPage);
      }
    } else {
      // 如果不是管理员,则只能查询自己的举报
      queryWrapper.eq("reporter", user.getUid());
      Page<Report> reportPage = reportService.getMapper().paginate(reportQueryRequest.getCurrent(), reportQueryRequest.getPageSize(), queryWrapper);
      return ResultUtil.ok(reportPage);
    }
  }

  @GetMapping("/redirect")
  public ResponseEntity<BaseResponse<String>> redirect(@RequestParam("id") Long id, HttpServletRequest request) {
    Report report = reportService.getById(id);
    if (report == null) {
      throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "Report not found", request);
    }
    return ResultUtil.ok(report.getReason());
  }

  @AuthCheck(must = "group.admin")
  @PostMapping("/status")
  public ResponseEntity<BaseResponse<Boolean>> status(@RequestBody ReportChangeStatusRequest reportChangeStatusRequest, HttpServletRequest request) {
    Report report = reportService.getById(reportChangeStatusRequest.getId());
    if (report == null) {
      throw new BusinessException(ReturnCode.NOT_FOUND_ERROR, "Report not found", request);
    }
    report.setStatus(reportChangeStatusRequest.getStatus());
    reportService.updateById(report);
    mailService.sendReportChangeMailToUser(report, request);
    return ResultUtil.ok(true);
  }

}
