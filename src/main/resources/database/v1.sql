-- liquibase formatted sql

-- changeset git:git

CREATE TABLE order_line
(
    id          BIGSERIAL PRIMARY KEY,
    price       NUMERIC(19, 2) NOT NULL,
    order_id    BIGINT
);

CREATE TABLE orders
(
    id       BIGSERIAL PRIMARY KEY,
    created  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    exported TIMESTAMP WITHOUT TIME ZONE,
    user_id  BIGINT NOT NULL
);

ALTER TABLE IF EXISTs order_line ADD CONSTRAINT fk_orders FOREIGN KEY (order_id) REFERENCES orders;

ALTER DEFAULT PRIVILEGES FOR USER invoice_admin IN SCHEMA public GRANT SELECT, INSERT, UPDATE ON TABLES TO invoice_admin;
ALTER DEFAULT PRIVILEGES FOR USER invoice_admin IN SCHEMA public GRANT SELECT, UPDATE ON SEQUENCES TO invoice_admin;

GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO invoice_user;
GRANT SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA public TO invoice_user;
