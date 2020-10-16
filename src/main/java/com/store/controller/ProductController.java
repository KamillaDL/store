package com.store.controller;

import com.store.domain.Basket;
import com.store.domain.Product;
import com.store.repos.BasketRepository;
import com.store.repos.ProductRepository;
import com.store.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/product")
    public String show_items(@RequestParam(name="id") Integer product_id,Principal principal, Map<String,Object> model) {
        Product product = productRepository.findById(product_id);
        model.put("product", product);
        if(principal != null)
            model.put("user", principal.getName());
        return "product";
    }

    @PostMapping("/add_to_basket_product")
    public ResponseEntity<Void> add_basket(Integer product_id, Long number, Principal principal, Map<String,Object> model) {
        List<Basket> b = basketRepository.findByProduct_id(product_id);
        for (Basket bb : b) {
            if (bb.getUser() == userRepository.findByUsername(principal.getName())) {
                bb.setValue(bb.getValue() + 1);
                basketRepository.save(bb);
                return ResponseEntity.noContent().<Void>build();
            }
        }
        Basket basket = new Basket();
        basket.setProduct(productRepository.findById(product_id));
        basket.setUser(userRepository.findByUsername(principal.getName()));
        basket.setValue(number);
        basketRepository.save(basket);
        return ResponseEntity.noContent().<Void>build();
    }

}
