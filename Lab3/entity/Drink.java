package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drink {
    private int id;
    private int categoryId;
    private String name;
    private double price;
    private String image;
    private String description;
    private boolean active;
}
