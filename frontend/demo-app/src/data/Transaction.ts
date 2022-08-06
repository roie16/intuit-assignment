export interface Transaction {
    latitude: number;
    longitude: number;
    pass: boolean;
    review: boolean;
    product: string;
    severity: string;
    country: string;
}


export interface TransactionWithTimeout {
    transaction: Transaction;
    timeout: number;
}