package com.birdy.blogbackend.util.gson;

import com.birdy.blogbackend.adapter.FileInputStreamAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.FileInputStream;

/**
 * @author birdy
 */
public final class GsonProvider {

  private static final Gson NORMAL = new GsonBuilder()
    .registerTypeAdapter(FileInputStream.class, new FileInputStreamAdapter())
    .disableHtmlEscaping().create();
  private static final Gson PRETTY_PRINTING = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
  private static final JsonParser NORMAL_PARSER = new JsonParser();

  private GsonProvider() {
    throw new AssertionError();
  }

  public static Gson normal() {
    return NORMAL;
  }

  public static Gson prettyPrinting() {
    return PRETTY_PRINTING;
  }

  public static JsonParser parser() {
    return NORMAL_PARSER;
  }

}
