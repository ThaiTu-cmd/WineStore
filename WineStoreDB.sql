SET NAMES utf8mb4;
DROP DATABASE IF EXISTS `WineStore`;
CREATE DATABASE `WineStore` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `WineStore`;

-- =====================================================
-- PHẦN 1: SCHEMA
-- =====================================================

CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL,
    `phone` VARCHAR(20) NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` ENUM('CUSTOMER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    `status` ENUM('ACTIVE', 'INACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE',
    `email_verified_at` DATETIME NULL,
    `deleted_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`),
    UNIQUE KEY `uk_users_phone` (`phone`),
    KEY `idx_users_role_status` (`role`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_addresses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `type` ENUM('SHIPPING', 'BILLING') NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NULL,
    `address_line1` VARCHAR(255) NOT NULL,
    `address_line2` VARCHAR(255) NULL,
    `city` VARCHAR(100) NOT NULL,
    `province` VARCHAR(100) NOT NULL,
    `country` VARCHAR(100) NOT NULL DEFAULT 'Vietnam',
    `postal_code` VARCHAR(20) NULL,
    `is_default` TINYINT(1) NOT NULL DEFAULT 0,
    `deleted_at` DATETIME NULL,
    `default_user_guard` BIGINT GENERATED ALWAYS AS (
        CASE WHEN `is_default` = 1 AND `deleted_at` IS NULL THEN `user_id` ELSE NULL END
    ) STORED,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_addresses_default_one_per_user` (`default_user_guard`),
    UNIQUE KEY `uk_user_addresses_id_user` (`id`, `user_id`),
    KEY `idx_user_addresses_user` (`user_id`, `type`, `deleted_at`),
    CONSTRAINT `fk_addr_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `payment_methods` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `payment_type` ENUM('CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'E_WALLET', 'CASH_ON_DELIVERY') NOT NULL,
    `provider` VARCHAR(50) NULL,
    `account_name` VARCHAR(100) NULL,
    `last_4_digits` VARCHAR(4) NULL,
    `token_ref` VARCHAR(255) NULL,
    `is_default` TINYINT(1) NOT NULL DEFAULT 0,
    `deleted_at` DATETIME NULL,
    `default_user_guard` BIGINT GENERATED ALWAYS AS (
        CASE WHEN `is_default` = 1 AND `deleted_at` IS NULL THEN `user_id` ELSE NULL END
    ) STORED,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_methods_default_one_per_user` (`default_user_guard`),
    UNIQUE KEY `uk_payment_methods_id_user` (`id`, `user_id`),
    KEY `idx_payment_methods_user` (`user_id`, `payment_type`, `deleted_at`),
    CONSTRAINT `fk_pm_user`
        FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `parent_id` BIGINT NULL,
    `name` VARCHAR(100) NOT NULL,
    `slug` VARCHAR(100) NOT NULL,
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    `deleted_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_categories_slug` (`slug`),
    KEY `idx_categories_parent` (`parent_id`),
    CONSTRAINT `fk_categories_parent`
        FOREIGN KEY (`parent_id`) REFERENCES `categories`(`id`)
        ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `category_id` BIGINT NULL,
    `sku` VARCHAR(50) NOT NULL,
    `slug` VARCHAR(150) NOT NULL,
    `name` VARCHAR(200) NOT NULL,
    `short_description` VARCHAR(500) NULL,
    `description` TEXT NULL,
    `price` DECIMAL(15,2) NOT NULL,
    `old_price` DECIMAL(15,2) NULL,
    `rating_avg` DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    `rating_count` INT NOT NULL DEFAULT 0,
    `stock_quantity` INT NOT NULL DEFAULT 0,
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    `deleted_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_products_sku` (`sku`),
    UNIQUE KEY `uk_products_slug` (`slug`),
    KEY `idx_products_category_active` (`category_id`, `is_active`, `deleted_at`),
    CONSTRAINT `chk_products_stock_quantity` CHECK (`stock_quantity` >= 0),
    CONSTRAINT `chk_products_price_non_negative` CHECK (`price` >= 0),
    CONSTRAINT `chk_products_old_price_non_negative` CHECK (`old_price` IS NULL OR `old_price` >= 0),
    CONSTRAINT `fk_product_category`
        FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
        ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `product_images` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `image_url` VARCHAR(255) NOT NULL,
    `sort_order` INT NOT NULL DEFAULT 0,
    `is_primary` TINYINT(1) NOT NULL DEFAULT 0,
    `primary_product_guard` BIGINT GENERATED ALWAYS AS (
        CASE WHEN `is_primary` = 1 THEN `product_id` ELSE NULL END
    ) STORED,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_images_primary_one_per_product` (`primary_product_guard`),
    KEY `idx_product_images_product` (`product_id`, `sort_order`),
    CONSTRAINT `fk_image_product`
        FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
        ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `product_tags` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL,
    `display_name` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_tags_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `discount_codes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL,
    `discount_type` ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL,
    `discount_value` DECIMAL(15,2) NOT NULL,
    `min_order_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `max_discount_amount` DECIMAL(15,2) NULL,
    `per_user_limit` INT NULL,
    `global_limit` INT NULL,
    `used_count` INT NOT NULL DEFAULT 0,
    `start_at` DATETIME NULL,
    `end_at` DATETIME NULL,
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_discount_codes_code` (`code`),
    CONSTRAINT `chk_discount_codes_value_non_negative` CHECK (`discount_value` >= 0),
    CONSTRAINT `chk_discount_codes_used_count_non_negative` CHECK (`used_count` >= 0),
    CONSTRAINT `chk_discount_codes_limits_valid` CHECK (`per_user_limit` IS NULL OR `per_user_limit` > 0),
    CONSTRAINT `chk_discount_codes_global_limit_valid` CHECK (`global_limit` IS NULL OR `global_limit` > 0),
    CONSTRAINT `chk_discount_codes_dates_valid` CHECK (`end_at` IS NULL OR `start_at` IS NULL OR `end_at` >= `start_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `shipping_methods` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `code` VARCHAR(50) NOT NULL,
    `fee` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_shipping_methods_code` (`code`),
    CONSTRAINT `chk_shipping_methods_fee_non_negative` CHECK (`fee` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `order_code` VARCHAR(50) NOT NULL,
    `shipping_address_id` BIGINT NULL,
    `payment_method_id` BIGINT NULL,
    `shipping_method_id` BIGINT NULL,
    `discount_code_id` BIGINT NULL,
    `recipient_name` VARCHAR(100) NOT NULL,
    `recipient_phone` VARCHAR(20) NOT NULL,
    `shipping_address_line1` VARCHAR(255) NOT NULL,
    `shipping_address_line2` VARCHAR(255) NULL,
    `shipping_city` VARCHAR(100) NOT NULL,
    `shipping_province` VARCHAR(100) NOT NULL,
    `shipping_country` VARCHAR(100) NOT NULL DEFAULT 'Vietnam',
    `shipping_postal_code` VARCHAR(20) NULL,
    `subtotal` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `discount_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `shipping_fee` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `total_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `status` ENUM('PENDING', 'PAID', 'PROCESSING', 'SHIPPED', 'COMPLETED', 'CANCELED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    `payment_status` ENUM('UNPAID', 'PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'UNPAID',
    `note` VARCHAR(500) NULL,
    `placed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `paid_at` DATETIME NULL,
    `canceled_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_orders_order_code` (`order_code`),
    KEY `idx_orders_user_status_created` (`user_id`, `status`, `created_at`),
    KEY `idx_orders_discount_code` (`discount_code_id`),
    CONSTRAINT `chk_orders_money_non_negative` CHECK (`subtotal` >= 0 AND `discount_amount` >= 0 AND `shipping_fee` >= 0 AND `total_amount` >= 0),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_order_addr_owner` FOREIGN KEY (`shipping_address_id`, `user_id`) REFERENCES `user_addresses`(`id`, `user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_order_pm_owner` FOREIGN KEY (`payment_method_id`, `user_id`) REFERENCES `payment_methods`(`id`, `user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_order_sm` FOREIGN KEY (`shipping_method_id`) REFERENCES `shipping_methods`(`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
    CONSTRAINT `fk_order_dc` FOREIGN KEY (`discount_code_id`) REFERENCES `discount_codes`(`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `order_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `product_name` VARCHAR(200) NOT NULL,
    `product_sku` VARCHAR(50) NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(15,2) NOT NULL,
    `line_total` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_items_order_product` (`order_id`, `product_id`),
    KEY `idx_order_items_product` (`product_id`),
    CONSTRAINT `chk_order_items_quantity` CHECK (`quantity` > 0),
    CONSTRAINT `chk_order_items_unit_price_non_negative` CHECK (`unit_price` >= 0),
    CONSTRAINT `chk_order_items_line_total_non_negative` CHECK (`line_total` >= 0),
    CONSTRAINT `fk_oi_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fk_oi_product` FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `discount_usages` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `discount_code_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `order_id` BIGINT NOT NULL,
    `used_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_discount_usages_order_once` (`order_id`),
    KEY `idx_discount_usages_code_user` (`discount_code_id`, `user_id`),
    CONSTRAINT `fk_du_code` FOREIGN KEY (`discount_code_id`) REFERENCES `discount_codes`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_du_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_du_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `order_status_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `old_status` VARCHAR(20) NULL,
    `new_status` VARCHAR(20) NOT NULL,
    `changed_note` VARCHAR(255) NULL,
    `changed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_status_history_order` (`order_id`, `changed_at`),
    CONSTRAINT `fk_osh_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `payments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `provider` VARCHAR(50) NULL,
    `payment_method` VARCHAR(50) NOT NULL,
    `transaction_code` VARCHAR(100) NULL,
    `amount` DECIMAL(15,2) NOT NULL,
    `currency` CHAR(3) NOT NULL DEFAULT 'VND',
    `status` ENUM('PENDING', 'AUTHORIZED', 'PAID', 'FAILED', 'REFUNDED', 'CANCELED') NOT NULL DEFAULT 'PENDING',
    `paid_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payments_transaction_code` (`transaction_code`),
    KEY `idx_payments_order_status` (`order_id`, `status`),
    CONSTRAINT `chk_payments_amount_non_negative` CHECK (`amount` >= 0),
    CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `stock_movements` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `reason` ENUM('ORDER', 'RETURN', 'ADJUSTMENT', 'PURCHASE', 'DAMAGED') NOT NULL,
    `reference_type` ENUM('ORDER', 'RETURN', 'MANUAL', 'PURCHASE') NULL,
    `reference_id` BIGINT NULL,
    `note` VARCHAR(255) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_stock_movements_product_created` (`product_id`, `created_at`),
    CONSTRAINT `fk_sm_product` FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `reviews` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `rating` INT NOT NULL,
    `comment` TEXT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uc_user_product` (`user_id`, `product_id`),
    KEY `idx_reviews_product` (`product_id`, `created_at`),
    CONSTRAINT `chk_reviews_rating` CHECK (`rating` BETWEEN 1 AND 5),
    CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PHẦN 2: DỮ LIỆU MẪU
-- =====================================================

INSERT INTO `categories` 
  (`id`, `parent_id`, `name`, `slug`, `is_active`, `created_at`, `updated_at`)
VALUES
  (1, NULL, 'Rượu Vang & Rượu Nhẹ', 'ruou-vang-ruou-nhe', 1, NOW(), NOW()),
  (2, NULL, 'Whiskey', 'whiskey', 1, NOW(), NOW()),
  (3, NULL, 'Vodka', 'vodka', 1, NOW(), NOW()),
  (4, NULL, 'Gin', 'gin', 1, NOW(), NOW()),
  (5, NULL, 'Rum', 'rum', 1, NOW(), NOW()),
  (6, NULL, 'Bia Thủ Công', 'bia-thu-cong', 1, NOW(), NOW());

INSERT INTO `products`
  (`id`, `category_id`, `sku`, `slug`, `name`, `short_description`, `description`,
   `price`, `old_price`, `rating_avg`, `rating_count`, `stock_quantity`,
   `is_active`, `created_at`)
VALUES
  (1, 1, 'RUOU-01-0001', 'château-lumière-cabernet-sauvignon-2019', 'Château Lumière Cabernet Sauvignon 2019', 'Château Lumière Cabernet Sauvignon 2019 | 750ml | 13.5% | Pháp', 'Château Lumière Cabernet Sauvignon 2019 dung tích 750ml, nồng độ 13.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   220000, NULL, 0.00, 0, 134,
   1, '2026-03-31 00:00:00'),
  (2, 1, 'RUOU-01-0002', 'château-lumière-cabernet-sauvignon-2022', 'Château Lumière Cabernet Sauvignon 2022', 'Château Lumière Cabernet Sauvignon 2022 | 750ml | 13.5% | Pháp', 'Château Lumière Cabernet Sauvignon 2022 dung tích 750ml, nồng độ 13.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1240000, NULL, 0.00, 0, 152,
   1, '2026-01-13 00:00:00'),
  (3, 1, 'RUOU-01-0003', 'château-lumière-merlot-reserve', 'Château Lumière Merlot Reserve', 'Château Lumière Merlot Reserve | 750ml | 14.0% | Pháp', 'Château Lumière Merlot Reserve dung tích 750ml, nồng độ 14.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1080000, NULL, 0.00, 0, 40,
   1, '2026-02-11 00:00:00'),
  (4, 1, 'RUOU-01-0004', 'island-sailor-merlot-reserve-2020', 'Island Sailor Merlot Reserve 2020', 'Island Sailor Merlot Reserve 2020 | 750ml | 14.0% | Jamaica', 'Island Sailor Merlot Reserve 2020 dung tích 750ml, nồng độ 14.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   880000, NULL, 0.00, 0, 63,
   1, '2026-03-27 00:00:00'),
  (5, 1, 'RUOU-01-0005', 'golden-barrel-chardonnay-2018', 'Golden Barrel Chardonnay 2018', 'Golden Barrel Chardonnay 2018 | 750ml | 12.5% | Ireland', 'Golden Barrel Chardonnay 2018 dung tích 750ml, nồng độ 12.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   770000, NULL, 0.00, 0, 160,
   1, '2026-03-12 00:00:00'),
  (6, 1, 'RUOU-01-0006', 'andes-reserve-chardonnay-2019', 'Andes Reserve Chardonnay 2019', 'Andes Reserve Chardonnay 2019 | 750ml | 12.5% | Argentina', 'Andes Reserve Chardonnay 2019 dung tích 750ml, nồng độ 12.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1250000, NULL, 0.00, 0, 0,
   1, '2026-04-29 00:00:00'),
  (7, 1, 'RUOU-01-0007', 'golden-barrel-rose-dry', 'Golden Barrel Rosé Dry', 'Golden Barrel Rosé Dry | 750ml | 12.0% | Ireland', 'Golden Barrel Rosé Dry dung tích 750ml, nồng độ 12.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   490000, NULL, 0.00, 0, 104,
   1, '2026-01-05 00:00:00'),
  (8, 1, 'RUOU-01-0008', 'highland-crown-rose-dry-2018', 'Highland Crown Rosé Dry 2018', 'Highland Crown Rosé Dry 2018 | 750ml | 12.0% | Scotland', 'Highland Crown Rosé Dry 2018 dung tích 750ml, nồng độ 12.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1050000, NULL, 0.00, 0, 19,
   1, '2026-01-13 00:00:00'),
  (9, 1, 'RUOU-01-0009', 'oak-valley-sparkling-brut-2022', 'Oak Valley Sparkling Brut 2022', 'Oak Valley Sparkling Brut 2022 | 750ml | 11.5% | Mỹ', 'Oak Valley Sparkling Brut 2022 dung tích 750ml, nồng độ 11.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1160000, NULL, 0.00, 0, 122,
   1, '2026-01-18 00:00:00'),
  (10, 1, 'RUOU-01-0010', 'château-lumière-sparkling-brut-2023', 'Château Lumière Sparkling Brut 2023', 'Château Lumière Sparkling Brut 2023 | 750ml | 11.5% | Pháp', 'Château Lumière Sparkling Brut 2023 dung tích 750ml, nồng độ 11.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1300000, NULL, 0.00, 0, 52,
   1, '2026-04-09 00:00:00'),
  (11, 2, 'RUOU-02-0011', 'andes-reserve-single-malt-12-years', 'Andes Reserve Single Malt 12 Years', 'Andes Reserve Single Malt 12 Years | 700ml | 40.0% | Argentina', 'Andes Reserve Single Malt 12 Years dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   890000, NULL, 0.00, 0, 43,
   1, '2026-03-14 00:00:00'),
  (12, 2, 'RUOU-02-0012', 'highland-crown-single-malt-12-years', 'Highland Crown Single Malt 12 Years', 'Highland Crown Single Malt 12 Years | 700ml | 40.0% | Scotland', 'Highland Crown Single Malt 12 Years dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   2670000, NULL, 0.00, 0, 101,
   0, '2026-02-27 00:00:00'),
  (13, 2, 'RUOU-02-0013', 'sakura-malt-bourbon-classic', 'Sakura Malt Bourbon Classic', 'Sakura Malt Bourbon Classic | 700ml | 43.0% | Nhật Bản', 'Sakura Malt Bourbon Classic dung tích 700ml, nồng độ 43.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   2610000, NULL, 0.00, 0, 19,
   1, '2026-04-10 00:00:00'),
  (14, 2, 'RUOU-02-0014', 'château-lumière-bourbon-classic', 'Château Lumière Bourbon Classic', 'Château Lumière Bourbon Classic | 700ml | 43.0% | Pháp', 'Château Lumière Bourbon Classic dung tích 700ml, nồng độ 43.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   2450000, NULL, 0.00, 0, 139,
   1, '2026-04-12 00:00:00'),
  (15, 2, 'RUOU-02-0015', 'sakura-malt-blended-whisky', 'Sakura Malt Blended Whisky', 'Sakura Malt Blended Whisky | 700ml | 40.0% | Nhật Bản', 'Sakura Malt Blended Whisky dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   950000, NULL, 0.00, 0, 30,
   0, '2026-02-18 00:00:00'),
  (16, 2, 'RUOU-02-0016', 'island-sailor-blended-whisky', 'Island Sailor Blended Whisky', 'Island Sailor Blended Whisky | 700ml | 40.0% | Jamaica', 'Island Sailor Blended Whisky dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1440000, NULL, 0.00, 0, 159,
   1, '2026-04-23 00:00:00'),
  (17, 2, 'RUOU-02-0017', 'nord-frost-irish-whiskey', 'Nord Frost Irish Whiskey', 'Nord Frost Irish Whiskey | 700ml | 40.0% | Nga', 'Nord Frost Irish Whiskey dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1070000, NULL, 0.00, 0, 100,
   0, '2026-01-26 00:00:00'),
  (18, 2, 'RUOU-02-0018', 'island-sailor-irish-whiskey', 'Island Sailor Irish Whiskey', 'Island Sailor Irish Whiskey | 700ml | 40.0% | Jamaica', 'Island Sailor Irish Whiskey dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   2580000, NULL, 0.00, 0, 11,
   1, '2026-04-17 00:00:00'),
  (19, 2, 'RUOU-02-0019', 'château-lumière-japanese-malt', 'Château Lumière Japanese Malt', 'Château Lumière Japanese Malt | 700ml | 43.0% | Pháp', 'Château Lumière Japanese Malt dung tích 700ml, nồng độ 43.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1920000, NULL, 0.00, 0, 81,
   1, '2026-01-06 00:00:00'),
  (20, 2, 'RUOU-02-0020', 'blue-harbor-japanese-malt', 'Blue Harbor Japanese Malt', 'Blue Harbor Japanese Malt | 700ml | 43.0% | Anh', 'Blue Harbor Japanese Malt dung tích 700ml, nồng độ 43.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1590000, NULL, 0.00, 0, 137,
   1, '2026-03-13 00:00:00'),
  (21, 3, 'RUOU-03-0021', 'monte-rosa-original-vodka', 'Monte Rosa Original Vodka', 'Monte Rosa Original Vodka | 700ml | 40.0% | Ý', 'Monte Rosa Original Vodka dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   780000, NULL, 0.00, 0, 32,
   1, '2026-03-17 00:00:00'),
  (22, 3, 'RUOU-03-0022', 'saigon-craft-co.-original-vodka', 'Saigon Craft Co. Original Vodka', 'Saigon Craft Co. Original Vodka | 700ml | 40.0% | Việt Nam', 'Saigon Craft Co. Original Vodka dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   340000, NULL, 0.00, 0, 13,
   1, '2026-04-25 00:00:00'),
  (23, 3, 'RUOU-03-0023', 'andes-reserve-citrus-vodka', 'Andes Reserve Citrus Vodka', 'Andes Reserve Citrus Vodka | 700ml | 37.5% | Argentina', 'Andes Reserve Citrus Vodka dung tích 700ml, nồng độ 37.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   260000, NULL, 0.00, 0, 75,
   1, '2026-03-02 00:00:00'),
  (24, 3, 'RUOU-03-0024', 'highland-crown-citrus-vodka', 'Highland Crown Citrus Vodka', 'Highland Crown Citrus Vodka | 700ml | 37.5% | Scotland', 'Highland Crown Citrus Vodka dung tích 700ml, nồng độ 37.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   720000, NULL, 0.00, 0, 42,
   0, '2026-04-02 00:00:00'),
  (25, 3, 'RUOU-03-0025', 'casa-rivera-premium-vodka', 'Casa Rivera Premium Vodka', 'Casa Rivera Premium Vodka | 1000ml | 40.0% | Chile', 'Casa Rivera Premium Vodka dung tích 1000ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   350000, NULL, 0.00, 0, 132,
   1, '2026-02-06 00:00:00'),
  (26, 3, 'RUOU-03-0026', 'saigon-craft-co.-premium-vodka', 'Saigon Craft Co. Premium Vodka', 'Saigon Craft Co. Premium Vodka | 1000ml | 40.0% | Việt Nam', 'Saigon Craft Co. Premium Vodka dung tích 1000ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   870000, NULL, 0.00, 0, 105,
   1, '2026-03-06 00:00:00'),
  (27, 3, 'RUOU-03-0027', 'sakura-malt-berry-vodka', 'Sakura Malt Berry Vodka', 'Sakura Malt Berry Vodka | 700ml | 37.5% | Nhật Bản', 'Sakura Malt Berry Vodka dung tích 700ml, nồng độ 37.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   780000, NULL, 0.00, 0, 98,
   1, '2026-03-11 00:00:00'),
  (28, 3, 'RUOU-03-0028', 'nord-frost-berry-vodka', 'Nord Frost Berry Vodka', 'Nord Frost Berry Vodka | 700ml | 37.5% | Nga', 'Nord Frost Berry Vodka dung tích 700ml, nồng độ 37.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   740000, NULL, 0.00, 0, 94,
   1, '2026-01-04 00:00:00'),
  (29, 4, 'RUOU-04-0029', 'saigon-craft-co.-london-dry-gin', 'Saigon Craft Co. London Dry Gin', 'Saigon Craft Co. London Dry Gin | 700ml | 40.0% | Việt Nam', 'Saigon Craft Co. London Dry Gin dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1290000, NULL, 0.00, 0, 18,
   1, '2026-01-29 00:00:00'),
  (30, 4, 'RUOU-04-0030', 'nord-frost-london-dry-gin', 'Nord Frost London Dry Gin', 'Nord Frost London Dry Gin | 700ml | 40.0% | Nga', 'Nord Frost London Dry Gin dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   680000, NULL, 0.00, 0, 30,
   1, '2026-04-05 00:00:00'),
  (31, 4, 'RUOU-04-0031', 'island-sailor-botanical-gin', 'Island Sailor Botanical Gin', 'Island Sailor Botanical Gin | 700ml | 42.0% | Jamaica', 'Island Sailor Botanical Gin dung tích 700ml, nồng độ 42.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   880000, NULL, 0.00, 0, 91,
   1, '2026-01-30 00:00:00'),
  (32, 4, 'RUOU-04-0032', 'island-sailor-botanical-gin-Schottland', 'Island Sailor Botanical Gin', 'Island Sailor Botanical Gin | 700ml | 46.0% | Jamaica', 'Island Sailor Botanical Gin dung tích 700ml, nồng độ 46.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   460000, NULL, 0.00, 0, 122,
   1, '2026-03-15 00:00:00'),
  (33, 4, 'RUOU-04-0033', 'andes-reserve-pink-gin', 'Andes Reserve Pink Gin', 'Andes Reserve Pink Gin | 700ml | 37.5% | Argentina', 'Andes Reserve Pink Gin dung tích 700ml, nồng độ 37.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   560000, NULL, 0.00, 0, 129,
   1, '2026-01-25 00:00:00'),
  (34, 4, 'RUOU-04-0034', 'oak-valley-pink-gin', 'Oak Valley Pink Gin', 'Oak Valley Pink Gin | 700ml | 37.5% | Mỹ', 'Oak Valley Pink Gin dung tích 700ml, nồng độ 37.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   500000, NULL, 0.00, 0, 69,
   0, '2026-03-09 00:00:00'),
  (35, 4, 'RUOU-04-0035', 'château-lumière-navy-strength-gin', 'Château Lumière Navy Strength Gin', 'Château Lumière Navy Strength Gin | 700ml | 57.0% | Pháp', 'Château Lumière Navy Strength Gin dung tích 700ml, nồng độ 57.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   550000, NULL, 0.00, 0, 39,
   1, '2026-03-21 00:00:00'),
  (36, 4, 'RUOU-04-0036', 'saigon-craft-co.-navy-strength-gin', 'Saigon Craft Co. Navy Strength Gin', 'Saigon Craft Co. Navy Strength Gin | 700ml | 57.0% | Việt Nam', 'Saigon Craft Co. Navy Strength Gin dung tích 700ml, nồng độ 57.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   480000, NULL, 0.00, 0, 94,
   0, '2026-03-20 00:00:00'),
  (37, 5, 'RUOU-05-0037', 'island-sailor-white-rum', 'Island Sailor White Rum', 'Island Sailor White Rum | 700ml | 37.5% | Jamaica', 'Island Sailor White Rum dung tích 700ml, nồng độ 37.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1150000, NULL, 0.00, 0, 103,
   0, '2026-02-20 00:00:00'),
  (38, 5, 'RUOU-05-0038', 'nord-frost-white-rum', 'Nord Frost White Rum', 'Nord Frost White Rum | 700ml | 37.5% | Nga', 'Nord Frost White Rum dung tích 700ml, nồng độ 37.5%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   500000, NULL, 0.00, 0, 91,
   0, '2026-02-11 00:00:00'),
  (39, 5, 'RUOU-05-0039', 'château-lumière-dark-rum', 'Château Lumière Dark Rum', 'Château Lumière Dark Rum | 700ml | 40.0% | Pháp', 'Château Lumière Dark Rum dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1190000, NULL, 0.00, 0, 18,
   1, '2026-03-11 00:00:00'),
  (40, 5, 'RUOU-05-0040', 'blue-harbor-dark-rum', 'Blue Harbor Dark Rum', 'Blue Harbor Dark Rum | 700ml | 40.0% | Anh', 'Blue Harbor Dark Rum dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   840000, NULL, 0.00, 0, 146,
   1, '2026-03-15 00:00:00'),
  (41, 5, 'RUOU-05-0041', 'golden-barrel-spiced-rum', 'Golden Barrel Spiced Rum', 'Golden Barrel Spiced Rum | 700ml | 35.0% | Ireland', 'Golden Barrel Spiced Rum dung tích 700ml, nồng độ 35.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   640000, NULL, 0.00, 0, 8,
   1, '2026-02-16 00:00:00'),
  (42, 5, 'RUOU-05-0042', 'monte-rosa-spiced-rum', 'Monte Rosa Spiced Rum', 'Monte Rosa Spiced Rum | 700ml | 35.0% | Ý', 'Monte Rosa Spiced Rum dung tích 700ml, nồng độ 35.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   640000, NULL, 0.00, 0, 21,
   1, '2026-04-29 00:00:00'),
  (43, 5, 'RUOU-05-0043', 'saigon-craft-co.-aged-rum-8-years', 'Saigon Craft Co. Aged Rum 8 Years', 'Saigon Craft Co. Aged Rum 8 Years | 700ml | 40.0% | Việt Nam', 'Saigon Craft Co. Aged Rum 8 Years dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   1080000, NULL, 0.00, 0, 74,
   0, '2026-04-05 00:00:00'),
  (44, 5, 'RUOU-05-0044', 'blue-harbor-aged-rum-8-years', 'Blue Harbor Aged Rum 8 Years', 'Blue Harbor Aged Rum 8 Years | 700ml | 40.0% | Anh', 'Blue Harbor Aged Rum 8 Years dung tích 700ml, nồng độ 40.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   540000, NULL, 0.00, 0, 141,
   1, '2026-02-16 00:00:00'),
  (45, 6, 'RUOU-06-0045', 'blue-harbor-ipa', 'Blue Harbor IPA', 'Blue Harbor IPA | 330ml | 6.2% | Anh', 'Blue Harbor IPA dung tích 330ml, nồng độ 6.2%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   45000, NULL, 0.00, 0, 99,
   1, '2026-03-20 00:00:00'),
  (46, 6, 'RUOU-06-0046', 'island-sailor-ipa', 'Island Sailor IPA', 'Island Sailor IPA | 330ml | 6.2% | Jamaica', 'Island Sailor IPA dung tích 330ml, nồng độ 6.2%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   55000, NULL, 0.00, 0, 97,
   1, '2026-02-19 00:00:00'),
  (47, 6, 'RUOU-06-0047', 'blue-harbor-pale-ale', 'Blue Harbor Pale Ale', 'Blue Harbor Pale Ale | 330ml | 5.4% | Anh', 'Blue Harbor Pale Ale dung tích 330ml, nồng độ 5.4%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   75000, NULL, 0.00, 0, 98,
   1, '2026-01-10 00:00:00'),
  (48, 6, 'RUOU-06-0048', 'saigon-craft-co.-pale-ale', 'Saigon Craft Co. Pale Ale', 'Saigon Craft Co. Pale Ale | 330ml | 5.4% | Việt Nam', 'Saigon Craft Co. Pale Ale dung tích 330ml, nồng độ 5.4%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   115000, NULL, 0.00, 0, 91,
   1, '2026-04-19 00:00:00'),
  (49, 6, 'RUOU-06-0049', 'saigon-craft-co.-stout', 'Saigon Craft Co. Stout', 'Saigon Craft Co. Stout | 330ml | 6.8% | Việt Nam', 'Saigon Craft Co. Stout dung tích 330ml, nồng độ 6.8%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   125000, NULL, 0.00, 0, 26,
   1, '2026-03-09 00:00:00'),
  (50, 6, 'RUOU-06-0050', 'island-sailor-stout', 'Island Sailor Stout', 'Island Sailor Stout | 330ml | 6.8% | Jamaica', 'Island Sailor Stout dung tích 330ml, nồng độ 6.8%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   125000, NULL, 0.00, 0, 95,
   0, '2026-01-24 00:00:00'),
  (51, 6, 'RUOU-06-0051', 'island-sailor-wheat-beer', 'Island Sailor Wheat Beer', 'Island Sailor Wheat Beer | 330ml | 5.0% | Jamaica', 'Island Sailor Wheat Beer dung tích 330ml, nồng độ 5.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   85000, NULL, 0.00, 0, 153,
   0, '2026-03-11 00:00:00'),
  (52, 6, 'RUOU-06-0052', 'blue-harbor-wheat-beer', 'Blue Harbor Wheat Beer', 'Blue Harbor Wheat Beer | 330ml | 5.0% | Anh', 'Blue Harbor Wheat Beer dung tích 330ml, nồng độ 5.0%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   115000, NULL, 0.00, 0, 27,
   1, '2026-02-17 00:00:00'),
  (53, 6, 'RUOU-06-0053', 'blue-harbor-lager', 'Blue Harbor Lager', 'Blue Harbor Lager | 330ml | 4.8% | Anh', 'Blue Harbor Lager dung tích 330ml, nồng độ 4.8%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   145000, NULL, 0.00, 0, 131,
   1, '2026-03-21 00:00:00'),
  (54, 6, 'RUOU-06-0054', 'saigon-craft-co.-lager', 'Saigon Craft Co. Lager', 'Saigon Craft Co. Lager | 330ml | 4.8% | Việt Nam', 'Saigon Craft Co. Lager dung tích 330ml, nồng độ 4.8%, phù hợp cho trang chi tiết sản phẩm mẫu.',
   75000, NULL, 0.00, 0, 125,
   1, '2026-04-17 00:00:00');

INSERT INTO `product_images`
  (`id`, `product_id`, `image_url`, `sort_order`, `is_primary`)
VALUES
  (1, 1, 'https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=600&q=80&fit=crop', 1, 1),
  (2, 2, 'https://images.unsplash.com/photo-1553361371-9b22f78e8b1d?w=600&q=80&fit=crop', 1, 1),
  (3, 3, 'https://images.unsplash.com/photo-1474722883778-792e7990302f?w=600&q=80&fit=crop', 1, 1),
  (4, 4, 'https://images.unsplash.com/photo-1552620896-a6ac30992d63?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D', 1, 1),
  (5, 5, 'https://images.unsplash.com/photo-1611571940159-425a28706d6f?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (6, 6, 'https://plus.unsplash.com/premium_photo-1698086426853-b3d45e9c0cbf?w=1000&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8Y2hhcmRvbm5heXxlbnwwfHwwfHx8MA%3D%3D', 1, 1),
  (7, 7, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80&fit=crop', 1, 1),
  (8, 8, 'https://images.unsplash.com/photo-1660814807174-85a4ce3af9ea?w=1000&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Um9zJUMzJUE5JTIwRHJ5fGVufDB8fDB8fHww', 1, 1),
  (9, 9, 'https://plus.unsplash.com/premium_photo-1665949503006-f82ca4b7e132?w=1000&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8U3BhcmtsaW5nJTIwQnJ1dCUyMDIwMjJ8ZW58MHx8MHx8fDA%3D', 1, 1),
  (10, 10, 'https://plus.unsplash.com/premium_photo-1677327746215-6d9411e306f1?w=1000&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8c3BhcmtsaW5nJTIwd2luZXxlbnwwfHwwfHx8MA%3D%3D', 1, 1),
  (11, 11, 'https://images.unsplash.com/photo-1569529465841-dfecdab7503b?w=600&q=80&fit=crop', 1, 1),
  (12, 12, 'https://images.unsplash.com/photo-1527281400683-1aae777175f8?w=600&q=80&fit=crop', 1, 1),
  (13, 13, 'https://images.unsplash.com/photo-1532634922-8fe0b757fb13?w=600&q=80&fit=crop', 1, 1),
  (14, 14, 'https://images.unsplash.com/photo-1681040900989-645cecfd8ea4?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (15, 15, 'https://images.unsplash.com/photo-1569529465841-dfecdab7503b?w=600&q=80&fit=crop', 1, 1),
  (16, 16, 'https://6a1c01260bc623d413b0e4f8.imgix.net/saurav-vyas-S8FEMWGQuhc-unsplash.jpg', 1, 1),
  (17, 17, 'https://images.unsplash.com/photo-1569529465841-dfecdab7503b?w=600&q=80&fit=crop', 1, 1),
  (18, 18, 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=600&q=80&fit=crop', 1, 1),
  (19, 19, 'https://images.unsplash.com/photo-1602166242292-93a00e63e8e8?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (20, 20, 'https://images.unsplash.com/photo-1595505467869-8cb257b13be2?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (21, 21, 'https://images.unsplash.com/photo-1591704951890-0862b2e98acb?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (22, 22, 'https://images.unsplash.com/photo-1645784125144-4c06a561fc58?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (23, 23, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600&q=80&fit=crop', 1, 1),
  (24, 24, 'https://images.unsplash.com/photo-1571204829887-3b8d69e4094d?w=600&q=80&fit=crop', 1, 1),
  (25, 25, 'https://images.unsplash.com/photo-1539606494565-02e568638d91?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (26, 26, 'https://images.unsplash.com/photo-1650477021184-6fe8ef5b846e?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (27, 27, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600&q=80&fit=crop', 1, 1),
  (28, 28, 'https://images.unsplash.com/photo-1571204829887-3b8d69e4094d?w=600&q=80&fit=crop', 1, 1),
  (29, 29, 'https://images.unsplash.com/photo-1563630440878-c25af103219a?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (30, 30, 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=600&q=80&fit=crop', 1, 1),
  (31, 31, 'https://images.unsplash.com/photo-1598934475133-46b0ee0b1476?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (32, 32, 'https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=600&q=80&fit=crop', 1, 1),
  (33, 33, 'https://images.unsplash.com/photo-1542895324-076f3fb6c2cb?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (34, 34, 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=600&q=80&fit=crop', 1, 1),
  (35, 35, 'https://images.unsplash.com/photo-1585409944718-79b9463b5c0b?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (36, 36, 'https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=600&q=80&fit=crop', 1, 1),
  (37, 37, 'https://images.unsplash.com/photo-1571204829887-3b8d69e4094d?w=600&q=80&fit=crop', 1, 1),
  (38, 38, 'https://images.unsplash.com/photo-1613140506142-277c6241b858?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (39, 39, 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=600&q=80&fit=crop', 1, 1),
  (40, 40, 'https://images.unsplash.com/photo-1569529465841-dfecdab7503b?w=600&q=80&fit=crop', 1, 1),
  (41, 41, 'https://images.unsplash.com/photo-1571204829887-3b8d69e4094d?w=600&q=80&fit=crop', 1, 1),
  (42, 42, 'https://images.unsplash.com/photo-1583552188783-709c90220745?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (43, 43, 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=600&q=80&fit=crop', 1, 1),
  (44, 44, 'https://images.unsplash.com/photo-1569529465841-dfecdab7503b?w=600&q=80&fit=crop', 1, 1),
  (45, 45, 'https://images.unsplash.com/photo-1558642452-9d2a7deb7f62?w=600&q=80&fit=crop', 1, 1),
  (46, 46, 'https://images.unsplash.com/photo-1608270586620-248524c67de9?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (47, 47, 'https://images.unsplash.com/photo-1618183479302-1e0aa382c36b?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (48, 48, 'https://images.unsplash.com/photo-1600788907416-456578634209?w=600&q=80&fit=crop', 1, 1),
  (49, 49, 'https://images.unsplash.com/photo-1623274545361-63e9d95e4643?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (50, 50, 'https://images.unsplash.com/photo-1608270586620-248524c67de9?w=600&q=80&fit=crop', 1, 1),
  (51, 51, 'https://images.unsplash.com/photo-1636391945755-4e260dd880cb?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (52, 52, 'https://images.unsplash.com/photo-1558642452-9d2a7deb7f62?w=600&q=80&fit=crop', 1, 1),
  (53, 53, 'https://images.unsplash.com/photo-1687771454203-97d0b08bbeb2?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1),
  (54, 54, 'https://images.unsplash.com/photo-1594035900144-17151c9910af?auto=format&fit=crop&fm=jpg&ixlib=rb-4.1.0&q=80&w=600', 1, 1);
-- =====================================================
-- 6. PROCEDURE VÀ TRIGGER
-- =====================================================

DELIMITER $$

-- =====================================================
-- PROCEDURE sp_refresh_product_rating
-- Mục đích:
-- - Tính lại rating_avg và rating_count của một sản phẩm.
--
-- Khi nào được gọi:
-- - Sau khi thêm review.
-- - Sau khi sửa review.
-- - Sau khi xóa review.
--
-- Cách hoạt động:
-- - Lấy tất cả review của product_id.
-- - Tính AVG(rating) làm rating_avg.
-- - Đếm số review làm rating_count.
-- - Nếu không còn review thì rating_avg = 0 và rating_count = 0.
-- =====================================================
CREATE PROCEDURE `sp_refresh_product_rating`(IN p_product_id BIGINT)
BEGIN
    UPDATE `products` p
    LEFT JOIN (
        SELECT
            `product_id`,
            ROUND(AVG(`rating`), 2) AS avg_val,
            COUNT(*) AS cnt_val
        FROM `reviews`
        WHERE `product_id` = p_product_id
        GROUP BY `product_id`
    ) r ON p.`id` = r.`product_id`
    SET
        p.`rating_avg` = COALESCE(r.`avg_val`, 0.00),
        p.`rating_count` = COALESCE(r.`cnt_val`, 0)
    WHERE p.`id` = p_product_id;
END$$

-- =====================================================
-- PROCEDURE sp_refresh_order_totals
-- Mục đích:
-- - Tính lại subtotal và total_amount của một đơn hàng.
--
-- Khi nào được gọi:
-- - Sau khi thêm order_items.
-- - Sau khi sửa order_items.
-- - Sau khi xóa order_items.
--
-- Cách hoạt động:
-- - subtotal = tổng line_total của các dòng hàng.
-- - total_amount = subtotal - discount_amount + shipping_fee.
-- - GREATEST(..., 0) để tránh tổng tiền bị âm.
-- =====================================================
CREATE PROCEDURE `sp_refresh_order_totals`(IN p_order_id BIGINT)
BEGIN
    UPDATE `orders` o
    LEFT JOIN (
        SELECT `order_id`, COALESCE(SUM(`line_total`), 0.00) AS subtotal_val
        FROM `order_items`
        WHERE `order_id` = p_order_id
        GROUP BY `order_id`
    ) x ON o.`id` = x.`order_id`
    SET
        o.`subtotal` = COALESCE(x.`subtotal_val`, 0.00),
        o.`total_amount` = GREATEST(COALESCE(x.`subtotal_val`, 0.00) - o.`discount_amount` + o.`shipping_fee`, 0.00)
    WHERE o.`id` = p_order_id;
END$$

-- =====================================================
-- NHÓM TRIGGER reviews
--
-- tr_reviews_after_insert:
-- - Khi thêm review mới, tự cập nhật lại điểm trung bình của sản phẩm.
--
-- tr_reviews_after_update:
-- - Khi sửa review, tự cập nhật lại điểm trung bình.
-- - Nếu review bị chuyển sang product_id khác, cập nhật lại cả sản phẩm cũ và sản phẩm mới.
--
-- tr_reviews_after_delete:
-- - Khi xóa review, tự cập nhật lại điểm trung bình của sản phẩm.
--
-- Vai trò:
-- - Giữ products.rating_avg và products.rating_count luôn đồng bộ với bảng reviews.
-- =====================================================
CREATE TRIGGER `tr_reviews_after_insert`
AFTER INSERT ON `reviews`
FOR EACH ROW
BEGIN
    CALL `sp_refresh_product_rating`(NEW.`product_id`);
END$$

CREATE TRIGGER `tr_reviews_after_update`
AFTER UPDATE ON `reviews`
FOR EACH ROW
BEGIN
    CALL `sp_refresh_product_rating`(NEW.`product_id`);
    IF OLD.`product_id` <> NEW.`product_id` THEN
        CALL `sp_refresh_product_rating`(OLD.`product_id`);
    END IF;
END$$

CREATE TRIGGER `tr_reviews_after_delete`
AFTER DELETE ON `reviews`
FOR EACH ROW
BEGIN
    CALL `sp_refresh_product_rating`(OLD.`product_id`);
END$$

--- ==========================================================
-- 2. LOGIC MỚI: TRỪ KHO NGAY KHI VỪA THÊM SẢN PHẨM VÀO ĐƠN
-- ==========================================================

-- A. Trước khi thêm sản phẩm vào đơn: Check xem kho đủ không
CREATE TRIGGER `tr_order_items_before_insert`
BEFORE INSERT ON `order_items`
FOR EACH ROW
BEGIN
    DECLARE v_current_stock INT;
    
    -- Tính thành tiền
    SET NEW.`line_total` = NEW.`quantity` * NEW.`unit_price`;
    
    -- Lấy tồn kho hiện tại
    SELECT `stock_quantity` INTO v_current_stock 
    FROM `products` WHERE `id` = NEW.`product_id`;
    
    -- Chặn lại ngay lập tức nếu kho không đủ
    IF v_current_stock < NEW.`quantity` THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock for product';
    END IF;
END$$

-- B. Sau khi thêm sản phẩm vào đơn hợp lệ: Trừ kho & Ghi log
CREATE TRIGGER `tr_order_items_after_insert`
AFTER INSERT ON `order_items`
FOR EACH ROW
BEGIN
    -- Tính lại tổng tiền của đơn hàng
    CALL `sp_refresh_order_totals`(NEW.`order_id`);
    
    -- Trừ tồn kho
    UPDATE `products`
    SET `stock_quantity` = `stock_quantity` - NEW.`quantity`
    WHERE `id` = NEW.`product_id`;
    
    -- Ghi lịch sử xuất kho (số âm)
    INSERT INTO `stock_movements` (`product_id`, `quantity`, `reason`, `reference_type`, `reference_id`, `note`, `created_at`)
    VALUES (NEW.`product_id`, -NEW.`quantity`, 'ORDER', 'ORDER', NEW.`order_id`, CONCAT('Deducted for new order: ', NEW.`order_id`), NOW());
END$$


-- ==========================================================
-- 3. LOGIC MỚI: KHÓA MÃ GIẢM GIÁ NGAY LÚC ĐẶT HÀNG
-- ==========================================================
CREATE TRIGGER `tr_orders_after_insert`
AFTER INSERT ON `orders`
FOR EACH ROW
BEGIN
    IF NEW.`discount_code_id` IS NOT NULL THEN
        -- Ghi nhận lịch sử dùng mã
        INSERT INTO `discount_usages` (`discount_code_id`, `user_id`, `order_id`, `used_at`)
        VALUES (NEW.`discount_code_id`, NEW.`user_id`, NEW.`id`, NOW());
        
        -- Tăng biến đếm số lượt đã dùng của mã
        UPDATE `discount_codes`
        SET `used_count` = `used_count` + 1
        WHERE `id` = NEW.`discount_code_id`;
    END IF;
END$$


-- ==========================================================
-- 4. LOGIC MỚI: XỬ LÝ CHUYỂN TRẠNG THÁI VÀ HOÀN TRẢ
-- ==========================================================

-- A. Validate khi chuyển trạng thái (Bỏ check kho vì đã check ở trên)
CREATE TRIGGER `tr_orders_before_update_validate_paid`
BEFORE UPDATE ON `orders`
FOR EACH ROW
BEGIN
    DECLARE v_missing_items INT DEFAULT 0;

    -- Nếu cập nhật thành PAID, chỉ cần đảm bảo đơn không trống
    IF OLD.`status` <> 'PAID' AND NEW.`status` = 'PAID' THEN
        SELECT COUNT(*) INTO v_missing_items FROM `order_items` WHERE `order_id` = NEW.`id`;
        IF v_missing_items = 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot mark order as PAID without order items';
        END IF;

        SET NEW.`payment_status` = 'PAID';
        SET NEW.`paid_at` = COALESCE(NEW.`paid_at`, NOW());
    END IF;

    -- Nếu đơn bị HỦY, set timestamp hủy
    IF NEW.`status` = 'CANCELED' AND OLD.`status` <> 'CANCELED' THEN
        SET NEW.`canceled_at` = COALESCE(NEW.`canceled_at`, NOW());
    END IF;
END$$

-- B. Ghi lịch sử đơn hàng & TỰ ĐỘNG HOÀN KHO NẾU BỊ HỦY
CREATE TRIGGER `tr_orders_after_update_status_history`
AFTER UPDATE ON `orders`
FOR EACH ROW
BEGIN
    -- Ghi lịch sử trạng thái
    IF OLD.`status` <> NEW.`status` THEN
        INSERT INTO `order_status_history` (`order_id`, `old_status`, `new_status`, `changed_note`, `changed_at`)
        VALUES (NEW.`id`, OLD.`status`, NEW.`status`, NULL, NOW());
    END IF;

    -- *** QUAN TRỌNG: NẾU ĐƠN BỊ HỦY -> HOÀN LẠI TỒN KHO & MÃ GIẢM GIÁ ***
    IF OLD.`status` <> 'CANCELED' AND NEW.`status` = 'CANCELED' THEN
        
        -- 1. Cộng lại tồn kho cho các sản phẩm
        UPDATE `products` p
        JOIN (
            SELECT `product_id`, SUM(`quantity`) AS total_qty
            FROM `order_items`
            WHERE `order_id` = NEW.`id`
            GROUP BY `product_id`
        ) x ON p.`id` = x.`product_id`
        SET p.`stock_quantity` = p.`stock_quantity` + x.`total_qty`;
        
        -- 2. Ghi lịch sử nhập kho hoàn trả (số dương)
        INSERT INTO `stock_movements` (`product_id`, `quantity`, `reason`, `reference_type`, `reference_id`, `note`, `created_at`)
        SELECT oi.`product_id`, oi.`quantity`, 'RETURN', 'ORDER', NEW.`id`, CONCAT('Restored for canceled order: ', NEW.`order_code`), NOW()
        FROM `order_items` oi
        WHERE oi.`order_id` = NEW.`id`;

        -- 3. Hoàn lại lượt dùng mã giảm giá
        IF NEW.`discount_code_id` IS NOT NULL THEN
            UPDATE `discount_codes`
            SET `used_count` = `used_count` - 1
            WHERE `id` = NEW.`discount_code_id`;
            
            DELETE FROM `discount_usages` WHERE `order_id` = NEW.`id`;
        END IF;

    END IF;
END$$

DELIMITER ;