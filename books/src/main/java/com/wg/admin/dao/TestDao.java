package com.wg.admin.dao;

import com.wg.admin.domain.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-11-6
 * Time: 上午10:41
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface TestDao extends JpaRepository<Test, Long> {
}
