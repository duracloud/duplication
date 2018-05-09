/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mill.workman;

/**
 * @author Daniel Bernstein
 */
public interface TaskWorker extends Runnable {

    public static final int MAX_ATTEMPTS = 4;

}
