package com.Code.Service;

import com.Code.model.Product;
import com.Code.model.Seller;
import com.Code.request.CreateProductRequest;
import org.springframework.data.domain.Page;


import java.util.List;

public interface ProductService {
    public Product createProduct(CreateProductRequest req , Seller seller);
    public List<Product> searchProducts();
    Product findProductById(Long productId);
    public Product updateProduct(Long productId, Product product);
    public void deleteProduct(Long productId) ;
    public Page<Product> getAllProducts(
            String category,
            String brand,
            String color,
            String sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber

    );
    List<Product> getProductBySellerId(Long sellerId);



}

