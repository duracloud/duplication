/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mill.audit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.audit.AuditLogStore;
import org.duracloud.audit.task.AuditTask;
import org.duracloud.common.util.TagUtil;
import org.duracloud.contentindex.client.ContentIndexClient;
import org.duracloud.contentindex.client.ContentIndexItem;
import org.duracloud.mill.workman.TaskExecutionFailedException;
import org.duracloud.mill.workman.TaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * As a processor of audit tasks as the name suggests, this class updates the
 * content index.
 * 
 * @author Daniel Bernstein Date: Mar 20, 2014
 */
public class ContentIndexUpdatingProcessor implements TaskProcessor {
    private final Logger       log = LoggerFactory
                                           .getLogger(ContentIndexUpdatingProcessor.class);

    private AuditLogStore      auditLogStore;
    private ContentIndexClient contentIndexClient;
    private AuditTask          task;

    public ContentIndexUpdatingProcessor(AuditTask task,
            ContentIndexClient contentIndexClient){
        this.contentIndexClient = contentIndexClient;
        this.task = task;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.duracloud.mill.workman.TaskProcessor#execute()
     */
    @Override
    public void execute() throws TaskExecutionFailedException {
        try {
            String account = task.getAccount();
            String storeId = task.getStoreId();
            String spaceId = task.getSpaceId();
            String contentId = task.getContentId();
            String action = task.getAction();
            String storeType = task.getStoreType();
            Map<String, String> props = task.getContentProperties();
            String acls = task.getSpaceACLs();
            Date timestamp = new Date(Long.valueOf(task.getDateTime()));

            ContentIndexItem indexItem = new ContentIndexItem(account, 
                                                              storeId,
                                                              spaceId, 
                                                              contentId);

            Map<String, String> contentProps = null;
            if (props != null) {
                // remove the tags to ensure the tag
                // data is not duplicated in content index
                contentProps = new HashMap<String, String>(props);
                contentProps.remove(TagUtil.TAGS);
                indexItem.setProps(contentProps);
                String tagString = props.get(TagUtil.TAGS);
                if (tagString != null) {
                    indexItem.setTags(new ArrayList<String>(TagUtil
                            .parseTags(tagString)));
                }
            }

            indexItem.setStoreType(storeType);
            
            contentIndexClient.save(indexItem);
            log.debug("content index item saved: {}", indexItem);
            log.debug("task successfully processed: {}", task);
        } catch (Exception e) {
            String message = "Failed to execute " + task + ": "
                    + e.getMessage();
            log.debug(message, e);
            throw new TaskExecutionFailedException(message, e);
        }
    }

}