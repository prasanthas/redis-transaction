package au.com.flexisoft.redis;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import au.com.flexisoft.redis.Account;
import au.com.flexisoft.redis.RedisCacheHashOperation;

@Service
public class RedisMultiThreadTransactionRetryTest {

    /**
     * Populate Redis db
     * cash = 11
     * credit = 22
     *
     */

    /**
     * 1st Thread
     * Start a thred
     * Read a key = cash
     * Sleep for 5 sec
     * Update the key = 1.11
     *
     * Expect an Exception
     * Call Retry to update it again
     *
     * */

    /**
     * 2nd Thread
     * Start a thred
     * Read a key = cash
     *
     * Update the key = 2.22
     *
     * */

    private final RedisCacheHashOperation hashOperation;

    public RedisMultiThreadTransactionRetryTest(RedisCacheHashOperation hashOperation) {
        this.hashOperation = hashOperation;
    }

//    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void populateRedis(Account cashAccount, Account creditAccount) throws InterruptedException {
        System.out.println("Populate First value");
        Account value = (Account) hashOperation.getValue(cashAccount.getType(), cashAccount.getKey());

        value.setAmount(creditAccount.getAmount());

        System.err.println("Going to sleep");
        Thread.sleep(10000);
        System.err.println("Woke up from sleep");
        hashOperation.putValue(cashAccount.getType(), cashAccount.getKey(), value);

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void populateRedisSecondCall(Account cashAccount, Account creditAccount)  {
        System.out.println("Populate First value");
        Account value = (Account) hashOperation.getValue(cashAccount.getType(), cashAccount.getKey());

        value.setAmount(199.999992);

        System.err.println("Going to sleep");

        System.err.println("Woke up from sleep");
        hashOperation.putValue(cashAccount.getType(), cashAccount.getKey(), value);

    }

    public void populateRedis() {
        Account cashAccount = Account.builder().id(1).amount(1.111).type("Cash").key("1").build();
        Account creditAccount = Account.builder().id(2).amount(2.222).type("Credit").key("2").build();

        hashOperation.putValue(cashAccount.getType(), cashAccount.getKey(), cashAccount);

        hashOperation.putValue(creditAccount.getType(), creditAccount.getKey(), creditAccount);
    }

//    @Async
    public void printThreadCall(Integer time) throws InterruptedException {
        Thread.sleep(time);
        System.err.println("TIME: "+time+":::"+Thread.currentThread().getName());
    }




    public void readRedis() {
        Account cashAccount = Account.builder().id(1).amount(1.1).type("Cash").key("1").build();
        Account creditAccount = Account.builder().id(2).amount(2.2).type("Credit").key("2").build();

        Account cashAcc = (Account) hashOperation.getValue(cashAccount.getType(), cashAccount.getKey());
        System.out.println(cashAcc);

        Account credAcc = (Account) hashOperation.getValue(creditAccount.getType(), creditAccount.getKey());
        System.out.println(credAcc);

    }



}
