package com.stackoverflow.questions.handler;

import com.stackoverflow.questions.dto.PolledFile;
import com.stackoverflow.questions.dto.WriteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentErrorHandler implements MessageHandler {

  private final MessageChannel studentErrorChannel;

  @Override
  public void handleMessage(Message<?> message) throws MessagingException {
    MessagingException exception = (MessagingException) message.getPayload();

    PolledFile payload = (PolledFile) exception.getFailedMessage().getPayload();
    log.warn("====== Handling {} ======", payload.getPolledFile());
    studentErrorChannel.send(
        MessageBuilder.withPayload(WriteResult.builder().filename(payload.getPolledFile()).build())
            .build());
  }
}
