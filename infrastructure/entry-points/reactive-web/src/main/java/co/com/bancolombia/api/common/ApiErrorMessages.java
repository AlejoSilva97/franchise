package co.com.bancolombia.api.common;

public final class ApiErrorMessages {
    private ApiErrorMessages() {}
    // Franchise
    public static final String FRANCHISE_NAME_REQUIRED = "The franchise name is required.";
    public static final String FRANCHISE_ID_REQUIRED   = "The franchise ID is required in the path.";
    // Branch
    public static final String BRANCH_NAME_REQUIRED    = "The branch name is required.";
    public static final String BRANCH_ID_REQUIRED      = "The branch ID is required in the path.";
    public static final String FRANCHISE_ID_BODY_REQUIRED = "The franchise ID is required.";
    // Product
    public static final String PRODUCT_NAME_REQUIRED   = "The product name is required.";
    public static final String PRODUCT_ID_REQUIRED     = "The product ID is required in the path.";
    public static final String PRODUCT_STOCK_REQUIRED  = "Stock is required and must be zero or greater.";
    public static final String BRANCH_ID_REQUIRED_BODY = "The branch ID is required.";
    // Generic
    public static final String REQUEST_BODY_MISSING    = "The request body is missing.";
}
