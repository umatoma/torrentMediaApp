package net.umatoma.torrentmediaapp;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class TestServerDispatcher extends Dispatcher {

    private MockResponse getFilesMockResponse;
    private MockResponse searchTorrentMockResponse;

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String requestPath = request.getPath();
        if (requestPath.equals("/files")) {
            return this.getFilesMockResponse;
        } else if (requestPath.equals("/rss.php")) {
            return this.searchTorrentMockResponse;
        }
        return new MockResponse().setResponseCode(400);
    }

    public TestServerDispatcher setGetFilesMockResponse(MockResponse getFilesMockResponse) {
        this.getFilesMockResponse = getFilesMockResponse;
        return this;
    }

    public TestServerDispatcher setSearchTorrentMockResponse(MockResponse searchTorrentMockResponse) {
        this.searchTorrentMockResponse = searchTorrentMockResponse;
        return this;
    }
}
