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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MainController {

    private final MailService mailService;

    @ApiOperation(value = "메일전송", notes = "smtp로 전송")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = SendResponse.class)})
    @PostMapping(
            value = "/send",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SendResponse sendRequest(
            @ApiParam(value = "송신 이메일", required = true)
            @RequestParam("1. senderEmail") String senderEmail,

            @ApiParam(value = "비밀번호", required = true)
            @RequestParam("2. password") String password,

            @ApiParam(value = "수신 이메일", required = true)
            @RequestParam("3. receiverEmail") String receiverEmail,

            @ApiParam(value = "제목", required = true)
            @RequestParam("4. title") String title,

            @ApiParam(value = "본문", required = true)
            @RequestParam("5. content") String content,

            @ApiParam(value = "첨부파일", required = false)
            @RequestParam(value = "6. attachment", required = false) MultipartFile attachment) {

        SendRequest sendRequest = new SendRequest();
        sendRequest.setSenderEmail(senderEmail);
        sendRequest.setPassword(password);
        sendRequest.setReceiverEmail(receiverEmail);
        sendRequest.setTitle(title);
        sendRequest.setContent(content);
        if (attachment != null && !attachment.isEmpty()) {
            sendRequest.setAttachment(attachment);
        }

        SendResponse sendResponse = mailService.sendEmail(sendRequest);

        return sendResponse;
    }
}