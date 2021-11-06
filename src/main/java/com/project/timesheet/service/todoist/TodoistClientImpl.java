package com.project.timesheet.service.todoist;

import com.project.timesheet.dto.TodoistTask;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.exception.ErrorCode;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TodoistClientImpl implements TodoistClient {

    private final String token;

    private final String todoistUrl = "https://api.todoist.com";

    public TodoistClientImpl(String token) {
        this.token = token;
    }

    @Override
    public TodoistTask getTask(String taskKey) throws BusinessServiceException {
        JSONObject issueJson = null;
        try {
            HttpResponse<JsonNode> response = Unirest.get(this.todoistUrl + "/sync/v8/items/get?item_id=" + taskKey)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + this.token)
                    .asJson();

            if (response.getStatus() != 200) {

                throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
            }
            issueJson = response.getBody().getObject();
        } catch (Exception ignored) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        TodoistTask todoistTask = new TodoistTask();

        todoistTask.setTaskId(issueJson.getJSONObject("item").getString("id"));
        todoistTask.setSummary(issueJson.getJSONObject("item").getString("content"));
        todoistTask.setDescription(issueJson.getJSONObject("item").getString("description"));
        todoistTask.setDueDate(issueJson.getJSONObject("item").getJSONObject("due").getString("date"));
        todoistTask.setProjectId(issueJson.getJSONObject("item").getString("project_id"));
        todoistTask.setProjectName(issueJson.getJSONObject("project").getString("name"));
        todoistTask.setStatus(issueJson.getJSONObject("item").getJSONArray("labels").toString());

        return todoistTask;
    }

    @Override
    public List<TodoistTask> getActiveTask() throws BusinessServiceException {
        List<TodoistTask> activeTasks = new ArrayList<>();
        JSONObject tasksJson = null;
        try {
            HttpResponse<String> response = Unirest.get("https://api.todoist.com/sync/v8/sync?sync_token=*&resource_types=[\"items\"]")
                    .header("Cookie", "csrf=f510e90a2a2045fe880824ccdda92fd3")
                    .header("Authorization", "Bearer " + this.token)
                    .asString();

            if (response.getStatus() != 200) {

                throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
            }
//            tasksJson = response.getBody().getObject();
        } catch (Exception ignored) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

//        tasksJson.getJSONArray("items").forEach(
//                item -> {
//                    JSONObject jo = (JSONObject) item;
//
//                    TodoistTask todoistTask = new TodoistTask();
//
//                    todoistTask.setTaskId(jo.getJSONObject("item").getString("id"));
//                    todoistTask.setSummary(jo.getJSONObject("item").getString("content"));
//                    todoistTask.setDescription(jo.getJSONObject("item").getString("description"));
//                    todoistTask.setDueDate(jo.getJSONObject("item").getJSONObject("due").getString("date"));
//                    todoistTask.setProjectId(jo.getJSONObject("item").getString("project_id"));
//                    todoistTask.setProjectName(jo.getJSONObject("project").getString("name"));
//                    todoistTask.setStatus(jo.getJSONObject("item").getJSONArray("labels").toString());
//
//                    activeTasks.add(todoistTask);
//                }
//        );
        return activeTasks;
    }

//    @Override
//    public JiraIssue getIssuesAssignedToMe(String projectKey) throws BusinessServiceException {
//        JSONObject issueJson = null;
//        try {
//            HttpResponse<JsonNode> response = Unirest.get(this.jiraUrl + "/rest/api/3/issue/")
//                    .basicAuth(this.username, this.password)
//                    .header("Accept", "application/json")
//                    .asJson();
//
//            if (response.getStatus() != 200) {
//
//                throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
//            }
//            issueJson = response.getBody().getObject();
//        }catch (Exception ignored){
//
//            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
//        }
//
//        String status = issueJson.getJSONObject("fields").getJSONObject("status").getString("name");
//        String issueType = issueJson.getJSONObject("fields").getJSONObject("issuetype").getString("name");
//        String summary = issueJson.getJSONObject("fields").getString("summary");
//
//        StringBuilder description = new StringBuilder();
//        JSONArray descriptionJson = issueJson.getJSONObject("fields").getJSONObject("description").getJSONArray("content");
//
//        for (int i = 0; i < descriptionJson.length(); i++) {
//            JSONObject ja = (JSONObject) descriptionJson.get(i);
//            JSONObject ja2 = (JSONObject) ja.getJSONArray("content").get(0);
//            description.append(ja2.getString("text"));
//            if (i != descriptionJson.length() - 1) {
//                description.append("\n");
//            }
//        }
//
//        return new JiraIssue(status, issueType, summary, description.toString());
//    }
}