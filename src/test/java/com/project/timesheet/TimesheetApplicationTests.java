package com.project.timesheet;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureMockMvc
@SpringBootTest
class TimesheetApplicationTests {

    @Test
    void contextLoads() throws Exception {

        HttpResponse<JsonNode> response = Unirest.get("https://ibook.atlassian.net/rest/api/3/issue/IBS-45")
                .basicAuth("1998alirezam@gmail.com", "auVMFmEJ1cIvyQF0aN1CD038")
                .header("Accept", "application/json")
                .asJson();

        System.out.println(response);
        System.out.println(response.getBody());
        System.out.println(response.getBody().getObject().getJSONObject("fields").getString("summary"));

        response.getBody().getObject().getJSONObject("fields").getJSONObject("description").getJSONArray("content").forEach(line -> {
            System.out.println(line.toString());
        });
    }
}
