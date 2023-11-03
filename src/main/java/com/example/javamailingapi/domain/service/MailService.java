package com.example.javamailingapi.domain.service;

import com.example.javamailingapi.domain.model.SendRequest;
import com.example.javamailingapi.domain.model.SendResponse;
import java.util.Objects;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.Base64;

@Service
public class MailService {

  private static String smtpNaverServer = "smtp.naver.com"; // SMTP 서버 주소
  private static int smtpNaverPort = 587; // SMTP 포트 번호
  private static String log = "";

  // SMTP 명령어를 전송하는 메서드
  private static void sendCommand(OutputStream outputStream, String command) throws IOException {
    command += "\r\n"; // 명령어 끝에 CRLF(Carriage Return, Line Feed) 추가
    outputStream.write(command.getBytes());
    outputStream.flush();
    log += String.format("< C: %s >", command);
    System.out.print("전송함: " + command);
  }

  public SendResponse sendEmail(SendRequest req) {
    SendResponse result = new SendResponse();

    try {
      Socket socket = null;
      BufferedReader reader = null;
      OutputStream outputStream = null;
      String response = null;

      try {
        // SMTP 서버에 연결
        socket = new Socket(smtpNaverServer, smtpNaverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = socket.getOutputStream();
      } catch (Exception e) {
        throw new RuntimeException("SMTP서버 소켓연결 오류");
      }
      try {
        // 파라미터 검사
        if(Objects.equals(req.getSenderEmail().split("@")[1],"naver.com")){
          System.out.println("네이버 이메일 인증완료");
        }else {
          throw new RuntimeException("네이버 이메일 밖에 지원하지 않습니다.");
        }

        if (req.getAttachment() != null) {
          System.out.println("req.getAttachment().getBytes().length = " + req.getAttachment().getSize());
          if (req.getAttachment().getSize() < 1048576) {
            System.out.println("첨부파일 탑재완료");
          } else {
            System.out.println("첨부가능한 파일 최대 용량은 1Mb입니다.");
            throw new RuntimeException("첨부가능한 파일 최대 용량은 1Mb입니다.");
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // SMTP 서버로부터 환영 메시지 받기
        response = reader.readLine();
        System.out.println("response1 = " + response);
        log += String.format("< S: %s >", response);

        if ("250".equals(response.split(" ")[0])) {
          System.out.println("서버가 클라이언트 연결을 수락");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // HELO 명령어 전송
        sendCommand(outputStream, "HELO");
        response = reader.readLine();
        System.out.println("response2 = " + response);
        log += String.format("< S: %s >", response);

        if ("220".equals(response.split(" ")[0])) {
          System.out.println("SMTP 서버의 성공 응답");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // STARTTLS 명령어 전송
        sendCommand(outputStream, "STARTTLS");
        response = reader.readLine();
        System.out.println("response3 = " + response);
        log += String.format("< S: %s >", response);

        if ("250".equals(response.split(" ")[0])) {
          System.out.println("SMTP 서버의 성공 응답");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // TLS 통신 설정
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket,
            socket.getInetAddress().getHostAddress(), socket.getPort(), true);
        sslSocket.startHandshake();
        reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
        outputStream = sslSocket.getOutputStream();
      } catch (Exception e) {
        throw new RuntimeException("TLS 설정오류");
      }
      try {
        // HELO 명령어 전송
        sendCommand(outputStream, "HELO naver");
        response = reader.readLine();
        System.out.println("response4 = " + response);
        log += String.format("< S: %s >", response);

        if ("250".equals(response.split(" ")[0])) {
          System.out.println("SMTP 서버의 성공 응답");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // 사용자 인증시작
        sendCommand(outputStream, "AUTH LOGIN");
        response = reader.readLine();
        System.out.println("response5 = " + response);
        log += String.format("< S: %s >", response);

        String log = "";

        log += response;
        if ("334".equals(response.split(" ")[0])) {
          System.out.println("base64로 인코딩된 사용자 이름/비밀번호 입력필요");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // 사용자 이름 전송 (Base64 인코딩된 값)
        sendCommand(outputStream, Base64.getEncoder().encodeToString(req.getSenderEmail().getBytes()));
        response = reader.readLine();
        System.out.println("서버 응답UserNM 6: " + response);
        log += String.format("< S: %s >", response);

        if ("334".equals(response.split(" ")[0])) {
          System.out.println("base64로 인코딩된 사용자 이름/비밀번호 입력필요");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // 비밀번호 전송 (Base64 인코딩된 값)
        sendCommand(outputStream, Base64.getEncoder().encodeToString(req.getPassword().getBytes()));
        response = reader.readLine();
        System.out.println("response6 = " + response);
        log += String.format("< S: %s >", response);

        if ("235".equals(response.split(" ")[0])) {
          System.out.println("사용자 인증완료");
        } else if ("535".equals(response.split(" ")[0])) {
          System.out.println("올바른 발신자 이메일/비밀번호를 입력히세요.");
          throw new RuntimeException("올바른 발신자 이메일/비밀번호를 입력히세요. SMTP허용 여부를 확인해주세요.");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // 발신자 이메일 주소 전송
        sendCommand(outputStream, "MAIL FROM: <" + req.getSenderEmail() + ">");
        response = reader.readLine();
        System.out.println("response7 = " + response);
        log += String.format("< S: %s >", response);

        if ("250".equals(response.split(" ")[0])) {
          System.out.println("발신자 이메일 입력완료");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // 수신자 이메일 주소 전송
        sendCommand(outputStream, "RCPT TO: <" + req.getReceiverEmail() + ">");
        response = reader.readLine();
        System.out.println("response8 = " + response);
        log += String.format("< S: %s >", response);

        if ("250".equals(response.split(" ")[0])) {
          System.out.println("수신자 이메일 입력완료");
        }else if ("553".equals(response.split(" ")[0])) {
          throw new RuntimeException("수신자 이메일이 유효하지 않습니다.");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // 데이터 전송 시작
        sendCommand(outputStream, "DATA");
        response = reader.readLine();
        System.out.println("response9 = " + response);
        log += String.format("< S: %s >", response);

        if ("354".equals(response.split(" ")[0])) {
          System.out.println("본문 입력시작");
        }

      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }

      String boundary = "----=_NextPart_" + System.currentTimeMillis();

      try {
        // 메일 헤더 및 본문 전송
        sendCommand(outputStream, "MIME-Version: 1.0");
        sendCommand(outputStream, "Content-Type: multipart/mixed; boundary=\"" + boundary + "\"");
        sendCommand(outputStream, "Subject: " + req.getTitle());
        sendCommand(outputStream, "From: " + req.getSenderEmail());
        sendCommand(outputStream, "To: " + req.getReceiverEmail());
        sendCommand(outputStream, ""); // 헤더와 본문 사이의 개행

        // 본문 전송
        sendCommand(outputStream, "--" + boundary);
        sendCommand(outputStream, "Content-Type: text/plain");
        sendCommand(outputStream, "");
        sendCommand(outputStream, req.getContent());

        // 첨부 파일이 있는 경우 전송
        if (req.getAttachment() != null) {
          byte[] attachmentBytes = req.getAttachment().getBytes();
          String encodedAttachment = Base64.getEncoder().encodeToString(attachmentBytes);

          sendCommand(outputStream, "--" + boundary);
          sendCommand(outputStream, "Content-Type: application/octet-stream"); // 파일 유형에 따라 변경 가능
          sendCommand(outputStream,
              "Content-Disposition: attachment; filename=\"" + req.getAttachment().getOriginalFilename() + "\"");
          sendCommand(outputStream, "Content-Transfer-Encoding: base64");
          sendCommand(outputStream, "");
          sendCommand(outputStream, encodedAttachment);
        }

        // 종료 경계 문자열 전송
        sendCommand(outputStream, "--" + boundary + "--");
        sendCommand(outputStream, ".");
        response = reader.readLine();
        System.out.println("response10 = " + response);
        log += String.format("< S: %s >", response);

        if ("250".equals(response.split(" ")[0])) {
          System.out.println("메일전송완료");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
      try {
        // 커넥션 종료
        sendCommand(outputStream, "QUIT");

        response = reader.readLine();
        System.out.println("response11 = " + response);
        log += String.format("< S: %s >", response);

        result.setMessage("정상발송완료");

        if ("221".equals(response.split(" ")[0])) {
          System.out.println("정상종료완료");
        }
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      } finally {
        // 시스템종료 - 소켓 및 스트림 닫기
        reader.close();
        outputStream.close();
        socket.close();
      }

    } catch (Exception e) {
      result.setMessage(e.getMessage());
    }

    result.setLog(log);
    return result;
  }

}