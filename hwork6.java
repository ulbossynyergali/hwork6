import java.io.*;
import java.util.*;

class ConfigurationManager {
    private static volatile ConfigurationManager instance;
    private Map<String, String> settings;

    private ConfigurationManager() {
        settings = new HashMap<>();
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    public void setSetting(String key, String value) {
        settings.put(key, value);
    }

    public String getSetting(String key) {
        return settings.get(key);
    }

    public void saveToFile(String filename) throws IOException {
        Properties props = new Properties();
        props.putAll(settings);
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            props.store(fos, "App Configuration");
        }
    }

    public void loadFromFile(String filename) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filename)) {
            props.load(fis);
            for (String key : props.stringPropertyNames()) {
                settings.put(key, props.getProperty(key));
            }
        }
    }

    public void printAllSettings() {
        System.out.println("Текущие настройки:");
        settings.forEach((k, v) -> System.out.println(k + " = " + v));
    }
}

interface IReportBuilder {
    void setHeader(String header);
    void setContent(String content);
    void setFooter(String footer);
    Report getReport();
}

class Report {
    private String header;
    private String content;
    private String footer;

    public void setHeader(String header) { this.header = header; }
    public void setContent(String content) { this.content = content; }
    public void setFooter(String footer) { this.footer = footer; }

    public void display() {
        System.out.println(header);
        System.out.println(content);
        System.out.println(footer);
    }
}

class TextReportBuilder implements IReportBuilder {
    private Report report = new Report();

    public void setHeader(String header) { report.setHeader("=== " + header + " ==="); }
    public void setContent(String content) { report.setContent(content); }
    public void setFooter(String footer) { report.setFooter("--- " + footer + " ---"); }
    public Report getReport() { return report; }
}

class HtmlReportBuilder implements IReportBuilder {
    private Report report = new Report();

    public void setHeader(String header) { report.setHeader("<h1>" + header + "</h1>"); }
    public void setContent(String content) { report.setContent("<p>" + content + "</p>"); }
    public void setFooter(String footer) { report.setFooter("<footer>" + footer + "</footer>"); }
    public Report getReport() { return report; }
}

class ReportDirector {
    public void constructReport(IReportBuilder builder, String header, String content, String footer) {
        builder.setHeader(header);
        builder.setContent(content);
        builder.setFooter(footer);
    }
}

class Product implements Cloneable {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(Product other) {
        this.name = other.name;
        this.price = other.price;
        this.quantity = other.quantity;
    }

    @Override
    public Product clone() {
        return new Product(this);
    }

    @Override
    public String toString() {
        return name + " (цена: " + price + ", количество: " + quantity + ")";
    }
}

class Discount implements Cloneable {
    private String type;
    private double value;

    public Discount(String type, double value) {
        this.type = type;
        this.value = value;
    }

    public Discount(Discount other) {
        this.type = other.type;
        this.value = other.value;
    }

    @Override
    public Discount clone() {
        return new Discount(this);
    }

    @Override
    public String toString() {
        return type + " скидка " + value + "%";
    }
}

class Order implements Cloneable {
    private List<Product> products;
    private Discount discount;
    private double deliveryCost;
    private String paymentMethod;

    public Order(List<Product> products, Discount discount, double deliveryCost, String paymentMethod) {
        this.products = products;
        this.discount = discount;
        this.deliveryCost = deliveryCost;
        this.paymentMethod = paymentMethod;
    }

    public Order(Order other) {
        this.products = new ArrayList<>();
        for (Product p : other.products) {
            this.products.add(p.clone());
        }
        this.discount = other.discount.clone();
        this.deliveryCost = other.deliveryCost;
        this.paymentMethod = other.paymentMethod;
    }

    @Override
    public Order clone() {
        return new Order(this);
    }

    @Override
    public String toString() {
        return "Order{" +
                "products=" + products +
                ", discount=" + discount +
                ", deliveryCost=" + deliveryCost +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}

public class hwork6 {
    public static void main(String[] args) throws Exception {

        System.out.println("Singleton:");
        ConfigurationManager config1 = ConfigurationManager.getInstance();
        ConfigurationManager config2 = ConfigurationManager.getInstance();

        config1.setSetting("AppName", "My Application");
        config1.setSetting("Version", "3.4");

        config2.printAllSettings();
        System.out.println("Один и тот же объект? " + (config1 == config2));
        System.out.println();

        System.out.println("Builder:");
        ReportDirector director = new ReportDirector();

        IReportBuilder textBuilder = new TextReportBuilder();
        director.constructReport(textBuilder, "Отчет продаж", "Продажи выросли на 27%", "Конец отчета");
        textBuilder.getReport().display();

        System.out.println();

        IReportBuilder htmlBuilder = new HtmlReportBuilder();
        director.constructReport(htmlBuilder, "HTML Report", "Sales increased by 27%", "End of report");
        htmlBuilder.getReport().display();

        System.out.println();

        System.out.println("Prototype:");
        List<Product> products = Arrays.asList(
                new Product("Ноутбук", 640000, 1),
                new Product("Мышь", 4200, 2)
        );
        Discount discount = new Discount("Сезонная", 15);
        Order original = new Order(products, discount, 1500, "Картой");

        Order clone = original.clone();

        System.out.println("Оригинал:\n" + original);
        System.out.println("\nКлон:\n" + clone);
    }
}
