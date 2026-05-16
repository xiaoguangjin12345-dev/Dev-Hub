package com.xgj.devpulse;

import com.xgj.devpulse.AITest.AIRequestTest;
import com.xgj.devpulse.AITest.DTO.OpenAIRequestDTO;
import com.xgj.devpulse.common.cache.RedisService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
public class MainTestCalling {

//    @Autowired
//    private Productor productor;

    private static AIRequestTest aiRequest;

    private static RedisService redisService;


    public static void main(String[] args) throws InterruptedException {

        // 启动 Spring 容器
        ApplicationContext context = SpringApplication.run(DevPulseServerApplication.class, args);

        // 手动获取
        redisService = context.getBean(RedisService.class);
        aiRequest = context.getBean(AIRequestTest.class);


        Scanner scanner = new Scanner(System.in);

        System.out.print("我: ");
        String input = scanner.nextLine();
        List<OpenAIRequestDTO.Msg> messages = new ArrayList<>();

        while (!input.equals("exit")) {
            System.out.println("计时：");

            messages.add(new OpenAIRequestDTO.Msg("user", input));
            redisService.set("ai:status", 1);
            aiRequest.request(messages);

            int seconds = 0;

            while(redisService.get("ai:status", Integer.class) != 2){
                Thread.sleep(1000);
                seconds++;
                System.out.print(seconds + "\t");
                if(seconds % 5 == 0){
                    System.out.println();
                }
            }
            String content = redisService.get("ai:content", String.class);
            System.out.println("\n\n千问: " + content + "\n");

            messages.add(new OpenAIRequestDTO.Msg("assistant", content));


            System.out.print("我: ");
            input = scanner.nextLine();
        }



        scanner.close();
    }

//    @Test
//    public void mainTest() throws InterruptedException {
//        aiRequest.request("你好");
//    }

//    @Test
//    public void mainTest() throws InterruptedException {
//        productor.send("Hello World");
//
//    }

//    @Test
//    public void mainTest() throws InterruptedException {
//        redisTest.setUserNameTest(1, "xiao");
//
//        String result = redisTest.getUserNameTest(1);
//
//        System.out.println(result);
//    }

}
