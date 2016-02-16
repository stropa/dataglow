package org.dataglow.core.data;

/**
 * A pre-configured provider of data for analysis. You call provide ann get access to data.
 */
public interface DataProvider {

    DataAccess provide();

}
