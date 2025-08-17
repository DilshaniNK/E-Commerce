package com.Code.Service.Impl;

import com.Code.Service.ProductService;
import com.Code.exception.productException;
import com.Code.model.Category;
import com.Code.model.Product;
import com.Code.model.Seller;
import com.Code.repository.CategoryRepository;
import com.Code.repository.ProductRepository;
import com.Code.request.CreateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product createProduct(CreateProductRequest req, Seller seller) {
        Category category1 = categoryRepository.findByCategoryId(req.getCategory());
        if(category1 == null){
            Category category = new Category();
            category.setCategoryId(req.getCategory());
            category.setLevel(1);
            category1 = categoryRepository.save(category);
        }
        Category category2 = categoryRepository.findByCategoryId(req.getCategory2());
        if(category2 == null){
            Category category = new Category();
            category.setCategoryId(req.getCategory2());
            category.setLevel(2);
            category.setParentCategory(category1);
            category2 = categoryRepository.save(category);
        }
        Category category3 = categoryRepository.findByCategoryId(req.getCategory3());
        if(category3 == null){
            Category category = new Category();
            category.setCategoryId(req.getCategory3());
            category.setLevel(3);
            category.setParentCategory(category2);
            category3 = categoryRepository.save(category);
        }

        int discountPercentage = calculateDiscountPercentage(req.getMrpPrice(), req.getSellingPrice());


        Product product = new Product();
        product.setSeller(seller);
        product.setCategory(category3);
        product.setDescription(req.getDescription());
        product.setCreatedAt(LocalDateTime.now());
        product.setTitle(req.getTitle());
        product.setColor(req.getColor());
        product.setSellingPrice(req.getSellingPrice());
        product.setImages(req.getImages());
        product.setMrpPrice(req.getMrpPrice());
        product.setSizes(req.getSizes());
        product.setDiscountPercent(discountPercentage);


        return productRepository.save(product);
    }

    private int calculateDiscountPercentage(int mrpPrice, int sellingPrice) {
        if(mrpPrice<=0){
            throw new IllegalArgumentException("MrpPrice must be greater than 0");
        }
        double discount = mrpPrice - sellingPrice;
        double discountPercentage = (discount / mrpPrice) * 100;
        return (int)discountPercentage;

    }

    @Override
    public List<Product> searchProducts() {
        return List.of();
    }

    @Override
    public Product findProductById(Long productId) throws productException {

        return productRepository.findById(productId).orElseThrow(()->
                new productException("Product not found with id"+ productId));
    }

    @Override
    public Product updateProduct(Long productId, Product product) throws productException {
        findProductById(productId);
        product.setId(productId);
        return productRepository.save(product);

    }

    @Override
    public void deleteProduct(Long productId) throws productException {
        Product product = findProductById(productId);
        productRepository.delete(product);
    }

    @Override
    public Page<Product> getAllProducts(String category, String brand, String color, String sizes, Integer minPrice, Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber) {
        return null;
    }

    @Override
    public List<Product> getProductBySellerId(Long sellerId) {
        return List.of();
    }
}
