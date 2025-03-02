--liquibase formatted sql

--changeset init:001-create-user-credentials
CREATE TABLE `user_credentials` (
    `id` INT(11) NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL UNIQUE COLLATE 'utf8mb4_uca1400_ai_ci',
    `password` VARCHAR(255) NOT NULL COLLATE 'utf8mb4_uca1400_ai_ci',
    PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_uca1400_ai_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;
--rollback DROP TABLE `user_credentials`;
