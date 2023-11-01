package com.example.javamailingapi.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendResponse {

  @ApiModelProperty(notes = "상태코드")
  private int status = 200;
  @ApiModelProperty(notes = "문제원인")
  private String cause;
  @ApiModelProperty(notes = "메세지")
  private String message;

  @ApiModelProperty(notes = "Log")
  private String log;


}
