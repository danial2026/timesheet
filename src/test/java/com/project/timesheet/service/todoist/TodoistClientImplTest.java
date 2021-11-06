package com.project.timesheet.service.todoist;

import com.project.timesheet.exception.BusinessServiceException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class TodoistClientImplTest {

    @Test
    void getIssue() throws BusinessServiceException {

        // get api token from https://id.atlassian.com/manage-profile/security/api-tokens
        TodoistClientImpl client = new TodoistClientImpl("a798ee637398013c86039269d3879f54fa269905");
        client.getTask("5291239914").toString();
    }

    @Test
    void getActiveTask() throws BusinessServiceException, IOException {
//        HttpResponse<String> response = Unirest.get("https://api.todoist.com/api/v8/sync?resource_types=%5B\\\"items\\\"%5D")
//                .header("Authorization", "Bearer a798ee637398013c86039269d3879f54fa269905")
//                .header("Cookie", "csrf=f510e90a2a2045fe880824ccdda92fd3")
//                .asString();


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.todoist.com/sync/v8/sync?sync_token=*&resource_types=[\"items\"]")
                .method("GET", null)
                .addHeader("Authorization", "Bearer a798ee637398013c86039269d3879f54fa269905")
                .addHeader("Cookie", "csrf=f510e90a2a2045fe880824ccdda92fd3")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .build();
        Response response = client.newCall(request).execute();

        response.body();
        // get api token from https://id.atlassian.com/manage-profile/security/api-tokens
//        TodoistClientImpl client = new TodoistClientImpl("a798ee637398013c86039269d3879f54fa269905");
//        client.getActiveTask();
    }

//    @Test
//    void getIssuesAssignedToMe() throws BusinessServiceException {
//
//        // get api token from https://id.atlassian.com/manage-profile/security/api-tokens
//        JiraClientImpl client = new JiraClientImpl("your-email@gm ail.com", "<API-Token>", "https://ibook.atlassian.net");
//        client.getIssuesAssignedToMe("<project-key>").toString();
//    }
}