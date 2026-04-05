package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    private int    id;
    private int    userId;
    private int    cardId;
    private int    tableId;         // FK → cafe_tables.id  (0 = no table / takeaway)
    private String code;
    private Date   createdAt;
    private double total;
    private String status;          // waiting | finish | cancel
    private String paymentMethod;   // cash | card | momo | zalopay
}
