package com.unicesumar.entities;

import java.math.BigDecimal;

public class ProductSale extends Entity {
    private Product product;
    private int quantity;
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public ProductSale(Product product, int quantity) {
        super(product.getUuid());
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = BigDecimal.valueOf(product.getPrice()).multiply(new BigDecimal(quantity));
    }

    public ProductSale(Product product, int quantity, BigDecimal totalPrice) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String toString(){
        return "Produto: " + product.getName() + "\n" +
                "Quantidade: " + quantity + "\n" +
                "Preço Total: " + totalPrice + "\n";
    }
}
