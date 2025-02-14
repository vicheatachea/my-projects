DROP USER IF EXISTS 'simulation_user'@'localhost';

CREATE USER IF NOT EXISTS 'simulation_user'@'localhost' IDENTIFIED BY 'password';

GRANT ALL PRIVILEGES ON simulation.* TO 'simulation_user'@'localhost';
FLUSH PRIVILEGES;
