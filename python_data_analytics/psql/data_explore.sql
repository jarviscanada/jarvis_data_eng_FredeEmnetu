-- Show table schema 
\d + retail;
-- Show first 10 rows
SELECT
	*
FROM
	retail
LIMIT 10;
-- Check # of records
SELECT
	count(*)
FROM
	retail;
-- number of clients (e.g. unique client ID)
SELECT
	count(DISTINCT customer_id)
FROM
	retail;
-- invoice date range
SELECT
	min(invoice_date) AS earliest,
	max(invoice_date) AS latest
FROM
	retail;
-- number of SKU/merchants
SELECT
	count(DISTINCT stock_code)
FROM
	retail;
-- average invoice amount excluding canceled orders
SELECT
	avg(amount)
FROM
	(
	SELECT
		invoice_no,
		sum(unit_price * quantity) AS amount
	FROM
		retail
	GROUP BY
		invoice_no
	HAVING
		sum(unit_price * quantity) > 0) AS subtable;
-- calculate total tevene
SELECT
	sum(unit_price * quantity)
FROM
	retail;
-- Total revene by YYYYMM
SELECT
	to_char(invoice_date, 'YYYY-MM') AS yyyymm,
	sum(unit_price * quantity)
FROM
	retail
GROUP BY
	yyyymm
ORDER BY
	yyyymm ASC;
