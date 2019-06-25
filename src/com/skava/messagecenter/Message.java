package com.skava.messagecenter;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class Message<T> {

  private String sender;
  private String id = UUID.randomUUID().toString();
  private Date timestamp = new Date();
  private T payload;
  private String messageType;

  private String correlationId;

  private String collectionId;

  private String version;

  private String authToken;

  @Getter
  @Setter
  private Map<String, Object> msgProps;

  public Message() {
  }

  public Message(String type, String sender, T payload) {
    this.messageType = type;
    this.sender = sender;
    this.payload = payload;
  }

  public String getMessageType() {
    return messageType;
  }

  public Message<T> setMessageType(String messageType) {
    this.messageType = messageType;
    return this;
  }

  public String getId() {
    return id;
  }

  public Message<T> setId(String id) {
    this.id = id;
    return this;
  }

  public T getPayload() {
    return payload;
  }

  public Message<T> setPayload(T payload) {
    this.payload = payload;
    return this;
  }

  public String getSender() {
    return sender;
  }

  public Message<T> setSender(String sender) {
    this.sender = sender;
    return this;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public Message<T> setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  public String getCollectionId() {
    return collectionId;
  }

  public Message<T> setCollectionId(String collectionId) {
    this.collectionId = collectionId;
    return this;
  }

  public String getVersion() {
    return version;
  }

  public Message<T> setVersion(String version) {
    this.version = version;
    return this;
  }

  public String getAuthToken() {
    return authToken;
  }

  public Message<T> setAuthToken(String authToken) {
    this.authToken = authToken;
    return this;
  }

  @Override
  public String toString() {
    return "Message [messageType=" + messageType + ", id=" + id + ", timestamp=" + timestamp +
      ", sender=" + sender + "payload=" + payload + "]";
  }
}
