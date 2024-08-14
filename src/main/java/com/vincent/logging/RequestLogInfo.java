package com.vincent.logging;

import org.slf4j.MDC;

public class RequestLogInfo {

    public String traceId;
    public String memberId;
    public String requestMethod;
    public String requestUri;
    public String xAmznTraceId;
    public String xSdpIdfv;
    public String userAgent;

    public RequestLogInfo(RequestWrapper requestWrapper) {
        String queryString = requestWrapper.getQueryString();
        this.traceId = MDC.get("traceId");
        this.requestMethod = requestWrapper.getMethod();
        this.requestUri =
            requestWrapper.getRequestURI() + (queryString == null ? "" : "?" + queryString);
        this.xAmznTraceId = requestWrapper.getHeader("x-amzn-trace-id");
        this.xSdpIdfv = requestWrapper.getHeader("x-sdp-idfv");
        this.userAgent = requestWrapper.getHeader("user-agent");
        this.memberId = MDC.get("memberId");
    }

    public String toJson() {
        return "{"
            + "\"traceId\":\"" + traceId + "\","
            + "\"memberId\":\"" + memberId + "\","
            + "\"requestMethod\":\"" + requestMethod + "\","
            + "\"requestUri\":\"" + requestUri + "\","
            + "\"xAmznTraceId\":\"" + xAmznTraceId + "\","
            + "\"xSdpIdfv\":\"" + xSdpIdfv + "\","
            + "\"userAgent\":\"" + userAgent + "\""
            + "}";
    }
}
