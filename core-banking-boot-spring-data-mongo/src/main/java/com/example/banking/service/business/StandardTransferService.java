package com.example.banking.service.business;

import org.springframework.stereotype.Service;

import com.example.banking.document.Account;
import com.example.banking.repository.CustomerMongoRepository;
import com.example.banking.service.TransferService;
import com.example.banking.service.business.exception.AccountNotFoundException;
import com.example.banking.service.business.exception.CustomerNotFoundException;

@Service
public class StandardTransferService implements TransferService {
	
	private CustomerMongoRepository customerMongoRepository;

	public StandardTransferService(CustomerMongoRepository customerMongoRepository) {
		this.customerMongoRepository = customerMongoRepository;
	}

	@Override
	public void transfer(String fromIdentity, String fromIban, String toIdentity, String toIban, double amount) {
		
		//sending customer and her/his account check
		
		boolean isSendingCustomerPresent = customerMongoRepository.findById(fromIdentity).isPresent();
		
		if (!isSendingCustomerPresent) {
			throw new CustomerNotFoundException("The sending customer is not found", fromIdentity);
		}
		
		boolean isSendingCustomerAccountPresent = customerMongoRepository.findById(fromIdentity).get()
				.getAccounts().stream().filter(a -> a.getIban().equals(fromIban)).findFirst().isPresent();
		
		if(!isSendingCustomerAccountPresent) {
			throw new AccountNotFoundException("The sending customer's account is not found", fromIban);
		}
		
		//receiving customer and her/his account check
		
		boolean isReceivingCustomerPresent = customerMongoRepository.findById(toIdentity).isPresent();
		
		if (!isReceivingCustomerPresent) {
			throw new CustomerNotFoundException("The receiving customer is not found", toIdentity);
		}
		
		boolean isReceivingCustomerAccountPresent = customerMongoRepository.findById(toIdentity).get()
				.getAccounts().stream().filter(a -> a.getIban().equals(toIban)).findFirst().isPresent();
		
		if(!isReceivingCustomerAccountPresent) {
			throw new AccountNotFoundException("The receiving customer's account is not found", toIban);
		}
		
		//withdraw and deposit operations
		
		Account fromAccount = customerMongoRepository.findById(fromIdentity).get()
						.getAccounts().stream().filter(a -> a.getIban().equals(fromIban)).findFirst().get();
		
		Account toAccount = customerMongoRepository.findById(toIdentity).get()
				.getAccounts().stream().filter(a -> a.getIban().equals(toIban)).findFirst().get();
		
		fromAccount.withdraw(amount);
		toAccount.deposit(amount);
		
	}

}

