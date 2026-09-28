CREATE TABLE IF NOT EXISTS product (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1200),
    price DECIMAL(38, 2) NOT NULL,
    stock INT NOT NULL,
    category VARCHAR(255),
    size VARCHAR(255),
    image_url VARCHAR(255),
    active BIT NOT NULL,
    created_at DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS store_orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    customer_phone VARCHAR(255),
    customer_document VARCHAR(255),
    shipping_address VARCHAR(1200),
    shipping_zip_code VARCHAR(255),
    shipping_street VARCHAR(255),
    shipping_number VARCHAR(255),
    shipping_complement VARCHAR(255),
    shipping_neighborhood VARCHAR(255),
    shipping_city VARCHAR(255),
    shipping_state VARCHAR(255),
    total DECIMAL(38, 2),
    status VARCHAR(30),
    mercado_pago_preference_id VARCHAR(255),
    checkout_url VARCHAR(1200),
    payment_id VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT,
    product_id BIGINT,
    product_name VARCHAR(255),
    quantity INT,
    unit_price DECIMAL(38, 2),
    subtotal DECIMAL(38, 2),
    PRIMARY KEY (id),
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id)
        REFERENCES store_orders (id),
    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id)
        REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_product_active_created_at ON product (active, created_at);
CREATE INDEX idx_store_orders_created_at ON store_orders (created_at);
CREATE INDEX idx_order_item_order_id ON order_item (order_id);
CREATE INDEX idx_order_item_product_id ON order_item (product_id);
