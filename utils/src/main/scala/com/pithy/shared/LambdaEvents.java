package com.pithy.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * HandlerInputEvents defines the schema for the events that backend receives from frontend. <br>
 *
 * It includes fields that might, or might not be present in the JSON payload depending on which lambda function was called.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LambdaEvents {
    private String platform;
    private String postUrl;
    private Map<String, String> queries;

    private String RedditAuthCode;

    // No-argument constructor
    public LambdaEvents() {
    }

    // Getters and Setters
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String postUrl) {
        this.platform = postUrl;
    }
    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
    }

    public String getRedditAuthCode() {
        return RedditAuthCode;
    }

    public void setRedditAuthCode(String RedditAuthCode) {
        this.RedditAuthCode = RedditAuthCode;
    }
}
