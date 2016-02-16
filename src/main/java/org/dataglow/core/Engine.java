package org.dataglow.core;


import org.dataglow.core.data.DataProvider;

/**
 * Engine can run analytical script and give some output
 */
public interface Engine {

    AResults run(AScript script, DataProvider dataProvider);

}
