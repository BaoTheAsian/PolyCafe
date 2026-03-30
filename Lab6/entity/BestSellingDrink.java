package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestSellingDrink {
    private int id;
    private String name;
    private double price;
    private String image;
    private int totalQuantity;
    private double totalRevenue;
}
