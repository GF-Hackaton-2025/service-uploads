package br.com.uploads.app.ports;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

public interface UploadQueue {

  Mono<SendMessageResponse> sendMessage(String body);

}
