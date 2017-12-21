
select date_trunc('day', v.createdat) as dt, v.post_id, p.title, count(distinct v.clientip) as count
from visits as v
  left join seo_robots_agents as ra
    on case when ra.isregexp then lower(nullif(v.useragent,'')) ~* lower(ra.useragent)
       else lower(nullif(v.useragent,'')) like concat('%',lower(ra.useragent),'%') end
  inner join posts as p
    on v.post_id = p.id
where v.isadmin = false
      and ra.id isnull
group by date_trunc('day', v.createdat), v.post_id, p.title
order by date_trunc('day', v.createdat), v.post_id