package br.com.uploads.app.usecases;

import br.com.uploads.app.exception.BusinessException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailUseCase {

  private final JavaMailSender mailSender;

  public Mono<Void> sendEmail(String to, String subject, String body, Path attachment) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, true);

      if (attachment != null && attachment.toFile().exists()) {
        helper.addAttachment(attachment.getFileName().toString(), attachment.toFile());
      }

      mailSender.send(message);
      log.info("Email sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send email to {}: {}", to, e.getMessage());
      return Mono.error(new BusinessException(e.getMessage()));
    }

    return Mono.empty();
  }
}
