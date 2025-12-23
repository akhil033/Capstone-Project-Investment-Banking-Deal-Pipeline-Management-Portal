package com.investbank.dealpipeline.repository;

import com.investbank.dealpipeline.model.Deal;
import com.investbank.dealpipeline.model.DealStage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends MongoRepository<Deal, String> {
    
    List<Deal> findByCurrentStage(DealStage stage);
    
    List<Deal> findByCreatedBy(String userId);
    
    List<Deal> findByAssignedTo(String userId);
    
    List<Deal> findByClientNameContainingIgnoreCase(String clientName);
    
    List<Deal> findBySectorIgnoreCase(String sector);
}
