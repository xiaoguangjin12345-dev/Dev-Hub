package com.xgj.devpulse.AITest.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OpenAIRequestDTO {
    private String model;
    private List<Msg> messages;

    @Data
    public static class Msg{
        private String role;
        private String content;

        public Msg(String role, String content){
            this.role = role;
            this.content = content;
        }

    }
}
