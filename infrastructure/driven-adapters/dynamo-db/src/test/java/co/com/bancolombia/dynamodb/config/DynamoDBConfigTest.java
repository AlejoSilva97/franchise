package co.com.bancolombia.dynamodb.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DynamoDBConfigTest {

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    private final DynamoDBConfig dynamoDBConfig = new DynamoDBConfig();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dynamoDBConfig, "region", "us-east-1");
    }

    @Test
    void testDynamoDbAsyncClient() {
        DynamoDbAsyncClient result = dynamoDBConfig.dynamoDbAsyncClient();
        assertNotNull(result);
    }

    @Test
    void testDynamoDbEnhancedAsyncClient() {
        DynamoDbEnhancedAsyncClient result = dynamoDBConfig.dynamoDbEnhancedAsyncClient(dynamoDbAsyncClient);
        assertNotNull(result);
    }
}
