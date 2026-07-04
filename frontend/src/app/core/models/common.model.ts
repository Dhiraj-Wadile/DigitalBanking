export interface Account {
  id: number;
  accountNumber: string;
  accountType: string;
  status: string;
  balance: number;
  availableBalance: number;
  holdAmount: number;
  interestRate: number;
  dailyTransactionLimit: number;
  singleTransactionLimit: number;
  ifscCode: string;
  openedDate: string;
  branchName: string;
}

export interface Transaction {
  id: number;
  referenceNumber: string;
  transactionType: string;
  status: string;
  amount: number;
  balanceAfter: number;
  description: string;
  channel: string;
  counterpartyName: string;
  counterpartyAccount: string;
  transactionDate: string;
  accountNumber: string;
}

export interface TransferRequest {
  fromAccountNumber: string;
  toAccountNumber: string;
  amount: number;
  transferType: string;
  description?: string;
  ifscCode?: string;
}

export interface Payment {
  id: number;
  paymentReference: string;
  paymentType: string;
  paymentMethod: string;
  status: string;
  amount: number;
  fee: number;
  tax: number;
  description: string;
  beneficiaryName: string;
  beneficiaryAccount: string;
  paymentDate: string;
  completionDate: string;
  accountNumber: string;
}

export interface Beneficiary {
  id: number;
  name: string;
  accountNumber: string;
  ifscCode: string;
  bankName: string;
  phone: string;
  type: string;
  verified: boolean;
  active: boolean;
  upiId: string;
}

export interface Card {
  id: number;
  cardNumber: string;
  cardType: string;
  cardNetwork: string;
  status: string;
  cardHolderName: string;
  expiryMonth: string;
  expiryYear: string;
  internationalEnabled: boolean;
  onlineEnabled: boolean;
  tapToPayEnabled: boolean;
  isVirtual: boolean;
  accountNumber: string;
}

export interface Loan {
  id: number;
  loanAccountNumber: string;
  loanType: string;
  status: string;
  sanctionedAmount: number;
  outstandingAmount: number;
  interestRate: number;
  tenureMonths: number;
  emiAmount: number;
  processingFee: number;
  applicationDate: string;
  approvalDate: string;
  disbursementDate: string;
  emisPaid: number;
  emisRemaining: number;
  accountNumber: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
