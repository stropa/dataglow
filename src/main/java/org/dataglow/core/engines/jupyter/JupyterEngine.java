package org.dataglow.core.engines.jupyter;

import org.dataglow.core.AResults;
import org.dataglow.core.AScript;
import org.dataglow.core.Engine;
import org.dataglow.core.data.DataProvider;

/**
 * An Engine that proxies {@link org.dataglow.core.AScript} execution to Jupyter (http://jupyter.org/) backend using
 * it's REST API and getting results in form of .ipynb document.
 */
public class JupyterEngine implements Engine {

    @Override
    public AResults run(AScript script, DataProvider dataProvider) {
        return null;
    }
}
