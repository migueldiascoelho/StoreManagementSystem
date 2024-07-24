import java.io.IOException;
import java.util.*;

public class Calculos {

    // Mapas para armazenar descontos por categoria, fidelidade e custos de envio
    private static final Map<String, Double> categoryDiscounts = new HashMap<>();
    private static final Map<Integer, Double> loyaltyDiscounts = new HashMap<>();
    private static final Map<String, Double> shippingCosts = new HashMap<>();

    // Bloco estático para carregar os dados de cálculos a partir do ficheiro Prolog
    static {
        try {
            PrologUtils.parsePrologFileForCalculations(new Calculos());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para analisar e armazenar descontos por categoria a partir de uma linha do ficheiro
    public void parseDiscount(String line) {
        try {
            String[] parts = line.substring(9, line.length() - 2).split(",");
            String category = parts[0].replace("'", "").trim();
            double discount = Double.parseDouble(parts[1].trim());
            categoryDiscounts.put(category, discount);
        } catch (Exception e) {
            System.err.println("Error parsing discount: " + line);
            e.printStackTrace();
        }
    }

    // Método para analisar e armazenar descontos de fidelidade a partir de uma linha do ficheiro
    public void parseLoyaltyDiscount(String line) {
        try {
            String[] parts = line.substring(16, line.length() - 2).split(",");
            String yearsStr = parts[0].replace("(", "").trim();
            int years;

            if (yearsStr.contains(">")) {
                years = Integer.parseInt(yearsStr.replaceAll("\\D+", "")) + 1; // Trata ">", por exemplo ">5" como 6
            } else {
                years = Integer.parseInt(yearsStr);
            }

            double discount = Double.parseDouble(parts[1].trim());
            loyaltyDiscounts.put(years, discount);
        } catch (Exception e) {
            System.err.println("Error parsing loyalty discount: " + line);
            e.printStackTrace();
        }
    }

    // Método para analisar e armazenar custos de envio a partir de uma linha do ficheiro
    public void parseShippingCost(String line) {
        try {
            String[] parts = line.substring(14, line.length() - 2).split(",");
            String district = parts[0].replace("'", "").trim();
            double cost = Double.parseDouble(parts[1].trim());
            shippingCosts.put(district, cost);
        } catch (Exception e) {
            System.err.println("Error parsing shipping cost: " + line);
            e.printStackTrace();
        }
    }

    // Método para calcular o preço final da compra
    public static CalculationResult calculateFinalPrice(Purchase purchase) {
        Customer customer = purchase.getCustomer();
        List<Item> cartItems = purchase.getCart().getItems();

        // Calcula o preço total antes dos descontos
        double totalPriceBeforeDiscount = cartItems.stream()
            .mapToDouble(item -> item.getPrice() * purchase.getCart().getQuantityInCart(item.getId()))
            .sum();

        // Calcula o desconto total por categoria
        double totalCategoryDiscount = cartItems.stream()
            .mapToDouble(item -> {
                double discount = categoryDiscounts.getOrDefault(item.getCategory(), 0.0);
                return item.getPrice() * purchase.getCart().getQuantityInCart(item.getId()) * discount;
            }).sum();

        // Calcula o valor do desconto de fidelidade
        double loyaltyDiscountAmount = getLoyaltyDiscount(customer.getYearsOfLoyalty(), totalPriceBeforeDiscount);

        // Obtém o custo de envio
        double shippingCost = shippingCosts.getOrDefault(customer.getDistrict(), 0.0);

        // Calcula o preço final
        double finalPrice = totalPriceBeforeDiscount - totalCategoryDiscount - loyaltyDiscountAmount + shippingCost;

        // Retorna os resultados do cálculo
        return new CalculationResult(
            customer.getId(),
            customer.getName(),
            cartItems,
            totalPriceBeforeDiscount,
            totalCategoryDiscount,
            loyaltyDiscountAmount,
            shippingCost,
            finalPrice
        );
    }

    // Método para obter o desconto de fidelidade baseado nos anos de fidelidade do cliente
    private static double getLoyaltyDiscount(int years, double totalPriceBeforeDiscount) {
        double discount = 0.0;
        for (Map.Entry<Integer, Double> entry : loyaltyDiscounts.entrySet()) {
            if (years >= entry.getKey()) {
                discount = entry.getValue();
            }
        }
        return totalPriceBeforeDiscount * discount;
    }

    // Classe para armazenar os resultados do cálculo
    public static class CalculationResult {
        private int customerId;
        private String customerName;
        private List<Item> cartItems;
        private double totalPriceBeforeDiscount;
        private double totalCategoryDiscount;
        private double loyaltyDiscountAmount;
        private double shippingCost;
        private double finalPrice;

        public CalculationResult(int customerId, String customerName, List<Item> cartItems, double totalPriceBeforeDiscount,
                                 double totalCategoryDiscount, double loyaltyDiscountAmount, double shippingCost, double finalPrice) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.cartItems = cartItems;
            this.totalPriceBeforeDiscount = totalPriceBeforeDiscount;
            this.totalCategoryDiscount = totalCategoryDiscount;
            this.loyaltyDiscountAmount = loyaltyDiscountAmount;
            this.shippingCost = shippingCost;
            this.finalPrice = finalPrice;
        }

        // Getters
        public int getCustomerId() { return customerId; }
        public String getCustomerName() { return customerName; }
        public List<Item> getCartItems() { return cartItems; }
        public double getTotalPriceBeforeDiscount() { return totalPriceBeforeDiscount; }
        public double getTotalCategoryDiscount() { return totalCategoryDiscount; }
        public double getLoyaltyDiscountAmount() { return loyaltyDiscountAmount; }
        public double getShippingCost() { return shippingCost; }
        public double getFinalPrice() { return finalPrice; }
    }
}
