/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mill.dup;

import java.io.File;

import org.duracloud.common.queue.TaskQueue;
import org.duracloud.common.queue.task.Task;
import org.duracloud.mill.common.storageprovider.StorageProviderFactory;
import org.duracloud.mill.credentials.CredentialsRepo;
import org.duracloud.mill.credentials.CredentialsRepoException;
import org.duracloud.mill.credentials.StorageProviderCredentials;
import org.duracloud.mill.manifest.ManifestStore;
import org.duracloud.mill.task.DuplicationTask;
import org.duracloud.mill.workman.TaskProcessor;
import org.duracloud.mill.workman.TaskProcessorCreationFailedException;
import org.duracloud.mill.workman.TaskProcessorFactoryBase;
import org.duracloud.storage.provider.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for creating DuplicationTaskProcessors
 *
 * @author Daniel Bernstein
 */
public class DuplicationTaskProcessorFactory extends TaskProcessorFactoryBase {
    private static Logger log =
        LoggerFactory.getLogger(DuplicationTaskProcessorFactory.class);

    private TaskQueue auditTaskQueue;
    private StorageProviderFactory storageProviderFactory;
    private ManifestStore manifestStore;

    public DuplicationTaskProcessorFactory(CredentialsRepo repo,
                                           StorageProviderFactory storageProviderFactory,
                                           File workDir,
                                           TaskQueue auditTaskQueue,
                                           ManifestStore manifestStore) {
        super(repo, workDir);
        this.auditTaskQueue = auditTaskQueue;
        this.storageProviderFactory = storageProviderFactory;
        this.manifestStore = manifestStore;
    }

    @Override
    public boolean isSupported(Task task) {
        return task.getType().equals(Task.Type.DUP);
    }

    @Override
    protected TaskProcessor createImpl(Task task)
        throws TaskProcessorCreationFailedException {

        DuplicationTask dtask = new DuplicationTask();
        dtask.readTask(task);
        String subdomain = dtask.getAccount();

        try {
            StorageProvider sourceStore =
                createStorageProvider(dtask.getSourceStoreId(),
                                      subdomain,
                                      auditTaskQueue);
            StorageProvider destStore =
                createStorageProvider(dtask.getDestStoreId(),
                                      subdomain,
                                      auditTaskQueue);
            return new DuplicationTaskProcessor(dtask, sourceStore, destStore,
                                                getWorkDir(), manifestStore);
        } catch (Exception e) {
            log.error("failed to create task: unable to locate credentials " +
                      "for subdomain: " + e.getMessage(), e);
            throw new TaskProcessorCreationFailedException(
                "failed to create task: unable to locate credentials " +
                "for subdomain: " + subdomain, e);
        }
    }

    /**
     * @param storeId
     * @param subdomain
     * @return
     */
    private StorageProvider createStorageProvider(String storeId,
                                                  String subdomain,
                                                  TaskQueue auditTaskQueue) {
        try {
            StorageProviderCredentials credentials =
                getCredentialRepo().getStorageProviderCredentials(subdomain,
                                                                  storeId);
            return this.storageProviderFactory.createWithAudit(credentials,
                                                               subdomain,
                                                               auditTaskQueue);
        } catch (CredentialsRepoException e) {
            throw new RuntimeException(e);
        }
    }
}
