package com.auth0.net;

import com.auth0.client.MockServer;
import com.auth0.client.mgmt.TokenProvider;
import com.auth0.exception.Auth0Exception;
import com.auth0.net.client.*;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static com.auth0.client.MockServer.AUTH_TOKENS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class VoidRequestTest {
    private Auth0HttpClient client;
    private TokenProvider tokenProvider;
    private MockServer server;

    @Before
    public void setUp() throws Exception {
        client = new DefaultHttpClient.Builder().build();
        server = new MockServer();
        tokenProvider = new TokenProvider() {
            @Override
            public String getToken() throws Auth0Exception {
                return "Bearer xyz";
            }

            @Override
            public CompletableFuture<String> getTokenAsync() {
                return CompletableFuture.completedFuture("Bearer xyz");
            }
        };
    }

    @Test
    public void shouldCreateGETRequest() throws Exception {
        VoidRequest request = new VoidRequest(client, tokenProvider, server.getBaseUrl(), HttpMethod.GET);
        assertThat(request, is(notNullValue()));

        server.jsonResponse(AUTH_TOKENS, 200);
        Void execute = request.execute().getBody();
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getMethod(), is(HttpMethod.GET.toString()));
        assertThat(execute, is(nullValue()));
    }

    @Test
    public void shouldCreatePOSTRequest() throws Exception {
        VoidRequest request = new VoidRequest(client, tokenProvider, server.getBaseUrl(), HttpMethod.POST);
        assertThat(request, is(notNullValue()));
        request.addParameter("non_empty", "body");

        server.jsonResponse(AUTH_TOKENS, 200);
        Void execute = request.execute().getBody();
        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(recordedRequest.getMethod(), is(HttpMethod.POST.toString()));
        assertThat(execute, is(nullValue()));
    }
}
