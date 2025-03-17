package com.birdy.blogbackend.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author birdy
 */
public class FileInputStreamAdapter extends TypeAdapter<FileInputStream> {
    @Override
    public void write(JsonWriter out, FileInputStream value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        // 序列化为文件路径或其他元数据
        out.value(value.getChannel().toString());
    }

    @Override
    public FileInputStream read(JsonReader in) throws IOException {
        // 反序列化逻辑（根据需求实现）
        return null;
    }
}