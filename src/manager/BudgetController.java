package manager;

import model.Budget;
import model.BudgetService;
import model.Transaction;

import java.time.YearMonth;
import java.util.List;
import model.BudgetCategory;

/**
 * BudgetController – lives in the manager package to match project structure.
 * Bridges the view layer and BudgetService.
 */
public class BudgetController {

    private final BudgetService service = BudgetService.getInstance();

    private static BudgetController instance;
    public static BudgetController getInstance() {
        if (instance == null) instance = new BudgetController();
        return instance;
    }
    private BudgetController() {}

    // ── US #4 – Create / Edit Budget ─────────────────────────────────────────

    public ControllerResult<Budget> saveBudget(int category, double amount, YearMonth period) {
        try {
            return ControllerResult.success(service.createBudget(category, amount, period));
        } catch (Exception e) {
            return ControllerResult.error(e.getMessage());
        }
    }

    public ControllerResult<Budget> updateBudget(int id, int category, double amount, YearMonth period) {
        try {
            return ControllerResult.success(service.editBudget(id, category, amount, period));
        } catch (Exception e) {
            return ControllerResult.error(e.getMessage());
        }
    }

    public List<Budget> getAllBudgets()                  { return service.getAllBudgets(); }
    public List<Budget> getBudgetsByPeriod(YearMonth p) { return service.getBudgetsByPeriod(p); }

    // ── Dashboard data ────────────────────────────────────────────────────────

    public double            getMonthlyIncome(YearMonth p)    { return service.getMonthlyIncome(p); }
    public double            getMonthlyExpenses(YearMonth p)  { return service.getMonthlyExpenses(p); }
    public double            getTotalBalance(YearMonth p)     { return service.getTotalBalance(p); }
    public int[]             getStatusCounts()                { return service.getStatusCounts(); }
    public List<Transaction> getRecentTransactions(int limit) { return service.getRecentTransactions(limit); }
    public List<Budget>      getBudgetWarnings(YearMonth p)   { return service.getBudgetWarnings(p); }
    public List<BudgetCategory>      getCategories()                  { return service.getCategories(); }

    // ── Result wrapper ────────────────────────────────────────────────────────

    public static class ControllerResult<T> {
        private final boolean success;
        private final T       data;
        private final String  errorMessage;

        private ControllerResult(boolean s, T d, String e) {
            this.success = s; this.data = d; this.errorMessage = e;
        }
        public static <T> ControllerResult<T> success(T data)   { return new ControllerResult<>(true,  data, null); }
        public static <T> ControllerResult<T> error(String msg) { return new ControllerResult<>(false, null, msg);  }

        public boolean isSuccess()       { return success; }
        public T       getData()         { return data; }
        public String  getErrorMessage() { return errorMessage; }
    }
}