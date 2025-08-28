package data0828_01;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TransactionDataGenerator {
    static final int DATA_SIZE = 1_000_000;
    static final String[] ITEM_NAMES = {"Apple", "Banana", "Book", "Pen", "Laptop", "Phone", "Bag", "Shoes", "Watch", "Bottle"};
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        Random rand = new Random();
        try (FileWriter writer = new FileWriter("transactions.csv")) {
            writer.write("TransactionID,Date,CustomerCode,ItemName,Price\n");
            for (int i = 0; i < DATA_SIZE; i++) {
                String transactionId = String.format("T%07d", i + 1); // 交易代碼
                LocalDate date = LocalDate.of(2020 + rand.nextInt(6), 1 + rand.nextInt(12), 1 + rand.nextInt(28));
                String customerCode = String.format("C%06d", rand.nextInt(1000000));
                String itemName = ITEM_NAMES[rand.nextInt(ITEM_NAMES.length)];
                double price = 10 + rand.nextDouble() * 990;
                writer.write(String.format("%s,%s,%s,%s,%.2f\n", transactionId, date.format(DATE_FORMAT), customerCode, itemName, price));
            }
            System.out.println("交易資料已產生並儲存至 transactions.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
