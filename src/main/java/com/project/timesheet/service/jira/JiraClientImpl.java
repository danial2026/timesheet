package com.project.timesheet.service.jira;

import com.project.timesheet.dto.JiraIssue;
import com.project.timesheet.exception.BusinessServiceException;
import com.project.timesheet.exception.ErrorCode;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class JiraClientImpl implements JiraClient {

    private String username;

    private String password;

    private String jiraUrl;

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
        }catch (Exception ignored){

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
}