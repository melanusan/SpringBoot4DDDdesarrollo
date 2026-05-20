package com.debuggeandoideas.erp_lite.domain.ports;

import com.debuggeandoideas.erp_lite.domain.product.ProductImage;

/**
 *  Port for storage S3 files
 */
public interface ImageStorageService {

    ProductImage upload(String imageName, byte[] imageData);
    void delete(ProductImage img);
    byte[] download(ProductImage img);
}
