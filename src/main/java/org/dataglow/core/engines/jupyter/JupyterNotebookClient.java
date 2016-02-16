package org.dataglow.core.engines.jupyter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A client to Jupyter Notebook REST API.
 */
public class JupyterNotebookClient {

    private static final Logger logger = LoggerFactory.getLogger(JupyterNotebookClient.class);

    private String notebookAppUrl;
    private String workDir = "/tmp";

    private HttpClient client;


    public void init() {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(60, TimeUnit.SECONDS);
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(100);

        client = HttpClientBuilder.create().setConnectionManager(connManager).build();
    }


    /**
     * Start new notebook and start session.
     *
     * @param name notebook name.
     * @return a path to document in filesystem.
     */
    public String createNotebook(String name) {
        try {
            HttpPost request = new HttpPost(notebookAppUrl + "/api" + "/contents" + workDir);
            StringEntity entity = new StringEntity("{\"type\":\"notebook\"}");
            entity.setContentType("application/json");
            request.setEntity(entity);
            HttpResponse response = client.execute(request);
            logger.debug("response: {}", response);
            if (response.getStatusLine().getStatusCode() == 201) {
                return response.getFirstHeader("Location").getValue();
            } else {
                throw new IllegalStateException("Failed to create new Notebook. Got unexpected response from jupyter: "
                        + response.getStatusLine());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create new Notebook.", e);
        }

    }


    public String getNotebookAppUrl() {
        return notebookAppUrl;
    }

    public void setNotebookAppUrl(String notebookAppUrl) {
        this.notebookAppUrl = notebookAppUrl;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }
}
