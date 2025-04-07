package aj.FiTracker.FiTrackerExpenses;

import org.springframework.boot.SpringApplication;

public class TestFiTrackerExpensesApplication {

	public static void main(String[] args) {
		SpringApplication.from(FiTrackerExpensesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
