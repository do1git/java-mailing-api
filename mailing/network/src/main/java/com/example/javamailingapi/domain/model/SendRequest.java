package com.example.javamailingapi.domain.model;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SendRequest {
  @ApiModelProperty(notes = "송신 이메일", position = 1, example = "@naver.com")
  private String senderEmail;
  @ApiModelProperty(notes = "비밀번호", position = 2, example ="''")
  private String password;
  @ApiModelProperty(notes = "수신 이메일", position = 3, example ="''")
  private String receiverEmail;
   @ApiModelProperty(notes = "제목", position = 4, example ="''")
  private String title;
  @ApiModelProperty(notes = "본문", position = 5, example ="''")
  private String content;
  @ApiModelProperty(notes = "파일", position = 6, example ="''")
  private MultipartFile attachment;

}
