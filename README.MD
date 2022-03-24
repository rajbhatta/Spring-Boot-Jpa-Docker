# What is located inside Project? #
## 1: Database script ##
- Located inside Database folder

## 2: Postman script ##
- Postman GET and POST APIs to test different scenario is located inside Postman folder.

## 3: UI script ##
- UI created with VueJs is located inside vue-ui folder.


## 4: REST API created with Spring Boot, Jpa and MySQL ##
- REST API created with Spring Boot, Jpa and MSQL.
- Docker file contains the script for creating and running docke image.


# 5. Source code details #
## 5.1 Source code for API server ##
```java
package com.test.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestapiApplication.class, args);
    }

}

```

```java
package com.test.restapi.service;

import ClientBalanceException;

/**
 *
 * @param <T> generic object
 *  Validate balance and balance with quantity
 */
public interface ValidationService<T, U> {
    public boolean checkBalance(T t, U u) throws ClientBalanceException;

    public boolean checkBalanceWithQuantity(T t, U u) throws ClientBalanceException;

    public boolean checkBalanceWithAmount(double totalBalance, double purchaseAmount);
}

```

```java
package com.test.restapi.service;

import GiftCardException;

public interface GiftService<T> {
    public void processGiftTransaction(T t) throws GiftCardException;
}

```

```java
package com.test.restapi.service;

import ClientException;
import Client;
import ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public List<Client> get() {
        List<Client> clientsList = new ArrayList<Client>();
        clientRepository.findAll().forEach(client -> clientsList.add(client));
        return clientsList;
    }

    public Client getClientById(int clientId) throws ClientException {
        Optional<Client> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            return clientOptional.get();
        }
        throw new ClientException("UNABLE TO GET CLIENT BY [" + clientId + "]");
    }

}
```

```java
package com.test.restapi.service;

import ClientBalanceException;
import ClientBalance;
import Purchase;
import com.test.restapi.service.ClientBalanceService;
import com.test.restapi.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiftPurchaseValidationService implements ValidationService<Purchase, Integer> {

    @Autowired
    ClientBalanceService clientBalanceService;

    @Override
    public boolean checkBalance(Purchase purchase, Integer clientId) throws ClientBalanceException {

        ClientBalance clientBalance = clientBalanceService.getBalanceById(clientId);
        if (Double.compare(clientBalance.getBalance(), purchase.getAmount()) == 0) {
            return false;
        } else if (Double.compare(clientBalance.getBalance(), purchase.getAmount()) < 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean checkBalanceWithQuantity(Purchase purchase, Integer clientId) throws ClientBalanceException {

        ClientBalance clientBalance = clientBalanceService.getBalanceById(clientId);
        double totalCost = purchase.getAmount() * purchase.getQuantity();

        if (Double.compare(clientBalance.getBalance(), totalCost) == 0) {
            return false;
        } else if (Double.compare(clientBalance.getBalance(), totalCost) < 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkBalanceWithAmount(double totalBalance, double purchaseAmount) {
        if (Double.compare(totalBalance, purchaseAmount) == 0) {
            return false;
        } else if (Double.compare(totalBalance, purchaseAmount) < 0) {
            return false;
        } else {
            return true;
        }
    }

}
```

```java
package com.test.restapi.service;

import ClientBalanceException;
import ClientException;
import GiftCardException;
import com.test.restapi.modal.*;
import com.test.restapi.repository.ClientTransactionRepository;
import com.test.restapi.service.ClientBalanceService;
import com.test.restapi.service.ClientService;
import com.test.restapi.service.GiftPurchaseValidationService;
import com.test.restapi.service.GiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ClientGiftTransactionServiceV2 implements GiftService<Gift> {

    private AtomicInteger atomicInteger;

    public ClientGiftTransactionServiceV2() {
        atomicInteger = new AtomicInteger(1);
    }

    @Autowired
    GiftPurchaseValidationService giftPurchaseValidationService;

    @Autowired
    ClientBalanceService clientBalanceService;


    @Autowired
    ClientTransactionRepository clientTransactionRepository;

    @Autowired
    ClientService clientService;

    private static int generatedId = 0;

    @Override
    public void processGiftTransaction(Gift gift) throws GiftCardException {
        try {
            int transactionCounter = atomicInteger.incrementAndGet();
            HashMap<String, ClientTranaction> linkedHashMap = new LinkedHashMap<>();
            Client client = clientService.getClientById(gift.getClientId());

            double clientBalance = getLatestClientBalance(client);

            gift.getTotalPurchase().stream().forEach((purchase) -> {
                try {
                    processPurchase(purchase, gift, transactionCounter, linkedHashMap, clientBalance, client);
                } catch (ClientBalanceException | ClientException e) {
                    e.printStackTrace();
                }
            });

            //write map to database as a batch
            //writeTransactionLedgerBatch(linkedHashMap);

        } catch (Exception ex) {
            throw new GiftCardException("UNABLE TO PROCESS GIFT CARD FOR [" + gift.getClientId() + "]");
        }


    }

    private void processPurchase(Purchase purchase,
                                 Gift gift,
                                 int transactionCounter,
                                 HashMap<String, ClientTranaction> mapClient,
                                 double clientBalance,
                                 Client client) throws ClientBalanceException, ClientException {

        ClientTranaction processedClientTransaction = getLastRecordFromHashMap(mapClient);
        if (processedClientTransaction == null) {
            //Validate balance
            if (giftPurchaseValidationService.checkBalanceWithAmount(clientBalance, purchase.getAmount() * purchase.getQuantity())) {
                double remindBalance = clientBalance - purchase.getQuantity() * purchase.getAmount();
                ClientTranaction clientTransaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client);
                mapClient.put("transaction" + transactionCounter, new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client));
                writeTransactionLedger(clientTransaction);
            }
        } else {
            double remindBalance = processedClientTransaction.getRemindBalance() - purchase.getQuantity() * purchase.getAmount();
            //validate balance
            if (giftPurchaseValidationService.checkBalanceWithAmount(remindBalance, purchase.getAmount() * purchase.getQuantity())) {
                ClientTranaction clientTransaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client);
                mapClient.put("transaction" + transactionCounter, new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), remindBalance, purchase.getQuantity(), client));
                writeTransactionLedger(clientTransaction);
            }
        }
    }

    private ClientTranaction getLastRecordFromHashMap(HashMap<String, ClientTranaction> clientTransactionMap) {
        if (clientTransactionMap != null) {
            ClientTranaction clientTranaction = null;
            for (Map.Entry<String, ClientTranaction> entry : clientTransactionMap.entrySet()) {
                clientTranaction = entry.getValue();
            }
            return clientTranaction;
        }
        return null;
    }

    public ClientTranaction getLastTransactionRecordByClient(Client client) {
        List<ClientTranaction> clientTransactionsList = clientTransactionRepository.findByClient(client);
        if (clientTransactionsList.size() >= 1) {
            //Last record contains the latest transaction value
            return clientTransactionsList.get(clientTransactionsList.size() - 1);
        }
        return null;
    }

    private Double getLatestClientBalance(Client client) throws ClientBalanceException {

        //If previous transaction is detected then get value from transaction table.
        ClientTranaction clientTransactionRecord = getLastTransactionRecordByClient(client);

        if (clientTransactionRecord == null) {
            //If no transaction then return from client balance table
            ClientBalance clientBalance = clientBalanceService.getBalanceById(client.getId());
            return clientBalance.getBalance();
        } else {
            return clientTransactionRecord.getRemindBalance();
        }

    }

    private int writeTransactionLedger(ClientTranaction clientTranaction) {
        clientTransactionRepository.save(clientTranaction);
        return clientTranaction.getClientTransactionId();
    }

    private void writeTransactionLedgerBatch(HashMap<String, ClientTranaction> linkedHashMap) {
        for (var entry : linkedHashMap.entrySet()) {
            ClientTranaction clientTranaction = entry.getValue();
            clientTransactionRepository.save(clientTranaction);
        }
    }

}
```

```java
package com.test.restapi.service;

import ClientBalanceException;
import ClientException;
import GiftCardException;
import com.test.restapi.modal.*;
import com.test.restapi.repository.ClientTransactionRepository;
import com.test.restapi.service.ClientBalanceService;
import com.test.restapi.service.ClientService;
import com.test.restapi.service.GiftPurchaseValidationService;
import com.test.restapi.service.GiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientGiftTransactionService implements GiftService<Gift> {

    @Autowired
    GiftPurchaseValidationService giftPurchaseValidationService;

    @Autowired
    ClientBalanceService clientBalanceService;


    @Autowired
    ClientTransactionRepository clientTransactionRepository;

    @Autowired
    ClientService clientService;

    private static int generatedId = 0;

    @Override
    public void processGiftTransaction(Gift gift) throws GiftCardException {
        try {
            gift.getTotalPurchase().stream().forEach((purchase) -> {
                try {
                    processPurchase(purchase, gift);
                } catch (ClientBalanceException | ClientException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            throw new GiftCardException("UNABLE TO PROCESS GIFT CARD FOR [" + gift.getClientId() + "]");
        }

    }

    private void processPurchase(Purchase purchase, Gift gift) throws ClientBalanceException, ClientException {

        ClientBalance clientBalance = clientBalanceService.getBalanceById(gift.getClientId());
        Client client = clientService.getClientById(gift.getClientId());

        if (clientBalance != null) {
            /**
             * First round generatedId will be 0. Thus, client balance is checked with amount*quantity.
             */
            if (generatedId == 0) {
                if (giftPurchaseValidationService.checkBalanceWithAmount(clientBalance.getBalance(), purchase.getAmount() * purchase.getQuantity())) {
                    double dueAmount = (clientBalance.getBalance() - purchase.getAmount() * purchase.getQuantity());
                    ClientTranaction clientTranaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), dueAmount, purchase.getQuantity(), client);
                    generatedId = writeTransactionLedger(clientTranaction);
                }
            } else {
                /**
                 *
                 */
                double remainingClientBalance = provideLastInsertedClientTransactionRecord(generatedId).getRemindBalance();
                double newDueAmount = (remainingClientBalance - purchase.getAmount() * purchase.getQuantity());
                if (giftPurchaseValidationService.checkBalanceWithAmount(remainingClientBalance, purchase.getAmount() * purchase.getQuantity())) {
                    ClientTranaction clientTranaction = new ClientTranaction(purchase.getQuantity(), purchase.getAmount(), newDueAmount, purchase.getQuantity(), client);
                    generatedId = writeTransactionLedger(clientTranaction);
                }
            }
        }
    }

    private ClientTranaction provideLastInsertedClientTransactionRecord(int clientTransactionId) {
        Optional<ClientTranaction> clientTranactionOptional = clientTransactionRepository.findById(clientTransactionId);
        if (clientTranactionOptional.isPresent()) {
            return clientTranactionOptional.get();
        }
        return null;
    }

    private int writeTransactionLedger(ClientTranaction clientTranaction) {
        clientTransactionRepository.save(clientTranaction);
        return clientTranaction.getClientTransactionId();
    }

}
```

```java
package com.test.restapi.service;

import ClientBalanceException;
import ClientBalance;
import ClientBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientBalanceService {

    @Autowired
    ClientBalanceRepository clientBalanceRepository;

    public List<ClientBalance> get() {
        List<ClientBalance> clientBalanceList = new ArrayList<ClientBalance>();
        clientBalanceRepository.findAll().forEach(clientBalance -> clientBalanceList.add(clientBalance));
        return clientBalanceList;
    }

    public ClientBalance getBalanceById(int clientId) throws ClientBalanceException {
        Optional<ClientBalance> clientOptional = clientBalanceRepository.findById(clientId);
        if (clientOptional.isPresent()) {
            return clientOptional.get();
        }
        throw new ClientBalanceException("UNABLE TO GET BALANCE ID for [" + clientId + "]");
    }

}
```

```java
package com.test.restapi.repository;

import ClientBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientBalanceRepository extends JpaRepository<ClientBalance, Integer> {
}
```

```java
package com.test.restapi.repository;

import Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

}
```

```java
package com.test.restapi.repository;

import Client;
import ClientTranaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientTransactionRepository extends JpaRepository<ClientTranaction, Integer> {

    List<ClientTranaction> findByClient(Client client);

}
```

```java
package com.test.restapi.modal;

import javax.persistence.*;

@Entity
@Table(name = "tbl_client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    public Client() {
    }

    public Client(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
```

```java
package com.test.restapi.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.restapi.modal.Client;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tbl_client_balance")
public class ClientBalance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int clientBalanceId;

    @Column(name = "balance")
    private double balance;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Client client;

    public int getClientBalanceId() {
        return clientBalanceId;
    }

    public void setClientBalanceId(int clientBalanceId) {
        this.clientBalanceId = clientBalanceId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
```

```java
package com.test.restapi.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.restapi.modal.Client;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "tbl_client_transaction")
public class ClientTranaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int clientTransactionId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "amount")
    private double amount;

    @Column(name = "remindBalance")
    private double remindBalance;

    @Column(name = "gift")
    private int gift;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private Client client;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;


    public ClientTranaction() {
    }

    public ClientTranaction(int quantity, double amount, double remindBalance, int gift, Client client) {
        this.quantity = quantity;
        this.amount = amount;
        this.remindBalance = remindBalance;
        this.gift = gift;
        this.client = client;
    }

    public int getClientTransactionId() {
        return clientTransactionId;
    }

    public void setClientTransactionId(int clientTransactionId) {
        this.clientTransactionId = clientTransactionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRemindBalance() {
        return remindBalance;
    }

    public void setRemindBalance(double remindBalance) {
        this.remindBalance = remindBalance;
    }

    public int getGift() {
        return gift;
    }

    public void setGift(int gift) {
        this.gift = gift;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
```

```java
package com.test.restapi.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.test.restapi.modal.Purchase;

import java.io.Serializable;
import java.util.List;

public class Gift implements Serializable {

    @JsonProperty(value = "client_id")
    private int clientId;

    @JsonProperty(value = "purchase")
    private List<Purchase> totalPurchase;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public List<Purchase> getTotalPurchase() {
        return totalPurchase;
    }

    public void setTotalPurchase(List<Purchase> totalPurchase) {
        this.totalPurchase = totalPurchase;
    }
}
```

```java
package com.test.restapi.modal;

import java.io.Serializable;

public class Purchase implements Serializable {
    private double amount;
    private int quantity;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
```

```java
package com.test.restapi.exception;

public class ClientBalanceException extends Exception{
    public ClientBalanceException() {
    }

    public ClientBalanceException(String message) {
        super(message);
    }

    public ClientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientBalanceException(Throwable cause) {
        super(cause);
    }

    public ClientBalanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
```

```java

package com.test.restapi.exception;

public class GiftCardException extends Exception{
    public GiftCardException() {
    }

    public GiftCardException(String message) {
        super(message);
    }

    public GiftCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public GiftCardException(Throwable cause) {
        super(cause);
    }

    public GiftCardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
```

```java
package com.test.restapi.exception;

public class ClientException extends Exception{
    public ClientException() {
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
```

```java
package com.test.restapi.controller;

import Gift;
import ClientGiftTransactionServiceV2;
import GiftPurchaseValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/v1/api-service")
public class GiftcardController {

    @Autowired
    GiftPurchaseValidationService giftPurchaseValidationService;

    //@Autowired
    //ClientGiftTransactionService clientGiftTransactionService;

    @Autowired
    ClientGiftTransactionServiceV2 clientGiftTransactionService;

    @PostMapping("/buy-gift")
    public ResponseEntity<Gift> buyGift(@RequestBody Gift gift) {
        try {
            clientGiftTransactionService.processGiftTransaction(gift);
            return new ResponseEntity<>(gift, HttpStatus.OK);
        } catch (Exception ee) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

}
```

```java
package com.test.restapi.controller;

import ClientException;
import Client;
import ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/v1/test-service")
public class ClientController {

    @Autowired
    ClientService clientService;

    @GetMapping("/clients")
    public ResponseEntity<List<Client>> getClients() {
        if (clientService.get() == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(clientService.get(), HttpStatus.OK);
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable("id") int id) {
        try {
            Client client = clientService.getClientById(id);
            return new ResponseEntity<>(client, HttpStatus.OK);
        } catch (ClientException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
```

```java

spring.datasource.url= jdbc:mysql://localhost:3306/test_db?useSSL=false
spring.datasource.username= root
spring.datasource.password=
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.MySQL5InnoDBDialect

spring.jpa.hibernate.ddl-auto= update


## RUNNING HTTP PORT ON 8085
server.port=8085

```

```java
FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

# expose port of docker
EXPOSE 8085

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```
