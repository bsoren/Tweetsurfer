package com.anibij.demoapp.model;

/**
 * Created by bsoren on 17-Jan-16.
 */
public class DirectMessage {

    long createdAt;
    long id;
    String receipientName;
    long recipientId;
    String receipientScreenName;
    String senderName;
    long senderId;
    String senderScreenName;
    String textMessage;
    String recipientImageUrl;

    public DirectMessage(long createdAt, long id, String receipientName, long recipientId, String receipientScreenName, String senderName, long senderId, String senderScreenName, String textMessage, String recipientImageUrl) {
        this.createdAt = createdAt;
        this.id = id;
        this.receipientName = receipientName;
        this.recipientId = recipientId;
        this.receipientScreenName = receipientScreenName;
        this.senderName = senderName;
        this.senderId = senderId;
        this.senderScreenName = senderScreenName;
        this.textMessage = textMessage;
        this.recipientImageUrl = recipientImageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReceipientName() {
        return receipientName;
    }

    public void setReceipientName(String receipientName) {
        this.receipientName = receipientName;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public String getReceipientScreenName() {
        return receipientScreenName;
    }

    public void setReceipientScreenName(String receipientScreenName) {
        this.receipientScreenName = receipientScreenName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getSenderScreenName() {
        return senderScreenName;
    }

    public void setSenderScreenName(String senderScreenName) {
        this.senderScreenName = senderScreenName;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getRecipientImageUrl() {
        return recipientImageUrl;
    }

    public void setRecipientImageUrl(String recipientImageUrl) {
        this.recipientImageUrl = recipientImageUrl;
    }

    @Override
    public String toString() {
        return "DirectMessage{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", senderScreenName='" + senderScreenName + '\'' +
                ", receipientScreenName='" + receipientScreenName + '\'' +
                ", textMessage='" + textMessage + '\'' +
                '}';
    }
}
