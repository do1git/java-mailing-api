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

    private static String boundary = "----=_NextPart_" + System.currentTimeMillis();

    private static void readAllLines(BufferedReader reader) throws IOException {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println("Server: " + line);
        if (!line.startsWith("250-")) {
          break;
        }
      }
    }

    // SMTP 명령어를 전송하는 메서드
    private static void sendCommand(OutputStream outputStream, String command) throws IOException {
      command += "\r\n"; // 명령어 끝에 CRLF(Carriage Return, Line Feed) 추가
      outputStream.write(command.getBytes());
      outputStream.flush();
      System.out.print("Client: " + command);
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
          throw new RuntimeException("SMTP Server Socket Connection Error");
        }
        try {
          // 파라미터 검사
          if(Objects.equals(req.getSenderEmail().split("@")[1],"naver.com")){
            System.out.println("Naver Email Authentication Complete");
          }else {
            throw new RuntimeException("We only support Naver Mail");
          }

          if (req.getAttachment() != null) {
            System.out.println("req.getAttachment().getBytes().length = " + req.getAttachment().getSize());
            if (req.getAttachment().getSize() < 1048576) {
              System.out.println("File Attached Successfully");
            } else {
              System.out.println("The maximum allowable file size for attachment is 1Mb");
              throw new RuntimeException("The maximum allowable file size for attachment is 1Mb");
            }
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // SMTP 서버로부터 환영 메시지 받기
          response = reader.readLine();
          System.out.println("Server: " + response);

          if ("250".equals(response.split(" ")[0])) {
            System.out.println("The server accepts the client connection");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // HELO 명령어 전송
          sendCommand(outputStream, "EHLO");
          readAllLines(reader);

          if ("220".equals(response.split(" ")[0])) {
            System.out.println("SMTP Server Success Response");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // STARTTLS 명령어 전송
          sendCommand(outputStream, "STARTTLS");
          response = reader.readLine();
          System.out.println("Server: " + response);

          if ("250".equals(response.split(" ")[0])) {
            System.out.println("SMTP Server Success Response");
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
          throw new RuntimeException("TLS Setting Error");
        }
        try {
          // HELO 명령어 전송
          sendCommand(outputStream, "EHLO");
          readAllLines(reader);

          if ("250".equals(response.split(" ")[0])) {
            System.out.println("SMTP Server Success Response");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // 사용자 인증시작
          sendCommand(outputStream, "AUTH LOGIN");
          response = reader.readLine();
          System.out.println("Server: " + response);

          if ("334".equals(response.split(" ")[0])) {
            System.out.println("Base64-encoded Email required");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // 사용자 이름 전송 (Base64 인코딩된 값)
          sendCommand(outputStream, Base64.getEncoder().encodeToString(req.getSenderEmail().getBytes()));
          response = reader.readLine();
          System.out.println("Server: " + response);
          if ("334".equals(response.split(" ")[0])) {
            System.out.println("Base64-encoded Password required");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // 비밀번호 전송 (Base64 인코딩된 값)
          sendCommand(outputStream, Base64.getEncoder().encodeToString(req.getPassword().getBytes()));
          response = reader.readLine();
          System.out.println("Server: " + response);
          if ("235".equals(response.split(" ")[0])) {
            System.out.println("Authentication Success");
          } else if ("535".equals(response.split(" ")[0])) {
            System.out.println("Please enter the correct sender's email or password");
            throw new RuntimeException("Please enter the correct sender's email or password. And check the SMTP permission status");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // 발신자 이메일 주소 전송
          sendCommand(outputStream, "MAIL FROM: <" + req.getSenderEmail() + ">");
          response = reader.readLine();
          System.out.println("Server: " + response);

          if ("250".equals(response.split(" ")[0])) {
            System.out.println("Sender's email entered successfully");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // 수신자 이메일 주소 전송
          sendCommand(outputStream, "RCPT TO: <" + req.getReceiverEmail() + ">");
          response = reader.readLine();
          System.out.println("Server: " + response);

          if ("250".equals(response.split(" ")[0])) {
            System.out.println("Receiver's email entered successfully.");
          }else if ("553".equals(response.split(" ")[0])) {
            throw new RuntimeException("Sender's email is not valid");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // 데이터 전송 시작
          sendCommand(outputStream, "DATA");
          response = reader.readLine();
          System.out.println("Server: " + response);

          if ("354".equals(response.split(" ")[0])) {
            System.out.println("Starting content input");
          }

        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }

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
          System.out.println("Server: " + response);

          if ("250".equals(response.split(" ")[0])) {
            System.out.println("Email sent successfully");
          }
        } catch (Exception e) {
          throw new RuntimeException(e.getMessage());
        }
        try {
          // 커넥션 종료
          sendCommand(outputStream, "QUIT");

          response = reader.readLine();
          System.out.println("Server: " + response);

          result.setMessage("Email successfully delivered");

          if ("221".equals(response.split(" ")[0])) {
            System.out.println("Successfully terminated");
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

      return result;
    }

  }