/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mill.queue;

import org.duracloud.mill.domain.Task;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public interface TaskQueue {

    /**
     * puts a task on the queue
     * 
     * @param task
     */
    void put(Task task);

    /**
     * Blocks until a task is available
     * 
     * @return
     */
    Task take() throws TimeoutException;

    /**
     * Returns the visbility timeout.
     * 
     * @return
     */
    long getDefaultVisibilityTimeout();

    /**
     * Responsible for robustly extending the visibility timeout.
     * 
     * @param task
     * @param milliseconds
     * @throws TaskNotFoundException
     */
    void extendVisibilityTimeout(Task task, long milliseconds)
            throws TaskNotFoundException;

    /**
     * Deletes a task from the queue.
     * 
     * @param task
     */
    void deleteTask(Task task);
}