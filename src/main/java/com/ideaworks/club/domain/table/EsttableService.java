package com.ideaworks.club.domain.table;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EsttableService {
    EsttableDTO saveEsttable(EsttableDTO esttableDTO);

    List<EsttableDTO> getAllEsttables();

    EsttableDTO getEsttableByBh(Integer bh);

    EsttableDTO updateEsttableByBh(EsttableDTO esttableDTO, Integer bh);

    void deleteEsttableByBh(Integer bh);

    Boolean isEsttableExist(Integer bh);

    Page<EsttableDTO> getPageByCondition(EsttableDTO esttabledto, Pageable pageable);
}
