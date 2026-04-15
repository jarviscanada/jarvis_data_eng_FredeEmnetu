# Introduction
This project utilizes relational test database containing information about a resort with tables tracking
bookings, members and facilities. The database is created in a `psql instance` running on a `docker container` and was initialized
with a sql file called clubdata.sql. Queries were generated to solve business problems and develop proficiency
in sql queries. Finally `Git` was used to track and manage files.

--- 
## Table Setup (DDL)


---
# SQL Queries
## Solutions
### Question 1: Adding a new facility - a spa
```sql
INSERT INTO cd.facilities
VALUES (9, 'Spa', 20, 30, 100000, 800);
````

### Question 2: Insert row with automated ID value

```sql
INSERT INTO cd.facilities 
SELECT (SELECT MAX(facid) FROM cd.facilities) + 1, 'Spa', 20, 30, 100000, 800;
```

### Question 3: Update incorrect value in initial outlay column

```sql
UPDATE cd.facilities 
SET initialoutlay = 10000 
WHERE facid = 1;
```

### Question 4: Update row based on another row

```sql
UPDATE cd.facilities AS f
SET membercost = f2.membercost * 1.1,
    guestcost = f2.guestcost * 1.1
FROM (SELECT * FROM cd.facilities WHERE facid = 0) AS f2
WHERE f.facid = 1;
```

### Question 5: Delete all bookings

```sql
DELETE FROM cd.bookings;
```

### Question 6: Delete a member from bookings table

```sql
DELETE FROM cd.members 
WHERE memid = 37;
```

### Question 7: Controlled retrieval

```sql
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost > 0 
  AND (monthlymaintenance * (1 / 50.0)) > membercost;
```

### Question 8: Basic String Search

```sql
SELECT * FROM cd.facilities
WHERE name LIKE '%Tennis%';
```

### Question 9: Match against multiple possible values

```sql
SELECT * FROM cd.facilities
WHERE facid IN (1, 5);
```

### Question 10: Working with dates

```sql
SELECT memid, surname, firstname, joindate
FROM cd.members 
WHERE joindate >= '2012-09-01 00:00:00';
```

### Question 11: Utilizing `UNION`

```sql
SELECT surname FROM cd.members
UNION
SELECT name FROM cd.facilities;
```

### Question 12: Retrieve start times

```sql
SELECT starttime
FROM cd.bookings AS b 
JOIN cd.members AS m
ON m.memid = b.memid
WHERE m.firstname = 'David' 
  AND m.surname = 'Farrell';
```

### Question 13: Start times of bookings for tennis courts

```sql
SELECT b.starttime AS start, f.name
FROM cd.bookings b 
JOIN cd.facilities f
ON b.facid = f.facid
WHERE f.name LIKE 'Tennis%' 
  AND b.starttime >= '2012-09-21' 
  AND b.starttime < '2012-09-22'
ORDER BY start;
```

### Question 14: Produce list of members along with recommender

```sql
SELECT m.firstname AS memfname, m.surname AS memsname, 
       r.firstname AS recfname, r.surname AS recsname
FROM cd.members AS m
LEFT JOIN (SELECT * FROM cd.members) AS r
ON m.recommendedby = r.memid
ORDER BY m.surname, m.firstname;
```

### Question 15: Members who have recommended another member

```sql
SELECT DISTINCT r.firstname, r.surname 
FROM cd.members AS m 
JOIN cd.members AS r
ON r.memid = m.recommendedby
ORDER BY r.surname, r.firstname;
```

### Question 16: Using subqueries instead of joins

```sql
SELECT DISTINCT m.firstname || ' ' || m.surname AS member, 
       (SELECT r.firstname || ' ' || r.surname AS recommender
        FROM cd.members AS r
        WHERE m.recommendedby = r.memid) 
FROM cd.members AS m
ORDER BY member;
```

### Question 17: Number of recommendations each member makes

```sql
SELECT recommendedby, COUNT(*)
FROM cd.members
WHERE recommendedby IS NOT NULL
GROUP BY recommendedby
ORDER BY recommendedby;
```

### Question 18: List the total number of slots booked per facility

```sql
SELECT f.facid, SUM(b.slots) AS "Total Slots"
FROM cd.bookings b 
JOIN cd.facilities f
ON b.facid = f.facid
GROUP BY f.facid
ORDER BY f.facid ASC;
```

### Question 19: Group by and order by

```sql
SELECT b.facid, SUM(b.slots)
FROM cd.bookings b
WHERE b.starttime >= '2012-09-01' 
  AND b.starttime < '2012-10-01'
GROUP BY b.facid
ORDER BY SUM(b.slots);
```

### Question 20: List the total slots booked per facility per month

```sql
SELECT b.facid, EXTRACT(MONTH FROM b.starttime) AS month, SUM(b.slots)
FROM cd.bookings b
WHERE b.starttime >= '2012-01-01' 
  AND b.starttime < '2013-01-01'
GROUP BY b.facid, month
ORDER BY b.facid, month;
```

### Question 21: Number of members who have made at least one booking

```sql
SELECT COUNT(DISTINCT m.memid)
FROM cd.members m 
JOIN cd.bookings b 
ON m.memid = b.memid
WHERE b.slots > 0;
```

### Question 22: Find members' first booking

```sql
SELECT m.surname, m.firstname, m.memid, MIN(b.starttime)
FROM cd.members m  
JOIN cd.bookings b 
ON m.memid = b.memid
WHERE b.starttime > '2012-09-01'
GROUP BY m.surname, m.firstname, m.memid
ORDER BY m.memid;
```

### Question 23: Utilizing window functions

```sql
SELECT COUNT(memid) AS count, firstname, surname
FROM cd.members
ORDER BY joindate;
```

### Question 24: Numbered list of members

```sql
SELECT ROW_NUMBER() OVER() AS row_number, firstname, surname
FROM cd.members
ORDER BY joindate;
```

### Question 25: Facility ID with the highest slot bookings

```sql
SELECT facid, total
FROM (
  SELECT b.facid, SUM(b.slots) AS total, RANK() OVER (ORDER BY SUM(b.slots) DESC) rank
  FROM cd.bookings b
  GROUP BY facid
) 
WHERE rank = 1;
```

### Question 26: Output all members

```sql
SELECT surname || ', ' || firstname AS name
FROM cd.members;
```

### Question 27: Find phone numbers with parentheses

```sql
SELECT memid, telephone
FROM cd.members
WHERE telephone LIKE '(___)%';
```

### Question 28: Count the number of members whose surname starts with each letter of the alphabet

```sql
SELECT SUBSTR(surname, 1, 1) AS letter, COUNT(*)
FROM cd.members
GROUP BY letter
ORDER BY letter ASC;
```

