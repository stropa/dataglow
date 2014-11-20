package ru.rbs.stats.data.merchants;

public class ReportEntry {
    long count;
    long amount;
    String merchant;
    String operation;
    int actionCode;

    ReportEntry(long count, long amount, String merchant, String operation, int actionCode) {
        this.count = count;
        this.amount = amount;
        this.merchant = merchant;
        this.operation = operation;
        this.actionCode = actionCode;
    }

    public static String getName() {
        return "merchant.operation.actionCode";
    }

    public static String[] getColumns() {
        return new String[]{"merchant", "operation", "actionCode", "count", "amount"};
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }
}
