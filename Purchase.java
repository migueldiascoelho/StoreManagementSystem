// Classe que liga o cliente ao seu carrinho
public class Purchase {
    private Customer customer;
    private Cart cart;

    // Construtor
    public Purchase(Customer customer, Cart cart) {
        this.customer = customer;
        this.cart = cart;
    }

    // Getters e setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return "Purchase{" +
               "customer=" + customer +
               ", cart=" + cart +
               '}';
    }
}
