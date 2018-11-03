package net.umatoma.torrentmediaapp;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class TestServerDispatcher extends Dispatcher {

    private MockResponse getFilesMockResponse;

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String requestPath = request.getPath();
        if (requestPath.equals("/files")) {
            return this.getFilesMockResponse;
        }
        return new MockResponse().setResponseCode(400);
    }

    public TestServerDispatcher setGetFilesMockResponse(MockResponse getFilesMockResponse) {
        this.getFilesMockResponse = getFilesMockResponse;
        return this;
    }
}
