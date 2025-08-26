package controller;

import java.util.Scanner;

import service.TransactionService;

public class TransactionController {

	private final Scanner scanner;
	private final TransactionService transactionService;

	public TransactionController(Scanner scanner) {
		this.scanner = scanner;
		this.transactionService = new TransactionService();
	}
}
