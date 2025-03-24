package com.birdy.blogbackend.domain.vo.response;

import com.birdy.blogbackend.domain.enums.ReturnCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author birdy
 */
@Data
@Schema(description = "通用返回类")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponse<T> implements Serializable {
  @Schema(description = "状态码", example = "20000")
  private int code;

  @Schema(description = "返回数据")
  private T data;

  @Schema(description = "消息")
  private String message;

  @Schema(description = "请求信息")
  private RequestInfo requestInfo;

  public BaseResponse(int code, T data, String message) {
    this.code = code;
    this.data = data;
    this.message = message;
  }

  public BaseResponse(int code, T data) {
    this(code, data, "");
  }

  public BaseResponse(ReturnCode returnCode) {
    this(returnCode.getCode(), null, returnCode.getMessage());
  }

  public BaseResponse(ReturnCode returnCode, T data) {
    this(returnCode.getCode(), data, returnCode.getMessage());
  }


  public static <T> BaseResponse<T> success(T data) {
    BaseResponse<T> response = new BaseResponse<>();
    response.setCode(ReturnCode.SUCCESS);
    response.setData(data);
    return response;
  }

  public static <T> BaseResponse<T> error(ReturnCode returnCode) {
    BaseResponse<T> response = new BaseResponse<>();
    response.setCode(returnCode);
    return response;
  }

  public static <T> BaseResponse<T> error(ReturnCode returnCode, String message) {
    BaseResponse<T> response = new BaseResponse<>();
    response.setMessage(message);
    response.setCode(returnCode);
    return response;
  }

  public void setCode(ReturnCode returnCode) {
    this.code = returnCode.getCode();
    if (this.message == null || this.message.isEmpty()) {
      this.message = returnCode.getMessage();
    }
  }

  @Data
  @Schema(description = "发起请求的信息，这个部分信息由后端自动填充")
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RequestInfo {
    @Schema(description = "请求ID")
    private String requestId;
    @Schema(description = "请求耗时")
    private long cost;
  }
}
