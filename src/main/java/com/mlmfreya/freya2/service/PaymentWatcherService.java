package com.mlmfreya.ferya2.service;
import com.mlmfreya.ferya2.model.PaymentRequest;
import com.mlmfreya.ferya2.model.Transaction;
import com.mlmfreya.ferya2.model.User;
import com.mlmfreya.ferya2.repository.PaymentRequestRepository;
import com.mlmfreya.ferya2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PaymentWatcherService {
    // Inject necessary services
    private final UserService userService;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final EmailService emailService; // You'll need a service for sending emails

    @Autowired
    private TronWebService tronWebService;

    private final PaymentRequestRepository paymentRequestRepository;

    public PaymentWatcherService(UserService userService,
                                 UserRepository userRepository,
                                 TransactionService transactionService,
                                 EmailService emailService,
                                 PaymentRequestRepository paymentRequestRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.paymentRequestRepository = paymentRequestRepository;
    }

    public void watchPayment(String walletAddress, BigDecimal amount) {
        // Start a new thread to watch for incoming payments
        new Thread(() -> {
            boolean paymentReceived = false;
            while (!paymentReceived) {
                try {
                    BigDecimal balance = tronWebService.checkWalletBalance(walletAddress).divide(BigDecimal.valueOf(1000000)) ;
                    if (balance.compareTo(amount) >= 0) {
                        processPayment(walletAddress, amount);
                        paymentReceived = true;
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    System.out.println("Error while checking wallet balance, retrying in 60 seconds...");

                    try {
                        Thread.sleep(60000); // sleep for 60 seconds before checking again
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    continue; // continue with the next loop iteration
                }

                try {
                    Thread.sleep(5000); // sleep for 5 seconds before checking again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void processPayment(String walletAddress, BigDecimal amount) {
        PaymentRequest paymentRequest = paymentRequestRepository.findPaymentRequestByWalletAddress(walletAddress);
        User user = new User();
        user.setPassword(paymentRequest.getPassword());
        user.setEmail(paymentRequest.getEmail());
        user.setFullName(paymentRequest.getName());
        user.setTronWalletAddress(paymentRequest.getWalletAddress());

        User newUser;
        if (paymentRequest.getParentId() == null) {
            // Register the user directly if parentId is null
            newUser = userService.registerUser(user);
        } else {
            User parent = userRepository.findById(paymentRequest.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent user not found"));

            // Determine the position
            String position;
            if (parent.getLeftChild() == null) {
                position = "LEFT";
            } else if (parent.getRightChild() == null) {
                position = "RIGHT";
            } else {
                throw new IllegalArgumentException("Parent user already has two children");
            }

            // Register the user with parent and position
            newUser = userService.registerUser(user, parent, position);
        }

        // Add the purchased package to the user's account
        userService.addPackageToUser(newUser, paymentRequest.getInvestmentPackage(), amount);

        // Send a welcome email to the user
        emailService.sendWelcomeEmail(user);

        // Update the transaction status
        transactionService.updateTransactionStatus(paymentRequest.getWalletAddress(), Transaction.Status.complete);
    }


}
