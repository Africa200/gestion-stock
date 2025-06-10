package com.example.gestionstock.services.impl;

import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Product;
import com.example.gestionstock.mapper.ProductMapper;
import com.example.gestionstock.repository.ProductRepository;
import com.example.gestionstock.services.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepository productRepository;
    private ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return null;
        return productMapper.toProductDTO(product);
    }


    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map(product -> productMapper.toProductDTO(product)).toList();
        return productDTOS;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
        Product product=ProductMapper.toProduct(productDTO);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toProductDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product product=ProductMapper.toProduct(productDTO);
        Product product1= productRepository.findById(product.getId()).orElse(null);
        if(product1!=null){
            product1.setLibelle(product.getLibelle());
            product1.setCategory(product.getCategory());
            product1.setPrixUnitaire(product.getPrixUnitaire());
            product1.setQuantiteEnStock(product.getQuantiteEnStock());
            Product save = productRepository.save(product1);
            return ProductMapper.toProductDTO(save);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public List<ProductDTO> getProductsByIds(List<Long> productIds) {
        if (productIds != null && !productIds.isEmpty()) {
            return productRepository.findAllById(productIds).stream().map(p->productMapper.toProductDTO(p)).toList();
        }
        return List.of();
    }

    @Override
    public long countAllProducts() {
        return productRepository.count();
    }

    @Override
    public long nombreProduitParEnStockFaible() {
        List<Product> products= productRepository.findAll();
        return products.stream().filter(p->p.getQuantiteEnStock()<=p.getSeuilAlerte()).count();
    }
}
