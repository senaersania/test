package com.phintraco.test.repository.common;

import com.phintraco.test.models.commons.Inbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundRepository extends JpaRepository<Inbound, Long> {
}
