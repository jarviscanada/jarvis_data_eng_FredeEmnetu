-- uncomment as needed
-- Cluster Resource Planning (hosts with below average memory)
   select hi.id, hi.hostname, hi.total_mem, AVG(hi.total_mem) over () as avg_memory
   from host_info hi
   where hi.total_mem < (select avg(total_mem) from host_info)
   order by  hi.total_mem asc;

-- Node Failure Detection (hosts with no report in the last 2 minutes

select hi.hostname, max(hu.timestamp) as last_reported, now() - max(hu.timestamp) as time_since_last_report
from host_info hi
left join host_usage hu on hu.host_id = hi.id
group by hi.hostname
having max(hu.timestamp) < Now() - INTERVAL '2 minutes' or max(hu.timestamp) is NULL
order by last_reported asc nulls first;
