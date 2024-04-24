create table auth_user (
    id			INT NOT NULL,
    username 	VARCHAR NOT NULL,
    password 	VARCHAR NOT NULL,
    email 		VARCHAR NOT NULL,
    status      VARCHAR(CHAR 20) NOT NULL,
    created     TIMESTAMP,
    updated     TIMESTAMP,
    version     INT,
    CONSTRAINT pk_user_id PRIMARY KEY (id),
    CONSTRAINT uk_user_username UNIQUE (username),
    CONSTRAINT uk_user_email UNIQUE (email)
);

create SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

create table role(
    id          INT NOT NULL,
    role_name    VARCHAR NOT NULL,
    created     TIMESTAMP,
    updated     TIMESTAMP,
    version     INT,
    CONSTRAINT pk_role_id PRIMARY KEY (id),
    CONSTRAINT uk_role_roleName UNIQUE (role_name)
);

create SEQUENCE role_seq START WITH 1 INCREMENT BY 1;

create table user_role (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id),
    CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role(id)
);

create table authority(
    id              INT NOT NULL,
    authority_name  VARCHAR NOT NULL,
    created         TIMESTAMP,
    updated         TIMESTAMP,
    version         INT,
    CONSTRAINT pk_authority_id PRIMARY KEY (id),
    CONSTRAINT uk_authority_authName UNIQUE (authority_name)
);

create SEQUENCE authority_seq START WITH 1 INCREMENT BY 1;

create table user_authority (
    user_id         INT NOT NULL,
    authority_id    INT NOT NULL,
    CONSTRAINT pk_user_auth PRIMARY KEY (user_id, authority_id),
    CONSTRAINT fk_user_auth_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id),
    CONSTRAINT fk_user_auth_auth_id FOREIGN KEY (authority_id) REFERENCES authority(id)
);
