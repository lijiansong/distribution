package org.apache.storm.storm_sql.planner;

/**
 * Created by json-lee on 17-6-4.
 */
public enum OpCode {
    SELECTION,
    JOIN,
    GROUP_BY,
    AGGR_FUNC,
    PROJECTION,
    SOURCE
}
