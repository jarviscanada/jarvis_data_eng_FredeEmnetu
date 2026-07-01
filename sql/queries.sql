-- sql queries
-- Question 1: adding a new facility - a spa.

INSERT
	INTO
	cd.facilities
VALUES (9,
'Spa',
20,
30,
100000,
800)

-- Question 2: insert row with automated id value
insert into cd.facilities 
select (select max(facid) from cd.facilities)+1, 'Spa', 20, 30, 100000, 800;

-- Question 3: update incorrect value in initial outlay column
update cd.facilities set initialoutlay=10000 where facid=1;

-- Question 4: update row based on another row
update cd.facilities as f
set membercost= f2.membercost*1.1,
	guestcost= f2.guestcost*1.1
from (select * from cd.facilities where facid=0) as f2
where f.facid=1;

-- Question 5: Delete all bookings
delete from cd.bookings;

-- Question 6: Delete a member from bookings table
delete from cd.members where memid=37;

-- Question 7: controlled retrevial
select facid, name, membercost, monthlymaintenance
from cd.facilities
where membercost > 0 and (monthlymaintenance * (1 / 50.0)) > membercost;

-- Question 8: Basic String Search
select * from cd.facilities
where name like '%Tennis%';

-- Question 9: match against multiple possible values
select * from cd.facilities
where facid in (1,5)

-- Question 10: working with dates
select memid, surname, firstname, joindate
from cd.members 
where joindate >= '2012-09-01 00:00:00'

-- Question 11: utlizing union
select surname from cd.members
union
select name from cd.facilities

--Question 12:  Retrieve start times
select starttime
from cd.bookings as b 
join cd.members as m
on m.memid=b.memid
where m.firstname='David' and m.surname='Farrell'

-- Question 13: start times of bookings for tennis courts
select b.starttime as start, f.name
from cd.bookings b join cd.facilities f
on b.facid = f.facid
where f.name like 'Tennis%' and b.starttime >= '2012-09-21' and b.starttime < '2012-09-22'
order by start

-- Question 14: producde list of members along with recommender
select m.firstname as memfname, m.surname as memsname, r.firstname as recfname,
r.surname as recsname
from cd.members as m
left join (select * from cd.members) as r
on m.recommendedby = r.memid
order by m.surname, m.firstname

-- Question 15: members who have recommended another member
select distinct r.firstname, r.surname 
from cd.members as m join cd.members as r
on r.memid = m.recommendedby
order by r.surname, r.firstname

-- Question 16: using subquerys instead of joins
select distinct m.firstname || ' ' || m.surname as member, 
(select r.firstname || ' ' || r.surname as recommender 
 	from cd.members as r
 	where m.recommendedby = r.memid)
from cd.members as m
order by member

-- Question 17: Number of recommendations each member makes
select recommendedby, count(*)
from cd.members
where recommendedby is not null
group by recommendedby
order by recommendedby

-- Question 18: list the total number of slots booked per facility
select f.facid, sum(b.slots) as "Total Slots"
from cd.bookings b join cd.facilities f
on b.facid = f.facid
group by f.facid
order by f.facid asc

-- Question 19: group by and order by
select b.facid, sum(b.slots)
from cd.bookings b
where b.starttime >= '2012-09-01' and b.starttime < '2012-10-01'
group by b.facid
order by sum(b.slots)

-- Question 20: List the total slots booked per facility per month
select b.facid, extract(month from b.starttime) as month, sum(b.slots)
from cd.bookings b
where b.starttime >= '2012-01-01' and b.starttime < '2013-01-01'
group by b.facid, month
order by b.facid, month

-- Question 21: numbers of members who have made at least 1 booking
select count(distinct m.memid)
from cd.members m join cd.bookings b on m.memid = b.memid
where b.slots > 0

-- Question 22: find members first booking
select m.surname, m.firstname, m.memid, min(b.starttime)
from cd.members m  join cd.bookings b on m.memid = b.memid
where b.starttime > '2012-09-01'
group by m.surname, m.firstname, m.memid
order by m.memid

-- Question 23: utilizing window functions
select count(memid) as count, firstname, surname
from cd.members
order by joindate

-- Question 24:numbered list of members
select row_number() over() as row_number, firstname, surname
from cd.membersn
order by joindate

-- Question 25: facility id with the highest slot bookings
select facid, total
from (
  select b.facid, sum(b.slots) as total, rank() over (order by sum(b.slots) desc) rank
  from cd.bookings b
  group by facid
  )
  where rank = 1

-- Question 26: output all members
select surname || ', ' || firstname as name
from cd.members

-- Question 27: Find phone numbers with parentheses
select memid, telephone
from cd.members
where telephone like '(___)%'

-- Question 28: Count the number of members whose surname starts with each letter of the alphabet
select substr(surname, 1, 1) as letter, count(*)
from cd.members
group by letter
order by letter asc