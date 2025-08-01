package br.com.uploads.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SqsConfig {

  @Value("${aws.region}")
  private String awsRegion;

  @Bean
  public SqsAsyncClient sqsClient() {
    return SqsAsyncClient.builder()
      .region(Region.of(awsRegion))
      .credentialsProvider(DefaultCredentialsProvider.create())
      .build();
  }
  
}
