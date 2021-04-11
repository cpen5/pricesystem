CREATE TABLE price (
                               id INT NOT NULL,
                               item VARCHAR(50),
                               unitPrice DECIMAL(20, 2),
                               specialPrice VARCHAR (50)

);

insert into price values(1, 'A', '3 for 140', 60);
insert into price values(2, 'B', '2 for 45', 30);
insert into price values(3, 'C', '', 20);
insert into price values(4, 'R', '', 10);

show columns from price;