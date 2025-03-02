--liquibase formatted sql

--changeset init:001-create-user
CREATE TABLE `user` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    `first_name` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    `is_registered` BIT(1) NULL DEFAULT NULL,
    `last_name` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    `password` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_user_email` (`email`) USING BTREE
)
COLLATE='utf8mb4_uca1400_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;
--rollback DROP TABLE `user`;

--changeset init:002-create-verification-code
CREATE TABLE `verification_code` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NULL DEFAULT NULL UNIQUE COLLATE 'utf8mb4_uca1400_ai_ci',
    `code` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_uca1400_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;
--rollback DROP TABLE `verification_code`;

--changeset init:003-create-email-outbox
CREATE TABLE `email_outbox` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `payload` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    `status` TINYINT(1) NOT NULL,
    `error_message` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_uca1400_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;
--rollback DROP TABLE `email_outbox`;

--changeset init:003-create-credentials-outbox
CREATE TABLE `credentials_outbox` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `payload` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    `status` TINYINT(1) NOT NULL,
    `error_message` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_uca1400_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;
--rollback DROP TABLE `credentials_outbox`;
