package com.example.javamailingapi.domain.api;

import com.example.javamailingapi.domain.model.SendRequest;
import com.example.javamailingapi.domain.model.SendResponse;
import com.example.javamailingapi.domain.service.MailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MainController {

  private final MailService mailService;

  @ApiOperation(value = "메일전송", notes = "smtp로 전송")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = SendResponse.class)})
  @PostMapping(
      value = "/send",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public SendResponse sendRequest(
      @ApiParam(value = "상품 연동(최초 연동) / 재연동(이미 연동한 경우)", required = true) @RequestBody SendRequest sendRequest) {
    SendResponse sendResponse = mailService.sendEmail(sendRequest);

    return sendResponse;
  }


}