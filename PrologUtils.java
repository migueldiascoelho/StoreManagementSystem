import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrologUtils {

    private static final String PROLOG_FILE = "store.pl";

    // Lê o ficheiro Prolog e retorna as linhas
    public static List<String> readPrologFile() throws IOException {
        List<String> lines = new ArrayList<>();
        // Usando try-with-resources para garantir que o BufferedReader seja fechado corretamente
        try (BufferedReader br = new BufferedReader(new FileReader(PROLOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line); // Adiciona cada linha lida à lista
            }
        }
        return lines;
    }

    // Guarda um novo cliente no ficheiro Prolog
    public static void saveCustomer(Customer customer) throws IOException {
        List<String> lines = readPrologFile(); // Lê todas as linhas do ficheiro Prolog
        List<String> updatedLines = new ArrayList<>(); // Lista para armazenar as linhas atualizadas
        boolean customerInserted = false;
        boolean foundCustomersSection = false;

        for (String line : lines) {
            if (line.startsWith("% Clientes")) {
                foundCustomersSection = true; // Marca que a seção de clientes foi encontrada
            }

            if (foundCustomersSection && !customerInserted && (line.trim().isEmpty() || line.startsWith("% Histórico de compras"))) {
                String customerData = String.format("customer(%d, '%s', '%s', %d).",
                        customer.getId(), customer.getName(), customer.getDistrict(), customer.getYearsOfLoyalty());
                updatedLines.add(customerData); // Adiciona os dados do cliente na lista
                customerInserted = true; // Marca que o cliente foi inserido
            }

            updatedLines.add(line); // Adiciona a linha atual na lista de linhas atualizadas
        }

        if (!customerInserted) { // Se o cliente não foi inserido na secção correta, adiciona no final
            String customerData = String.format("customer(%d, '%s', '%s', %d).",
                    customer.getId(), customer.getName(), customer.getDistrict(), customer.getYearsOfLoyalty());
            updatedLines.add(customerData);
        }

        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }

    // Atualiza o stock e o histórico de compras
    public static void updateStockAndPurchaseHistory(Calculos.CalculationResult result, Cart purchaseCart) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("item(")) {
                String updatedLine = updateStock(line, purchaseCart); // Atualiza a linha do item com a quantidade comprada
                updatedLines.add(updatedLine);
            } else {
                updatedLines.add(line); // Adiciona a linha não modificada
            }
        }

        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }

            // Adiciona a nova entrada de histórico de compras
            String purchaseDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String purchaseHistory = String.format("purchase_history(%d, '%s', %.2f, %.2f, %.2f, %.2f, %.2f).",
                    result.getCustomerId(),
                    purchaseDate,
                    result.getTotalPriceBeforeDiscount(),
                    result.getTotalCategoryDiscount(),
                    result.getLoyaltyDiscountAmount(),
                    result.getShippingCost(),
                    result.getFinalPrice());
            fw.write(purchaseHistory + "\n");
        }
    }

    // Atualiza o stock de um item
    private static String updateStock(String line, Cart purchaseCart) {
        try {
            String[] parts = line.substring(5, line.length() - 2).split(",");
            int itemId = Integer.parseInt(parts[0].trim());
            String name = parts[1].replace("'", "").trim();
            String category = parts[2].replace("'", "").trim();
            double price = Double.parseDouble(parts[3].trim());
            int currentStock = Integer.parseInt(parts[4].trim());

            // Atualiza a quantidade em stock com a quantidade comprada
            int quantityInCart = purchaseCart.getQuantityInCart(itemId);
            if (quantityInCart > 0) {
                int newStock = currentStock - quantityInCart;
                return String.format("item(%d, '%s', '%s', %.2f, %d).", itemId, name, category, price, newStock);
            }
        } catch (Exception e) {
            System.err.println("Error updating stock for line: " + line);
            e.printStackTrace();
        }
        return line; // Retorna a linha original se não houver atualização
    }

    // Analisa o ficheiro Prolog para cálculos
    public static void parsePrologFileForCalculations(Calculos calculos) throws IOException {
        List<String> lines = readPrologFile();
        for (String line : lines) {
            if (line.startsWith("discount(")) {
                calculos.parseDiscount(line); // Analisa a linha de desconto
            } else if (line.startsWith("loyalty_discount(")) {
                calculos.parseLoyaltyDiscount(line); // Analisa a linha de desconto de lealdade
            } else if (line.startsWith("shipping_cost(")) {
                calculos.parseShippingCost(line); // Analisa a linha de custo de envio
            }
        }
    }

    // Obtém vendas por data com Query Prolog
    public static List<String> getSalesByDate(String date) {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase(); // Obtém o sistema operativo
            String command;
            if (os.contains("win")) {
                // Comando para Windows
                command = String.format("swipl -s %s -g \"sales_by_date('%s', SalesString), write(SalesString), nl, sales_totals_by_date('%s', TotalsString), write(TotalsString), nl, halt.\"", PROLOG_FILE, date, date);
            } else {
                // Comando para Unix-based (Linux, macOS)
                command = String.format("sh -c \"swipl -s %s -g \\\"sales_by_date('%s', SalesString), write(SalesString), nl, sales_totals_by_date('%s', TotalsString), write(TotalsString), nl, halt.\\\"\"", PROLOG_FILE, date, date);
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Obtém vendas por cliente com Query Prolog
    public static List<String> getSalesByClient(int clientId) {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                // Comando para Windows
                command = String.format("swipl -s %s -g \"sales_by_client(%d, SalesString), write(SalesString), nl, halt.\"", PROLOG_FILE, clientId);
            } else {
                // Comando para Unix-based
                command = String.format("sh -c \"swipl -s %s -g \\\"sales_by_client(%d, SalesString), write(SalesString), nl, halt.\\\"\"", PROLOG_FILE, clientId);
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Obtém vendas por distrito através de Query Prolog
    public static List<String> getSalesByDistrict(String district) {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                // Comando para Windows
                command = String.format("swipl -s %s -g \"sales_by_district('%s', SalesString), write(SalesString), nl, sales_totals_by_district('%s', TotalsString), write(TotalsString), nl, halt.\"", PROLOG_FILE, district, district);
            } else {
                // Comando para Unix-based
                command = String.format("sh -c \"swipl -s %s -g \\\"sales_by_district('%s', SalesString), write(SalesString), nl, sales_totals_by_district('%s', TotalsString), write(TotalsString), nl, halt.\\\"\"", PROLOG_FILE, district, district);
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Obtém distritos por descontos através de Query Prolog
    public static List<String> getDistrictsByDiscounts() {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                // Comando para Windows
                command = String.format("swipl -s %s -g \"districts_by_discounts(DiscountsListString), write(DiscountsListString), nl, halt.\"", PROLOG_FILE);
            } else {
                // Comando para Unix-based
                command = String.format("sh -c \"swipl -s %s -g \\\"districts_by_discounts(DiscountsListString), write(DiscountsListString), nl, halt.\\\"\"", PROLOG_FILE);
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Obtém itens por categoria
    public static List<String> getItemsByCategory(String category) {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                if (category.equals("All")) {
                    // Comando para obter todos os itens no Windows
                    command = String.format("swipl -s %s -g \"getAllItems(ItemsString), write(ItemsString), nl, halt.\"", PROLOG_FILE);
                } else {
                    // Comando para obter itens por categoria no Windows
                    command = String.format("swipl -s %s -g \"getItemsByCategory('%s', ItemsString), write(ItemsString), nl, halt.\"", PROLOG_FILE, category);
                }
            } else {
                if (category.equals("All")) {
                    // Comando para obter todos os itens em Unix-based
                    command = String.format("sh -c \"swipl -s %s -g \\\"getAllItems(ItemsString), write(ItemsString), nl, halt.\\\"\"", PROLOG_FILE);
                } else {
                    // Comando para obter itens por categoria em Unix-based
                    command = String.format("sh -c \"swipl -s %s -g \\\"getItemsByCategory('%s', ItemsString), write(ItemsString), nl, halt.\\\"\"", PROLOG_FILE, category);
                }
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    
            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }
    
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Adiciona uma nova categoria com desconto padrão de 0
    public static void addCategory(String categoryName) throws IOException {
        List<String> lines = readPrologFile(); // Lê todas as linhas do ficheiro Prolog
        List<String> updatedLines = new ArrayList<>(); // Lista para armazenar as linhas atualizadas
        boolean categoryInserted = false;
        boolean foundDiscountSection = false;
    
        for (String line : lines) {
            if (line.startsWith("% Descontos por categoria de item")) {
                foundDiscountSection = true; // Marca que a seção de descontos foi encontrada
            }
    
            if (foundDiscountSection && !categoryInserted && (line.trim().isEmpty() || line.startsWith("% Desconto de lealdade por ano"))) {
                String categoryData = String.format("discount('%s', 0.0).", categoryName);
                updatedLines.add(categoryData); // Adiciona a nova categoria com desconto padrão de 0
                categoryInserted = true; // Marca que a categoria foi inserida
            }
    
            updatedLines.add(line); // Adiciona a linha atual na lista de linhas atualizadas
        }
    
        if (!categoryInserted) { // Se a categoria não foi inserida na seção correta, adiciona no final
            String categoryData = String.format("discount('%s', 0.0).", categoryName);
            updatedLines.add(categoryData);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Obtém todas as categorias
    public static List<String> getAllCategories() throws IOException {
        List<String> categories = new ArrayList<>();
        List<String> lines = readPrologFile();
        for (String line : lines) {
            if (line.startsWith("discount(")) {
                String category = line.split("'")[1];
                categories.add(category); // Adiciona cada categoria à lista
            }
        }
        return categories;
    }
    
    // Altera o nome de uma categoria existente
    public static void changeCategory(String oldCategory, String newCategory) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("'" + oldCategory + "'")) {
                line = line.replace("'" + oldCategory + "'", "'" + newCategory + "'"); // Substitui o nome da categoria
            }
            updatedLines.add(line);
        }
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Remove uma categoria
    public static void removeCategory(String category) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            if (!line.contains("'" + category + "'")) {
                updatedLines.add(line); // Adiciona todas as linhas que não contêm a categoria a ser removida
            }
        }
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Adiciona um novo item
    public static void addItem(String name, String category, double price, int quantity) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
        int maxId = 0;
    
        for (String line : lines) {
            if (line.startsWith("item(")) {
                int id = Integer.parseInt(line.split(",")[0].substring(5).trim());
                if (id > maxId) {
                    maxId = id; // Atualiza o ID máximo encontrado
                }
            }
            updatedLines.add(line);
        }
    
        int newId = maxId + 1; // Define o novo ID
        String itemData = String.format("item(%d, '%s', '%s', %.2f, %d).", newId, name, category, price, quantity);
        updatedLines.add(itemData); // Adiciona o novo item
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Atualiza um item existente
    public static void updateItem(int id, String newName, String newCategory, double newPrice, int newQuantity) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (line.startsWith("item(") && line.contains("(" + id + ",")) {
                // Atualiza os dados do item
                line = String.format("item(%d, '%s', '%s', %.2f, %d).", id, newName, newCategory, newPrice, newQuantity);
            }
            updatedLines.add(line);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Remove um item
    public static void removeItem(int id) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (!(line.startsWith("item(") && line.contains("(" + id + ","))) {
                updatedLines.add(line); // Adiciona todas as linhas que não correspondem ao item a ser removido
            }
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Guarda um item
    public static void saveItem(Item item) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
        boolean itemInserted = false;
        boolean foundItemsSection = false;

        for (String line : lines) {
            if (line.startsWith("% Item em inventário")) {
                foundItemsSection = true; // Marca que a seção de itens foi encontrada
            }

            if (foundItemsSection && !itemInserted && (line.trim().isEmpty() || line.startsWith("% Descontos por categoria de item"))) {
                String itemData = String.format("item(%d, '%s', '%s', %.2f, %d).",
                        item.getId(), item.getName(), item.getCategory(), item.getPrice(), item.getStockQuantity());
                updatedLines.add(itemData); // Adiciona os dados do item na lista
                itemInserted = true; // Marca que o item foi inserido
            }

            updatedLines.add(line); // Adiciona a linha atual na lista de linhas atualizadas
        }

        if (!itemInserted) { // Se o item não foi inserido na seção correta, adiciona no final
            String itemData = String.format("item(%d, '%s', '%s', %.2f, %d).",
                    item.getId(), item.getName(), item.getCategory(), item.getPrice(), item.getStockQuantity());
            updatedLines.add(itemData);
        }

        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }

    // Ponto de entrada principal
    public static void main(String[] args) {
        List<String> sales = getSalesByDate("20/05/2024");
        for (String sale : sales) {
            System.out.println(sale); // Imprime cada venda obtida pela data
        }
    }

    // Obtém os custos de envio
    public static List<String> getShippingCosts() throws IOException {
        return executePrologQuery("getShippingCosts(CostsString), write(CostsString), nl, halt.");
    }
    
    // Adiciona novo custo de envio
    public static void addShippingCost(String district, double cost) throws IOException {
        List<String> lines = readPrologFile(); // Lê todas as linhas do ficheiro Prolog
        List<String> updatedLines = new ArrayList<>(); // Lista para armazenar as linhas atualizadas
        boolean sectionFound = false;
        boolean inserted = false;
    
        for (String line : lines) {
            if (line.startsWith("% Custos de envio por distrito")) {
                sectionFound = true; // Marca que a seção de custos de envio foi encontrada
            }
    
            if (sectionFound && !inserted && (line.trim().isEmpty() || line.startsWith("% Clientes"))) {
                String shippingCostData = String.format("shipping_cost('%s', %.2f).", district, cost);
                updatedLines.add(shippingCostData); // Adiciona o novo custo de envio na seção correta
                inserted = true; // Marca que o custo de envio foi inserido
            }
    
            updatedLines.add(line); // Adiciona a linha atual na lista de linhas atualizadas
        }
    
        if (!inserted) { // Se o custo de envio não foi inserido na seção correta, adiciona no final
            String shippingCostData = String.format("shipping_cost('%s', %.2f).", district, cost);
            updatedLines.add(shippingCostData);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Altera custo de envio
    public static void changeShippingCost(String district, double cost) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (line.startsWith("shipping_cost(") && line.contains("'" + district + "'")) {
                line = String.format("shipping_cost('%s', %.2f).", district, cost); // Atualiza o custo de envio
            }
            updatedLines.add(line);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Remove custo de envio
    public static void removeShippingCost(String district) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (!line.startsWith("shipping_cost(") || !line.contains("'" + district + "'")) {
                updatedLines.add(line); // Adiciona todas as linhas que não correspondem ao custo de envio a ser removido
            }
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Obtém descontos por categoria
    public static List<String> getCategoryDiscounts() throws IOException {
        return executePrologQuery("getCategoryDiscounts(DiscountsString), write(DiscountsString), nl, halt.");
    }
    
    // Adiciona novo desconto de categoria
    public static void addCategoryDiscount(String category, double discount) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
        boolean sectionFound = false;
        boolean inserted = false;
    
        for (String line : lines) {
            if (line.startsWith("% Descontos por categoria de item")) {
                sectionFound = true; // Marca que a seção de descontos foi encontrada
            }
    
            if (sectionFound && !inserted && (line.trim().isEmpty() || line.startsWith("% Desconto de lealdade por ano"))) {
                String categoryDiscountData = String.format("discount('%s', %.2f).", category, discount);
                updatedLines.add(categoryDiscountData); // Adiciona o novo desconto de categoria na seção correta
                inserted = true; // Marca que o desconto de categoria foi inserido
            }
    
            updatedLines.add(line); // Adiciona a linha atual na lista de linhas atualizadas
        }
    
        if (!inserted) { // Se o desconto de categoria não foi inserido na seção correta, adiciona no final
            String categoryDiscountData = String.format("discount('%s', %.2f).", category, discount);
            updatedLines.add(categoryDiscountData);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Altera desconto de categoria existente
    public static void changeCategoryDiscount(String category, double discount) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (line.startsWith("discount(") && line.contains("'" + category + "'")) {
                line = String.format("discount('%s', %.2f).", category, discount); // Atualiza o desconto de categoria
            }
            updatedLines.add(line);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Remove desconto de categoria
    public static void removeCategoryDiscount(String category) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (!line.startsWith("discount(") || !line.contains("'" + category + "'")) {
                updatedLines.add(line); // Adiciona todas as linhas que não correspondem ao desconto de categoria a ser removido
            }
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Obtém descontos de lealdade
    public static List<String> getLoyaltyDiscounts() throws IOException {
        return executePrologQuery("getLoyaltyDiscounts(DiscountsString), write(DiscountsString), nl, halt.");
    }
    
    // Adiciona novo desconto de lealdade
    public static void addLoyaltyDiscount(int years, double discount) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
        boolean sectionFound = false;
        boolean inserted = false;
    
        for (String line : lines) {
            if (line.startsWith("% Desconto de lealdade por ano")) {
                sectionFound = true; // Marca que a seção de descontos de lealdade foi encontrada
            }
    
            if (sectionFound && !inserted && (line.trim().isEmpty() || line.startsWith("% Custos de envio por distrito"))) {
                String loyaltyDiscountData = String.format("loyalty_discount(%d, %.2f).", years, discount);
                updatedLines.add(loyaltyDiscountData); // Adiciona o novo desconto de lealdade na seção correta
                inserted = true; // Marca que o desconto de lealdade foi inserido
            }
    
            updatedLines.add(line); // Adiciona a linha atual na lista de linhas atualizadas
        }
    
        if (!inserted) { // Se o desconto de lealdade não foi inserido na seção correta, adiciona no final
            String loyaltyDiscountData = String.format("loyalty_discount(%d, %.2f).", years, discount);
            updatedLines.add(loyaltyDiscountData);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Altera desconto de lealdade
    public static void changeLoyaltyDiscount(int years, double discount) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (line.startsWith("loyalty_discount(") && line.contains("(" + years + ",")) {
                line = String.format("loyalty_discount(%d, %.2f).", years, discount); // Atualiza o desconto de lealdade
            }
            updatedLines.add(line);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Remove desconto de lealdade
    public static void removeLoyaltyDiscount(int years) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (!line.startsWith("loyalty_discount(") || !line.contains("(" + years + ",")) {
                updatedLines.add(line); // Adiciona todas as linhas que não correspondem ao desconto de lealdade a ser removido
            }
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Executa consulta Prolog
    private static List<String> executePrologQuery(String query) throws IOException {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                // Comando para Windows
                command = String.format("swipl -s %s -g \"%s\"", PROLOG_FILE, query);
            } else {
                // Comando para Unix-based
                command = String.format("sh -c \"swipl -s %s -g \\\"%s\\\"\"", PROLOG_FILE, query);
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error executing Prolog query", e);
        }
        return results;
    }

    // Obtém todos os clientes
    public static List<String> getAllCustomers() {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                // Comando para Windows
                command = String.format("swipl -s %s -g \"getAllCustomers(CustomersString), write(CustomersString), nl, halt.\"", PROLOG_FILE);
            } else {
                // Comando para Unix-based
                command = String.format("sh -c \"swipl -s %s -g \\\"getAllCustomers(CustomersString), write(CustomersString), nl, halt.\\\"\"", PROLOG_FILE);
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Obtém todos os distritos com Query Prolog
    public static List<String> getAllDistricts() throws IOException {
        return executePrologQuery("getAllDistricts(DistrictsString), write(DistrictsString), nl, halt.");
    }
    
    // Obtém clientes por distrito
    public static List<String> getCustomersByDistrict(String district) {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                // Comando para Windows
                command = String.format("swipl -s %s -g \"getCustomersByDistrict('%s', CustomersString), write(CustomersString), nl, halt.\"", PROLOG_FILE, district);
            } else {
                // Comando para Unix-based
                command = String.format("sh -c \"swipl -s %s -g \\\"getCustomersByDistrict('%s', CustomersString), write(CustomersString), nl, halt.\\\"\"", PROLOG_FILE, district);
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Obtém todos os anos de lealdade
    public static List<Integer> getAllLoyaltyYears() throws IOException {
        List<Integer> years = new ArrayList<>();
        List<String> results = executePrologQuery("getAllLoyaltyYears(YearsString), write(YearsString), nl, halt.");
        for (String result : results) {
            try {
                years.add(Integer.parseInt(result)); // Converte cada resultado em um inteiro e adiciona à lista
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return years;
    }
    
    // Obtém clientes por lealdade
    public static List<String> getCustomersByLoyalty(int years) {
        List<String> results = new ArrayList<>();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;
            if (os.contains("win")) {
                // Comando para Windows
                command = String.format("swipl -s %s -g \"getCustomersByLoyalty(%d, CustomersString), write(CustomersString), nl, halt.\"", PROLOG_FILE, years);
            } else {
                // Comando para Unix-based
                command = String.format("sh -c \"swipl -s %s -g \\\"getCustomersByLoyalty(%d, CustomersString), write(CustomersString), nl, halt.\\\"\"", PROLOG_FILE, years);
            }
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                results.add(line); // Adiciona cada linha de resultado à lista
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
    
    // Altera dados de um cliente
    public static void changeCustomer(Customer customer) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (line.startsWith("customer(") && line.contains("(" + customer.getId() + ",")) {
                // Atualiza os dados do cliente
                line = String.format("customer(%d, '%s', '%s', %d).",
                        customer.getId(), customer.getName(), customer.getDistrict(), customer.getYearsOfLoyalty());
            }
            updatedLines.add(line);
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
    
    // Remove um cliente
    public static void removeCustomer(int id) throws IOException {
        List<String> lines = readPrologFile();
        List<String> updatedLines = new ArrayList<>();
    
        for (String line : lines) {
            if (!line.startsWith("customer(") || !line.contains("(" + id + ",")) {
                updatedLines.add(line); // Adiciona todas as linhas que não correspondem ao cliente a ser removido
            }
        }
    
        // Escreve as linhas atualizadas de volta ao ficheiro Prolog
        try (FileWriter fw = new FileWriter(PROLOG_FILE)) {
            for (String updatedLine : updatedLines) {
                fw.write(updatedLine + "\n");
            }
        }
    }
}
