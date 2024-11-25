package de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.tab.kontos;

import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.entity.Account;
import de.tud.inf.mmt.wmscrape.gui.tabs.portfoliomanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public void save(Account account) {
        accountRepository.save(account);
    }
}
