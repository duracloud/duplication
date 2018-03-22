/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mill.workman;

import java.io.File;

import org.duracloud.common.queue.task.Task;
import org.duracloud.mill.credentials.CredentialsRepo;

/**
 * An abstract base class for building TaskProcessor factories that require
 * access to credentials.
 *
 * @author Daniel Bernstein
 */
public abstract class TaskProcessorFactoryBase implements TaskProcessorFactory {

    private CredentialsRepo credentialRepo;
    private File workDir;

    public TaskProcessorFactoryBase(CredentialsRepo credentialRepo) {
        this(credentialRepo, null);
    }

    public TaskProcessorFactoryBase(CredentialsRepo credentialRepo,
                                    File workDir) {
        this.credentialRepo = credentialRepo;
        this.workDir = workDir;
    }

    @Override
    public final TaskProcessor create(Task task)
        throws TaskProcessorCreationFailedException {
        if (!isSupported(task)) {
            throw new TaskProcessorCreationFailedException(
                task + " is not supported by this factory:" + getClass().getSimpleName());
        }

        return createImpl(task);
    }

    protected CredentialsRepo getCredentialRepo() {
        return credentialRepo;
    }

    protected File getWorkDir() {
        return workDir;
    }

    public abstract boolean isSupported(Task task);

    protected abstract TaskProcessor createImpl(Task task) throws TaskProcessorCreationFailedException;
}
