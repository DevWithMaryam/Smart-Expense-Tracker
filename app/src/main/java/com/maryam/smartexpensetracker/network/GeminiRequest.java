package com.maryam.smartexpensetracker.network;

import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {

    private List<Content> contents;

    public GeminiRequest(String prompt) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        Part part = new Part();
        part.text = prompt;
        List<Part> parts = new ArrayList<>();
        parts.add(part);
        content.parts = parts;
        this.contents.add(content);
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }
}