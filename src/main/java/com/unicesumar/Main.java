package com.unicesumar;

import com.unicesumar.entities.Product;
import com.unicesumar.entities.ProductSale;
import com.unicesumar.entities.Sale;
import com.unicesumar.entities.User;
import com.unicesumar.paymentMethods.PaymentType;
import com.unicesumar.repository.ProductRepository;
import com.unicesumar.repository.SalesRepository;
import com.unicesumar.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;


public class Main {
    public static void main(String[] args) {
        ProductRepository listaDeProdutos = null;
        UserRepository listaDeUsuarios = null;
        SalesRepository listaDeVendas = null;

        Connection conn = null;
        
        // Parâmetros de conexão
        String url = "jdbc:sqlite:database.sqlite";

        // Tentativa de conexão
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                listaDeProdutos = new ProductRepository(conn);
                listaDeUsuarios = new UserRepository(conn);
                listaDeVendas = new SalesRepository(conn);
            } else {
                System.out.println("Falha na conexão.");
                System.exit(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n---------MENU---------");
            System.out.println("1 - Cadastrar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Cadastrar Usuário");
            System.out.println("4 - Listar Usuários");
            System.out.println("5 - Registrar Venda");
            System.out.println("6 - Listar Vendas");
            System.out.println("7 - Sair");
            System.out.println("----------------------");
            System.out.println("Escolha uma opção: ");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("\nCadastrar Produto\n");
                    listaDeProdutos.save(new Product("Teste", 10));
                    listaDeProdutos.save(new Product("Computador", 3000));
                    System.out.println("Produtos cadastrados com sucesso!\n");
                    break;
                case 2:
                    System.out.println("\nListar Produtos\n");
                    List<Product> products = listaDeProdutos.findAll();
                    for (Product product : products) {
                        System.out.println(product.toStringWithUUID());
                    }
                    break;
                case 3:
                    System.out.println("\nCadastrar Usuário\n");
                    System.out.println("Digite o email: ");
                    String email = scanner.next();
                    listaDeUsuarios.save(new User("Rafael Labegalini", email, "1234"));
                    System.out.println("\nUsuário cadastrado com sucesso!\n");
                    break;
                case 4:
                    System.out.println("\nListar Usuários\n");
                    List<User> users = listaDeUsuarios.findAll();
                    users.forEach(System.out::println);
                    break;
                case 5:
                    System.out.println("\nRegistrar venda\n");

                    System.out.println("Digite o email do usuário: ");
                    String userEmail = scanner.next();
                    Optional<User> user = listaDeUsuarios.findByEmail(userEmail);
                    if (!user.isPresent()) {
                        System.out.println("\nUsuário não encontrado.");
                        return;
                    }
                    else 
                        System.out.println("\nUsuário encontrado: " + user.get().getName());

                    List<ProductSale> productSaleList = new ArrayList<>();
                    System.out.println("\nDigite os IDs dos produtos (separados por vírgula): ");
                    scanner.nextLine();
                    String productIdsLine = scanner.nextLine();
                    String[] productIds = productIdsLine.split(",");

                    System.out.println("\nProdutos encontrados:");
                    for (String productId : productIds) {
                        productId = productId.trim();
                        Optional<Product> product = listaDeProdutos.findById(UUID.fromString(productId));
                        if (!product.isPresent()) {
                            System.out.println("- Produto com ID " + productId + " não encontrado.");
                            return;
                        } else {
                            Product foundProduct = product.get();
                            System.out.println(foundProduct.toString());
                        }

                        System.out.println("\nDigite a quantidade para o produto " + product.get().getName() + ": ");
                        int quantity = scanner.nextInt();
                        if (quantity <= 0) {
                            System.out.println("\nQuantidade inválida para o produto " + product.get().getName() + ".");
                            continue;
                        }

                        productSaleList.add(new ProductSale(product.get(), quantity));
                        System.out.println("\nProduto " + product.get().getName() + " adicionado ao carrinho com sucesso!");
                    }

                    System.out.println("\nEscolha a forma de pagamento:");
                    System.out.println("1 - Cartão de Crédito");
                    System.out.println("2 - Boleto");
                    System.out.println("3 - PIX");
                    System.out.print("Opção: ");
                    int paymentOption = scanner.nextInt();

                    PaymentType paymentType;
                    switch (paymentOption) {
                        case 1:
                            paymentType = PaymentType.CARTAO;
                            break;
                        case 2:
                            paymentType = PaymentType.BOLETO;
                            break;
                        case 3:
                            paymentType = PaymentType.PIX;
                            break;
                        default:
                            System.out.println("Opção inválida. Operação cancelada.");
                            return;
                    }

                    System.out.println("\nAguarde, efetuando pagamento...");
                    PaymentMethodFactory.create(paymentType);

                    Sale sale = new Sale(user.get().getUuid(), paymentType);
                    
                    System.out.println("\nResumo da venda: ");
                    System.out.println("Cliente: " + user.get().getName());
                    System.out.println("Produtos: ");
                    BigDecimal totalValue = BigDecimal.ZERO;
                    for (ProductSale productSale : productSaleList) {
                        System.out.println(productSale.getProduct().toString());
                        totalValue = totalValue.add(productSale.getTotalPrice());
                    }
                    System.out.println("Valor total: R$" + totalValue);
                    System.out.println("Pagamento: " + sale.getPaymentType());

                    listaDeVendas.save(sale);
                    listaDeVendas.saveProducts(productSaleList, sale.getUuid());
                    System.out.println("Venda registrada com sucesso!");
                    break;
                case 6:
                    System.out.println("\nListar Vendas");
                    List<SimpleEntry<Sale, List<ProductSale>>> sales = listaDeVendas.findAllWithProducts();
                    
                    for (SimpleEntry<Sale, List<ProductSale>> saleEntry : sales) {
                        Sale saleItem = saleEntry.getKey();
                        User userItem = listaDeUsuarios.findById(saleItem.getUserId()).orElse(null);
                        List<ProductSale> productSales = saleEntry.getValue();
                        
                        System.out.println("\nVenda: " + saleItem.getUuid());
                        System.out.println("Usuário: " + userItem.getName());
                        System.out.println("Forma de pagamento: " + saleItem.getPaymentType());
                        System.out.println("Data da venda: " + saleItem.getSaleDate());
                        System.out.println("Produtos: ");
                        
                        for (ProductSale p : productSales) {
                            System.out.println("- " + p.getQuantity() + "x " + p.getProduct().getName() + " (R$" + p.getTotalPrice() + ")");
                        }
                    }
                    break;
                case 7:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente");
                    ;
            }

        } while (option != 5);

        scanner.close();
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
