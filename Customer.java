// Criação da classe do cliente
public class Customer {
    private int id;
    private String name;
    private String city;
    private String district;
    private int yearsOfLoyalty;

    // Construtor de Customer
    public Customer(int id, String name, String city, String district, int yearsOfLoyalty) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.district = district;
        this.yearsOfLoyalty = yearsOfLoyalty;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getYearsOfLoyalty() {
        return yearsOfLoyalty;
    }

    public void setYearsOfLoyalty(int yearsOfLoyalty) {
        this.yearsOfLoyalty = yearsOfLoyalty;
    }

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", city='" + city + '\'' +
               ", district='" + district + '\'' +
               ", yearsOfLoyalty=" + yearsOfLoyalty +
               '}';
    }
}
