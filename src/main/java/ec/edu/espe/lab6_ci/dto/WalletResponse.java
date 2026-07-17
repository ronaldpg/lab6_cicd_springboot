package ec.edu.espe.lab6_ci.dto;

public class WalletResponse {
    private final String wallerId;
    private final double balance;

    public WalletResponse(String wallerId, double balance) {
        this.wallerId = wallerId;
        this.balance = balance;
    }

    public String getWallerId() {
        return wallerId;
    }

    public double getBalance() {
        return balance;
    }
}
