package com.project.timesheet.service.sheet;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.Arrays;

public class GoogleAuthorizeUtil {

    // TODO : read this from json file or yaml file
    private static final String applicationClientId = "397300109176-efllj9hjd6tmmq7b0pk11mbqpl64fm2e.apps.googleusercontent.com";

    // This value indicates that Google's authorization server should return the authorization code in the browser
    private static final String redirectUri = "urn:ietf:wg:oauth:2.0:oob";

    public static String authorize() throws Exception {
        // get credentials similar to Java DrEdit example
        // https://developers.google.com/drive/examples/java
        String scope = SheetsScopes.SPREADSHEETS;

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        final String CLIENT_ID = applicationClientId;
        final String CLIENT_SECRET = null;

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
                Arrays.asList(
                        scope,
                        "https://spreadsheets.google.com/feeds",
                        "https://docs.google.com/feeds"))
                .setAccessType("offline")
                .setApprovalPrompt("auto").build();

        String url = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();

        return url;
    }

    public static Credential authorize(TokenResponse tokenResponse, String clientId) throws Exception {
        String scope = SheetsScopes.SPREADSHEETS;

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        tokenResponse.setScope(scope);

        tokenResponse.setTokenType("offline");

        Credential credential = createCredentialWithRefreshToken(httpTransport, jsonFactory, tokenResponse, clientId);
        return credential;
    }

    public static TokenResponse authenticateCode(String code) throws Exception {
        String scope = SheetsScopes.SPREADSHEETS;

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        final String CLIENT_ID = applicationClientId;
        final String CLIENT_SECRET = null;

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
                Arrays.asList(
                        scope,
                        "https://spreadsheets.google.com/feeds",
                        "https://docs.google.com/feeds")
                )
                .setAccessType("offline")
                .setApprovalPrompt("auto").build();

        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

        TokenResponse tokenResponse = new TokenResponse();

        tokenResponse.setAccessToken(response.getAccessToken());
        tokenResponse.setRefreshToken(response.getRefreshToken());
        tokenResponse.setTokenType("offline");
        tokenResponse.setExpiresInSeconds(response.getExpiresInSeconds());

        return tokenResponse;
    }

    public static Credential createCredentialWithAccessTokenOnly(
            HttpTransport transport, JsonFactory jsonFactory, TokenResponse tokenResponse) {
        return new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setFromTokenResponse(tokenResponse);
    }

    public static Credential createCredentialWithRefreshToken(
            HttpTransport transport, JsonFactory jsonFactory, TokenResponse tokenResponse, String clientId) {
        tokenResponse.setFactory(jsonFactory);
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).setTransport(
                transport)
                .setJsonFactory(jsonFactory)
                .setTransport(transport)
                .setTokenServerUrl(
                        new GenericUrl("https://accounts.google.com/o/oauth2/token")
                )
                .setClientAuthentication(new ClientParametersAuthentication(clientId, ""))
                .build()
                .setFromTokenResponse(tokenResponse);
    }
}