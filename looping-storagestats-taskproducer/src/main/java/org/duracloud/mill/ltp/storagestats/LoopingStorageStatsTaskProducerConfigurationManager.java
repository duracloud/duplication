/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mill.ltp.storagestats;

import java.io.File;

import org.duracloud.mill.config.ConfigConstants;
import org.duracloud.mill.ltp.LoopingTaskProducerConfigurationManager;
import org.duracloud.mill.ltp.PathFilterManager;

/**
 * @author Daniel Bernstein
 * Date: May 5, 2014
 */
public class LoopingStorageStatsTaskProducerConfigurationManager extends LoopingTaskProducerConfigurationManager {
    public PathFilterManager getPathFilterManager() {
        PathFilterManager pathFilterManager = new PathFilterManager();

        String exclusions = System.getProperty(ConfigConstants.LOOPING_STORAGE_STATS_EXCLUSION_LIST_KEY);
        if (exclusions != null) {
            pathFilterManager.setExclusions(new File(exclusions));
        }

        String inclusions = System.getProperty(ConfigConstants.LOOPING_STORAGE_STATS_INCLUSION_LIST_KEY);
        if (inclusions != null) {
            pathFilterManager.setInclusions(new File(inclusions));
        }

        return pathFilterManager;

    }

    public String getStorageStatsQueue() {
        return System.getProperty(ConfigConstants.QUEUE_NAME_STORAGE_STATS);
    }

}
