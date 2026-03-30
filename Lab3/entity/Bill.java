package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    private int id;
    private int userId;
    private int cardId;
    private String code;
    private Date createdAt;
    private double total;
    private String status;
}
