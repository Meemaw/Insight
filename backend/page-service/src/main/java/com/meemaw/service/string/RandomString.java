package com.meemaw.service.string;

import java.security.SecureRandom;

public class RandomString {

  private RandomString() {
  }

  private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String alphanumeric(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      int charCode = CHARS[RANDOM.nextInt(CHARS.length)];
      sb.appendCodePoint(charCode);
    }
    return sb.toString();
  }
}
