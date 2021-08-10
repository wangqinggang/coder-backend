package com.ideaworks.club.domain.table;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EsttableRepository extends JpaRepository<Esttable, Integer>, PagingAndSortingRepository<Esttable, Integer> , JpaSpecificationExecutor<Esttable> {
}
