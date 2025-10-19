package kz.pgrlv.springbootpractise.services;

import kz.pgrlv.springbootpractise.persistence.dto.CardDto;
import kz.pgrlv.springbootpractise.persistence.dto.TransferByPhoneDto;
import kz.pgrlv.springbootpractise.persistence.entity.Account;
import kz.pgrlv.springbootpractise.persistence.entity.User;
import kz.pgrlv.springbootpractise.persistence.repository.AccountRepository;
import kz.pgrlv.springbootpractise.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public void openAccount(User user){
        Account account = new Account(user);
        Random random = new Random();

        // Генерация cvv
        Integer cvvNumbers = 100 + random.nextInt(900);
        String cvv = Integer.toString(cvvNumbers);

        // Создание даты истечения
        LocalDate localDate = LocalDate.now().plusYears(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        String expirationDate = localDate.format(formatter);

        // Генерация номера карты
        String cardNumber = generateCardNumber();

        account.setCvv(cvv);
        account.setExpiration(expirationDate);
        account.setCardNumber(cardNumber);

        accountRepository.save(account);
    }

    private String generateCardNumber(){
        Random random = new Random();
        String cardNumber;

        while (true){
            cardNumber  = "4405";
            for (int j = 0; j < 3; j++) {
                Integer fourNumbers = 1000 + random.nextInt(9000);
                cardNumber += Integer.toString(fourNumbers);
            }
            if (!accountRepository.existsByCardNumber(cardNumber)){
                break;
            }
        }

        return cardNumber;
    }

    public void deleteAccountById(Integer id){
        accountRepository.deleteById(id);
    }

    public boolean deposit(CardDto cardDto, Double sum){
        Optional<Account> optionalAccount = accountFromCardDto(cardDto);
        Account account;
        if (optionalAccount.isEmpty()){
            return false;
        }else{
            account = optionalAccount.get();
        }

        Double balance = account.getBalance();
        account.setBalance(balance + sum);
        accountRepository.save(account);
        return true;
    }

    public boolean withdraw(CardDto cardDto, Double sum){
        Optional<Account> optionalAccount = accountFromCardDto(cardDto);
        Account account;
        if (optionalAccount.isEmpty()){
            return false;
        }else{
            account = optionalAccount.get();
        }

        Double balance = account.getBalance();
        Double result = balance - sum;
        if (result < 0){
            return false;
        }
        account.setBalance(result);
        accountRepository.save(account);
        return true;
    }

    @Transactional
    public boolean transferMoneyByPhone(TransferByPhoneDto transferData, User whoTransfer){
        Optional<User> optionalRecipient = userRepository.findUserByPhoneNumber(transferData.getPhoneNumber());
        if (optionalRecipient.isEmpty()){
            return false;
        }
        Account senderAccount = whoTransfer.getAccount();
        Account recipientAccount = optionalRecipient.get().getAccount();

        if(senderAccount.equals(recipientAccount)){
            return false;
        }

        Double value = transferData.getValue();

        Double withdrawResult = senderAccount.getBalance() - value;
        if (withdrawResult < 0){
            return false;
        }
        senderAccount.setBalance(withdrawResult);
        accountRepository.save(senderAccount);

        recipientAccount.setBalance(recipientAccount.getBalance() + value);
        accountRepository.save(recipientAccount);

        return true;
    }

    public void changeNumber(User user, String newNumber){
        user.setPhoneNumber(newNumber);
        userRepository.save(user);
    }

    public Boolean cardCheck(CardDto cardDto){
        Optional<Account> optionalAccount = accountFromCardDto(cardDto);
        Account account;
        // Проверка на существования кода карты
        if (optionalAccount.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public Double getBalanceByCard(CardDto cardDto){
        Optional<Account> optionalAccount = accountFromCardDto(cardDto);
        Account account;
        if (optionalAccount.isEmpty()){
            return -1.0;
        }else{
            account = optionalAccount.get();
        }

        return account.getBalance();
    }

    private Optional<Account> accountFromCardDto(CardDto cardDto){
        Optional<Account> optionalAccount = accountRepository.getAccountByCardNumber(cardDto.getCardNumber());
        Account account;
        // Проверка на существования кода карты
        if (optionalAccount.isEmpty()){
            return Optional.empty();
        }else{
            account = optionalAccount.get();
        }

        // Проверка на соответствие даты и cvv кода
        if (!Objects.equals(cardDto.getExpiration(), account.getExpiration()) || !Objects.equals(cardDto.getCvv(), account.getCvv())){
            return Optional.empty();
        }

        // Проверка, не истек ли срок службы карты
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expirationMonthYear = YearMonth.parse(account.getExpiration(), formatter);
        YearMonth today = YearMonth.now();
        if (expirationMonthYear.isBefore(today)){
            return Optional.empty();
        }

        return Optional.of(account);
    }
}
