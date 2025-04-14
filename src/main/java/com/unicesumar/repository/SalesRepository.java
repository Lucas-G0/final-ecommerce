package com.unicesumar.repository;

import com.unicesumar.entities.Product;
import com.unicesumar.entities.ProductSale;
import com.unicesumar.entities.Sale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SalesRepository implements EntityRepository<Sale> {
    private final Connection connection;

    public SalesRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Sale entity) {
        String query = "INSERT INTO sales VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.setString(1, entity.getUuid().toString());
            stmt.setString(2, entity.getUserId().toString());
            stmt.setString(3, entity.getPaymentType().toString());
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(entity.getSaleDate()));
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProducts(List<ProductSale> products, UUID saleId) {
        String query = "INSERT INTO sale_products VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            for (ProductSale product : products) {
                stmt.setString(1, saleId.toString());
                stmt.setString(2, product.getUuid().toString());
                stmt.setInt(3, product.getQuantity());
                stmt.setBigDecimal(4, product.getTotalPrice());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        String query = "SELECT * FROM sales WHERE uuid = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.setString(1, id.toString());
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Sale(
                        UUID.fromString(resultSet.getString("uuid")),
                        UUID.fromString(resultSet.getString("user_id")),
                        resultSet.getString("payment_method"),
                        resultSet.getTimestamp("sale_date").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Sale> findAll() {
        String query = "SELECT * FROM sales";
        ArrayList<Sale> sales = new ArrayList<>();

        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                sales.add(new Sale(
                    UUID.fromString(resultSet.getString("id")),
                    UUID.fromString(resultSet.getString("user_id")),
                    resultSet.getString("payment_method"),
                    resultSet.getTimestamp("sale_date").toLocalDateTime()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sales;
    }

    public List<SimpleEntry<Sale, List<ProductSale>>> findAllWithProducts() {
        String query = "SELECT s.id AS sale_id, s.user_id, s.payment_method, s.sale_date, " +
                       "sp.product_id, sp.quantity, sp.total_price " +
                       "FROM sales s " +
                       "LEFT JOIN sale_products sp ON s.id = sp.sale_id";
        List<SimpleEntry<Sale, List<ProductSale>>> result = new ArrayList<>();

        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();
            SimpleEntry<Sale, List<ProductSale>> currentEntry = null;
            UUID currentSaleId = null;

            while (resultSet.next()) {
                UUID saleId = UUID.fromString(resultSet.getString("sale_id"));

                if (currentSaleId == null || !currentSaleId.equals(saleId)) {
                    currentSaleId = saleId;
                    Sale sale = new Sale(
                        saleId,
                        UUID.fromString(resultSet.getString("user_id")),
                        resultSet.getString("payment_method"),
                        resultSet.getTimestamp("sale_date").toLocalDateTime()
                    );
                    currentEntry = new SimpleEntry<>(sale, new ArrayList<>());
                    result.add(currentEntry);
                }

                if (resultSet.getString("product_id") != null) {
                    ProductRepository productRepository = new ProductRepository(this.connection);
                    Optional<Product> product = productRepository.findById(
                        UUID.fromString(resultSet.getString("product_id"))
                    );

                    ProductSale productSale = new ProductSale(
                        product.orElseThrow(() -> new RuntimeException("Product not found")),
                        resultSet.getInt("quantity"),
                        resultSet.getBigDecimal("total_price")
                    );

                    currentEntry.getValue().add(productSale);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    

    @Override
    public void deleteById(UUID id) {
        String query = "DELETE FROM sales WHERE uuid = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
