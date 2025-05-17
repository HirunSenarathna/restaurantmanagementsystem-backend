CREATE TABLE payments (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          order_id BIGINT,
                          customer_id BIGINT,
                          amount DECIMAL(19,2),
                          status VARCHAR(50),
                          method VARCHAR(50),
                          transaction_id VARCHAR(255),
                          payment_link TEXT,
                          payment_gateway_response TEXT,
                          processed_by BIGINT,
                          receipt_url TEXT,
                          refund_amount DECIMAL(19,2),
                          refund_reason VARCHAR(255),
                          refund_transaction_id VARCHAR(255),
                          refund_response TEXT,
                          created_at DATETIME,
                          updated_at DATETIME
);