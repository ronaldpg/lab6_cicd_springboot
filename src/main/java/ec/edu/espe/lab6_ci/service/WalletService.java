package ec.edu.espe.lab6_ci.service;

import ec.edu.espe.lab6_ci.repository.WalletRepository;

import ec.edu.espe.lab6_ci.dto.WalletResponse;
import ec.edu.espe.lab6_ci.model.Wallet;
import ec.edu.espe.lab6_ci.repository.WalletRepository;

import java.util.Optional;

public class WalletService {
    private final WalletRepository walletRepository;
    private final RiskClient riskClient;

    public WalletService(WalletRepository walletRepository, RiskClient riskClient) {
        this.walletRepository = walletRepository;
        this.riskClient = riskClient;
    }

    //Crear una billetera
    public WalletResponse createWallet(String ownerEmail, double initialBalance) {
        //Validaciones
        if (ownerEmail == null || !ownerEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance must be positive");
        }

        if (riskClient.isBlocked(ownerEmail)) {
            throw new IllegalArgumentException("User is blocked");
        }

        //Regla de negocio: no duplicar la billetera por email
        if (walletRepository.existsByOwnerEmail(ownerEmail)) {
            throw new IllegalArgumentException("Wallet already exists for this email");
        }

        Wallet wallet = new Wallet(ownerEmail, initialBalance);
        Wallet save = walletRepository.save(wallet);
        return new WalletResponse(save.getId(), save.getBalance());
    }

    //Depositar dinero en la billetera
    public double deposit(String walletId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Optional<Wallet> found = walletRepository.findById(walletId);

        if (found.isEmpty()) {
            throw new IllegalArgumentException("Wallet not found");
        }

        Wallet wallet = found.get();
        wallet.deposit(amount);

        walletRepository.save(wallet);
        return wallet.getBalance();
    }

    public double withdraw(String walletId, double amount){
        if(amount <= 0){
            throw new IllegalArgumentException("WithDraw amount must be > 0");
        }
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));
        if (wallet.getBalance() < amount){
            throw new IllegalStateException("Insufficient funds");
        }

        wallet.withdraw(amount);;
        walletRepository.save(wallet);

        return wallet.getBalance();
    }
}
