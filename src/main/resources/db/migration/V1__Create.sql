CREATE TABLE IF NOT EXISTS users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) UNIQUE,
                       password VARCHAR(255),
                       email VARCHAR(255),
                       role VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS books (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255),
                       author VARCHAR(255),
                       description VARCHAR(1000),
                       quantity INT,
                       available_quantity INT
);

CREATE TABLE IF NOT EXISTS book_reservations (
                              id SERIAL PRIMARY KEY,
                              user_id INT,
                              book_id INT,
                              reservation_date TIMESTAMP,
                              expiration_date TIMESTAMP,
                              active BOOLEAN,
                              FOREIGN KEY (user_id) REFERENCES users(id),
                              FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE IF NOT EXISTS book_loans (
                       id SERIAL PRIMARY KEY,
                       user_id INT,
                       book_id INT,
                       loan_date TIMESTAMP,
                       due_date TIMESTAMP,
                       return_date TIMESTAMP,
                       returned BOOLEAN,
                       FOREIGN KEY (user_id) REFERENCES users(id),
                       FOREIGN KEY (book_id) REFERENCES books(id)
);
