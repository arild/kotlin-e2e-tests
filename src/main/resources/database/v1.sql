-- liquibase formatted sql

-- changeset git:git

create table order_line
(
    id          bigserial primary key,
    price       numeric(19, 2) not null,
    order_id    bigint
);

CREATE TABLE orders
(
    id       bigserial primary key,
    created  timestamp without time zone not null,
    exported timestamp without time zone,
    user_id  bigint not null
);

alter table if exists order_line add constraint fk_orders foreign key (order_id) references orders;
