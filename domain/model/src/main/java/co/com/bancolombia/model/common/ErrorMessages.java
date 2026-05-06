package co.com.bancolombia.model.common;

public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String FRANCHISE_NOT_FOUND    = "Franchise not found with ID: ";
    public static final String FRANCHISE_NAME_TOO_SHORT = "Name too short for a franchise";

    public static final String BRANCH_NOT_FOUND       = "Branch not found with ID: ";
    public static final String BRANCH_NAME_TOO_SHORT  = "Name too short for a branch";

    public static final String PRODUCT_NOT_FOUND      = "Product not found with ID: ";
    public static final String PRODUCT_STOCK_NEGATIVE = "Stock cannot be negative";
}
