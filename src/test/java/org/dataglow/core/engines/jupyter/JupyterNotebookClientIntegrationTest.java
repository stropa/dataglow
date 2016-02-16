package org.dataglow.core.engines.jupyter;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by konovalov on 17/02/16
 */
public class JupyterNotebookClientIntegrationTest {

    @Test
    public void testCreateNotebook() throws Exception {

        JupyterNotebookClient client = new JupyterNotebookClient();
        client.setNotebookAppUrl("http://localhost:8888");
        client.init();

        client.createNotebook("test_notebook_1");

    }
}