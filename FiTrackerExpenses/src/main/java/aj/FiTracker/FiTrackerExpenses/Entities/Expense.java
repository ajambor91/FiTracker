package aj.FiTracker.FiTrackerExpenses.Entities;


import aj.FiTracker.FiTrackerExpenses.DTO.REST.AddExpenseRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(schema = "app_data", name = "expenses")
@Getter
@Setter
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private long expenseId;

    @Column(name = "user_id")
    private long user;

    @Column(name = "name")
    private String name;

    @Column(name = "currency")
    private String currency;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "expenses")
    private Set<Category> categories = new HashSet<>();

    public Expense() {
    }

    public Expense(AddExpenseRequest addExpenseRequest, long userId, List<Category> categories) {
        this.currency = addExpenseRequest.getCurrency();
        this.user = userId;
        this.name = addExpenseRequest.getName();
        this.value = addExpenseRequest.getValue();
        this.categories.addAll(categories);
    }


    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.currency.isBlank()) {
            this.currency = "PLN";
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
