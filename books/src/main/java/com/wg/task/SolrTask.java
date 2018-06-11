package com.wg.task;

import com.wg.common.Enum.solr.CallType;
import com.wg.common.Enum.solr.SolrType;
import com.wg.solr.utils.SolrUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-8-28
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */

@Component
public class SolrTask {
    //delete
    @Transactional
    @Scheduled(fixedDelay = 120000)
    public void deleteIndex() {
        SolrUtils.tryDelete(SolrType.BookGroup.getType(), CallType.Task.getType());
        SolrUtils.tryDelete(SolrType.BookSheet.getType(), CallType.Task.getType());
        SolrUtils.tryDelete(SolrType.User.getType(), CallType.Task.getType());
        SolrUtils.tryDelete(SolrType.UserBook.getType(), CallType.Task.getType());
    }

    //update
    @Transactional
    @Scheduled(fixedDelay = 120000)
    public void updateIndexSlow() {
        SolrUtils.tryUpdate(SolrType.Book.getType(), CallType.Task.getType());
        SolrUtils.tryUpdate(SolrType.BookGroup.getType(), CallType.Task.getType());
        SolrUtils.tryUpdate(SolrType.BookSheet.getType(), CallType.Task.getType());
        SolrUtils.tryUpdate(SolrType.User.getType(), CallType.Task.getType());
        SolrUtils.tryUpdate(SolrType.UserBook.getType(), CallType.Task.getType());
    }
}
