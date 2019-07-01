-- user with id: a / pw: a
INSERT INTO users (authorities, email, password, username) VALUES ('ROLE_USER', null, '{bcrypt}$2a$10$Pi5rS9cBzaqop5UwvdgsROADAASHo0igVQj2jm9LzKajIlpiuzw9i', 'a')

-- user with id: test / pw: test
INSERT INTO users (authorities, email, password, username) VALUES ('ROLE_USER', null, '{bcrypt}$2a$12$MNFcg19Su6PDBRbMcyZ4K.I1mu8k8Flq6332Gtu/M3cT7qCB4mt9.', 'test')