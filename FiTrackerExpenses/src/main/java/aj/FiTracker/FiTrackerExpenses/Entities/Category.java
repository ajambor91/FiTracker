package aj.FiTracker.FiTrackerExpenses.Entities;

import aj.FiTracker.FiTrackerExpenses.DTO.REST.BaseCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(schema = "app_data", name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private long categoryId;

    @Column(nullable = false, unique = false, name = "name")
    private String name;

    @Column(nullable = true, unique = false, name = "description")
    private String description;

    @Column(nullable = false, unique = false, name = "zone_id")
    private String zoneId;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "exp_categories",
            schema = "app_data",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "expense_id")
    )
    private Set<Expense> expenses = new HashSet<>();

    public Category(BaseCategory addCategoryRequest, String zoneId) {
        this.description = addCategoryRequest.getDescription();
        this.name = addCategoryRequest.getName();
        this.zoneId = zoneId;
    }

    public Category(BaseCategory addCategoryRequest, long categoryId, String zoneId) {
        this.description = addCategoryRequest.getDescription();
        this.name = addCategoryRequest.getName();
        this.zoneId = zoneId;
        this.categoryId = categoryId;
    }


    public Category() {
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
