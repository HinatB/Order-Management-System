CREATE TABLE orders (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(64) NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_orders_status ON orders (status);
