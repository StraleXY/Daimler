insert into LOCATION (id, address, latitude, longitude) values ('144', 'Fruskogorska 25', '45.24328841896326', '19.84750265443095');

insert into USER (type, id, email, password) values ('passenger', '20', 'passenger20@gmail.com', 'passenger20password');
insert into USER (type, id, email, password) values ('passenger', '666', 'passenger666@gmail.com', 'passenger66password');
insert into USER (type, id, email, password) values ('driver', '21', 'driver21@gmail.com', 'driver21password');
insert into USER (type, id, email, password) values ('driver', '6666', 'driver6666@gmail.com', 'driver6666password');

insert into RIDE (id, status, driver_id, baby_transport, pet_transport, total_cost, distance)
values ('50', 'PENDING', '21', true, false, 500, 200.4);
insert into RIDE (id, status, driver_id, baby_transport, pet_transport, total_cost, distance, scheduled_timestamp)
values ('666', 'FINISHED', '6666', true, false, 6000, 600, 100000);
insert into RIDE (id, status, driver_id, baby_transport, pet_transport, total_cost, distance, scheduled_timestamp)
values ('6666', 'REJECTED', '6666', true, false, 6000, 600, 400000);

insert into RIDE_PASSENGERS (ride_id, passengers_id) values ('50', '20');
insert into RIDE_PASSENGERS (ride_id, passengers_id) values ('666', '666');
insert into RIDE_PASSENGERS (ride_id, passengers_id) values ('6666', '666');
