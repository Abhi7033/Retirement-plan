package com.blackrock.retirement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response body for the health endpoint.
 * Provides a simple liveness check with service status.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthResponse {

    private String status;
    private String service;
    private String version;
    private long timestamp;

    public HealthResponse() {
    }

    public HealthResponse(String status, String service, String version, long timestamp) {
        this.status = status;
        this.service = service;
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
