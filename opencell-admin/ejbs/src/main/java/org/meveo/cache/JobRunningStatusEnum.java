package org.meveo.cache;

/**
 * Specifies if Job is running and if so - on this or another cluster node
 * 
 * @author Andrius Karpavicius
 */
public enum JobRunningStatusEnum {

    NOT_RUNNING, RUNNING_THIS, RUNNING_OTHER;
}
