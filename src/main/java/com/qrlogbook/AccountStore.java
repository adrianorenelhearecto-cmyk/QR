package com.qrlogbook;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

/** Local demo account store. Passwords are PBKDF2-hashed; data stays on this device. */
public final class AccountStore {
    public record Account(String id, String role, String name, String email, String grade, String section) {}
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;
    private final Path file;
    private final Properties data = new Properties();
    private final SecureRandom random = new SecureRandom();

    public AccountStore() {
        this(Path.of(System.getProperty("user.home"), ".qr-logbook", "accounts.properties"));
    }

    AccountStore(Path file) {
        this.file = file;
        load();
        if (!data.containsKey("accounts.count")) seedDemoAccounts();
    }

    private synchronized void load() {
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file)) { data.load(reader); }
        catch (IOException e) { throw new IllegalStateException("Could not load local account data: " + e.getMessage(), e); }
    }

    private synchronized void persist() {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                data.store(writer, "QR Logbook local demo account data");
            }
        } catch (IOException e) { throw new IllegalStateException("Could not save local account data: " + e.getMessage(), e); }
    }

    private void seedDemoAccounts() {
        addAccount("admin", "admin", "System Administrator", "admin@example.com", "", "", "adminpassword");
        addAccount("student", "02000422020", "Rene Lhea Adriano", "juan.delacruz@example.com", "Grade 11", "STEM-A", "studentpassword");
        addAccount("teacher", "TCH-001", "Maria Santos", "maria.santos@example.com", "", "", "teacherpassword");
    }

    public synchronized List<Account> accounts(String role) {
        List<Account> result = new ArrayList<>();
        int count = Integer.parseInt(data.getProperty("accounts.count", "0"));
        for (int i = 0; i < count; i++) {
            String p = "account." + i + ".";
            String id = data.getProperty(p + "id", "");
            if (id.isEmpty()) continue;
            String accountRole = data.getProperty(p + "role", "student");
            if (role == null || role.equals(accountRole)) {
                result.add(new Account(id, accountRole, data.getProperty(p + "name", ""),
                        data.getProperty(p + "email", ""), data.getProperty(p + "grade", ""),
                        data.getProperty(p + "section", "")));
            }
        }
        result.sort(Comparator.comparing(Account::name, String.CASE_INSENSITIVE_ORDER));
        return result;
    }

    public synchronized boolean authenticate(String role, String id, char[] password) {
        int index = findIndex(role, id);
        if (index < 0) return false;
        String p = "account." + index + ".";
        try {
            byte[] salt = Base64.getDecoder().decode(data.getProperty(p + "salt", ""));
            byte[] expected = Base64.getDecoder().decode(data.getProperty(p + "hash", ""));
            return slowHash(password, salt).length == expected.length
                    && java.security.MessageDigest.isEqual(slowHash(password, salt), expected);
        } catch (IllegalArgumentException ex) { return false; }
    }

    public synchronized boolean verifyRecovery(String role, String id, String email) {
        int index = findIndex(role, id);
        return index >= 0 && !email.isBlank()
                && data.getProperty("account." + index + ".email", "").trim().equalsIgnoreCase(email.trim());
    }

    public synchronized void resetPassword(String role, String id, char[] password) {
        int index = findIndex(role, id);
        if (index < 0) throw new IllegalArgumentException("Account not found.");
        setPassword(index, password);
        persist();
    }

    public synchronized void addStudent(String id, String name, String email, String grade, String section, char[] tempPassword) {
        if (findIndex("student", id) >= 0) throw new IllegalArgumentException("That student number already exists.");
        addAccount("student", id, name, email, grade, section, new String(tempPassword));
    }

    public synchronized void updateStudent(String oldId, String id, String name, String email, String grade, String section) {
        int index = findIndex("student", oldId);
        if (index < 0) throw new IllegalArgumentException("Student account no longer exists.");
        int duplicate = findIndex("student", id);
        if (duplicate >= 0 && duplicate != index) throw new IllegalArgumentException("That student number already exists.");
        String p = "account." + index + ".";
        data.setProperty(p + "id", id.trim()); data.setProperty(p + "name", name.trim());
        data.setProperty(p + "email", email.trim()); data.setProperty(p + "grade", grade.trim());
        data.setProperty(p + "section", section.trim()); persist();
    }

    public synchronized void deleteStudent(String id) {
        int index = findIndex("student", id);
        if (index < 0) return;
        int count = Integer.parseInt(data.getProperty("accounts.count", "0"));
        for (int i = index; i < count - 1; i++) copyAccount(i + 1, i);
        clearAccount(count - 1); data.setProperty("accounts.count", String.valueOf(count - 1)); persist();
    }

    public synchronized long count(String role) { return accounts(role).size(); }

    private void copyAccount(int from, int to) {
        String a = "account." + from + ".", b = "account." + to + ".";
        for (String field : List.of("id", "role", "name", "email", "grade", "section", "salt", "hash")) {
            String value = data.getProperty(a + field);
            if (value != null) data.setProperty(b + field, value); else data.remove(b + field);
        }
    }

    private void clearAccount(int i) {
        String p = "account." + i + ".";
        for (String field : List.of("id", "role", "name", "email", "grade", "section", "salt", "hash")) data.remove(p + field);
    }

    private int findIndex(String role, String id) {
        if (id == null) return -1;
        int count = Integer.parseInt(data.getProperty("accounts.count", "0"));
        for (int i = 0; i < count; i++) {
            String p = "account." + i + ".";
            if (id.trim().equalsIgnoreCase(data.getProperty(p + "id", ""))
                    && role.equalsIgnoreCase(data.getProperty(p + "role", ""))) return i;
        }
        return -1;
    }

    private void addAccount(String role, String id, String name, String email, String grade, String section, String password) {
        int i = Integer.parseInt(data.getProperty("accounts.count", "0"));
        String p = "account." + i + ".";
        data.setProperty(p + "id", id.trim()); data.setProperty(p + "role", role);
        data.setProperty(p + "name", name.trim()); data.setProperty(p + "email", email.trim());
        data.setProperty(p + "grade", grade.trim()); data.setProperty(p + "section", section.trim());
        data.setProperty("accounts.count", String.valueOf(i + 1)); setPassword(i, password.toCharArray()); persist();
    }

    private void setPassword(int index, char[] password) {
        byte[] salt = new byte[16]; random.nextBytes(salt);
        String p = "account." + index + ".";
        data.setProperty(p + "salt", Base64.getEncoder().encodeToString(salt));
        data.setProperty(p + "hash", Base64.getEncoder().encodeToString(slowHash(password, salt)));
        Arrays.fill(password, '\0');
    }

    private static byte[] slowHash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BITS);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (Exception e) { throw new IllegalStateException("Password hashing is unavailable.", e); }
        finally { spec.clearPassword(); }
    }
}
