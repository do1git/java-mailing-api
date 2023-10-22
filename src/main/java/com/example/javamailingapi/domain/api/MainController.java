package com.example.javamailingapi.domain.api;

import com.example.javamailingapi.domain.model.SendRequest;
import com.example.javamailingapi.domain.service.MailService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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

  @PostMapping("/send")
  @ApiOperation(value = "메일전송", notes = "smtp로 전송")
  public ResponseEntity<?> sendRequest(@RequestBody SendRequest signupDto, Errors errors){
    return null;
  }


}