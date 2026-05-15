package com.debuggeandoideas.erp_lite.persistence.mongo.repositories;

import com.debuggeandoideas.erp_lite.persistence.mongo.documents.CatalogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CatalogRepository extends MongoRepository<CatalogDocument, String> {
}
