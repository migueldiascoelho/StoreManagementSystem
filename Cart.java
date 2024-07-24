import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Classe do carrinho, tem itens e quantidade de itens (contidos)
public class Cart {
    private List<ItemEntry> items = new ArrayList<>();

    // Entrada de items no carrinho
    private static class ItemEntry {
        Item item;
        int quantityInCart;

        ItemEntry(Item item, int quantityInCart) {
            this.item = item;
            this.quantityInCart = quantityInCart;
        }
    }

    // Método para adicionar itens
    public void addItem(Item item) {
        if (item.getStockQuantity() <= 0) {
            return; 
        }

        // Update da quantidade no carrinho
        for (ItemEntry entry : items) {
            if (entry.item.getId() == item.getId()) {
                entry.quantityInCart++;
                return;
            }
        }
        items.add(new ItemEntry(item, 1));
    }

    // Método para remover item (não chegou a ser utilizado)
    public void removeItem(int itemId) {
        items.removeIf(entry -> entry.item.getId() == itemId);
    }

    // Contagem de itens dentro do carrinho
    public int getItemCount() {
        int totalCount = 0;
        for (ItemEntry entry : items) {
            totalCount += entry.quantityInCart;  // Sum up the quantity of each item in the cart
        }
        return totalCount;
    }
    
    // Getter que retorna uma lista dos itens, converte de ItemEntry para Item.
    public List<Item> getItems() {
        return items.stream().map(entry -> entry.item).collect(Collectors.toList());
    }

    // Vai buscar a quantidade de itens no carrinho
    public int getQuantityInCart(int itemId) {
        for (ItemEntry entry : items) {
            if (entry.item.getId() == itemId) {
                return entry.quantityInCart;
            }
        }
        return 0;
    }
}

// Classe dos itens
class Item {
    private int id;
    private String name;
    private String category;
    private double price;
    private int stockQuantity; // Quantidade disponível em stock != quantidade que está no carrinho

    // Constructor
    public Item(int id, String name, String category, double price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void decrementStockQuantity() {
        if (this.stockQuantity > 0) {
            this.stockQuantity--;
        }
    }

    @Override
    public String toString() {
        return "Item{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", category='" + category + '\'' +
               ", price=" + price +
               ", stockQuantity=" + stockQuantity +
               '}';
    }
}


