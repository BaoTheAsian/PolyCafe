package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDetail {
    private int    id;
    private int    billId;
    private int    drinkId;
    private String drinkName;   // populated by JOIN in findByBillId()
    private int    quantity;
    private double price;
    private String size;        // S / M / L  (nullable, default M)
    private String note;        // e.g. "ít đường, nhiều đá"
}
