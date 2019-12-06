package io.growing.sdk.java.sender.net;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 12/6/19 11:46 AM
 */
public enum ContentTypeEnum {
   PROTOBUF("application/protobuf"),
   JSON("application/json");

   private String value;

   ContentTypeEnum(String value) {
      this.value = value;
   }


   @Override
   public String toString() {
      return this.value;
   }
}