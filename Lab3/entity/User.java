package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int    id;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    /**
     * Role VARCHAR(30): "manager" | "cashier" | "barista" | "staff"
     * Also handles old BIT schema where getString("role") returns "true"/"false"
     */
    private String role;
    private boolean active;

    public boolean isManager() {
        // New schema: "manager"
        // Old BIT schema fallback: rs.getString("role") returns "true" for BIT=1
        return "manager".equalsIgnoreCase(role)
            || "true".equalsIgnoreCase(role)
            || "1".equals(role);
    }
}
