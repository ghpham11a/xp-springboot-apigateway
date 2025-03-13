package com.ghpham11a.xp_springboot.controllers;

import com.ghpham11a.xp_springboot.models.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    @GetMapping
    public ResponseEntity<List<Account>> getAll() {
        Random rand = new Random();

        // Create a list to hold our random accounts
        List<Account> accounts = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Account account = new Account();

            // Randomize the ID
            account.setId(rand.nextInt(1000));  // e.g., any ID between 0 and 999

            // Create a random email
            String email = "user" + rand.nextInt(10000) + "@example.com";
            account.setEmail(email);

            // Random Date of Birth (past 10,000 days from today)
            account.setDateOfBirth(LocalDate.now().minusDays(rand.nextInt(10000)));

            // Random account number
            account.setAccountNumber("ACC-" + rand.nextInt(100000));

            // Random balance (0 to 10,000 range)
            BigDecimal balance = BigDecimal.valueOf(rand.nextDouble() * 10000).setScale(2, RoundingMode.HALF_UP);
            account.setBalance(balance);

            // Random createdAt date/time (within the last 30 days, for example)
            account.setCreatedAt(LocalDateTime.now().minusSeconds(rand.nextInt(2_592_000))); // 30 days in seconds

            // Add to the list
            accounts.add(account);
        }

        // Return the list wrapped in a 200 OK response
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable int id) {

        // In a real application, you'd fetch the account from a database, for example:
        // Account account = accountService.findById(id);

        // For demonstration, we'll manually create and populate an Account instance:
        Account account = new Account();
        account.setId(id);
        account.setEmail("demo@example.com");
        account.setDateOfBirth(LocalDate.of(1990, 1, 1));
        account.setAccountNumber("ACC-123456");
        account.setBalance(BigDecimal.valueOf(1000.00));
        account.setCreatedAt(LocalDateTime.now());

        // Return the account wrapped in a 200 OK ResponseEntity
        return ResponseEntity.ok(account);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<String> create(@RequestBody Account account) {
        return ResponseEntity.ok("Creating account " + account.getAccountNumber());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody Account updatedAccount) {
        return ResponseEntity.ok("Updating account " + updatedAccount.getAccountNumber());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        return ResponseEntity.ok("Deleting account " + id);
    }
}
