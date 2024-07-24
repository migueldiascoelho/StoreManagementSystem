import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// A classe loja é onde está a lógica do interface
// É a classe principal e interage com todas as outras classes
public class Store {
    private static List<Customer> customers = new ArrayList<>();
    private static Cart cart = new Cart();
    private static JLabel cartStatusLabel = new JLabel("Cart: 0 items");

    // A função main que dá início ao programa
    public static void main(String[] args) {
        loadCustomersFromProlog();
        loadItemsFromProlog();
        showMainMenu();
    }

    private static void showMainMenu() {
        JFrame frame = new JFrame("Enchanted Emporium Main Menu"); // Frame do início do programa
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        // Diferentes butões do main menu
        JButton btnComprar = new JButton("Purchase");
        JButton btnHistorico = new JButton("Sales History");
        JButton btnGestaoInventario = new JButton("Inventory Management");
        JButton btnCustosDescontos = new JButton("Costs and Discounts");
        JButton btnClientes = new JButton("Customer Data");

        // Define a posição e o tamanho dos botões
        btnComprar.setBounds(50, 40, 200, 30);
        btnHistorico.setBounds(50, 80, 200, 30);
        btnGestaoInventario.setBounds(50, 120, 200, 30);
        btnCustosDescontos.setBounds(50, 160, 200, 30);
        btnClientes.setBounds(50, 200, 200, 30);

        // Atribui funcionalidade a cada butão 
        btnComprar.addActionListener(e -> showBuyMenu(frame));
        btnHistorico.addActionListener(e -> showSalesHistoryMenu(frame));
        btnGestaoInventario.addActionListener(e -> showInventoryManagementMenu(frame));
        btnCustosDescontos.addActionListener(e -> showCostsAndDiscountsMenu(frame));
        btnClientes.addActionListener(e -> showCustomerDataMenu(frame));

        frame.setLayout(null);
        frame.add(btnComprar);
        frame.add(btnHistorico);
        frame.add(btnGestaoInventario);
        frame.add(btnCustosDescontos);
        frame.add(btnClientes);
        frame.setVisible(true);
    }

    private static void showInventoryManagementMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Inventory Management"); // Cria um novo frame para a gestão de inventário
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Define a ação de fechar o frame
        frame.setSize(300, 250);
    
        JButton btnViewItems = new JButton("View Items");
        JButton btnManageCategories = new JButton("Manage Categories");
        JButton btnManageItems = new JButton("Manage Items");
        JButton btnBack = new JButton("Back");
    
        btnViewItems.setBounds(50, 40, 200, 30);
        btnManageCategories.setBounds(50, 80, 200, 30);
        btnManageItems.setBounds(50, 120, 200, 30);
        btnBack.setBounds(50, 160, 200, 30);
    
        btnViewItems.addActionListener(e -> showViewItemsMenu(frame));
        btnManageCategories.addActionListener(e -> showManageCategoriesMenu(frame));
        btnManageItems.addActionListener(e -> showManageItemsMenu(frame));
        btnBack.addActionListener(e -> {
            frame.dispose();
            showMainMenu();
        });
    
        frame.setLayout(null);
        frame.add(btnViewItems);
        frame.add(btnManageCategories);
        frame.add(btnManageItems);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    private static void showViewItemsMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("View Items");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
    
        JButton btnViewAllItems = new JButton("View All Items");
        btnViewAllItems.setBounds(50, 40, 200, 30);
    
        List<String> categories; // Lista para armazenar as categorias
        try {
            categories = PrologUtils.getAllCategories(); // Obtém todas as categorias de itens do Prolog
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading categories: " + ex.getMessage());
            return;
        }
    
        frame.setLayout(null);
        frame.add(btnViewAllItems);
    
        btnViewAllItems.addActionListener(e -> showItemsByCategory(frame, "All"));
    
        int y = 80; // Posição inicial para os botões de categorias
        for (String category : categories) {
            JButton btnCategory = new JButton(category);
            btnCategory.setBounds(50, y, 200, 30);
            frame.add(btnCategory);
            btnCategory.addActionListener(e -> showItemsByCategory(frame, category));
            y += 40; // Incrementa a posição y para o próximo botão
        }
    
        // Cria um botão para voltar ao menu de gestão de inventário
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, y, 200, 30);
        frame.add(btnBack);
    
        btnBack.addActionListener(e -> {
            frame.dispose();
            showInventoryManagementMenu(new JFrame());
        });
    
        frame.setVisible(true);
    }
    
    
    // Mostra os itens por categoria
    private static void showItemsByCategory(JFrame parentFrame, String category) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Items: " + category);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);

        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");

        // Obtém a lista de itens da categoria especificada utilizando a função de PrologUtils
        List<String> items = PrologUtils.getItemsByCategory(category); 

        StringBuilder resultText = new StringBuilder();
        for (String item : items) {                     // Para cada item na lista de itens
            resultText.append(item).append("\n");   // Adiciona o item ao texto de resultados
        }
        if (items.isEmpty()) {
            resultText.append("No items found in this category.");
        }
        txtResult.setText(resultText.toString());

        btnBack.addActionListener(e -> {
            frame.dispose();
            showViewItemsMenu(new JFrame());
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Menu da compra
    private static void showBuyMenu(JFrame parentFrame) { 
        parentFrame.dispose();
        JFrame frame = new JFrame("Comprar");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 250);

        JButton btnNewCustomer = new JButton("New Customer");
        JButton btnExistingCustomer = new JButton("Existing Customer");
        JButton btnBack = new JButton("Back");
        btnNewCustomer.setBounds(50, 40, 200, 30);
        btnExistingCustomer.setBounds(50, 80, 200, 30);
        btnBack.setBounds(50, 120, 200, 30);

        btnNewCustomer.addActionListener(e -> createNewCustomerForm(frame));
        btnExistingCustomer.addActionListener(e -> showSearchCustomerForm(frame, false)); // Diferencia entre outra funcionalidade
        btnBack.addActionListener(e -> {
            frame.dispose();
            showMainMenu();
        });

        frame.setLayout(null);
        frame.add(btnNewCustomer);
        frame.add(btnExistingCustomer);
        frame.add(btnBack);
        frame.setVisible(true);
    }

    private static void showManageCategoriesMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Manage Categories");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
    
        JButton btnAddCategory = new JButton("Add Category");
        JButton btnChangeCategory = new JButton("Change Category");
        JButton btnRemoveCategory = new JButton("Remove Category");
        JButton btnBack = new JButton("Back");
    
        btnAddCategory.setBounds(50, 40, 200, 30);
        btnChangeCategory.setBounds(50, 80, 200, 30);
        btnRemoveCategory.setBounds(50, 120, 200, 30);
        btnBack.setBounds(50, 160, 200, 30);
    
        btnAddCategory.addActionListener(e -> showAddCategoryMenu(frame));
        btnChangeCategory.addActionListener(e -> showChangeCategoryMenu(frame));
        btnRemoveCategory.addActionListener(e -> showRemoveCategoryMenu(frame));
        btnBack.addActionListener(e -> {
            frame.dispose();
            showInventoryManagementMenu(new JFrame());
        });
    
        frame.setLayout(null);
        frame.add(btnAddCategory);
        frame.add(btnChangeCategory);
        frame.add(btnRemoveCategory);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Mostra o menu de gestão de categorias
    private static void showAddCategoryMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Add Category");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(null);
    
        JLabel lblName = new JLabel("Category Name:");
        JTextField txtName = new JTextField();
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");
    
        lblName.setBounds(10, 30, 100, 25);
        txtName.setBounds(120, 30, 150, 25);
        btnSave.setBounds(50, 70, 200, 30);
        btnBack.setBounds(50, 110, 200, 30);
    
        btnSave.addActionListener(e -> {
            String categoryName = txtName.getText().trim();
            try {
                PrologUtils.addCategory(categoryName);
                JOptionPane.showMessageDialog(frame, "Category added successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error adding category: " + ex.getMessage());
            }
        });
    
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageCategoriesMenu(new JFrame());
        });
    
        frame.add(lblName);
        frame.add(txtName);
        frame.add(btnSave);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Mostra o menu de alteração de categorias
    private static void showChangeCategoryMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Alterar Categoria");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);

        List<String> categories;
        try {
            // Obtém todas as categorias utilizando a função de PrologUtils
            categories = PrologUtils.getAllCategories();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Erro ao carregar categorias: " + ex.getMessage());
            return;
        }

        // Cada categoria é representada por um botão
        int y = 40;
        for (String category : categories) {
            JButton btnCategory = new JButton(category);
            btnCategory.setBounds(50, y, 200, 30);
            frame.add(btnCategory);
            // Adiciona ação ao botão para mostrar o menu de renomear categoria
            btnCategory.addActionListener(e -> showRenameCategoryMenu(frame, category));
            y += 40;
        }

        JButton btnBack = new JButton("Voltar");
        btnBack.setBounds(50, y, 200, 30);
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageCategoriesMenu(new JFrame());
        });
        frame.add(btnBack);

        frame.setVisible(true);
    }

    
    // Mostra o menu para remover categorias
    private static void showRemoveCategoryMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Remover Categoria");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);

        List<String> categories;
        try {
            // Obtém todas as categorias utilizando a função de PrologUtils
            categories = PrologUtils.getAllCategories();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Erro ao carregar categorias: " + ex.getMessage());
            return;
        }

        int y = 40;
        // Cada categoria é representada por um botão
        for (String category : categories) {
            JButton btnCategory = new JButton(category);
            btnCategory.setBounds(50, y, 200, 30);
            frame.add(btnCategory);
            // Adiciona ação ao botão para remover a categoria
            btnCategory.addActionListener(e -> {
                try {
                    PrologUtils.removeCategory(category);
                    JOptionPane.showMessageDialog(frame, "Categoria removida com sucesso!");
                    frame.dispose();
                    showRemoveCategoryMenu(new JFrame()); // Atualiza o GUI
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Erro ao remover categoria: " + ex.getMessage());
                }
            });
            y += 40;
        }

        JButton btnBack = new JButton("Voltar");
        btnBack.setBounds(50, y, 200, 30);
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageCategoriesMenu(new JFrame());
        });
        frame.add(btnBack);

        frame.setVisible(true);
    }

        
    // Mostra o menu para mudar o nome de uma categoria
    private static void showRenameCategoryMenu(JFrame parentFrame, String oldCategory) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Rename Category: " + oldCategory);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(null);

        JLabel lblNewName = new JLabel("New Name:");
        JTextField txtNewName = new JTextField();
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");

        lblNewName.setBounds(10, 30, 100, 25);
        txtNewName.setBounds(120, 30, 150, 25);
        btnSave.setBounds(50, 70, 200, 30);
        btnBack.setBounds(50, 110, 200, 30);

        // Ação ao clicar no botão Save
        btnSave.addActionListener(e -> {
            String newCategoryName = txtNewName.getText().trim();
            try {
                PrologUtils.changeCategory(oldCategory, newCategoryName); // Renomeia a categoria
                JOptionPane.showMessageDialog(frame, "Category renamed successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error renaming category: " + ex.getMessage());
            }
        });

        // Ação ao clicar no botão Back
        btnBack.addActionListener(e -> {
            frame.dispose();
            showChangeCategoryMenu(new JFrame());
        });

        frame.add(lblNewName);
        frame.add(txtNewName);
        frame.add(btnSave);
        frame.add(btnBack);
        frame.setVisible(true);
    }

    // Mostra o menu de histórico de vendas
    private static void showSalesHistoryMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Sales History");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);

        JButton btnSalesByDate = new JButton("Sales by Date");
        JButton btnSalesByClient = new JButton("Sales by Client");
        JButton btnSalesByDistrict = new JButton("Sales by District");
        JButton btnBack = new JButton("Back");
        btnSalesByDate.setBounds(50, 40, 200, 30);
        btnSalesByClient.setBounds(50, 80, 200, 30);
        btnSalesByDistrict.setBounds(50, 120, 200, 30);
        btnBack.setBounds(50, 160, 200, 30);

        // Ação ao clicar no botão Sales by Date
        btnSalesByDate.addActionListener(e -> showSalesByDateMenu(frame));
        // Ação ao clicar no botão Sales by Client
        btnSalesByClient.addActionListener(e -> showSearchCustomerForm(frame, true));
        // Ação ao clicar no botão Sales by District
        btnSalesByDistrict.addActionListener(e -> showSalesByDistrictMenu(frame));
        // Ação ao clicar no botão Back
        btnBack.addActionListener(e -> {
            frame.dispose();
            showMainMenu();
        });

        frame.setLayout(null);
        frame.add(btnSalesByDate);
        frame.add(btnSalesByClient);
        frame.add(btnSalesByDistrict);
        frame.add(btnBack);
        frame.setVisible(true);
    }


    // Mostra o menu de vendas por distrito
    private static void showSalesByDistrictMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Sales by District");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);

        JButton btnSearchDistrict = new JButton("Search District");
        JButton btnDistrictsByDiscounts = new JButton("Districts by Discounts");
        JButton btnBack = new JButton("Back");

        btnSearchDistrict.setBounds(50, 40, 200, 30);
        btnDistrictsByDiscounts.setBounds(50, 80, 200, 30);
        btnBack.setBounds(50, 120, 200, 30);

        // Ação ao clicar no botão Search District
        btnSearchDistrict.addActionListener(e -> showSearchDistrictMenu(frame));
        // Ação ao clicar no botão Districts by Discounts
        btnDistrictsByDiscounts.addActionListener(e -> showDistrictsByDiscountsMenu(frame));
        // Ação ao clicar no botão Back
        btnBack.addActionListener(e -> {
            frame.dispose();
            showSalesHistoryMenu(new JFrame());
        });

        frame.setLayout(null);
        frame.add(btnSearchDistrict);
        frame.add(btnDistrictsByDiscounts);
        frame.add(btnBack);
        frame.setVisible(true);
    }

    // Mostra o menu de vendas por data
    private static void showSalesByDateMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Sales by Date");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);

        JLabel lblDate = new JLabel("Enter Date (dd/MM/yyyy):");
        JTextField txtDate = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");

        // Ação ao clicar no botão Search
        btnSearch.addActionListener(e -> {
            String date = txtDate.getText().trim();
            List<String> sales = PrologUtils.getSalesByDate(date);
            if (sales.size() > 5) {
                StringBuilder resultText = new StringBuilder();
                for (int i = 0; i < sales.size() - 5; i++) {
                    resultText.append(sales.get(i)).append("\n");
                }

                resultText.append("\nTotals:\n");
                resultText.append(sales.get(sales.size() - 5)).append("\n");
                resultText.append(sales.get(sales.size() - 4)).append("\n");
                resultText.append(sales.get(sales.size() - 3)).append("\n");
                resultText.append(sales.get(sales.size() - 2)).append("\n");
                resultText.append(sales.get(sales.size() - 1)).append("\n");

                txtResult.setText(resultText.toString());
            } else {
                txtResult.setText("No sales found for the given date.");
            }
        });

        // Ação ao clicar no botão Back
        btnBack.addActionListener(e -> {
            frame.dispose();
            showSalesHistoryMenu(new JFrame());
        });

        frame.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(lblDate);
        topPanel.add(txtDate);
        topPanel.add(btnSearch);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Mostra o menu de vendas por cliente
    private static void showSalesByClientMenu(int clientId) {
        JFrame frame = new JFrame("Sales by Client");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);

        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");

        // Obtém a lista de vendas do cliente especificado utilizando a função de PrologUtils
        List<String> sales = PrologUtils.getSalesByClient(clientId);
        if (!sales.isEmpty()) {
            txtResult.setText(String.join("\n", sales));
        } else {
            txtResult.setText("No sales found for the given client.");
        }

        // Ação ao clicar no botão Back
        btnBack.addActionListener(e -> {
            frame.dispose();
            showSalesHistoryMenu(new JFrame());
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Mostra o menu de pesquisa por distrito
    private static void showSearchDistrictMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame searchFrame = new JFrame("Search District");
        searchFrame.setSize(350, 200);
        searchFrame.setLayout(new FlowLayout());
        searchFrame.setLocationRelativeTo(parentFrame);

        JTextField searchText = new JTextField(20);
        JButton searchButton = new JButton("Search");

        // Ação ao clicar no botão Search
        searchButton.addActionListener(e -> {
            searchFrame.getContentPane().removeAll();
            searchFrame.getContentPane().add(searchText);
            searchFrame.getContentPane().add(searchButton);
            String district = searchText.getText().toLowerCase();
            // Filtra a lista de clientes para encontrar os que pertencem ao distrito especificado
            List<Customer> filteredCustomers = customers.stream()
                    .filter(c -> c.getDistrict().toLowerCase().contains(district))
                    .collect(Collectors.toList());
            if (!filteredCustomers.isEmpty()) {
                List<String> districts = filteredCustomers.stream()
                        .map(Customer::getDistrict)
                        .distinct()
                        .collect(Collectors.toList());
                // Para cada distrito, cria um botão e adiciona ao content pane
                for (String dist : districts) {
                    JButton btnDistrict = new JButton(dist);
                    btnDistrict.addActionListener(e2 -> showSalesByDistrict(dist));
                    searchFrame.getContentPane().add(btnDistrict);
                }
            } else {
                JLabel noResults = new JLabel("District not found.");
                searchFrame.getContentPane().add(noResults);
            }
            // Atualiza a interface gráfica para refletir as mudanças
            searchFrame.revalidate();
            searchFrame.repaint();
        });

        searchFrame.getContentPane().add(searchText);
        searchFrame.getContentPane().add(searchButton);
        searchFrame.setVisible(true);
    }

    // Mostra o menu de vendas por um distrito específico
    private static void showSalesByDistrict(String district) {
        JFrame frame = new JFrame("Sales by District: " + district);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);

        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");

        // Obtém a lista de vendas do distrito especificado utilizando a função de PrologUtils
        List<String> sales = PrologUtils.getSalesByDistrict(district);
        if (!sales.isEmpty()) {
            txtResult.setText(String.join("\n", sales));
        } else {
            txtResult.setText("No sales found for the given district.");
        }

        // Ação ao clicar no botão Back
        btnBack.addActionListener(e -> {
            frame.dispose();
            showSalesByDistrictMenu(new JFrame());
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);

        frame.setVisible(true);
    }


    // Mostra o menu de distritos por descontos
    private static void showDistrictsByDiscountsMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Districts by Discounts");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);

        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");

        // Obtém a lista de descontos por distrito utilizando a função de PrologUtils
        List<String> discounts = PrologUtils.getDistrictsByDiscounts();
        if (!discounts.isEmpty()) {
            // Se a lista não estiver vazia, exibe os descontos no campo de texto
            txtResult.setText(String.join("\n", discounts));
        } else {
            // Caso contrário, exibe uma mensagem a indicar que não foram encontrados descontos
            txtResult.setText("No discounts found.");
        }

        btnBack.addActionListener(e -> {
            frame.dispose();
            showSalesByDistrictMenu(new JFrame());
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Carrega os clientes a partir de um ficheiro Prolog
    private static void loadCustomersFromProlog() {
        try {
            // Lê as linhas do ficheiro Prolog
            List<String> lines = PrologUtils.readPrologFile();
            for (String line : lines) {
                // Para cada linha que começa com "customer(", chama a função parseCustomer
                if (line.startsWith("customer(")) {
                    parseCustomer(line);
                }
            }
        } catch (IOException ex) {
            System.err.println("Error reading from Prolog file: " + ex.getMessage());
        }
    }

    // Carrega os itens a partir de um ficheiro Prolog
    private static void loadItemsFromProlog() {
        try {
            // Lê as linhas do ficheiro Prolog
            List<String> lines = PrologUtils.readPrologFile();
            for (String line : lines) {
                // Para cada linha que começa com "item(", chama a função parseItem
                if (line.startsWith("item(")) {
                    parseItem(line);
                }
            }
        } catch (IOException ex) {
            System.err.println("Error reading from Prolog file: " + ex.getMessage());
        }
    }

    // Analisa uma linha de cliente do ficheiro Prolog e cria um objeto Customer
    private static void parseCustomer(String line) {
        try {
            // Divide a linha em partes
            String[] parts = line.substring(9, line.length() - 2).split(",");
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].replace("'", "").trim();
            String district = parts[2].replace("'", "").trim();
            int years = Integer.parseInt(parts[3].trim());
            // Adiciona o cliente à lista de clientes
            customers.add(new Customer(id, name, "", district, years));
        } catch (Exception e) {
            System.err.println("Error parsing customer: " + line);
            e.printStackTrace();
        }
    }

    // Analisa uma linha de item do ficheiro Prolog e cria um objeto Item
    private static void parseItem(String line) {
        try {
            // Divide a linha em partes
            String[] parts = line.substring(5, line.length() - 2).split(",");
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].replace("'", "").trim();
            String category = parts[2].replace("'", "").trim();
            double price = Double.parseDouble(parts[3].trim());
            int quantity = Integer.parseInt(parts[4].trim());
            // Adiciona o item ao carrinho
            cart.addItem(new Item(id, name, category, price, quantity));
        } catch (Exception e) {
            System.err.println("Error parsing item: " + line);
            e.printStackTrace();
        }
    }

    private static void showItems(Purchase purchase) {
        JFrame itemsFrame = new JFrame("Available Products");
        itemsFrame.setSize(600, 400);
        itemsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Layout em grelha para mostrar os produtos
        JScrollPane scrollPane = new JScrollPane(mainPanel); // Scroll
        itemsFrame.add(scrollPane, BorderLayout.CENTER);
    
        // Mostra o nome do cliente que está a comprar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel customerLabel = new JLabel("Customer: " + purchase.getCustomer().getName());
        bottomPanel.add(customerLabel);
    
        // Obtém o número de itens no carrinho para poder mostrar no UI
        JLabel cartStatusLabel = new JLabel("Cart: " + purchase.getCart().getItemCount() + " items");
        bottomPanel.add(cartStatusLabel);
    
        // Botão de checkout
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> {
            if (purchase.getCart().getItems().isEmpty()) {
                JOptionPane.showMessageDialog(itemsFrame, "Cart is empty.", "Empty Cart", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Tenta calcular o preço final e mostrar o recibo
                try {
                    Calculos.CalculationResult result = Calculos.calculateFinalPrice(purchase);
                    itemsFrame.getContentPane().removeAll(); // Remove o conteúdo anterior da frame
                    itemsFrame.setLayout(new BorderLayout(10, 10));
                    JTextArea receiptArea = new JTextArea();
                    receiptArea.setEditable(false);
                    receiptArea.setFont(new Font("SansSerif", Font.BOLD, 18));
                    receiptArea.setMargin(new Insets(10, 20, 10, 20));
    
                    // Monta o texto do recibo
                    StringBuilder receipt = new StringBuilder();
                    receipt.append("Customer ID: ").append(result.getCustomerId()).append("\n");
                    receipt.append("Customer Name: ").append(result.getCustomerName()).append("\n\n");
                    receipt.append("Cart Items:\n");
                    for (Item item : result.getCartItems()) {
                        receipt.append("- ").append(item.getName())
                                .append(" (").append(item.getCategory()).append("), Qty: ")
                                .append(purchase.getCart().getQuantityInCart(item.getId()))
                                .append(", Price: ").append(String.format("%.2f€", item.getPrice() * purchase.getCart().getQuantityInCart(item.getId())))
                                .append("\n");
                    }
                    receipt.append("\n");
                    receipt.append("Total Price Before Discount: ").append(String.format("%.2f€", result.getTotalPriceBeforeDiscount())).append("\n");
                    receipt.append("Category Discount: -").append(String.format("%.2f€", result.getTotalCategoryDiscount())).append("\n");
                    receipt.append("Loyalty Discount: -").append(String.format("%.2f€", result.getLoyaltyDiscountAmount())).append("\n");
                    receipt.append("Shipping Cost (").append(purchase.getCustomer().getDistrict()).append("): +").append(String.format("%.2f€", result.getShippingCost())).append("\n");
                    receipt.append("\n");
                    receipt.append("Final Cart Price: ").append(String.format("%.2f€", result.getFinalPrice()));
    
                    receiptArea.setText(receipt.toString()); // Define o texto do recibo
                    itemsFrame.add(receiptArea, BorderLayout.CENTER);
    
                    // Botão de pagamento que termina o programa
                    JButton payButton = new JButton("PAY (Exits Program)");
                    payButton.addActionListener(ev -> {
                        try {
                            PrologUtils.updateStockAndPurchaseHistory(result, purchase.getCart());
                            System.exit(0); // Sai do programa
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(itemsFrame, "Failed to update stock and purchase history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    });
                    itemsFrame.add(payButton, BorderLayout.SOUTH);
    
                    itemsFrame.revalidate();
                    itemsFrame.repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(itemsFrame, "Failed to calculate final price: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    
        bottomPanel.add(checkoutButton);
        itemsFrame.add(bottomPanel, BorderLayout.SOUTH);
        Border blackline = new LineBorder(Color.BLACK);
    
        // Loop pelos itens disponíveis (neste caso o carrinho serve como inventário)
        for (Item item : cart.getItems()) {
            JPanel itemPanel = new JPanel();
            itemPanel.setBorder(blackline);
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
    
            JLabel idLabel = new JLabel("ID: " + item.getId()); // ID do produto
            JLabel nameLabel = new JLabel(item.getName()); // Nome do produto
            JLabel priceLabel = new JLabel(String.format("Price: %.2f€", item.getPrice())); // Preço
            JLabel quantityLabel = new JLabel("Stock Qty: " + item.getStockQuantity()); // Mostra quantidade em stock
            JButton addButton = new JButton("ADD"); // Botão para adicionar ao carrinho da compra
            addButton.addActionListener(ev -> {
                item.decrementStockQuantity();
                purchase.getCart().addItem(item);
                cartStatusLabel.setText("Cart: " + purchase.getCart().getItemCount() + " items"); // Atualiza o número de itens no carrinho
                quantityLabel.setText("Stock Qty: " + item.getStockQuantity()); // Atualiza a quantidade em stock
                itemsFrame.repaint();
            });
    
            itemPanel.add(idLabel);
            itemPanel.add(nameLabel);
            itemPanel.add(priceLabel);
            itemPanel.add(quantityLabel);
            itemPanel.add(addButton);
            mainPanel.add(itemPanel);
        }
    
        itemsFrame.setVisible(true);
    }
    
    // Update no número de itens no carrinho
    public static void updateCartStatus() {
        cartStatusLabel.setText("Cart: " + cart.getItemCount() + " items");
    }

    // Método que trata da janela de pesquisa de registo de cliente (para compras)
    private static void showSearchCustomerForm(JFrame parent, boolean isSalesByClient) {
        JFrame searchFrame = new JFrame("Search Customer by Name");
        searchFrame.setSize(350, 200);
        searchFrame.setLayout(new FlowLayout());
        searchFrame.setLocationRelativeTo(parent);

        JTextField searchText = new JTextField(20);
        JButton searchButton = new JButton("Search"); // Botão de busca
        // Ao clicar no botão de busca encontra correspondências nos registos
        searchButton.addActionListener(e -> {
            searchFrame.getContentPane().removeAll();
            searchFrame.getContentPane().add(searchText);
            searchFrame.getContentPane().add(searchButton);
            String name = searchText.getText().toLowerCase();
            List<Customer> filteredCustomers = customers.stream()
                    .filter(c -> c.getName().toLowerCase().contains(name))
                    .collect(Collectors.toList());
            if (!filteredCustomers.isEmpty()) {
                // Se houver correspondências são criados botões de cliente
                // Ao clicar num desses botões o utilizador passa para o ecrã da compra
                for (Customer customer : filteredCustomers) {
                    JButton btnCustomer = new JButton(customer.getName() + ", ID: " + customer.getId()
                            + ", District: " + customer.getDistrict() + ", City: " + customer.getCity());
                    btnCustomer.addActionListener(e2 -> {
                        if (isSalesByClient) {
                            showSalesByClientMenu(customer.getId());
                        } else {
                            Purchase purchase = new Purchase(customer, new Cart());
                            showItems(purchase);
                        }
                        searchFrame.dispose();
                    });
                    searchFrame.getContentPane().add(btnCustomer);
                }
            } else {
                JLabel noResults = new JLabel("Customer not found.");
                searchFrame.getContentPane().add(noResults);
            }
            searchFrame.revalidate();
            searchFrame.repaint();
        });

        searchFrame.getContentPane().add(searchText);
        searchFrame.getContentPane().add(searchButton);
        searchFrame.setVisible(true);
    }

    // Formulário para criar registo de Customer
    private static void createNewCustomerForm(JFrame parent) {
        JFrame inputFrame = new JFrame("Enter New Customer Details");
        inputFrame.setSize(350, 250);
        inputFrame.setLayout(null);
        inputFrame.setLocationRelativeTo(parent);

        JTextField textId = new JTextField();
        JTextField textName = new JTextField();
        JTextField textCity = new JTextField();
        JTextField textDistrict = new JTextField();
        JTextField textYears = new JTextField();

        addComponent(inputFrame, "ID:", textId, 10);
        addComponent(inputFrame, "Name:", textName, 40);
        addComponent(inputFrame, "City:", textCity, 70);
        addComponent(inputFrame, "District:", textDistrict, 100);
        addComponent(inputFrame, "Years of Loyalty:", textYears, 130);

        JButton btnSave = new JButton("Save");
        btnSave.setBounds(120, 160, 200, 30);
        btnSave.addActionListener(e -> {
            try {
                int id = Integer.parseInt(textId.getText());
                String name = textName.getText();
                String city = textCity.getText();
                String district = textDistrict.getText();
                int years = Integer.parseInt(textYears.getText());
                Customer customer = new Customer(id, name, city, district, years);
                PrologUtils.saveCustomer(customer); // Guarda registo no ficheiro Prolog
                Purchase purchase = new Purchase(customer, new Cart());
                JOptionPane.showMessageDialog(inputFrame, "Customer saved successfully!");
                showItems(purchase);
                inputFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Invalid input. Please enter valid numbers for ID and Years.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(inputFrame, "Error saving customer: " + ex.getMessage());
            }
        });

        inputFrame.add(btnSave);
        inputFrame.setVisible(true);
    }

    // Simplifica a adição de componentes ao JFrame
    private static void addComponent(JFrame frame, String label, JTextField textField, int y) {
        JLabel jLabel = new JLabel(label);
        jLabel.setBounds(10, y, 100, 25);
        textField.setBounds(120, y, 200, 25);
        frame.add(jLabel);
        frame.add(textField);
    }

    // Mostra o sub-menu de gestão de itens
    private static void showManageItemsMenu(JFrame parentFrame) {
        parentFrame.dispose(); // Fecha o frame anterior
        JFrame frame = new JFrame("Manage Items"); // Cria um novo frame
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
    
        JButton btnAddItem = new JButton("Add Item");
        JButton btnChangeItem = new JButton("Change Item");
        JButton btnRemoveItem = new JButton("Remove Item");
        JButton btnBack = new JButton("Back");
    
        // Define as posições dos botões
        btnAddItem.setBounds(50, 40, 200, 30);
        btnChangeItem.setBounds(50, 80, 200, 30);
        btnRemoveItem.setBounds(50, 120, 200, 30);
        btnBack.setBounds(50, 160, 200, 30);
    
        // Ações dos botões
        btnAddItem.addActionListener(e -> showAddItemMenu(frame));
        btnChangeItem.addActionListener(e -> showChangeItemMenu(frame));
        btnRemoveItem.addActionListener(e -> showRemoveItemMenu(frame));
        btnBack.addActionListener(e -> {
            frame.dispose();
            showInventoryManagementMenu(new JFrame());
        });
    
        frame.setLayout(null); // Usa layout nulo para posicionamento absoluto dos botões
        frame.add(btnAddItem);
        frame.add(btnChangeItem);
        frame.add(btnRemoveItem);
        frame.add(btnBack);
        frame.setVisible(true); // Torna o frame visível
    }
    
    private static void showAddItemMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Add Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);
    
        JLabel lblName = new JLabel("Item Name:");
        JTextField txtName = new JTextField();
        JLabel lblCategory = new JLabel("Category:");
        JComboBox<String> cmbCategory = new JComboBox<>();
        JLabel lblPrice = new JLabel("Price:");
        JTextField txtPrice = new JTextField();
        JLabel lblQuantity = new JLabel("Quantity:");
        JTextField txtQuantity = new JTextField();
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");
    
        // Define as posições dos componentes
        lblName.setBounds(10, 30, 100, 25);
        txtName.setBounds(120, 30, 150, 25);
        lblCategory.setBounds(10, 70, 100, 25);
        cmbCategory.setBounds(120, 70, 150, 25);
        lblPrice.setBounds(10, 110, 100, 25);
        txtPrice.setBounds(120, 110, 150, 25);
        lblQuantity.setBounds(10, 150, 100, 25);
        txtQuantity.setBounds(120, 150, 150, 25);
        btnSave.setBounds(50, 190, 200, 30);
        btnBack.setBounds(50, 230, 200, 30);
    
        // Carrega todas as categorias disponíveis para o JComboBox
        List<String> categories;
        try {
            categories = PrologUtils.getAllCategories();
            for (String category : categories) {
                cmbCategory.addItem(category); // Adiciona cada categoria ao JComboBox
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading categories: " + ex.getMessage());
        }
    
        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String category = (String) cmbCategory.getSelectedItem();
            double price;
            int quantity;
            try {
                price = Double.parseDouble(txtPrice.getText().trim());
                quantity = Integer.parseInt(txtQuantity.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid numbers for price and quantity.");
                return;
            }
    
            int newId = cart.getItems().size() + 1; // Gera um novo ID
            Item newItem = new Item(newId, name, category, price, quantity);
    
            try {
                PrologUtils.saveItem(newItem); // Grava o novo item
                JOptionPane.showMessageDialog(frame, "Item added successfully!");
                frame.dispose();
                showManageItemsMenu(new JFrame()); // Atualiza a interface
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error adding item: " + ex.getMessage());
            }
        });
    
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageItemsMenu(new JFrame());
        });
    
        frame.add(lblName);
        frame.add(txtName);
        frame.add(lblCategory);
        frame.add(cmbCategory);
        frame.add(lblPrice);
        frame.add(txtPrice);
        frame.add(lblQuantity);
        frame.add(txtQuantity);
        frame.add(btnSave);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Mostra a interface de alteração de item
    private static void showChangeItemMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Change Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(null);
    
        List<Item> items = cart.getItems();
    
        int y = 40;
        // Adiciona um botão para cada item disponível no carrinho
        for (Item item : items) {
            JButton btnItem = new JButton(item.getName());
            btnItem.setBounds(50, y, 200, 30);
            frame.add(btnItem);
            // Ao clicar no botão, abre o menu de edição do item
            btnItem.addActionListener(e -> showEditItemMenu(frame, item));
            y += 40;
        }
    
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, y, 200, 30);
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageItemsMenu(new JFrame());
        });
        frame.add(btnBack);
    
        frame.setVisible(true);
    }
    
    private static void showEditItemMenu(JFrame parentFrame, Item item) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Edit Item: " + item.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);
    
        JLabel lblName = new JLabel("Item Name:");
        JTextField txtName = new JTextField(item.getName());
        JLabel lblCategory = new JLabel("Category:");
        JComboBox<String> cmbCategory = new JComboBox<>();
        JLabel lblPrice = new JLabel("Price:");
        JTextField txtPrice = new JTextField(String.valueOf(item.getPrice()));
        JLabel lblQuantity = new JLabel("Quantity:");
        JTextField txtQuantity = new JTextField(String.valueOf(item.getStockQuantity()));
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");
    
        // Carrega todas as categorias disponíveis no JComboBox e seleciona a atual
        try {
            List<String> categories = PrologUtils.getAllCategories();
            for (String category : categories) {
                cmbCategory.addItem(category);
            }
            cmbCategory.setSelectedItem(item.getCategory());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading categories: " + ex.getMessage());
        }
    
        // Atualiza as informações do item no sistema
        btnSave.addActionListener(e -> {
            String newName = txtName.getText().trim();
            String newCategory = (String) cmbCategory.getSelectedItem();
            double newPrice = Double.parseDouble(txtPrice.getText().trim());
            int newQuantity = Integer.parseInt(txtQuantity.getText().trim());
            try {
                PrologUtils.updateItem(item.getId(), newName, newCategory, newPrice, newQuantity);
                JOptionPane.showMessageDialog(frame, "Item updated successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error updating item: " + ex.getMessage());
            }
        });
    
        btnBack.addActionListener(e -> {
            frame.dispose();
            showChangeItemMenu(new JFrame());
        });
    
        frame.add(lblName);
        frame.add(txtName);
        frame.add(lblCategory);
        frame.add(cmbCategory);
        frame.add(lblPrice);
        frame.add(txtPrice);
        frame.add(lblQuantity);
        frame.add(txtQuantity);
        frame.add(btnSave);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    private static void showRemoveItemMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Remove Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(null);
    
        List<Item> items = cart.getItems();
    
        int y = 40;
        // Adiciona um botão para cada item disponível no carrinho
        for (Item item : items) {
            JButton btnItem = new JButton(item.getName());
            btnItem.setBounds(50, y, 200, 30);
            frame.add(btnItem);
            // Ao clicar no botão, remove o item do sistema
            btnItem.addActionListener(e -> {
                try {
                    PrologUtils.removeItem(item.getId()); // Remove o item do sistema
                    JOptionPane.showMessageDialog(frame, "Item removed successfully!");
                    frame.dispose();
                    showRemoveItemMenu(new JFrame()); // Atualiza a interface
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error removing item: " + ex.getMessage());
                }
            });
            y += 40;
        }
    
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, y, 200, 30);
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageItemsMenu(new JFrame()); // Volta ao menu de gestão de itens
        });
        frame.add(btnBack);
    
        frame.setVisible(true);
    }
    
    // Sub-Menu dos Custos e Descontos
    private static void showCostsAndDiscountsMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Costs and Discounts");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
    
        JButton btnViewShippingCosts = new JButton("View Shipping Costs");
        JButton btnViewCategoryDiscounts = new JButton("View Category Discounts");
        JButton btnViewLoyaltyDiscounts = new JButton("View Loyalty Discounts");
        JButton btnManageShippingCosts = new JButton("Manage Shipping Costs");
        JButton btnManageCategoryDiscount = new JButton("Manage Category Discount");
        JButton btnManageLoyaltyDiscount = new JButton("Manage Loyalty Discount");
        JButton btnBack = new JButton("Back");
    
        btnViewShippingCosts.setBounds(50, 40, 200, 30);
        btnViewCategoryDiscounts.setBounds(50, 80, 200, 30);
        btnViewLoyaltyDiscounts.setBounds(50, 120, 200, 30);
        btnManageShippingCosts.setBounds(50, 160, 200, 30);
        btnManageCategoryDiscount.setBounds(50, 200, 200, 30);
        btnManageLoyaltyDiscount.setBounds(50, 240, 200, 30);
        btnBack.setBounds(50, 280, 200, 30);
    
        btnViewShippingCosts.addActionListener(e -> showDataViewMenu(frame, "Shipping Costs", () -> PrologUtils.getShippingCosts()));
        btnViewCategoryDiscounts.addActionListener(e -> showDataViewMenu(frame, "Category Discounts", () -> PrologUtils.getCategoryDiscounts()));
        btnViewLoyaltyDiscounts.addActionListener(e -> showDataViewMenu(frame, "Loyalty Discounts", () -> PrologUtils.getLoyaltyDiscounts()));
        btnManageShippingCosts.addActionListener(e -> showManageShippingCostsMenu(frame));
        btnManageCategoryDiscount.addActionListener(e -> showManageCategoryDiscountsMenu(frame));
        btnManageLoyaltyDiscount.addActionListener(e -> showManageLoyaltyDiscountsMenu(frame));
        btnBack.addActionListener(e -> {
            frame.dispose();
            showMainMenu(); // Volta ao menu principal
        });
    
        frame.setLayout(null);
        frame.add(btnViewShippingCosts);
        frame.add(btnViewCategoryDiscounts);
        frame.add(btnViewLoyaltyDiscounts);
        frame.add(btnManageShippingCosts);
        frame.add(btnManageCategoryDiscount);
        frame.add(btnManageLoyaltyDiscount);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Mostra uma janela de visualização de dados com base no título e nos dados fornecidos pelo DataSupplier
    private static void showDataViewMenu(JFrame parentFrame, String title, DataSupplier dataSupplier) {
        parentFrame.dispose();
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
    
        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");
    
        try {
            // Obtém os dados fornecidos pelo DataSupplier e exibe-os na área de texto
            List<String> data = dataSupplier.getData();
            if (!data.isEmpty()) {
                txtResult.setText(String.join("\n", data)); // Exibe os dados
            } else {
                txtResult.setText("No data found.");
            }
        } catch (IOException ex) {
            txtResult.setText("Error loading data: " + ex.getMessage()); // Mostra mensagem de erro se falhar
        }
    
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCostsAndDiscountsMenu(new JFrame()); // Volta ao menu de custos e descontos
        });
    
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);
    
        frame.setVisible(true);
    }
    
    // Interface funcional para fornecer dados de diferentes fontes
    @FunctionalInterface
    interface DataSupplier {
        List<String> getData() throws IOException;
    }
    
    public static void showManageShippingCostsMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Manage Shipping Costs");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
    
        List<String> shippingCosts;
        try {
            shippingCosts = PrologUtils.getShippingCosts(); // Obtém os custos de envio
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading shipping costs: " + ex.getMessage());
            return;
        }
    
        int y = 40;
        // Adiciona um botão para cada custo de envio
        for (String cost : shippingCosts) {
            JButton btnCost = new JButton(cost);
            btnCost.setBounds(50, y, 200, 30);
            frame.add(btnCost);
            btnCost.addActionListener(e -> showChangeRemoveShippingCostMenu(frame, cost)); // Edita ou remove o custo de envio
            y += 40;
        }
    
        JButton btnAddShippingCost = new JButton("Add Shipping Cost");
        btnAddShippingCost.setBounds(50, y, 200, 30);
        frame.add(btnAddShippingCost);
        btnAddShippingCost.addActionListener(e -> showAddShippingCostMenu(frame)); // Adiciona um novo custo de envio
    
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, y + 40, 200, 30);
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCostsAndDiscountsMenu(new JFrame()); // Volta ao menu de custos e descontos
        });
        frame.add(btnBack);
    
        frame.setLayout(null);
        frame.setVisible(true);
    }
    
    // Adciona Distrito e shipping cost
    private static void showAddShippingCostMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Add Shipping Cost");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(null);
    
        JLabel lblDistrict = new JLabel("District:");
        JTextField txtDistrict = new JTextField();
        JLabel lblCost = new JLabel("Cost:");
        JTextField txtCost = new JTextField();
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");
    
        lblDistrict.setBounds(10, 30, 100, 25);
        txtDistrict.setBounds(120, 30, 150, 25);
        lblCost.setBounds(10, 70, 100, 25);
        txtCost.setBounds(120, 70, 150, 25);
        btnSave.setBounds(50, 110, 200, 30);
        btnBack.setBounds(50, 150, 200, 30);
    
        btnSave.addActionListener(e -> {
            String district = txtDistrict.getText().trim();
            double cost;
            try {
                cost = Double.parseDouble(txtCost.getText().trim()); // Converte o custo para double
                PrologUtils.addShippingCost(district, cost); // Adiciona o custo de envio
                JOptionPane.showMessageDialog(frame, "Shipping cost added successfully!");
                frame.dispose();
                showManageShippingCostsMenu(new JFrame()); // Atualiza o menu de custos de envio
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error adding shipping cost: " + ex.getMessage());
            }
        });
    
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageShippingCostsMenu(new JFrame()); // Volta ao menu de gestão de custos de envio
        });
    
        frame.add(lblDistrict);
        frame.add(txtDistrict);
        frame.add(lblCost);
        frame.add(txtCost);
        frame.add(btnSave);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Altera dados de Distrito e Shipping Cost
    private static void showChangeRemoveShippingCostMenu(JFrame parentFrame, String cost) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Edit Shipping Cost");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);
    
        String[] parts = cost.split(", ");
        String district = parts[0].split(": ")[1].replace("'", "");
        String costValue = parts[1].split(": ")[1];
    
        JLabel lblDistrict = new JLabel("District:");
        JTextField txtDistrict = new JTextField(district);
        JLabel lblCost = new JLabel("Cost:");
        JTextField txtCost = new JTextField(costValue);
        JButton btnSave = new JButton("Save");
        JButton btnRemove = new JButton("Remove");
        JButton btnBack = new JButton("Back");
    
        lblDistrict.setBounds(10, 30, 100, 25);
        txtDistrict.setBounds(120, 30, 150, 25);
        lblCost.setBounds(10, 70, 100, 25);
        txtCost.setBounds(120, 70, 150, 25);
        btnSave.setBounds(50, 110, 200, 30);
        btnRemove.setBounds(50, 150, 200, 30);
        btnBack.setBounds(50, 190, 200, 30);
    
        btnSave.addActionListener(e -> {
            double newCost;
            try {
                newCost = Double.parseDouble(txtCost.getText().trim()); // Converte o novo custo para double
                PrologUtils.changeShippingCost(district, newCost); // Atualiza o custo de envio
                JOptionPane.showMessageDialog(frame, "Shipping cost changed successfully!");
                frame.dispose();
                showManageShippingCostsMenu(new JFrame()); // Atualiza o menu de custos de envio
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error changing shipping cost: " + ex.getMessage());
            }
        });
    
        btnRemove.addActionListener(e -> {
            try {
                PrologUtils.removeShippingCost(district); // Remove o custo de envio
                JOptionPane.showMessageDialog(frame, "Shipping cost removed successfully!");
                frame.dispose();
                showManageShippingCostsMenu(new JFrame()); // Atualiza o menu de custos de envio
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error removing shipping cost: " + ex.getMessage());
            }
        });
    
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageShippingCostsMenu(new JFrame()); // Volta ao menu de gestão de custos de envio
        });
    
        frame.add(lblDistrict);
        frame.add(txtDistrict);
        frame.add(lblCost);
        frame.add(txtCost);
        frame.add(btnSave);
        frame.add(btnRemove);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Gestão de Descontos de Categoria
    public static void showManageCategoryDiscountsMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Manage Category Discounts");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
    
        List<String> categoryDiscounts;
        try {
            categoryDiscounts = PrologUtils.getCategoryDiscounts(); // Obtém os descontos por categoria
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading category discounts: " + ex.getMessage());
            return;
        }
    
        int y = 40;
        // Adiciona um botão para cada desconto por categoria
        for (String discount : categoryDiscounts) {
            JButton btnDiscount = new JButton(discount);
            btnDiscount.setBounds(50, y, 200, 30);
            frame.add(btnDiscount);
            btnDiscount.addActionListener(e -> showChangeRemoveCategoryDiscountMenu(frame, discount)); // Edita ou remove o desconto por categoria
            y += 40;
        }
    
        JButton btnAddCategoryDiscount = new JButton("Add Category Discount");
        btnAddCategoryDiscount.setBounds(50, y, 200, 30);
        frame.add(btnAddCategoryDiscount);
        btnAddCategoryDiscount.addActionListener(e -> showAddCategoryDiscountMenu(frame)); // Adiciona um novo desconto por categoria
    
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, y + 40, 200, 30);
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCostsAndDiscountsMenu(new JFrame()); // Volta ao menu de custos e descontos
        });
        frame.add(btnBack);
    
        frame.setLayout(null);
        frame.setVisible(true);
    }
    
    // Adiciona Desconto de Categoria
    private static void showAddCategoryDiscountMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Add Category Discount");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(null);
    
        JLabel lblCategory = new JLabel("Category:");
        JTextField txtCategory = new JTextField();
        JLabel lblDiscount = new JLabel("Discount:");
        JTextField txtDiscount = new JTextField();
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");
    
        lblCategory.setBounds(10, 30, 100, 25);
        txtCategory.setBounds(120, 30, 150, 25);
        lblDiscount.setBounds(10, 70, 100, 25);
        txtDiscount.setBounds(120, 70, 150, 25);
        btnSave.setBounds(50, 110, 200, 30);
        btnBack.setBounds(50, 150, 200, 30);
    
        // Adiciona um novo desconto por categoria
        btnSave.addActionListener(e -> {
            String category = txtCategory.getText().trim();
            double discount;
            try {
                discount = Double.parseDouble(txtDiscount.getText().trim());
                PrologUtils.addCategoryDiscount(category, discount);
                JOptionPane.showMessageDialog(frame, "Category discount added successfully!");
                frame.dispose();
                showManageCategoryDiscountsMenu(new JFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error adding category discount: " + ex.getMessage());
            }
        });
    
        // Volta ao menu de gestão de descontos por categoria
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageCategoryDiscountsMenu(new JFrame());
        });
    
        frame.add(lblCategory);
        frame.add(txtCategory);
        frame.add(lblDiscount);
        frame.add(txtDiscount);
        frame.add(btnSave);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Altera ou remove desconto de categoria
    private static void showChangeRemoveCategoryDiscountMenu(JFrame parentFrame, String discount) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Edit Category Discount");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);
    
        String[] parts = discount.split(", ");
        String category = parts[0].split(": ")[1].replace("'", "");
        String discountValue = parts[1].split(": ")[1];
    
        JLabel lblCategory = new JLabel("Category:");
        JTextField txtCategory = new JTextField(category);
        JLabel lblDiscount = new JLabel("Discount:");
        JTextField txtDiscount = new JTextField(discountValue);
        JButton btnSave = new JButton("Save");
        JButton btnRemove = new JButton("Remove");
        JButton btnBack = new JButton("Back");
    
        lblCategory.setBounds(10, 30, 100, 25);
        txtCategory.setBounds(120, 30, 150, 25);
        lblDiscount.setBounds(10, 70, 100, 25);
        txtDiscount.setBounds(120, 70, 150, 25);
        btnSave.setBounds(50, 110, 200, 30);
        btnRemove.setBounds(50, 150, 200, 30);
        btnBack.setBounds(50, 190, 200, 30);
    
        // Grava as alterações no desconto por categoria
        btnSave.addActionListener(e -> {
            String newCategory = txtCategory.getText().trim();
            double newDiscount;
            try {
                newDiscount = Double.parseDouble(txtDiscount.getText().trim());
                PrologUtils.changeCategoryDiscount(newCategory, newDiscount);
                JOptionPane.showMessageDialog(frame, "Category discount changed successfully!");
                frame.dispose();
                showManageCategoryDiscountsMenu(new JFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error changing category discount: " + ex.getMessage());
            }
        });
    
        // Remove o desconto por categoria
        btnRemove.addActionListener(e -> {
            try {
                PrologUtils.removeCategoryDiscount(category);
                JOptionPane.showMessageDialog(frame, "Category discount removed successfully!");
                frame.dispose();
                showManageCategoryDiscountsMenu(new JFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error removing category discount: " + ex.getMessage());
            }
        });
    
        // Volta ao menu de gestão de descontos por categoria
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageCategoryDiscountsMenu(new JFrame());
        });
    
        frame.add(lblCategory);
        frame.add(txtCategory);
        frame.add(lblDiscount);
        frame.add(txtDiscount);
        frame.add(btnSave);
        frame.add(btnRemove);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Sub-menu de Descontos de Lealdade
    public static void showManageLoyaltyDiscountsMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Manage Loyalty Discounts");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
    
        List<String> loyaltyDiscounts;
        try {
            loyaltyDiscounts = PrologUtils.getLoyaltyDiscounts();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading loyalty discounts: " + ex.getMessage());
            return;
        }
    
        int y = 40;
        for (String discount : loyaltyDiscounts) {
            JButton btnDiscount = new JButton(discount);
            btnDiscount.setBounds(50, y, 200, 30);
            frame.add(btnDiscount);
            // Abre o menu para alterar ou remover o desconto de lealdade
            btnDiscount.addActionListener(e -> showChangeRemoveLoyaltyDiscountMenu(frame, discount));
            y += 40;
        }
    
        JButton btnAddLoyaltyDiscount = new JButton("Add Loyalty Discount");
        btnAddLoyaltyDiscount.setBounds(50, y, 200, 30);
        frame.add(btnAddLoyaltyDiscount);
        // Abre o menu para adicionar um novo desconto de lealdade
        btnAddLoyaltyDiscount.addActionListener(e -> showAddLoyaltyDiscountMenu(frame));
    
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, y + 40, 200, 30);
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCostsAndDiscountsMenu(new JFrame());
        });
        frame.add(btnBack);
    
        frame.setLayout(null);
        frame.setVisible(true);
    }
    
    // Adiciona desconto de lealdade
    private static void showAddLoyaltyDiscountMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Add Loyalty Discount");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(null);
    
        JLabel lblYears = new JLabel("Years:");
        JTextField txtYears = new JTextField();
        JLabel lblDiscount = new JLabel("Discount:");
        JTextField txtDiscount = new JTextField();
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");
    
        lblYears.setBounds(10, 30, 100, 25);
        txtYears.setBounds(120, 30, 150, 25);
        lblDiscount.setBounds(10, 70, 100, 25);
        txtDiscount.setBounds(120, 70, 150, 25);
        btnSave.setBounds(50, 110, 200, 30);
        btnBack.setBounds(50, 150, 200, 30);
    
        // Grava o novo desconto de lealdade
        btnSave.addActionListener(e -> {
            int years;
            double discount;
            try {
                years = Integer.parseInt(txtYears.getText().trim());
                discount = Double.parseDouble(txtDiscount.getText().trim());
                PrologUtils.addLoyaltyDiscount(years, discount);
                JOptionPane.showMessageDialog(frame, "Loyalty discount added successfully!");
                frame.dispose();
                showManageLoyaltyDiscountsMenu(new JFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error adding loyalty discount: " + ex.getMessage());
            }
        });
    
        // Volta ao menu de gestão de descontos por lealdade
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageLoyaltyDiscountsMenu(new JFrame());
        });
    
        frame.add(lblYears);
        frame.add(txtYears);
        frame.add(lblDiscount);
        frame.add(txtDiscount);
        frame.add(btnSave);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Altera ou remove desconto de lealdade
    private static void showChangeRemoveLoyaltyDiscountMenu(JFrame parentFrame, String discount) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Edit Loyalty Discount");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);
    
        String[] parts = discount.split(", ");
        String years = parts[0].split(": ")[1];
        String discountValue = parts[1].split(": ")[1];
    
        JLabel lblYears = new JLabel("Years:");
        JTextField txtYears = new JTextField(years);
        JLabel lblDiscount = new JLabel("Discount:");
        JTextField txtDiscount = new JTextField(discountValue);
        JButton btnSave = new JButton("Save");
        JButton btnRemove = new JButton("Remove");
        JButton btnBack = new JButton("Back");
    
        lblYears.setBounds(10, 30, 100, 25);
        txtYears.setBounds(120, 30, 150, 25);
        lblDiscount.setBounds(10, 70, 100, 25);
        txtDiscount.setBounds(120, 70, 150, 25);
        btnSave.setBounds(50, 110, 200, 30);
        btnRemove.setBounds(50, 150, 200, 30);
        btnBack.setBounds(50, 190, 200, 30);
    
        // Grava as alterações no desconto de lealdade
        btnSave.addActionListener(e -> {
            int newYears;
            double newDiscount;
            try {
                newYears = Integer.parseInt(txtYears.getText().trim());
                newDiscount = Double.parseDouble(txtDiscount.getText().trim());
                PrologUtils.changeLoyaltyDiscount(newYears, newDiscount);
                JOptionPane.showMessageDialog(frame, "Loyalty discount changed successfully!");
                frame.dispose();
                showManageLoyaltyDiscountsMenu(new JFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error changing loyalty discount: " + ex.getMessage());
            }
        });
    
        // Remove o desconto de lealdade
        btnRemove.addActionListener(e -> {
            try {
                PrologUtils.removeLoyaltyDiscount(Integer.parseInt(years));
                JOptionPane.showMessageDialog(frame, "Loyalty discount removed successfully!");
                frame.dispose();
                showManageLoyaltyDiscountsMenu(new JFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error removing loyalty discount: " + ex.getMessage());
            }
        });
    
        // Volta ao menu de gestão de descontos por lealdade
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageLoyaltyDiscountsMenu(new JFrame());
        });
    
        frame.add(lblYears);
        frame.add(txtYears);
        frame.add(lblDiscount);
        frame.add(txtDiscount);
        frame.add(btnSave);
        frame.add(btnRemove);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Sub-Menu de dados de cliente
    private static void showCustomerDataMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Customer Data");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
    
        JButton btnAllCustomers = new JButton("All Customers");
        JButton btnCustomersByDistrict = new JButton("Customers by District");
        JButton btnSalesByCustomer = new JButton("Sales by Customer");
        JButton btnCustomersByLoyalty = new JButton("Customers by Loyalty");
        JButton btnManageCustomers = new JButton("Manage Customers");
        JButton btnBack = new JButton("Back");
    
        btnAllCustomers.setBounds(50, 40, 200, 30);
        btnCustomersByDistrict.setBounds(50, 80, 200, 30);
        btnSalesByCustomer.setBounds(50, 120, 200, 30);
        btnCustomersByLoyalty.setBounds(50, 160, 200, 30);
        btnManageCustomers.setBounds(50, 200, 200, 30);
        btnBack.setBounds(50, 240, 200, 30);
    
        // Exibe todos os clientes
        btnAllCustomers.addActionListener(e -> showAllCustomersMenu(frame));
    
        // Exibe os clientes por distrito
        btnCustomersByDistrict.addActionListener(e -> showCustomersByDistrictMenu(frame));
    
        // Exibe as vendas por cliente
        btnSalesByCustomer.addActionListener(e -> showSearchCustomerForm(frame, true));
    
        // Exibe os clientes por anos de fidelidade
        btnCustomersByLoyalty.addActionListener(e -> showCustomersByLoyaltyMenu(frame));
    
        // Abre o menu de gestão de clientes
        btnManageCustomers.addActionListener(e -> showManageCustomersMenu(frame));
    
        // Volta ao menu principal
        btnBack.addActionListener(e -> {
            frame.dispose();
            showMainMenu();
        });
    
        frame.setLayout(null);
        frame.add(btnAllCustomers);
        frame.add(btnCustomersByDistrict);
        frame.add(btnSalesByCustomer);
        frame.add(btnCustomersByLoyalty);
        frame.add(btnManageCustomers);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Mostra todos os clientes
    private static void showAllCustomersMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("All Customers");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
    
        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");
    
        // Obtém todos os clientes e exibe no TextArea
        List<String> customers = PrologUtils.getAllCustomers();
        if (!customers.isEmpty()) {
            txtResult.setText(String.join("\n", customers));
        } else {
            txtResult.setText("No customers found.");
        }
    
        // Volta ao menu de dados do cliente
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCustomerDataMenu(new JFrame());
        });
    
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);
    
        frame.setVisible(true);
    }
    
    // Mostra o menu onde se escolhe o distrito a pesquisar
    private static void showCustomersByDistrictMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Customers by District");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
    
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panel);
    
        try {
            // Obtém todos os distritos e adiciona botões para cada um
            List<String> districts = PrologUtils.getAllDistricts();
            for (String district : districts) {
                JButton btnDistrict = new JButton(district);
                btnDistrict.addActionListener(e -> showCustomersByDistrict(frame, district));
                panel.add(btnDistrict);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading districts: " + ex.getMessage());
        }
    
        // Volta ao menu de dados do cliente
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCustomerDataMenu(new JFrame());
        });
    
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);
    
        frame.setVisible(true);
    }
    
    // Mostra os clientes de determinado distrito
    private static void showCustomersByDistrict(JFrame parentFrame, String district) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Customers in " + district);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
    
        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");
    
        // Obtém os clientes por distrito e exibe no TextArea
        List<String> customers = PrologUtils.getCustomersByDistrict(district);
        if (!customers.isEmpty()) {
            txtResult.setText(String.join("\n", customers));
        } else {
            txtResult.setText("No customers found in this district.");
        }
    
        // Volta ao menu de clientes por distrito
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCustomersByDistrictMenu(new JFrame());
        });
    
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);
    
        frame.setVisible(true);
    }
    
    // Mostra os diferentes descontos de lealdade como butões
    private static void showCustomersByLoyaltyMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Customers by Loyalty Years");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
    
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panel);
    
        try {
            // Obtém os anos de fidelidade e adiciona botões para cada um
            List<Integer> loyaltyYears = PrologUtils.getAllLoyaltyYears();
            for (int year : loyaltyYears) {
                JButton btnYear = new JButton(String.valueOf(year));
                btnYear.addActionListener(e -> showCustomersByLoyalty(frame, year));
                panel.add(btnYear);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading loyalty years: " + ex.getMessage());
        }
    
        // Volta ao menu de dados do cliente
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCustomerDataMenu(new JFrame());
        });
    
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);
    
        frame.setVisible(true);
    }
    
    // Mostra os diferentes clientes que correspondem a determinados anos de lealdade
    private static void showCustomersByLoyalty(JFrame parentFrame, int years) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Customers with " + years + " Loyalty Years");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
    
        JTextArea txtResult = new JTextArea();
        txtResult.setEditable(false);
        JButton btnBack = new JButton("Back");
    
        // Obtém os clientes por anos de fidelidade e exibe no TextArea
        List<String> customers = PrologUtils.getCustomersByLoyalty(years);
        if (!customers.isEmpty()) {
            txtResult.setText(String.join("\n", customers));
        } else {
            txtResult.setText("No customers found with " + years + " loyalty years.");
        }
    
        // Volta ao menu de clientes por anos de fidelidade
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCustomersByLoyaltyMenu(new JFrame());
        });
    
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        frame.add(btnBack, BorderLayout.SOUTH);
    
        frame.setVisible(true);
    }
    
    // Mostra todos os clientes como butões e permite ainda adicionar mais
    private static void showManageCustomersMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Manage Customers");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
    
        List<Customer> customers = Store.customers;
    
        int y = 40;
        // Adiciona botões para cada cliente, permitindo edição
        for (Customer customer : customers) {
            JButton btnCustomer = new JButton(customer.getName());
            btnCustomer.setBounds(50, y, 200, 30);
            frame.add(btnCustomer);
            btnCustomer.addActionListener(e -> showEditCustomerMenu(frame, customer));
            y += 40;
        }
    
        JButton btnAddCustomer = new JButton("Add Customer");
        btnAddCustomer.setBounds(50, y, 200, 30);
        frame.add(btnAddCustomer);
        // Abre o menu para adicionar um novo cliente
        btnAddCustomer.addActionListener(e -> showAddCustomerMenu(frame));
    
        // Volta ao menu de dados do cliente
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(50, y + 40, 200, 30);
        btnBack.addActionListener(e -> {
            frame.dispose();
            showCustomerDataMenu(new JFrame());
        });
        frame.add(btnBack);
    
        frame.setLayout(null);
        frame.setVisible(true);
    }
    
    // Interface que permite adicionar cliente (com dados básicos, ID é gerado automaticamente deste modo)
    private static void showAddCustomerMenu(JFrame parentFrame) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Add Customer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);
    
        JLabel lblName = new JLabel("Name:");
        JTextField txtName = new JTextField();
        JLabel lblDistrict = new JLabel("District:");
        JTextField txtDistrict = new JTextField();
        JLabel lblYears = new JLabel("Years:");
        JTextField txtYears = new JTextField();
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");
    
        lblName.setBounds(10, 30, 100, 25);
        txtName.setBounds(120, 30, 150, 25);
        lblDistrict.setBounds(10, 70, 100, 25);
        txtDistrict.setBounds(120, 70, 150, 25);
        lblYears.setBounds(10, 110, 100, 25);
        txtYears.setBounds(120, 110, 150, 25);
        btnSave.setBounds(50, 150, 200, 30);
        btnBack.setBounds(50, 190, 200, 30);
    
        // Grava um novo cliente
        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String district = txtDistrict.getText().trim();
            int years;
            try {
                years = Integer.parseInt(txtYears.getText().trim());
                int newId = customers.size() + 1; // Gera um novo ID de cliente
                Customer newCustomer = new Customer(newId, name, "", district, years);
                PrologUtils.saveCustomer(newCustomer);
                customers.add(newCustomer);
                JOptionPane.showMessageDialog(frame, "Customer added successfully!");
                frame.dispose();
                showManageCustomersMenu(new JFrame());
            } catch (NumberFormatException | IOException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid data.");
            }
        });
    
        // Volta ao menu de gestão de clientes
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageCustomersMenu(new JFrame());
        });
    
        frame.add(lblName);
        frame.add(txtName);
        frame.add(lblDistrict);
        frame.add(txtDistrict);
        frame.add(lblYears);
        frame.add(txtYears);
        frame.add(btnSave);
        frame.add(btnBack);
        frame.setVisible(true);
    }
    
    // Edita as infos do cliente
    private static void showEditCustomerMenu(JFrame parentFrame, Customer customer) {
        parentFrame.dispose();
        JFrame frame = new JFrame("Edit Customer: " + customer.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(null);
    
        JLabel lblName = new JLabel("Name:");
        JTextField txtName = new JTextField(customer.getName());
        JLabel lblDistrict = new JLabel("District:");
        JTextField txtDistrict = new JTextField(customer.getDistrict());
        JLabel lblYears = new JLabel("Years:");
        JTextField txtYears = new JTextField(String.valueOf(customer.getYearsOfLoyalty()));
        JButton btnSave = new JButton("Save");
        JButton btnRemove = new JButton("Remove");
        JButton btnBack = new JButton("Back");
    
        lblName.setBounds(10, 30, 100, 25);
        txtName.setBounds(120, 30, 150, 25);
        lblDistrict.setBounds(10, 70, 100, 25);
        txtDistrict.setBounds(120, 70, 150, 25);
        lblYears.setBounds(10, 110, 100, 25);
        txtYears.setBounds(120, 110, 150, 25);
        btnSave.setBounds(50, 150, 200, 30);
        btnRemove.setBounds(50, 190, 200, 30);
        btnBack.setBounds(50, 230, 200, 30);
    
        // Grava as alterações no cliente
        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String district = txtDistrict.getText().trim();
            int years;
            try {
                years = Integer.parseInt(txtYears.getText().trim());
                Customer updatedCustomer = new Customer(customer.getId(), name, "", district, years);
                PrologUtils.changeCustomer(updatedCustomer);
                customers.removeIf(c -> c.getId() == customer.getId());
                customers.add(updatedCustomer);
                JOptionPane.showMessageDialog(frame, "Customer updated successfully!");
                frame.dispose();
                showManageCustomersMenu(new JFrame());
            } catch (NumberFormatException | IOException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid data.");
            }
        });
    
        // Remove o cliente do sistema
        btnRemove.addActionListener(e -> {
            try {
                PrologUtils.removeCustomer(customer.getId());
                customers.removeIf(c -> c.getId() == customer.getId());
                JOptionPane.showMessageDialog(frame, "Customer removed successfully!");
                frame.dispose();
                showManageCustomersMenu(new JFrame());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error removing customer: " + ex.getMessage());
            }
        });
    
        // Volta ao menu de gestão de clientes
        btnBack.addActionListener(e -> {
            frame.dispose();
            showManageCustomersMenu(new JFrame());
        });
    
        frame.add(lblName);
        frame.add(txtName);
        frame.add(lblDistrict);
        frame.add(txtDistrict);
        frame.add(lblYears);
        frame.add(txtYears);
        frame.add(btnSave);
        frame.add(btnRemove);
        frame.add(btnBack);
        frame.setVisible(true);
    }
}