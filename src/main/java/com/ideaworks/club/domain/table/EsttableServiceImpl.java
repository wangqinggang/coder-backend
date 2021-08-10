//package com.ideaworks.club.domain.table;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Service;
//
//import javax.persistence.criteria.Predicate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class EsttableServiceImpl implements EsttableService {
//    @Autowired
//    private EsttableRepository esttableRepository;
//
//    public EsttableDTO saveEsttable(final EsttableDTO esttableDTO) {
//        final Esttable esttable = new Esttable();
//        mapToEntity(esttableDTO,esttable);
//        return mapToDTO(esttableRepository.save(esttable),new EsttableDTO());
//    }
//
//    public List<EsttableDTO> getAllEsttables() {
//        return esttableRepository.findAll()
//                .stream()
//                .map(esttable -> mapToDTO(esttable, new EsttableDTO()))
//                .collect(Collectors.toList());
//    }
//
//    public EsttableDTO getEsttableByBh(final Integer bh) {
//        return esttableRepository.findById(bh)
//                .map(esttable->mapToDTO(esttable,new EsttableDTO()))
//                .orElse(new EsttableDTO());
//    }
//
//    public EsttableDTO updateEsttableByBh(final EsttableDTO esttableDTO, final Integer bh) {
//        final Esttable esttable = esttableRepository.findById(bh).orElse(new Esttable());
//        mapToEntity(esttableDTO ,esttable);
//        return mapToDTO(esttableRepository.save(esttable),new EsttableDTO());
//    }
//
//    public void deleteEsttableByBh(final Integer bh) {
//        esttableRepository.deleteById(bh);
//    }
//
//    public Boolean isEsttableExist(Integer bh) {
//        return esttableRepository.existsById(bh);
//    }
//
//    public Page<EsttableDTO> getPageByCondition(final EsttableDTO esttableDTO,
//                                                final Pageable pageable) {
//        return esttableRepository.findAll(this.createSpecification(esttableDTO),pageable)
//                .map(esttable ->{
//                    EsttableDTO esttableDTO1 = new EsttableDTO();
//                    return mapToDTO(esttable,esttableDTO1);
//                });
//    }
//
//    public Specification<Esttable> createSpecification(final EsttableDTO esttableDTO) {
//        return (root,query,criteriaBuilder) -> {
//                List<Predicate> predicates = new ArrayList<Predicate>();
//        if(!StringUtils.isEmpty(esttableDTO.getBh())){
//            Predicate predicate = criteriaBuilder.equal(root.get("bh"),esttableDTO.getBh());
//            predicates.add(predicate);
//        }
//        if(!StringUtils.isEmpty(esttableDTO.getBh())){
//            Predicate predicate = criteriaBuilder.equal(root.get("bh"),esttableDTO.getBh());
//            predicates.add(predicate);
//        }
//        if(!StringUtils.isEmpty(esttableDTO.getUserid())){
//            Predicate predicate = criteriaBuilder.equal(root.get("userid"),esttableDTO.getUserid());
//            predicates.add(predicate);
//        }
//        if(esttableDTO.getTid()!=null){
//            Predicate predicate = criteriaBuilder.equal(root.get("tid"),esttableDTO.getTid());
//            predicates.add(predicate);
//        }
//        if(esttableDTO.getTname()!=null){
//            Predicate predicate = criteriaBuilder.equal(root.get("tname"),esttableDTO.getTname());
//            predicates.add(predicate);
//        }
//        return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();};
//    }
//
//    public Esttable mapToEntity(final EsttableDTO esttableDTO, final Esttable esttable) {
//        BeanUtils.copyProperties(esttableDTO,esttable);
//        return esttable;
//    }
//
//    public EsttableDTO mapToDTO(final Esttable esttable, final EsttableDTO esttableDTO) {
//        BeanUtils.copyProperties(esttable,esttableDTO);
//        return esttableDTO;
//    }
//}
