package com.project.timesheet.service.jira;

import com.project.timesheet.dto.JiraIssue;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.exception.ErrorCode;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JiraClientImpl implements JiraClient {

    private final String username;

    private final String password;

    private final String jiraUrl;

    public JiraClientImpl(String username, String password, String jiraUrl) {
        this.username = username;
        this.password = password;
        this.jiraUrl = jiraUrl;
    }

    @Override
    public JiraIssue getIssue(String issueKey) throws BusinessServiceException {
        JSONObject issueJson = null;
        try {
            HttpResponse<JsonNode> response = Unirest.get(this.jiraUrl + "/rest/api/3/issue/" + issueKey)
                    .basicAuth(this.username, this.password)
                    .header("Accept", "application/json")
                    .asJson();

            if (response.getStatus() != 200) {

                throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
            }
            issueJson = response.getBody().getObject();
        } catch (Exception ignored) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        String status = issueJson.getJSONObject("fields").getJSONObject("status").getString("name");
        String issueType = issueJson.getJSONObject("fields").getJSONObject("issuetype").getString("name");
        String summary = issueJson.getJSONObject("fields").getString("summary");

        StringBuilder description = new StringBuilder();
        JSONArray descriptionJson = issueJson.getJSONObject("fields").getJSONObject("description").getJSONArray("content");

        for (int i = 0; i < descriptionJson.length(); i++) {
            JSONObject ja = (JSONObject) descriptionJson.get(i);
            JSONObject ja2 = (JSONObject) ja.getJSONArray("content").get(0);
            description.append(ja2.getString("text"));
            if (i != descriptionJson.length() - 1) {
                description.append("\n");
            }
        }

        return new JiraIssue(status, issueType, summary, description.toString());
    }

    @Override
    public List<JiraIssue> getIssuesAssignedToMe(String projectKey) throws BusinessServiceException {
        JSONObject issueJson = null;
        try {
            HttpResponse<JsonNode> response = Unirest.post(this.jiraUrl + "/rest/api/3/search")
                    .basicAuth(this.username, this.password)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body("{\"jql\": \"project = " + projectKey + " AND status = \\\"TO DO\\\" AND assignee = currentuser()\", " +
                            "  \"startAt\": 0 , " +
                            "  \"maxResults\": 25, " +
                            "  \"fieldsByKeys\": false, " +
                            "  \"fields\": [ " +
                            "    \"status\", " +
                            "    \"assignee\", " +
                            "    \"summary\", " +
                            "    \"description\", " +
                            "    \"sub-tasks\" " +
                            "  ] " +
                            "}")
                    .asJson();

            if (response.getStatus() != 200) {

                throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
            }
            issueJson = response.getBody().getObject();
        } catch (Exception ignored) {

            throw new BusinessServiceException(ErrorCode.NOT_AUTHORIZED);
        }

        try {

            List<JiraIssue> resultList = new ArrayList<>();

            issueJson.getJSONArray("issues").forEach(
                    item -> {
                        JSONObject jo = (JSONObject) item;
                        JiraIssue issue = new JiraIssue();

                        issue.setIssueKey(jo.getString("key"));
                        StringBuilder issueDescription = new StringBuilder();
                        try {
                            jo.getJSONObject("fields").getJSONObject("description").getJSONArray("content").forEach(
                                    description -> {
                                        JSONObject jod = (JSONObject) description;
                                        if (jod.getString("type").equals("paragraph")) {

                                            jod.getJSONArray("content").forEach(
                                                    paragraph -> {
                                                        JSONObject jop = (JSONObject) paragraph;
                                                        issueDescription.append(jop.getString("text"));
                                                        issueDescription.append("\n");
                                                    }
                                            );
                                        }
                                    }
                            );

                            issue.setDescription(issueDescription.toString());

                        } catch (Exception ignored) {
                        }

                        try {
                            issue.setSummary(jo.getJSONObject("fields").getString("summary"));
                        } catch (Exception ignored) {
                        }
                        issue.setStatus(jo.getJSONObject("fields").getJSONObject("status").getString("name"));

                        try {
                            issue.setAssignee(jo.getJSONObject("fields").getJSONObject("assignee").getString("displayName"));
                        } catch (Exception ignored) {
                        }

                        resultList.add(issue);
                    }
            );

            return resultList;
        } catch (Exception ignored) {

            throw new BusinessServiceException(ErrorCode.NOT_FOUND);
        }
    }
}