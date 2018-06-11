package com.wg.solr.dao;

import com.wg.solr.domain.TempSolrIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-24
 * Time: 下午8:40
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface TempSolrIndexDao extends JpaRepository<TempSolrIndex, Long> {
    List<TempSolrIndex> findByType(int solrType);
}
