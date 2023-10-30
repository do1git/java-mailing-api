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
  @ApiModelProperty(notes = "참조", position = 4)
  private List<String> ccList;
  @ApiModelProperty(notes = "숨김참조", position = 5)
  private List<String> bccList;
  @ApiModelProperty(notes = "제목", position = 6, example ="''")
  private String title;
  @ApiModelProperty(notes = "본문", position = 7, example ="''")
  private String content;
  @ApiModelProperty(notes = "파일", position = 8)
  private List<MultipartFile> files;
}
